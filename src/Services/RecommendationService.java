package Services;

import Classes.Course;
import Classes.RecommendedCourse;
import Classes.RecommendedCourse.Priority;
import Classes.Student;

import java.util.*;

/**
 * Produces an ordered list of {@link RecommendedCourse} objects for a student.
 *
 * <h2>Algorithm overview</h2>
 * <ol>
 *   <li>Start from the student's <em>available</em> courses (prerequisites met,
 *       category limits respected) — computed externally by the existing
 *       AdvisorService_* classes and passed in.</li>
 *   <li>Score every available course on five weighted dimensions:
 *       <ul>
 *         <li><b>Semester alignment</b> – courses whose year/semester match
 *             the student's current position score higher.</li>
 *         <li><b>Category priority</b> – core/required categories outrank
 *             electives.</li>
 *         <li><b>Prerequisite unlock value</b> – courses that unlock many
 *             future courses are prioritised.</li>
 *         <li><b>Difficulty balance</b> – avoids grouping too many "hard"
 *             courses in the same recommended set.</li>
 *         <li><b>GPA-adjusted load</b> – students with lower GPAs are steered
 *             toward lighter, foundational courses.</li>
 *       </ul>
 *   </li>
 *   <li>Sort by score descending and attach a {@link Priority} tier and
 *       human-readable reason strings.</li>
 *   <li>Apply a soft credit-hour cap so the returned list does not suggest
 *       more than the student's allowed load.</li>
 * </ol>
 */
public class RecommendationService {

    // ── Tuneable weights (must sum to ~100) ──────────────────────────────────
    private static final int W_SEMESTER_ALIGN  = 30;
    private static final int W_CATEGORY        = 25;
    private static final int W_UNLOCK_VALUE    = 20;
    private static final int W_DIFFICULTY      = 15;
    private static final int W_GPA_FIT         = 10;

    // ── Category priority map (higher = more important) ─────────────────────
    // Core/required academic categories get the highest scores.
    private static final Map<String, Integer> CATEGORY_PRIORITY = Map.ofEntries(
        Map.entry("CS_CORE",        100),
        Map.entry("AI_CORE",        100),
        Map.entry("MATH",            90),
        Map.entry("PHYSICS",         85),
        Map.entry("CS_REQUIRED",     80),
        Map.entry("AI_REQUIRED",     80),
        Map.entry("ENGINEERING",     75),
        Map.entry("CS_ELECTIVES",    55),
        Map.entry("AI_ELECTIVES",    55),
        Map.entry("Social Sciences", 35),
        Map.entry("Humanities",      30),
        Map.entry("ARTS",            25)
    );

    // Categories considered "difficult" for difficulty-balance purposes
    private static final Set<String> HARD_CATEGORIES = Set.of(
        "CS_CORE", "AI_CORE", "MATH", "PHYSICS", "ENGINEERING"
    );

    // ── State ────────────────────────────────────────────────────────────────
    private final List<Course> allCourses;   // full catalogue for unlock-value calc

    // ── Constructor ──────────────────────────────────────────────────────────
    public RecommendationService(List<Course> allCourses) {
        if (allCourses == null) throw new IllegalArgumentException("allCourses cannot be null");
        this.allCourses = allCourses;
    }

    // =========================================================================
    // Public API
    // =========================================================================

    /**
     * Returns a ranked list of recommended courses for {@code student} chosen
     * from {@code availableCourses} (courses whose prerequisites are already
     * satisfied).
     *
     * @param student          the student requesting advice
     * @param availableCourses pre-filtered list from AdvisorService
     * @param maxRecommended   soft cap on how many courses to return (use
     *                         {@link LoadService#getMaxRecommendations} for a
     *                         sensible default)
     */
    public List<RecommendedCourse> recommend(
            Student        student,
            List<Course>   availableCourses,
            int            maxRecommended) {

        if (availableCourses == null || availableCourses.isEmpty()) {
            return Collections.emptyList();
        }

        // Pre-compute unlock map once (expensive, O(n²) but n is small)
        Map<String, Integer> unlockCounts = computeUnlockCounts(student);

        // Score every available course
        List<RecommendedCourse> scored = new ArrayList<>();
        for (Course c : availableCourses) {
            ScoredResult result = scoreCourse(c, student, unlockCounts);
            RecommendedCourse rc = new RecommendedCourse(
                c,
                derivePriority(result.score),
                result.score,
                result.reasons
            );
            scored.add(rc);
        }

        // Sort: highest score first; break ties by year then semester
        scored.sort(Comparator
            .comparingInt(RecommendedCourse::getScore).reversed()
            .thenComparingInt(rc -> rc.getCourse().getYear())
            .thenComparingInt(rc -> rc.getCourse().getSemester()));

        // Apply soft credit-hour cap
        int maxCredits = LoadService.getMaxCreditHours(student.getGpa());
        List<RecommendedCourse> result = new ArrayList<>();
        int usedCredits = 0;
        int usedHard    = 0;

        for (RecommendedCourse rc : scored) {
            if (result.size() >= maxRecommended) break;

            int ch = rc.getCreditHours();
            // Prevent recommending if it would bust the credit cap
            if (usedCredits + ch > maxCredits) continue;

            // Difficulty guard: at most 3 "hard" courses in the same set
            // (unless the student is a high-achiever with GPA ≥ 3.5)
            boolean isHard = HARD_CATEGORIES.contains(rc.getCategory());
            int hardCap = student.getGpa() >= 3.5 ? 4 : 3;
            if (isHard && usedHard >= hardCap) continue;

            result.add(rc);
            usedCredits += ch;
            if (isHard) usedHard++;
        }

        return Collections.unmodifiableList(result);
    }

    // =========================================================================
    // Scoring internals
    // =========================================================================

    /** Holder for a raw score plus the human-readable reason strings. */
    private static class ScoredResult {
        final int          score;
        final List<String> reasons;
        ScoredResult(int score, List<String> reasons) {
            this.score   = score;
            this.reasons = reasons;
        }
    }

    /**
     * Computes a 0-100 composite score for a single course.
     * Each dimension contributes a fraction of its weight.
     */
    private ScoredResult scoreCourse(
            Course            course,
            Student           student,
            Map<String, Integer> unlockCounts) {

        List<String> reasons = new ArrayList<>();
        double totalScore    = 0;

        // ── 1. Semester alignment ────────────────────────────────────────────
        int courseAbsSem  = (course.getYear() - 1) * 2 + course.getSemester();
        int studentAbsSem = (student.getYear() - 1) * 2 + student.getCurrentSemester();
        int semDiff       = courseAbsSem - studentAbsSem;

        double semScore;
        if      (semDiff == 0)  { semScore = 1.00; reasons.add("Perfectly aligned with your current semester"); }
        else if (semDiff == 1)  { semScore = 0.85; reasons.add("Scheduled for next semester — great to take now"); }
        else if (semDiff == -1) { semScore = 0.70; reasons.add("Slightly behind schedule — catching up is recommended"); }
        else if (semDiff > 1 && semDiff <= 2) { semScore = 0.50; }
        else if (semDiff < -1 && semDiff >= -2) { semScore = 0.40; }
        else    { semScore = 0.20; }

        totalScore += semScore * W_SEMESTER_ALIGN;

        // ── 2. Category priority ─────────────────────────────────────────────
        int catPriority = CATEGORY_PRIORITY.getOrDefault(course.getCategory(), 40);
        double catScore = catPriority / 100.0;

        if (catPriority >= 80) reasons.add("Core requirement — must complete for graduation");
        else if (catPriority >= 60) reasons.add("Required course for your major");
        else if (catPriority >= 40) reasons.add("Elective that broadens your profile");
        else reasons.add("General education / arts elective");

        totalScore += catScore * W_CATEGORY;

        // ── 3. Prerequisite unlock value ─────────────────────────────────────
        int unlocks      = unlockCounts.getOrDefault(course.getCode(), 0);
        double unlockScr = Math.min(unlocks / 5.0, 1.0); // normalise to max ~5 unlocks

        if (unlocks >= 3) reasons.add("Unlocks " + unlocks + " future courses — high strategic value");
        else if (unlocks >= 1) reasons.add("Unlocks " + unlocks + " future course" + (unlocks > 1 ? "s" : ""));

        totalScore += unlockScr * W_UNLOCK_VALUE;

        // ── 4. Difficulty balance ────────────────────────────────────────────
        // Prefer courses that don't pile difficulty; slightly favour lighter
        // courses when GPA < 2.5
        boolean isHard = HARD_CATEGORIES.contains(course.getCategory());
        double diffScore;
        if (student.getGpa() < 2.0) {
            // Struggling student: prioritise foundational / lighter courses
            diffScore = isHard ? 0.3 : 0.9;
            if (!isHard) reasons.add("Lower difficulty — good choice to boost your GPA");
        } else if (student.getGpa() < 2.5) {
            diffScore = isHard ? 0.5 : 0.8;
        } else {
            // Healthy GPA: difficulty is not a penalty
            diffScore = 0.8;
        }
        totalScore += diffScore * W_DIFFICULTY;

        // ── 5. GPA-adjusted load fit ─────────────────────────────────────────
        // Reward lightweight courses when the student is on probation
        int ch = course.getCreditHours();
        double gpaFit;
        if (student.getGpa() >= 3.0) {
            gpaFit = 1.0; // high achiever: any credit load is fine
            if (ch >= 3) reasons.add("You have a strong GPA — ready for full credit load");
        } else if (student.getGpa() >= 2.0) {
            gpaFit = ch <= 3 ? 0.9 : 0.6;
        } else {
            gpaFit = ch <= 2 ? 0.9 : 0.4;
            reasons.add("Lighter course (" + ch + " credits) — helps manage workload");
        }
        totalScore += gpaFit * W_GPA_FIT;

        return new ScoredResult((int) Math.round(totalScore), reasons);
    }

    /**
     * Builds a map: courseCode → number of NOT-yet-completed courses that list
     * this course as a direct prerequisite.
     */
    private Map<String, Integer> computeUnlockCounts(Student student) {
        Map<String, Integer> counts = new HashMap<>();

        for (Course candidate : allCourses) {
            if (student.hasCompleted(candidate.getCode())) continue;

            for (String prereq : candidate.getPrerequisites()) {
                // Only count courses the student hasn't taken yet as "unlockable"
                if (!student.hasCompleted(prereq)) {
                    counts.merge(prereq, 1, Integer::sum);
                }
            }
        }
        return counts;
    }

    /** Maps a 0-100 score to a three-tier priority label. */
    private static Priority derivePriority(int score) {
        if (score >= 65) return Priority.HIGH;
        if (score >= 40) return Priority.MEDIUM;
        return Priority.LOW;
    }
}
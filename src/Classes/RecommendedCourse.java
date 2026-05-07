package Classes;

import java.util.List;
import java.util.ArrayList;

/**
 * Wraps a {@link Course} with recommendation metadata produced by
 * {@link Services.RecommendationService}.
 *
 * <p>Immutable once constructed; all mutation happens inside the service
 * before the object is handed to callers.
 */
public class RecommendedCourse {

    // ── Priority tiers ──────────────────────────────────────────────────────
    public enum Priority {
        HIGH,   // strongly recommended this semester
        MEDIUM, // good choice but not urgent
        LOW     // optional / elective filler
    }

    // ── Fields ──────────────────────────────────────────────────────────────
    private final Course     course;
    private final Priority   priority;
    private final int        score;          // 0-100 composite score (higher = better)
    private final List<String> reasons;      // human-readable recommendation reasons

    // ── Constructor ─────────────────────────────────────────────────────────
    public RecommendedCourse(Course course, Priority priority, int score, List<String> reasons) {
        if (course   == null) throw new IllegalArgumentException("course cannot be null");
        if (priority == null) throw new IllegalArgumentException("priority cannot be null");
        if (score < 0 || score > 100)
            throw new IllegalArgumentException("score must be 0–100, got " + score);

        this.course   = course;
        this.priority = priority;
        this.score    = score;
        this.reasons  = new ArrayList<>(reasons == null ? List.of() : reasons);
    }

    // ── Getters ─────────────────────────────────────────────────────────────
    public Course   getCourse()   { return course;   }
    public Priority getPriority() { return priority; }
    public int      getScore()    { return score;    }

    /** Returns an unmodifiable view of the recommendation reasons. */
    public List<String> getReasons() {
        return java.util.Collections.unmodifiableList(reasons);
    }

    // Convenience delegates so callers don't always need to unwrap the course
    public String getCode()        { return course.getCode();        }
    public String getName()        { return course.getName();        }
    public int    getCreditHours() { return course.getCreditHours(); }
    public String getCategory()    { return course.getCategory();    }

    @Override
    public String toString() {
        return "RecommendedCourse{" +
               "code='"    + getCode()    + '\'' +
               ", name='"  + getName()    + '\'' +
               ", score="  + score        +
               ", priority=" + priority   +
               ", reasons=" + reasons     +
               '}';
    }
}
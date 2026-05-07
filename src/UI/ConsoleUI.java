package UI;

import Classes.Course;
import Classes.RecommendedCourse;
import Classes.RecommendedCourse.Priority;
import Classes.Student;
import Services.LoadService;

import java.util.List;

/**
 * Renders the Smart Academic Advisor output to the console using ANSI colour
 * codes and box-drawing characters for a clean, modern terminal UI.
 *
 * <p>All rendering is side-effect-only (prints to stdout); no state is held.
 */
public class ConsoleUI {

    // ── ANSI colour/style codes ───────────────────────────────────────────────
    private static final String RESET   = "\u001B[0m";
    private static final String BOLD    = "\u001B[1m";
    private static final String DIM     = "\u001B[2m";

    // Foreground colours
    private static final String FG_WHITE   = "\u001B[97m";
    private static final String FG_CYAN    = "\u001B[96m";
    private static final String FG_GREEN   = "\u001B[92m";
    private static final String FG_YELLOW  = "\u001B[93m";
    private static final String FG_RED     = "\u001B[91m";
    private static final String FG_BLUE    = "\u001B[94m";
    private static final String FG_MAGENTA = "\u001B[95m";
    private static final String FG_GRAY    = "\u001B[90m";

    // Background colours
    private static final String BG_DARK_BLUE = "\u001B[44m";
    private static final String BG_DARK_GRAY = "\u001B[100m";

    // ── Box-drawing constants ─────────────────────────────────────────────────
    private static final int BOX_WIDTH = 72;
    private static final String H_LINE = "─".repeat(BOX_WIDTH - 2);

    private ConsoleUI() {}  // utility class

    // =========================================================================
    // Top-level render methods
    // =========================================================================

    /**
     * Prints a full advisor report for the student, combining the available
     * courses section and the recommended courses section.
     */
    public static void renderReport(
            Student               student,
            List<Course>          available,
            List<RecommendedCourse> recommended) {

        printBanner();
        printStudentCard(student);
        printLoadSummary(student);
        printDivider();
        printAvailableSection(available);
        printDivider();
        printRecommendedSection(recommended, student);
        printFooter();
    }

    // =========================================================================
    // Section renderers
    // =========================================================================

    /** Prints the top banner. */
    private static void printBanner() {
        System.out.println();
        box("╔", "╗", "═");
        centerLine("  🎓  SMART ACADEMIC ADVISOR  🎓  ", FG_CYAN + BOLD);
        centerLine("Personalised Course Planning System", FG_GRAY);
        box("╚", "╝", "═");
        System.out.println();
    }

    /** Prints a summary card for the student. */
    private static void printStudentCard(Student student) {
        System.out.println(FG_BLUE + BOLD + "  ┌─ STUDENT PROFILE " + H_LINE.substring(18) + "┐" + RESET);
        labelValue("  │  Name",    student.getName());
        labelValue("  │  ID",      student.getId());
        labelValue("  │  Major",   student.getMajor());
        labelValue("  │  Year",    "Year " + student.getYear()
                                  + "  ·  Semester " + student.getCurrentSemester());
        labelValue("  │  GPA",     formatGpa(student.getGpa()));
        labelValue("  │  Courses Completed",
                   student.getCompletedCoursesCount() + " course(s)");
        System.out.println(FG_BLUE + BOLD + "  └" + H_LINE + "┘" + RESET);
        System.out.println();
    }

    /** Prints the GPA-based load advisory. */
    private static void printLoadSummary(Student student) {
        String loadType  = LoadService.getLoadType(student.getGpa());
        String advice    = LoadService.getLoadAdvice(student.getGpa());
        int    maxCh     = LoadService.getMaxCreditHours(student.getGpa());

        String colour = student.getGpa() >= 3.0 ? FG_GREEN
                      : student.getGpa() >= 2.0 ? FG_YELLOW
                      : FG_RED;

        System.out.println(colour + BOLD + "  ⚡  Load Classification: " + loadType
                         + "  (max " + maxCh + " credit hours)" + RESET);
        System.out.println(FG_GRAY + "     " + advice + RESET);
        System.out.println();
    }

    /** Prints the "Available Courses" section. */
    private static void printAvailableSection(List<Course> available) {
        sectionHeader("📋  AVAILABLE COURSES", FG_CYAN,
                      available.size() + " course(s) eligible for registration");

        if (available.isEmpty()) {
            emptyCourseNotice("No courses available right now.");
            return;
        }

        // Table header
        System.out.println(FG_GRAY
            + "  ┌──────────────┬─────────────────────────────────────────────┬──────┐"
            + RESET);
        System.out.printf(FG_GRAY + "  │ %-12s │ %-43s │ %4s │%n" + RESET,
            BOLD + "Code" + RESET + FG_GRAY,
            BOLD + "Course Name" + RESET + FG_GRAY,
            BOLD + "CH" + RESET + FG_GRAY);
        System.out.println(FG_GRAY
            + "  ├──────────────┼─────────────────────────────────────────────┼──────┤"
            + RESET);

        for (Course c : available) {
            System.out.printf("  │ " + FG_WHITE + "%-12s" + RESET
                            + " │ %-43s │ " + FG_CYAN + "%4d" + RESET + " │%n",
                c.getCode(),
                truncate(c.getName(), 43),
                c.getCreditHours());
        }
        System.out.println(FG_GRAY
            + "  └──────────────┴─────────────────────────────────────────────┴──────┘"
            + RESET);
    }

    /** Prints the "Recommended Courses" section. */
    private static void printRecommendedSection(
            List<RecommendedCourse> recommended,
            Student student) {

        sectionHeader("✨  RECOMMENDED COURSES", FG_MAGENTA,
                      "Top " + recommended.size() + " picks for your next semester");

        if (recommended.isEmpty()) {
            emptyCourseNotice("No recommendations available — you may have "
                            + "completed all required courses!");
            return;
        }

        int rank = 1;
        for (RecommendedCourse rc : recommended) {
            printRecommendationCard(rc, rank++, student);
        }
    }

    // =========================================================================
    // Card renderer
    // =========================================================================

    /** Prints a single course recommendation card. */
    private static void printRecommendationCard(
            RecommendedCourse rc, int rank, Student student) {

        String priorityColour = priorityColour(rc.getPriority());
        String priorityBadge  = priorityBadge(rc.getPriority());
        String scoreBar       = scoreBar(rc.getScore());

        System.out.println();
        // Card top border
        System.out.println(priorityColour + BOLD
            + "  ╔═══ #" + rank + " ════════════════════════════════════════════════════╗"
            + RESET);

        // Course code & name
        System.out.printf(priorityColour + BOLD + "  ║  %-14s" + RESET
                        + " %-43s" + priorityColour + BOLD + "  ║%n" + RESET,
            rc.getCode(),
            truncate(rc.getName(), 43));

        // Priority badge + score bar
        System.out.printf("  ║  %s  Score: %s  %3d/100  %s%n",
            priorityBadge,
            scoreBar,
            rc.getScore(),
            priorityColour + "║" + RESET);

        // Metadata row
        System.out.printf("  ║  " + DIM + "Category: %-18s  Credits: %-3d  "
                        + "Year %d · Sem %d" + RESET + "  %s%n",
            rc.getCategory(),
            rc.getCreditHours(),
            rc.getCourse().getYear(),
            rc.getCourse().getSemester(),
            priorityColour + "║" + RESET);

        // Reasons
        System.out.println("  ║" + RESET);
        for (String reason : rc.getReasons()) {
            System.out.printf("  ║  " + FG_GREEN + "▸  " + RESET + "%-55s" + priorityColour + "║%n" + RESET,
                truncate(reason, 55));
        }

        // Card bottom border
        System.out.println(priorityColour + BOLD
            + "  ╚═══════════════════════════════════════════════════════════╝"
            + RESET);
    }

    // =========================================================================
    // Formatting helpers
    // =========================================================================

    private static void printDivider() {
        System.out.println();
        System.out.println(FG_GRAY + "  " + "·".repeat(BOX_WIDTH - 2) + RESET);
        System.out.println();
    }

    private static void printFooter() {
        System.out.println();
        System.out.println(FG_GRAY + DIM
            + "  Smart Academic Advisor  ·  Results generated based on your "
            + "completed courses and GPA."
            + RESET);
        System.out.println();
    }

    private static void sectionHeader(String title, String colour, String subtitle) {
        System.out.println(colour + BOLD + "  " + title + RESET);
        System.out.println(FG_GRAY + "  " + subtitle + RESET);
        System.out.println();
    }

    private static void emptyCourseNotice(String message) {
        System.out.println(FG_YELLOW + "  ⚠  " + message + RESET);
        System.out.println();
    }

    private static void labelValue(String label, String value) {
        System.out.printf(FG_GRAY + "%-30s" + RESET + " %s%n", label + ":", value);
    }

    private static void centerLine(String text, String style) {
        int padding = Math.max(0, (BOX_WIDTH - text.length()) / 2);
        System.out.println(style + " ".repeat(padding) + text + RESET);
    }

    private static void box(String left, String right, String fill) {
        System.out.println(FG_BLUE + BOLD + left + fill.repeat(BOX_WIDTH - 2) + right + RESET);
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    // ── GPA colour ────────────────────────────────────────────────────────────
    private static String formatGpa(double gpa) {
        String colour = gpa >= 3.5 ? FG_GREEN
                      : gpa >= 3.0 ? FG_CYAN
                      : gpa >= 2.0 ? FG_YELLOW
                      : FG_RED;
        return colour + BOLD + String.format("%.2f", gpa) + RESET
             + "  " + FG_GRAY + "(" + LoadService.getLoadType(gpa) + ")" + RESET;
    }

    // ── Priority helpers ──────────────────────────────────────────────────────
    private static String priorityColour(Priority p) {
        return switch (p) {
            case HIGH   -> FG_GREEN;
            case MEDIUM -> FG_YELLOW;
            case LOW    -> FG_GRAY;
        };
    }

    private static String priorityBadge(Priority p) {
        return switch (p) {
            case HIGH   -> FG_GREEN  + BOLD + "● HIGH   " + RESET;
            case MEDIUM -> FG_YELLOW + BOLD + "● MEDIUM " + RESET;
            case LOW    -> FG_GRAY   + BOLD + "● LOW    " + RESET;
        };
    }

    /**
     * Renders a compact 10-character bar showing the score visually.
     * e.g. score=70 → "███████░░░"
     */
    private static String scoreBar(int score) {
        int filled = (int) Math.round(score / 10.0);
        String bar = FG_GREEN + "█".repeat(filled)
                + FG_GRAY  + "░".repeat(10 - filled)
                + RESET;
        return bar;
    }
}
package Services;

/**
 * Utility helpers for workload decisions based on GPA.
 *
 * <p>These rules are used both by the recommendation engine and by the UI
 * to decide how many courses / credit hours to suggest per semester.
 */
public class LoadService {

    private LoadService() {} // utility class — no instances

    // ── Credit-hour caps ─────────────────────────────────────────────────────

    /**
     * Returns the maximum credit hours a student should register for in one
     * semester, based on their GPA.
     */
    public static int getMaxCreditHours(double gpa) {
        if      (gpa < 2.0) return 15;
        else if (gpa < 3.0) return 19;
        else                return 21;
    }

    /**
     * Returns a short human-readable label for the load type.
     */
    public static String getLoadType(double gpa) {
        if      (gpa < 2.0) return "Half Load";
        else if (gpa < 3.0) return "Normal Load";
        else                return "Overload";
    }

    // ── Recommendation count cap ─────────────────────────────────────────────

    /**
     * Returns the maximum number of courses to include in the recommendation
     * list, scaled to the student's academic standing.
     *
     * <ul>
     *   <li>GPA &lt; 2.0 → 4 courses  (probationary – keep it manageable)</li>
     *   <li>GPA 2.0–2.9 → 5 courses  (average load)</li>
     *   <li>GPA ≥ 3.0   → 6 courses  (strong student – offer more choice)</li>
     * </ul>
     */
    public static int getMaxRecommendations(double gpa) {
        if      (gpa < 2.0) return 4;
        else if (gpa < 3.0) return 5;
        else                return 6;
    }

    // ── Advisory messages ────────────────────────────────────────────────────

    /**
     * Returns a concise advisory sentence shown in the UI under the
     * recommendations panel.
     */
    public static String getLoadAdvice(double gpa) {
        if (gpa < 2.0) {
            return "Your GPA is below 2.0. We recommend a lighter course load "
                + "this semester to focus on quality over quantity.";
        } else if (gpa < 2.5) {
            return "Your GPA is improving. A balanced load will help you stay "
                + "on track while building confidence.";
        } else if (gpa < 3.0) {
            return "You're in good standing. A normal load keeps you on schedule "
                + "for graduation.";
        } else if (gpa < 3.5) {
            return "Strong GPA! You can handle a full or slightly heavier load "
                + "this semester.";
        } else {
            return "Excellent academic standing. You're cleared for the maximum "
                + "load — consider advanced electives to challenge yourself.";
        }
    }
}
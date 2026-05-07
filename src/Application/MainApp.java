package Application;

import Classes.Course;
import Classes.RecommendedCourse;
import Classes.Student;
import Services.*;
import UI.ConsoleUI;

import java.util.*;

/**
 * Entry point for the Smart Academic Advisor System.
 *
 * <h2>Flow</h2>
 * <ol>
 *   <li>Collect student details and completed courses from stdin.</li>
 *   <li>Load the major-specific course catalogue from JSON.</li>
 *   <li>Determine <em>available</em> courses via {@link AdvisorService_CS} /
 *       {@link AdvisorService_AI} (prerequisite checking, elective limits).</li>
 *   <li>Produce <em>recommended</em> courses via {@link RecommendationService}
 *       (GPA-aware, difficulty-balanced, unlock-value ranked).</li>
 *   <li>Render both lists via {@link ConsoleUI}.</li>
 * </ol>
 *
 * <h2>Compile / Run</h2>
 * <pre>
 *   cd src
 *   javac -cp ".;../lib/gson-2.10.1.jar" Application/MainApp.java
 *   java  -cp ".;../lib/gson-2.10.1.jar" Application.MainApp
 * </pre>
 */
public class MainApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // ── 1. Collect student profile ────────────────────────────────────────
        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter ID: ");
        String id = scanner.nextLine().trim();

        System.out.print("Enter Major (AI / CS): ");
        String major = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter Track"
                + (major.equals("CS") ? " (BigData / Media / General)" : " (AI)")
                + ": ");
        String track = scanner.nextLine().trim().toUpperCase();

        double gpa = readDouble(scanner, "Enter GPA (0.0 – 4.0): ", 0.0, 4.0);
        int semester = (int) readDouble(scanner, "Enter Semester (1 or 2): ", 1, 2);
        int year     = (int) readDouble(scanner, "Enter Year (1 – 4): ", 1, 4);

        // ── 2. Collect completed courses ──────────────────────────────────────
        Set<String> completed = new HashSet<>();
        System.out.println("Enter completed course codes one per line "
                         + "(type 'done' when finished):");
        while (true) {
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("done")) break;
            if (!line.isEmpty()) completed.add(line.toUpperCase());
        }

        // ── 3. Load course catalogue ──────────────────────────────────────────
        String filePath = resolveCataloguePath(major);
        LoadData loader = new LoadData();
        List<Course> allCourses = loader.loadCourses(filePath);

        // ── 4. Build Student object ───────────────────────────────────────────
        Student student = new Student(id, name, gpa, semester, completed, year, major, track);

        // ── 5. Get available courses (existing logic) ─────────────────────────
        List<Course> available = getAvailableCourses(major, allCourses, student);

        // ── 6. Get recommended courses (new logic) ────────────────────────────
        RecommendationService recommender = new RecommendationService(allCourses);
        int maxRecs = LoadService.getMaxRecommendations(gpa);
        List<RecommendedCourse> recommended =
                recommender.recommend(student, available, maxRecs);

        // ── 7. Render the full report ─────────────────────────────────────────
        ConsoleUI.renderReport(student, available, recommended);

        scanner.close();
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /**
     * Delegates to the correct AdvisorService based on major.
     *
     * <p>Keeping both services intact means existing behaviour is unchanged;
     * the recommendation layer only operates on top of their output.
     */
    private static List<Course> getAvailableCourses(
            String major, List<Course> allCourses, Student student) {

        return switch (major) {
            case "AI" -> new AdvisorService_AI(allCourses).getAvailableCourses(student);
            case "CS" -> new AdvisorService_CS(allCourses).getAvailableCourses(student);
            default   -> throw new IllegalStateException("Unknown major: " + major);
        };
    }

    /** Returns the JSON file path for the given major. */
    private static String resolveCataloguePath(String major) {
        return switch (major) {
            case "AI" -> "../Data/AI_courses.json";
            case "CS" -> "../Data/CS_courses.json";
            default   -> throw new IllegalStateException("Unknown major: " + major);
        };
    }

    /**
     * Reads a double within {@code [min, max]} from stdin, re-prompting on
     * invalid input.
     */
    private static double readDouble(Scanner scanner, String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = scanner.nextLine().trim();
                double val  = Double.parseDouble(line);
                if (val >= min && val <= max) return val;
                System.out.printf("  ⚠  Please enter a value between %.0f and %.0f.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  ⚠  Invalid input — please enter a number.");
            }
        }
    }
}
package Application;

import Classes.*;
import Services.*;
import java.util.*;

//how to run first you have to write inside terminal   cd Smart-Academic-Advisor-System
//then    cd src
//then compile

// javac -cp ".;../lib/gson-2.10.1.jar" Application/MainApp.java
// then run 

// java -cp ".;../lib/gson-2.10.1.jar" Application.MainApp

public class MainApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("========================================");
        System.out.println("   SMART ACADEMIC ADVISOR SYSTEM");
        System.out.println("========================================");

        // =====================================================
        // STUDENT INFO
        // =====================================================

        System.out.print("\nEnter Student Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine();

        // =====================================================
        // MAJOR
        // =====================================================

        String major;

        while (true) {

            System.out.print("\nEnter Major (CS / AI): ");

            major = scanner.nextLine().toUpperCase();

            if (major.equals("CS")
                    || major.equals("AI")) {

                break;
            }

            System.out.println("Invalid Major.");
        }

        // =====================================================
        // TRACK
        // =====================================================

        String track;

        if (major.equals("CS")) {

            while (true) {

                System.out.print(
                        "Enter Track "
                                + "(BIGDATA / MEDIA / GENERAL): "
                );

                track = scanner.nextLine().toUpperCase();

                if (track.equals("BIGDATA")
                        || track.equals("MEDIA")
                        || track.equals("GENERAL")) {

                    break;
                }

                System.out.println("Invalid Track.");
            }

        } else {

            track = "AI";

            System.out.println(
                    "\nTrack Selected Automatically: AI"
            );
        }

        // =====================================================
        // GPA
        // =====================================================

        System.out.print("\nEnter GPA (0.0 - 4.0): ");
        double gpa = scanner.nextDouble();

        // =====================================================
        // SEMESTER
        // =====================================================

        int semester;

        while (true) {

            System.out.print("Enter Semester (1 or 2): ");
            semester = scanner.nextInt();

            if (semester == 1 || semester == 2) {
                break;
            }

            System.out.println("Invalid Semester.");
        }

        // =====================================================
        // YEAR
        // =====================================================

        int year;

        while (true) {

            System.out.print("Enter Academic Year (1 - 4): ");
            year = scanner.nextInt();

            if (year >= 1 && year <= 4) {
                break;
            }

            System.out.println("Invalid Year.");
        }

        scanner.nextLine();

        // =====================================================
        // LOAD COURSES
        // =====================================================

        String filePath;

        if (major.equals("AI")) {

            filePath = "../Data/AI_courses.json";

        } else {

            filePath = "../Data/CS_courses.json";
        }

        LoadData loader = new LoadData();

        List<Course> courses =
                loader.loadCourses(filePath);

        // =====================================================
        // COMPLETED COURSES
        // =====================================================

        Set<String> completed =
                new HashSet<>();

        System.out.println("\n========================================");
        System.out.println("        COMPLETED COURSES");
        System.out.println("========================================");

        System.out.println(
                "Enter completed courses "
                        + "(type 'done' to finish)"
        );

        System.out.print("Course Code: ");
        while (true) {


            String course =
                    scanner.nextLine();

            if (course.equalsIgnoreCase("done")) {
                break;
            }

            completed.add(course.toUpperCase());
        }

        // =====================================================
        // CREATE STUDENT
        // =====================================================

        Student student = new Student(
                id,
                name,
                gpa,
                semester,
                completed,
                year,
                major,
                track
        );

        // =====================================================
        // AVAILABLE COURSES
        // =====================================================

        List<Course> available;

        if (major.equals("AI")) {

            AdvisorService_AI advisor =
                    new AdvisorService_AI(courses);

            available =
                    advisor.getAvailableCourses(student);

        } else {

            AdvisorService_CS advisor =
                    new AdvisorService_CS(courses);

            available =
                    advisor.getAvailableCourses(student);
        }

        // =====================================================
        // PRINT AVAILABLE COURSES
        // =====================================================

        System.out.println("\n========================================");
        System.out.println("         AVAILABLE COURSES");
        System.out.println("========================================");

        if (available.isEmpty()) {

            System.out.println(
                    "No available courses."
            );

        } else {

            int count = 1;

            for (Course c : available) {

                System.out.println(
                        count + ". "
                                + c.getCode()
                                + " | "
                                + c.getName()
                                + " | "
                                + c.getCreditHours()
                                + " CH"
                );

                count++;
            }
        }

        // =====================================================
        // RECOMMENDATION
        // =====================================================

        RecommendationService recommender =
                new RecommendationService(courses);

        List<RecommendationBlock> recommendations =
                recommender.getSemesterRecommendation(student);

        System.out.println("\n========================================");
        System.out.println("       RECOMMENDED SEMESTER PLAN");
        System.out.println("========================================");

        System.out.println(
                "Load Type: "
                        + LoadService.getLoadType(student.getGpa())
        );

        System.out.println(
                "Maximum Credit Hours: "
                        + LoadService.getMaxCreditHours(student.getGpa())
        );

        for (RecommendationBlock block : recommendations) {

            System.out.println("\n----------------------------------------");

            System.out.println(
                    block.getMessage()
            );

            System.out.println("----------------------------------------");

            int count = 1;

            for (Course c : block.getCourses()) {

                System.out.println(
                        count + ". "
                                + c.getCode()
                                + " | "
                                + c.getName()
                                + " | "
                                + c.getCreditHours()
                                + " CH"
                );

                count++;
            }
        }

        scanner.close();
    }
}
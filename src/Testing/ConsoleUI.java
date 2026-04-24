package Testing;

import Classes.*;
import Services.LoadService;

import java.util.*;

public class ConsoleUI {

    private Scanner sc = new Scanner(System.in);

    public void start() {

        CourseManager manager = new CourseManager();
        manager.loadCourses();

        // 1. Receive input
        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        System.out.print("Enter ID: ");
        String id = sc.nextLine();

        System.out.print("Enter GPA: ");
        double gpa = Double.parseDouble(sc.nextLine());

        System.out.print("Enter current semester: ");
        int semester = Integer.parseInt(sc.nextLine());

        System.out.print("Enter current year: ");
        int year = Integer.parseInt(sc.nextLine());

        System.out.print("Enter completed courses (comma separated): ");
        String input = sc.nextLine();

        Set<String> completed = new HashSet<>(Arrays.asList(input.split(",")));

        Student student = new Student(id, name, gpa, semester, completed, year);

        System.out.println("\nLoad Type: " + LoadService.getLoadType(gpa));

        System.out.println("\nAvailable Courses:");
        for (Course c : manager.getAvailableCourses(student.getCompletedCourses())) {
            System.out.println(c.getCode());
        }

        System.out.println("\nRecommended Courses:");
        for (Course c : manager.getRecommendedCourses(student)) {
            System.out.println(c.getCode());
        }
    }
}

package Testing;

import Classes.Student;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        new ConsoleUI().start();

        CourseManager manager = new CourseManager();
        manager.loadCourses();

        Set<String> completed = new HashSet<>();
        completed.add("CSCI101");

        Student student = new Student(
                "241001750",
                "Tarek",
                2.8,
                2,
                completed,
                1,
                "cs",
                "general");

        System.out.println("\nLoad Type: " + Services.LoadService.getLoadType(student.getGpa()));

        System.out.println("\nAvailable Courses:");
        for (var c : manager.getAvailableCourses(student.getCompletedCourses())) {
            System.out.println(c.getCode());
        }

        System.out.println("\nRecommended Courses:");
        for (var c : manager.getRecommendedCourses(student)) {
            System.out.println(c.getCode());
        }
    }
}

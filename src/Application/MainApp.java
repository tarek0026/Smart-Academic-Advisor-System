package Application;

import Classes.*;
import Services.*;
import java.util.*;

// javac -cp ".;../lib/gson-2.10.1.jar" Application/MainApp.java
// java -cp ".;../lib/gson-2.10.1.jar" Application.MainApp

public class MainApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // 🔹 2. Take user input

        System.out.print("Enter Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter major: ");
        String major = scanner.nextLine();

        System.out.print("Enter track: ");
        String track = scanner.nextLine();

        System.out.print("Enter GPA: ");
        double gpa = scanner.nextDouble();

        System.out.print("Enter Semester (1 or 2): ");
        int semester = scanner.nextInt();

        System.out.print("Enter Year (1-4): ");
        int year = scanner.nextInt();

        scanner.nextLine(); // fix buffer
        String filePath;

        if (major.equals("AI")) {
            filePath = "../Data/AI_courses.json";
        } else if (major.equals("CS")) {
            filePath = "../Data/CS_courses.json";
        } else {
            throw new IllegalStateException("Unknown major: " + major);
        }

        // Load
        LoadData loader = new LoadData();
        List<Course> courses = loader.loadCourses(filePath);

        // 🔹 completed courses
        Set<String> completed = new HashSet<>();

        System.out.println("Enter completed courses (type 'done' to finish):");

        while (true) {
            String course = scanner.nextLine();

            if (course.equalsIgnoreCase("done"))
                break;

            completed.add(course);
        }

        // create student
        Student student = new Student(id, name, gpa, semester, completed, year, major, track);
        List<Course> available;

        // 🔹 3. Run AdvisorService
        if (major.equals("AI")) {
            AdvisorService_AI advisor = new AdvisorService_AI(courses);
            available = advisor.getAvailableCourses(student);

        } else if (major.equals("CS")) {
            AdvisorService_CS advisor = new AdvisorService_CS(courses);
            available = advisor.getAvailableCourses(student);

        } else {
            throw new IllegalStateException("Unknown major: " + major);
        }

        // 🔹 4. Print result
        System.out.println("\nAvailable Courses:");

        if (available.isEmpty()) {
            System.out.println("No available courses.");
        } else {
            for (Course c : available) {
                System.out.println(c.getCode() + " - " + c.getName());
            }
        }

        scanner.close();
    }
}

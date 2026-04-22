package Testing;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        CourseManager manager = new CourseManager();

        manager.loadCourses();

        System.out.println("\nAll Courses:");
        manager.printCourses();

        Set<String> completed = new HashSet<>();
        completed.add("MATH100");
        completed.add("CSCI102");

        System.out.println("\nCan take CSCI112?");
        System.out.println(manager.canTake("CSCI112", completed));


        completed.add("CSCI101");
        System.out.println("\nCan take CSCI112?");
        System.out.println(manager.canTake("CSCI112", completed));
    }
}
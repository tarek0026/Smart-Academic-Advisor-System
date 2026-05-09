package Testing;

import Classes.Student;
import java.util.Set;

public class TestStudent {
    public static void main(String[] args) {

        // create student
        Student s = new Student("241001750","Tarek",4,1,Set.of(" cs101 ", "cS102"),2, "cs", "general");

        // print student
        System.out.println(s);

        // test add
        s.addCompletedCourse("cs103");
        System.out.println("After adding CS103: " + s.getCompletedCourses());

        // test remove
        s.removeCompletedCourse("CS101");
        System.out.println("After removing CS101: " + s.getCompletedCourses());

        // test check
        System.out.println("Has CS102? " + s.hasCompleted("cs102"));

        // test count
        System.out.println("Total courses: " + s.getCompletedCoursesCount());
    }
}

package Services;
import Classes.Course;
import Classes.Student;
import java.util.ArrayList;
import java.util.List;

public class AdvisorService {

    private List<Course> allCourses;

    public AdvisorService(List<Course> courses) {
        this.allCourses = courses;
    }

    // Main function
    public List<Course> getAvailableCourses(Student student) {
        List<Course> available = new ArrayList<>();

        for (Course course : allCourses) {

            // skip if student already completed it
            if (student.hasCompleted(course.getCode())) {
                continue;
            }

            // check prerequisites
            boolean canTake = true;

            for (String pre : course.getPrerequisites()) {
                if (!student.hasCompleted(pre)) {
                    canTake = false;
                    break;
                }
            }

            // if all prerequisites satisfied then add
            if (canTake) {
                available.add(course);
            }
        }

        return available;
    }
}

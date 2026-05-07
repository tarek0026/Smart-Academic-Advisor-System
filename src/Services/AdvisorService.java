package Services;

import java.util.List;

import Classes.Course;
import Classes.Student;

public interface AdvisorService 
{
    List<String> recommendCourses(Student student);
    List<Course> getAvailableCourses(Student student);

}

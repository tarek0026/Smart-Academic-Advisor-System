package Services;

import Classes.Course;
import Classes.Student;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdvisorService_AI {
    
    private List<Course> allCourses;
    private Map<String, Integer> courseMinCredits = new HashMap<>();
    private Map<String, Integer> categoryLimits = new HashMap<>();

    public AdvisorService_AI(List<Course> courses) {
        this.allCourses = courses;
        courseMinCredits.put("AIS390", 60);// intern
        courseMinCredits.put("AIS490", 60);// intern
        courseMinCredits.put("AIS495", 88);// grad1
        categoryLimits.put("CS_ELECTIVES", 2);
        categoryLimits.put("AI_ELECTIVES", 6);
    }

    // handle electives count number of each category
    private Map<String, Integer> getCompletedByCategory(Student student) {
        Map<String, Integer> count = new HashMap<>();

        for (Course c : allCourses) {
            if (student.hasCompleted(c.getCode())) {
                String cat = c.getCategory();
                count.put(cat, count.getOrDefault(cat, 0) + 1);
            }
        }

        return count;
    }

    // number of credits hour finished
    private int getCompletedCredits(Student student) {
        int total = 0;

        for (Course c : allCourses) {
            if (student.hasCompleted(c.getCode())) {
                total += c.getCreditHours();
            }
        }

        return total;
    }

    // Main function
    public List<Course> getAvailableCourses(Student student) {
        List<Course> available = new ArrayList<>();
        Map<String, Integer> completedByCat = getCompletedByCategory(student);

        for (Course course : allCourses) {

            // skip if student already completed it
            if (student.hasCompleted(course.getCode())) {
                continue;
            }
            // handele problem calc1,pre calc
            if (course.getCode().equals("MATH100") && student.hasCompleted("MATH111i")) {
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
            // handle problem for internships,gradproject
            Integer minCredits = courseMinCredits.get(course.getCode());

            if (minCredits != null && getCompletedCredits(student) < minCredits) {
                continue;
            }
            // handle problem for electives
            Integer limit = categoryLimits.get(course.getCategory());

            if (limit != null && completedByCat.getOrDefault(course.getCategory(), 0) >= limit) {
                continue;
            }

            // if all prerequisites satisfied then add
            if (canTake) {
                available.add(course);
            }
        }

        return available;
    }
}
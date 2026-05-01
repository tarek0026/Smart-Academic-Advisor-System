package Services;

import Classes.Course;
import Classes.Student;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdvisorService_CS implements AdvisorService {
    
    private List<Course> allCourses;
    private Map<String, Integer> courseMinCredits = new HashMap<>();
    private Map<String, Integer> categoryLimits = new HashMap<>();
    //i have to handle big data,media

    public AdvisorService_CS(List<Course> courses) {
        this.allCourses = courses;
        courseMinCredits.put("COMM401", 60);// intern
        courseMinCredits.put("CSCI490", 60);// intern
        courseMinCredits.put("CSCI495", 95);// grad1
        categoryLimits.put("ARTS", 1);
    }

    

    private boolean isMustCourse(Course course, Student student) {

    if (course.getUnlocks().size() >= 2) {
        return true;
    }

    for (String unlock : course.getUnlocks()) {
        if (!student.hasCompleted(unlock)) {
            return true;
        }
    }

    return false;
}

    @Override
    public List<String> recommendCourses(Student student)
    {

    List<Course> available = getAvailableCourses(student);

    List<String> must = new ArrayList<>();
    List<String> advised = new ArrayList<>();
    List<String> journey = new ArrayList<>();

    Map<String, Integer> completedByCat = getCompletedByCategory(student);

    for (Course course : available) {

        if (isMustCourse(course, student)) {
            must.add(course.getCode());
            continue;
        }

        if (course.isCore()) {
            advised.add(course.getCode());
            continue;
        }

        if (course.isElective() || course.isEnglish()) {

            int taken = completedByCat.getOrDefault(course.getCategory(), 0);
            int limit = categoryLimits.getOrDefault(course.getCategory(), Integer.MAX_VALUE);

            if (taken < limit) {
                journey.add(course.getCode());
            }
        }
    }

    return mergeWithLimits(must, advised, journey);
}

    private List<String> mergeWithLimits(List<String> must, List<String> advised, List<String> journey) {

    List<String> result = new ArrayList<>();

    result.addAll(must.stream().limit(3).toList());
    result.addAll(advised.stream().limit(3).toList());
    result.addAll(journey.stream().limit(2).toList());

    return result;
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
    @Override
    public List<Course> getAvailableCourses(Student student) {
        List<Course> available = new ArrayList<>();
        Map<String, Integer> completedByCat = getCompletedByCategory(student);

        for (Course course : allCourses) {

            // skip if student already completed it
            if (student.hasCompleted(course.getCode())) {
                continue;
            }
            // handele problem calc1,pre calc
            if (course.getCode().equals("MATH100") && student.hasCompleted("MATH111")) {
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

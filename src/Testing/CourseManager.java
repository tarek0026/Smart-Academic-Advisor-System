package Testing;

import Classes.Course;
import Classes.Student;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CourseManager {

    private Map<String, Course> courseMap = new HashMap<>();

    private final Gson gson = new Gson();

    // Load courses from JSON file into courseMap
    public void loadCourses() {
        try {
            List<Course> courses = readCourses(Path.of("src", "Data", "courses.json"));

            for (Course c : courses) {
                courseMap.put(c.getCode(), c);
            }

            System.out.println("Loaded " + courseMap.size() + " courses.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Gson Parsing
    private List<Course> readCourses(Path filePath) throws IOException {

        String json = Files.readString(filePath);

        Type listType = new TypeToken<List<Course>>() {}.getType();

        List<Course> courses = gson.fromJson(json, listType);

        if (courses == null) {
            return new ArrayList<>();
        }

        return courses;
    }

    // get all available courses into list of courses
    public List<Course> getAvailableCourses(Set<String> completed) {

        List<Course> available = new ArrayList<>();

        for (Course c : courseMap.values()) {

            boolean taken = completed.contains(c.getCode());
            // boolean prereqOk = completed.containsAll(c.getPrerequisites());
            Set<String> prereq = c.getPrerequisites();
            boolean prereqOk = (prereq == null || completed.containsAll(prereq));

            if (!taken && prereqOk) {
                available.add(c);
            }
        }

        return available;
    }

    // Find recommended courses based on current semester and GPA
    public List<Course> getRecommendedCourses(Student student) {

        List<Course> available = getAvailableCourses(student.getCompletedCourses());

        List<Course> delayed = new ArrayList<>();
        List<Course> current = new ArrayList<>();
        List<Course> others = new ArrayList<>();

        for (Course c : available) {

            if (c.getSemester() < student.getCurrentSemester()) {
                delayed.add(c);
            } else if (c.getSemester() == student.getCurrentSemester()) {
                current.add(c);
            } else {
                others.add(c);
            }
        }

        List<Course> result = new ArrayList<>();
        result.addAll(delayed);
        result.addAll(current);
        result.addAll(others);

        int max = Services.LoadService.getMaxCourses(student.getGpa());

        if (result.isEmpty()) return new ArrayList<>();

        return result.subList(0, Math.min(max, result.size()));
    }

    // Print all courses
    public void printCourses() {
        for (Course c : courseMap.values()) {
            System.out.println(c.getCode() + ": " + c.getName());
        }
    }

    // Check if a student can take a course based on completed courses
    public boolean canTake(String code, Set<String> completed) {

        Course c = courseMap.get(code);

        if (c == null || c.getPrerequisites() == null)
            return false;

        return completed.containsAll(c.getPrerequisites());
    }
}
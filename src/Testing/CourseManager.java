package Testing;

import Classes.Course;
import Classes.Student;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseManager {

    private Map<String, Course> courseMap = new HashMap<>();

    // Load courses from JSON file into courseMap
    public void loadCourses() {
        try {
            List<Course> courses = readCourses(Path.of("Data", "AI_courses.json"));

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
        List<Course> courses = new ArrayList<>();

        Pattern objectPattern = Pattern.compile("\\{(.*?)\\}", Pattern.DOTALL);
        Matcher objectMatcher = objectPattern.matcher(json);

        while (objectMatcher.find()) {
            String objectBody = objectMatcher.group(1);

            try {
                String code = extractString(objectBody, "code");
                String name = extractString(objectBody, "name");
                String category = extractString(objectBody, "category");
                int year = extractInt(objectBody, "year");
                int semester = extractInt(objectBody, "semester");
                int creditHours = extractInt(objectBody, "creditHours");
                Set<String> prerequisites = extractStringSet(objectBody, "prerequisites");

                courses.add(new Course(code, name, prerequisites, year, semester, creditHours, category));
            } catch (IllegalArgumentException e) {
                String code = safeExtractCode(objectBody);
                System.out.println("Skipping invalid course record" +
                        (code == null ? "" : " (" + code + ")") +
                        ": " + e.getMessage());
            }
        }

        return courses;
    }

    private String extractString(String objectBody, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(fieldName) + "\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(objectBody);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Missing field: " + fieldName);
        }

        return matcher.group(1);
    }

    private int extractInt(String objectBody, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(fieldName) + "\"\\s*:\\s*(-?\\d+)");
        Matcher matcher = pattern.matcher(objectBody);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Missing field: " + fieldName);
        }

        return Integer.parseInt(matcher.group(1));
    }

    private Set<String> extractStringSet(String objectBody, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(fieldName) + "\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(objectBody);

        if (!matcher.find()) {
            return new HashSet<>();
        }

        String arrayBody = matcher.group(1).trim();
        Set<String> values = new HashSet<>();

        if (arrayBody.isEmpty()) {
            return values;
        }

        Pattern itemPattern = Pattern.compile("\"(.*?)\"");
        Matcher itemMatcher = itemPattern.matcher(arrayBody);

        while (itemMatcher.find()) {
            values.add(itemMatcher.group(1));
        }

        return values;
    }

    private String safeExtractCode(String objectBody) {
        try {
            return extractString(objectBody, "code");
        } catch (IllegalArgumentException e) {
            return null;
        }
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

        if (c == null)
            return false;

        return completed.containsAll(c.getPrerequisites());
    }
}

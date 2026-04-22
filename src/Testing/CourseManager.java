package Testing;

import Classes.Course;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseManager {

    private Map<String, Course> courseMap = new HashMap<>();

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

    private List<Course> readCourses(Path filePath) throws IOException {
        String json = Files.readString(filePath);
        List<Course> courses = new ArrayList<>();

        Matcher objectMatcher = Pattern.compile("\\{([^{}]*)\\}", Pattern.DOTALL).matcher(json);
        while (objectMatcher.find()) {
            String object = objectMatcher.group(1);

            String code = getStringValue(object, "code");
            String name = getStringValue(object, "name");
            Set<String> prerequisites = getStringSet(object, "prerequisites");
            int semester = getIntValue(object, "semester");
            int creditHours = getIntValue(object, "creditHours");

            if (code != null) {
                courses.add(new Course(code, name, prerequisites, semester, creditHours));
            }
        }

        return courses;
    }

    private String getStringValue(String object, String key) {
        Matcher matcher = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"").matcher(object);
        return matcher.find() ? matcher.group(1) : null;
    }

    private int getIntValue(String object, String key) {
        Matcher matcher = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\d+)").matcher(object);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    private Set<String> getStringSet(String object, String key) {
        Matcher matcher = Pattern.compile("\"" + key + "\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL).matcher(object);
        Set<String> values = new HashSet<>();

        if (matcher.find()) {
            Matcher valueMatcher = Pattern.compile("\"([^\"]*)\"").matcher(matcher.group(1));
            while (valueMatcher.find()) {
                values.add(valueMatcher.group(1));
            }
        }

        return values;
    }

    public void printCourses() {
        for (Course c : courseMap.values()) {
            System.out.println(c.getCode() + ": " + c.getName());
        }
    }

    public boolean canTake(String code, Set<String> completed) {
        Course c = courseMap.get(code);

        if (c == null) return false;

        for (String pre : c.getPrerequisites()) {
            if (!completed.contains(pre)) {
                return false;
            }
        }
        return true;
    }
}

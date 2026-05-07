package Services;

import Classes.Course;

import java.util.ArrayList;
import java.util.List;

public class RecommendationBlock {

    private String type;
    private String message;

    private List<Course> courses =
            new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}
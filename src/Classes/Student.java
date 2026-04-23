package Classes;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Student {

    private String id;
    private String name;
    private double gpa;
    private int currentSemester;
    private Set<String> completedCourses;

    public Student(String id, String name, double gpa, int currentSemester, Set<String> completedCourses) {
        validateId(id); // to make sure that id isnot null then initializate it else if null throw
                        // exception

        validateName(name); // to make sure that name isnot null then initializate it else if null throw
                            // exception

        setGpa(gpa); // setGpa() have validation to make sure the gpa range from 0 to 4 else throw
                     // exception
        validateSemester(currentSemester);

        this.completedCourses = new HashSet<>(); // new set

        if (completedCourses != null) {
            completedCourses.forEach(this::addCompletedCourse);
        }
    }

    // Getters

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getGpa() {
        return gpa;
    }

    // Return the completed courses as read-only set (user can view but cannot
    // modify it)
    public Set<String> getCompletedCourses() {
        return Collections.unmodifiableSet(completedCourses);
    }

    // trim to ignore spaces in string
    private void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty");
        } else {
            this.id = id.trim();
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty");
        } else {
            this.name = name.trim();
        }
    }

    public void setGpa(double gpa) {
        if (gpa < 0.0 || gpa > 4.0) {
            throw new IllegalArgumentException("GPA must be between 0.0 and 4.0");
        } else {
            this.gpa = gpa;
        }

    }

    private void validateSemester(int semester) {
        if (semester < 1 || semester > 8) {
            throw new IllegalArgumentException("Semester must be between 1 and 8");
        } else {
            this.currentSemester = semester;
        }
    }

    // Methods

    public void addCompletedCourse(String code) {
        String normalized = normalize(code);
        if (normalized != null) {
            completedCourses.add(normalized);
        }
    }

    public void addCompletedCourses(Set<String> codes) {
        if (codes == null)
            return;
        codes.forEach(this::addCompletedCourse);
    }

    public boolean hasCompleted(String code) {
        String normalized = normalize(code);
        return normalized != null && completedCourses.contains(normalized);// is this subject from the subjects user
                                                                           // took
    }

    public void removeCompletedCourse(String code) {
        String normalized = normalize(code);
        if (normalized != null) {
            completedCourses.remove(normalized);
        }
    }

    public int getCompletedCoursesCount() {
        return completedCourses.size();
    }

    public int getCurrentSemester() {
        return currentSemester;
    }

    private String normalize(String code) {
        return code.trim().toUpperCase();
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", gpa=" + gpa +
                ", currentSemester=" + currentSemester +
                ", completedCourses=" + completedCourses +
                '}';
    }

}
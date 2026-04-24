package Classes;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Student {

    private String id;
    private String name;
    private double gpa;
    private int currentSemester;
    private int year;
    private Set<String> completedCourses;

    public Student(String id, String name, double gpa, int currentSemester, Set<String> completedCourses, int year) {
        setId(id); // to make sure that id isnot null then initializate it else if null throw
                   // exception
        setName(name);
        setGpa(gpa);
        setSemester(currentSemester);
        setYear(year);
        // Create a new independent Set for the student
        // This prevents linking the internal data with the external input
        // (Encapsulation)
        this.completedCourses = new HashSet<>(); // new set
        // Add each course using addCompletedCourse()
        // instead of direct assignment to:
        // 1) Apply validation (avoid null or invalid values)
        // 2) Normalize course codes (e.g., cs101 -> CS101)
        // 3) Avoid external modification affecting internal data (Defensive Copy)
        if (completedCourses != null) {
            completedCourses.forEach(this::addCompletedCourse);
        }
    }

    // trim to ignore spaces in string
    private void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty");
        } else {
            this.id = id.trim();
        }
    }

    private void setName(String name) {
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

    private void setYear(int year) {
        if (year < 1 || year > 4) {
            throw new IllegalArgumentException("Year must be between 1 and 4");
        }
        this.year = year;
    }

    private void setSemester(int semester) {
        if (semester < 1 || semester > 2) {
            throw new IllegalArgumentException("Semester must be  1 or 2");
        } else {
            this.currentSemester = semester;
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

    public int getCurrentSemester() {
        return currentSemester;
    }

    public int getYear() {
        return currentSemester;
    }

    // Return the completed courses as read-only set (user can view but cannot
    // modify it)
    public Set<String> getCompletedCourses() {
        return Collections.unmodifiableSet(completedCourses);
    }

    public int getCompletedCoursesCount() {
        return completedCourses.size();
    }

    // Methods

    private String normalize(String code) {
        return code.trim().toUpperCase();
    }

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

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", gpa=" + gpa +
                ", year=" + year + 
                ", currentSemester=" + currentSemester +
                ", completedCourses=" + completedCourses +
                '}';
    }
}
package Classes;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Course {

    private String code;
    private String name;
    private Set<String> prerequisites;
    private int year;
    private int semester;
    private String category;
    private int creditHours;

    public Course(String code, String name, Set<String> prerequisites,
            int year, int semester, int creditHours, String category) {

        setCode(code);
        setName(name);
        setYear(year);
        setSemester(semester);
        setCreditHours(creditHours);
        setCategory(category);
        setPrerequisites(prerequisites);
    }

    // 🔹 Setters (validation + assignment)

    private void setCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty");
        }
        this.code = normalize(code);
    }

    private void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty");
        }
        this.name = name.trim();
    }

    private void setYear(int year) {
        if (year < 1 || year > 4) {
            throw new IllegalArgumentException("Year must be between 1 and 4");
        }
        this.year = year;
    }

    private void setSemester(int semester) {
        if (semester < 1 || semester > 2) {
            throw new IllegalArgumentException("Semester must be 1 or 2");
        }
        this.semester = semester;
    }

    private void setCreditHours(int creditHours) {
        if (creditHours < 1 || creditHours > 4) {
            throw new IllegalArgumentException("Credit hours must be between 1 and 4");
        }
        this.creditHours = creditHours;
    }

    private void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
        this.category = category.trim();
    }

    private void setPrerequisites(Set<String> prerequisites) {
        this.prerequisites = new HashSet<>();

        if (prerequisites == null)
            return;

        for (String course : prerequisites) {
            String normalized = normalize(course);

            if (normalized == null) {
                throw new IllegalArgumentException("Invalid prerequisite course");
            }

            if (normalized.equals(this.code)) {// course cannot be prerequest for itself
                throw new IllegalArgumentException("Course cannot be prerequisite of itself");
            }

            this.prerequisites.add(normalized);
        }
    }

    private String normalize(String code) {
        if (code == null)
            return null;
        String cleaned = code.trim().toUpperCase();
        return cleaned.isEmpty() ? null : cleaned;
    }

    // 🔹 Getters
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public int getSemester() {
        return semester;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public String getCategory() {
        return category;
    }

    public Set<String> getPrerequisites() {
        return Collections.unmodifiableSet(prerequisites);
    }


    @Override
    public String toString() {
        return "Course{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", year=" + year +
                ", semester=" + semester +
                ", creditHours=" + creditHours +
                ", category='" + category + '\'' +
                ", prerequisites=" + prerequisites +
                '}';
    }

}

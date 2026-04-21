import java.util.List;

public class Student {
    private String id;
    private String name;
    private double gpa;
    private int currentSemester;
    private List<String> completedCourses;

    public Student(String id, String name, double gpa, int currentSemester, List<String> completedCourses) {
        this.id = id;
        this.name = name;
        this.gpa = gpa;
        this.currentSemester = currentSemester;
        this.completedCourses = completedCourses;
    }

    public String getID() { return id; }
    public String getNameString() { return name; }
    public double getGpa() { return gpa; }
    public int getCurrentSemester() { return currentSemester; }
    public List<String> getCompletedCourses() { return completedCourses; }
}

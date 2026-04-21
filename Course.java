import java.util.List;

public class Course {
    private String code;
    private String name;
    private List<String> prerequisites;
    private int semester;
    private int creditHours;

    public Course(String code, String name, List<String> prerequisites, int semester, int creditHours) {
        this.code = code;
        this.name = name;
        this.prerequisites = prerequisites;
        this.semester = semester;
        this.creditHours = creditHours;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public List<String> getPrerequisites() { return prerequisites; }
    public int getSemester() { return semester; }
    public int getCreditHours() { return creditHours; }
}

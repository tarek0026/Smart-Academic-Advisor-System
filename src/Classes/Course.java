package Classes;
import java.util.Set;
public class Course {
    private String code;
    private String name;
    private Set<String> prerequisites;
    private int semester;
    private int creditHours;

    public Course(String code, String name, Set<String> prerequisites, int semester, int creditHours) {
        this.code = code;
        this.name = name;
        this.prerequisites = prerequisites;
        this.semester = semester;
        this.creditHours = creditHours;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public Set<String> getPrerequisites() { return prerequisites; }
    public int getSemester() { return semester; }
    public int getCreditHours() { return creditHours; }
}
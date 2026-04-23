package Classes;
import java.util.HashSet;
import java.util.Set;
public class Course {
    private String code;
    private String name;
    private Set<String> prerequisites = new HashSet<>();
    private int year;
    private int semester;
    private String category;
    private int creditHours;

    public Course(String code, String name, Set<String> prerequisites, int year, int semester, int creditHours, String category) {
        this.code = code;
        this.name = name;
        this.prerequisites = prerequisites;
        this.year = year;
        this.semester = semester;
        this.creditHours = creditHours;
        this.category = category;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public int getSemester() { return semester; }
    public int getCreditHours() { return creditHours; }
    public int getYear() { return year; }
    public String getCategory() { return category; }
    public Set<String> getPrerequisites() 
    { return prerequisites; }


    
}
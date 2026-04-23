package Services;

public class LoadService {

    public static int getMaxCourses(double gpa) {
        if (gpa < 2.0) { return 3; }
        else if (gpa < 3.2) { return 5; }
        else { return 6; } 
    }

    public static String getLoadType(double gpa) {
        if (gpa < 2.0) { return "Half Load"; }
        else if (gpa < 3.2) { return "Normal Load"; }
        else { return "Overload"; }
    }
}
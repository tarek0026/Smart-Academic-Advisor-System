package Services;

public class LoadService {

    public static int getMaxCreditHours(double gpa) {
        if (gpa < 2.0) {
            return 12;
        } else if (gpa <= 3.0) {
            return 19;
        } else {
            return 21;
        }
    }

    public static String getLoadType(double gpa) {
        if (gpa < 2.0) {
            return "Half Load";
        } else if (gpa <= 3.0) {
            return "Normal Load";
        } else {
            return "Overload";
        }
    }
}
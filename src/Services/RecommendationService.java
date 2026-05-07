package Services;

import Classes.Course;
import Classes.Student;
import java.util.ArrayList;
import java.util.List;

public class RecommendationService {

    private List<Course> allCourses;

    public RecommendationService(List<Course> allCourses) {

        this.allCourses = allCourses;
    }

    // =====================================================
    // MAIN FUNCTION
    // =====================================================

    public List<RecommendationBlock> getSemesterRecommendation(Student student) {

        List<Course> availableCourses = getAvailableCourses(student);

        List<RecommendationBlock> result = new ArrayList<>();

        List<Course> fixedCourses = new ArrayList<>();

        List<Course> bigDataElectives = new ArrayList<>();

        List<Course> mediaElectives = new ArrayList<>();

        List<Course> aiElectives = new ArrayList<>();

        List<Course> csElectives = new ArrayList<>();

        List<Course> generalPool = new ArrayList<>();

        // =====================================================
        // CLASSIFY COURSES
        // =====================================================

        for (Course course : availableCourses) {

            String category = course.getCategory();

            // ================= BIG DATA ELECTIVES =================

            if (category.equals("Big_Data_Electives")) {

                if (student.getTrack()
                        .equals("BIGDATA")) {

                    bigDataElectives.add(course);
                    continue;
                }

                if (student.getTrack()
                        .equals("GENERAL")) {

                    generalPool.add(course);
                    continue;
                }
            }

            // ================= MEDIA ELECTIVES =================

            if (category.equals("Media_Electives")) {

                if (student.getTrack()
                        .equals("MEDIA")) {

                    mediaElectives.add(course);
                    continue;
                }

                if (student.getTrack()
                        .equals("GENERAL")) {

                    generalPool.add(course);
                    continue;
                }
            }

            // ================= GENERAL TRACK =================

            if (student.getTrack()
                    .equals("GENERAL")) {

                if (category.equals("Big_Data")
                        || category.equals("Media")) {

                    generalPool.add(course);
                    continue;
                }
            }

            // ================= AI ELECTIVES =================

            if (category.equals("AI_ELECTIVES")) {

                aiElectives.add(course);
                continue;
            }

            // ================= CS ELECTIVES =================

            if (category.equals("CS_ELECTIVES")) {

                csElectives.add(course);
                continue;
            }

            // ================= FIXED COURSES =================

            fixedCourses.add(course);
        }

        // =====================================================
        // SORT FIXED COURSES
        // =====================================================

        fixedCourses.sort((c1, c2) -> calculateScore(student, c2)
                - calculateScore(student, c1));

        fixedCourses.sort((c1, c2) -> calculateScore(student, c2)
                - calculateScore(student, c1));

        // =====================================================
        // APPLY GPA LOAD LIMIT
        // =====================================================

        int maxHours = LoadService.getMaxCreditHours(student.getGpa());

        List<Course> semesterPlan = new ArrayList<>();

        int totalHours = 0;

        for (Course c : fixedCourses) {

            if (totalHours + c.getCreditHours() <= maxHours) {

                semesterPlan.add(c);

                totalHours += c.getCreditHours();
            }
        }

        fixedCourses = semesterPlan;

        // =====================================================
        // REMAINING HOURS
        // =====================================================

        int usedHours = 0;

        for (Course c : fixedCourses) {

            usedHours += c.getCreditHours();
        }

        int remainingHours = maxHours - usedHours;

        // =====================================================
        // FIXED BLOCK
        // =====================================================

        if (!fixedCourses.isEmpty()) {

            RecommendationBlock block = new RecommendationBlock();

            block.setType("FIXED");

            block.setMessage(
                    "Recommended Courses");

            block.setCourses(fixedCourses);

            result.add(block);
        }

        // =====================================================
        // BIG DATA TRACK
        // =====================================================

        if (student.getTrack()
                .equals("BIGDATA")
                && !bigDataElectives.isEmpty()) {

            RecommendationBlock block = new RecommendationBlock();

            block.setType("CHOICE");

            block.setMessage(
                    "Choose 1 Big Data Elective");

            List<Course> filteredElectives = new ArrayList<>();

            for (Course c : bigDataElectives) {

                if (c.getCreditHours() <= remainingHours) {

                    filteredElectives.add(c);
                }
            }

            block.setCourses(filteredElectives);

            if (!filteredElectives.isEmpty()) {
                result.add(block);
            }
        }

        // =====================================================
        // MEDIA TRACK
        // =====================================================
        if (student.getTrack()
                .equals("MEDIA")
                && !mediaElectives.isEmpty()) {

            RecommendationBlock block = new RecommendationBlock();

            block.setType("CHOICE");

            block.setMessage(
                    "Choose 1 Media Elective");

            List<Course> filteredElectives = new ArrayList<>();

            for (Course c : mediaElectives) {

                if (c.getCreditHours() <= remainingHours) {

                    filteredElectives.add(c);
                }
            }

            block.setCourses(filteredElectives);

            if (!filteredElectives.isEmpty()) {

                result.add(block);
            }
        }

        // =====================================================
        // GENERAL TRACK
        // =====================================================
        if (student.getTrack()
                .equals("GENERAL")
                && !generalPool.isEmpty()) {

            RecommendationBlock block = new RecommendationBlock();

            block.setType("CHOICE");

            block.setMessage(
                    "Choose 3 Courses From General Pool");

            List<Course> filteredGeneral = new ArrayList<>();

            for (Course c : generalPool) {

                if (c.getCreditHours() <= remainingHours) {

                    filteredGeneral.add(c);
                }
            }

            block.setCourses(filteredGeneral);

            if (!filteredGeneral.isEmpty()) {

                result.add(block);
            }
        }

        // =====================================================
        // AI MAJOR ELECTIVES
        // =====================================================
        if (student.getMajor()
                .equals("AI")
                && !aiElectives.isEmpty()) {

            RecommendationBlock block = new RecommendationBlock();

            block.setType("CHOICE");

            block.setMessage(
                    "Choose AI Electives");

            List<Course> filteredElectives = new ArrayList<>();

            for (Course c : aiElectives) {

                if (c.getCreditHours() <= remainingHours) {

                    filteredElectives.add(c);
                }
            }

            block.setCourses(filteredElectives);

            if (!filteredElectives.isEmpty()) {

                result.add(block);
            }
        }
        if (student.getMajor()
                .equals("AI")
                && !csElectives.isEmpty()) {

            RecommendationBlock block = new RecommendationBlock();

            block.setType("CHOICE");

            block.setMessage(
                    "Choose CS Electives");

            List<Course> filteredElectives = new ArrayList<>();

            for (Course c : csElectives) {

                if (c.getCreditHours() <= remainingHours) {

                    filteredElectives.add(c);
                }
            }

            block.setCourses(filteredElectives);

            if (!filteredElectives.isEmpty()) {

                result.add(block);
            }
        }

        return result;
    }

    private List<Course> getAvailableCourses(Student student) {
        if (student.getMajor().equals("AI")) {
            return new AdvisorService_AI(allCourses).getAvailableCourses(student);
        }

        return new AdvisorService_CS(allCourses).getAvailableCourses(student);
    }

    // =====================================================
    // SMART SCORE
    // =====================================================

    private int calculateScore(Student student, Course course) {

        int score = 0;

        int studentLevel = student.getYear() * 10
                + student.getCurrentSemester();

        int courseLevel = course.getYear() * 10
                + course.getSemester();

        // previous semesters
        if (courseLevel < studentLevel) {

            score += 100;
        }

        // current semester
        else if (courseLevel == studentLevel) {

            score += 70;
        }

        // future semesters
        else {

            score += 20;
        }

        // dependency importance
        int dependencyCount = 0;

        for (Course c : allCourses) {

            if (c.getPrerequisites()
                    .contains(course.getCode())) {

                dependencyCount++;
            }
        }

        score += dependencyCount * 10;

        return score;
    }
}




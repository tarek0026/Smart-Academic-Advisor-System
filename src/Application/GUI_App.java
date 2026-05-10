package Application;

import Classes.Course;
import Classes.Student;
import Services.*;
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

//how ton run
// javac -encoding UTF-8 --module-path "D:/javafx-sdk-17.0.19/lib" --add-modules javafx.controls,javafx.fxml -cp 
// ".;lib/gson-2.10.1.jar" src/Application/*.java src/Classes/*.java src/Services/*.java
// 

// java --module-path "D:/javafx-sdk-17.0.19/lib" --add-modules javafx.controls,javafx.fxml -cp
//  ".;lib/gson-2.10.1.jar;src" Application.GUI_App


public class GUI_App extends Application {

    // ─── THEME FLAG ───────────────────────────────────────────────────────────
    private boolean darkMode = true;   // starts in dark mode

    // ─── DARK PALETTE ─────────────────────────────────────────────────────────
    private static final String D_BG      = "#0D1117";
    private static final String D_SURFACE = "#161B22";
    private static final String D_CARD    = "#1C2128";
    private static final String D_BORDER  = "#30363D";
    private static final String D_ACCENT  = "#2EA043";
    private static final String D_ACCENT2 = "#388BFD";
    private static final String D_TPRI    = "#E6EDF3";
    private static final String D_TSEC    = "#8B949E";
    private static final String D_DANGER  = "#F85149";
    private static final String D_GOLD    = "#D29922";
    private static final String D_BTNHOV  = "#5299e0";
    private static final String D_FONT1   = "Georgia";
    private static final String D_FONT2   = "Palatino Linotype";

    // ─── LIGHT PALETTE ────────────────────────────────────────────────────────
    private static final String L_BG      = "#F5EDE3";
    private static final String L_SURFACE = "#EDE0D4";
    private static final String L_CARD    = "#FDF7F2";
    private static final String L_BORDER  = "#D4B8A8";
    private static final String L_ACCENT  = "#7C4F3A";
    private static final String L_ACCENT2 = "#B5614A";
    private static final String L_TPRI    = "#2E1A0E";
    private static final String L_TSEC    = "#7A5C4F";
    private static final String L_DANGER  = "#C0392B";
    private static final String L_GOLD    = "#A0612A";
    private static final String L_BTNHOV  = "#C97058";
    private static final String L_FONT1   = "Georgia";
    private static final String L_FONT2   = "Palatino Linotype";

    // ─── ACTIVE THEME GETTERS ─────────────────────────────────────────────────
    private String BG()     { return darkMode ? D_BG      : L_BG;      }
    private String SURF()   { return darkMode ? D_SURFACE : L_SURFACE; }
    private String CARD()   { return darkMode ? D_CARD    : L_CARD;    }
    private String BORD()   { return darkMode ? D_BORDER  : L_BORDER;  }
    private String ACC()    { return darkMode ? D_ACCENT  : L_ACCENT;  }
    private String ACC2()   { return darkMode ? D_ACCENT2 : L_ACCENT2; }
    private String TPRI()   { return darkMode ? D_TPRI    : L_TPRI;    }
    private String TSEC()   { return darkMode ? D_TSEC    : L_TSEC;    }
    private String DNG()    { return darkMode ? D_DANGER  : L_DANGER;  }
    private String GLD()    { return darkMode ? D_GOLD    : L_GOLD;    }
    private String BHOV()   { return darkMode ? D_BTNHOV  : L_BTNHOV;  }
    private String F1()     { return darkMode ? D_FONT1   : L_FONT1;   }
    private String F2()     { return darkMode ? D_FONT2   : L_FONT2;   }

    // ─── STATE ────────────────────────────────────────────────────────────────
    private Stage       primaryStage;
    private int         currentStep   = 0;
    private TextField   nameField     = new TextField();
    private TextField   idField       = new TextField();
    private TextField   gpaField      = new TextField();
    private ToggleGroup majorGroup    = new ToggleGroup();
    private ToggleGroup trackGroup    = new ToggleGroup();
    private ToggleGroup yearGroup     = new ToggleGroup();
    private ToggleGroup semesterGroup = new ToggleGroup();
    private List<Course> allCourses   = new ArrayList<>();
    private Set<String>  checkedCodes = new LinkedHashSet<>();

    // ─── ENTRY POINT ──────────────────────────────────────────────────────────
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Smart Academic Advisor");
        stage.setWidth(820);
        stage.setHeight(700);
        stage.setResizable(false);
        showStep1();
        stage.show();
    }

    // Redraws whichever step is currently showing with the new theme colors
    private void applyTheme() {
        switch (currentStep) {
            case 0 -> showStep1();
            case 1 -> showStep2();
            case 2 -> showStep3();
            case 3 -> showStep4();
            case 4 -> showStep5();
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CHROME – shared outer shell with theme-toggle button in header
    // ══════════════════════════════════════════════════════════════════════════
    private BorderPane buildShell(Node content, int step) {
        currentStep = step;

        BorderPane shell = new BorderPane();
        shell.setStyle("-fx-background-color: " + BG() + ";");

        // Header bar
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 30, 18, 30));
        header.setStyle("-fx-background-color: " + SURF() + ";"
                + "-fx-border-color: " + BORD() + "; -fx-border-width: 0 0 1 0;");

        Label logo = new Label("◈ ACADEMIC ADVISOR");
        logo.setFont(Font.font(F1(), FontWeight.BOLD, 15));
        logo.setTextFill(Color.web(ACC()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // ── Theme toggle
        Button themeBtn = makeThemeBtn();
        themeBtn.setOnAction(e -> { darkMode = !darkMode; applyTheme(); });

        Label stepCounter = new Label("STEP " + (step + 1) + " / 5");
        stepCounter.setFont(Font.font(F1(), FontWeight.BOLD, 12));
        stepCounter.setTextFill(Color.web(TSEC()));

        header.getChildren().addAll(logo, spacer, themeBtn, new Label("   "), stepCounter);

        // Progress dots
        HBox progress = new HBox(12);
        progress.setAlignment(Pos.CENTER);
        progress.setPadding(new Insets(16, 30, 8, 30));
        String[] stepNames = {"Identity", "Major & Track", "GPA", "Year & Semester", "Courses"};
        for (int i = 0; i < 5; i++) {
            VBox dot = new VBox(5);
            dot.setAlignment(Pos.CENTER);
            Circle c = new Circle(7);
            if (i < step) {
                c.setFill(Color.web(ACC())); c.setStroke(Color.web(ACC()));
            } else if (i == step) {
                c.setFill(Color.web(ACC2())); c.setStroke(Color.web(ACC2()));
                ScaleTransition pulse = new ScaleTransition(Duration.millis(900), c);
                pulse.setFromX(1); pulse.setToX(1.3);
                pulse.setFromY(1); pulse.setToY(1.3);
                pulse.setAutoReverse(true);
                pulse.setCycleCount(Animation.INDEFINITE);
                pulse.play();
            } else {
                c.setFill(Color.web(SURF())); c.setStroke(Color.web(BORD())); c.setStrokeWidth(1.5);
            }
            Label name = new Label(stepNames[i]);
            name.setFont(Font.font(F1(), 9));
            name.setTextFill(i == step ? Color.web(ACC2()) : Color.web(TSEC()));
            dot.getChildren().addAll(c, name);
            progress.getChildren().add(dot);
            if (i < 4) {
                Rectangle line = new Rectangle(40, 1);
                line.setFill(i < step ? Color.web(ACC()) : Color.web(BORD()));
                line.setTranslateY(-8);
                progress.getChildren().add(line);
            }
        }

        shell.setTop(new VBox(0, header, progress));
        shell.setCenter(content);
        return shell;
    }

    // Helper: creates the ☀/🌙 toggle button styled for current theme
    private Button makeThemeBtn() {
        Button b = new Button(darkMode ? "☀  Light Mode" : "🌙  Dark Mode");
        b.setFont(Font.font(F1(), FontWeight.BOLD, 11));
        b.setTextFill(Color.web(TSEC()));
        String base = "-fx-border-radius: 6; -fx-padding: 5 12; -fx-cursor: hand;";
        b.setStyle("-fx-background-color: transparent; -fx-border-color: " + BORD() + ";" + base);
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: " + SURF() + "; -fx-border-color: " + ACC2() + ";" + base));
        b.setOnMouseExited(e  -> b.setStyle("-fx-background-color: transparent; -fx-border-color: " + BORD() + ";" + base));
        return b;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  STEP 1 – Name & ID
    // ══════════════════════════════════════════════════════════════════════════
    private void showStep1() {
        VBox content = new VBox(28);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50, 120, 40, 120));

        Button next = nextBtn("Continue →");
        next.setOnAction(e -> {
            if (nameField.getText().isBlank() || idField.getText().isBlank()) {
                shakeNode(next);
                showInlineError(content, "Please fill in both fields.");
                return;
            }
            showStep2();
        });

        content.getChildren().addAll(
            bigTitle("Who are you?"),
            subLabel("Enter your name and student ID to get started."),
            labeledField("Full Name", nameField, "e.g. Mohammed Elneny"),
            labeledField("Student ID", idField, "e.g. 241001750"),
            next
        );
        fadeIn(content);
        primaryStage.setScene(new Scene(buildShell(content, 0)));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  STEP 2 – Major & Track
    // ══════════════════════════════════════════════════════════════════════════
    private void showStep2() {
        VBox content = new VBox(24);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(40, 100, 40, 100));

        HBox majorRow = new HBox(16);
        majorRow.setAlignment(Pos.CENTER);
        String[] majors    = {"CS", "AI"};
        String[] majorFull = {"Computer Science", "Artificial Intelligence"};
        for (int i = 0; i < majors.length; i++) {
            RadioButton rb = styledRadio(majors[i], majorFull[i], majorGroup);
            if (i == 0) rb.setSelected(true);
            majorRow.getChildren().add(wrapRadio(rb, majors[i], majorFull[i]));
        }

        HBox trackRow = new HBox(12);
        trackRow.setAlignment(Pos.CENTER);
        String[] csTracks    = {"BIGDATA", "MEDIA", "GENERAL"};
        String[] csTrackFull = {"Big Data", "Media", "General"};
        String[] aiTracks    = {"AI"};
        String[] aiTrackFull = {"AI Track"};

        Runnable updateTracks = () -> {
            trackRow.getChildren().clear();
            trackGroup = new ToggleGroup();
            String major    = ((RadioButton) majorGroup.getSelectedToggle()).getUserData().toString();
            String[] tracks = major.equals("AI") ? aiTracks    : csTracks;
            String[] full   = major.equals("AI") ? aiTrackFull : csTrackFull;
            for (int i = 0; i < tracks.length; i++) {
                RadioButton rb = new RadioButton();
                rb.setUserData(tracks[i]);
                rb.setToggleGroup(trackGroup);
                if (i == 0) rb.setSelected(true);
                trackRow.getChildren().add(wrapRadio(rb, tracks[i], full[i]));
            }
        };
        majorGroup.selectedToggleProperty().addListener((o, old, nw) -> updateTracks.run());
        updateTracks.run();

        Button next = nextBtn("Continue →");
        Button back = backBtn();
        back.setOnAction(e -> showStep1());
        next.setOnAction(e -> showStep3());

        HBox nav = new HBox(12, back, next);
        nav.setAlignment(Pos.CENTER);
        content.getChildren().addAll(
            bigTitle("Your Major"),
            subLabel("Select your department and specialization track."),
            sectionLabel("MAJOR"), majorRow,
            sectionLabel("TRACK"), trackRow, nav
        );
        fadeIn(content);
        primaryStage.setScene(new Scene(buildShell(content, 1)));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  STEP 3 – GPA
    // ══════════════════════════════════════════════════════════════════════════
    private void showStep3() {
        VBox content = new VBox(24);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50, 140, 40, 140));

        Label indicator = new Label("─");
        indicator.setFont(Font.font(F1(), FontWeight.BOLD, 13));
        indicator.setTextFill(Color.web(TSEC()));

        gpaField.textProperty().addListener((o, old, nw) -> {
            try {
                double g = Double.parseDouble(nw);
                if      (g < 2.0)  { indicator.setText("⚠  Half Load  (max 12 CH)");   indicator.setTextFill(Color.web(DNG())); }
                else if (g <= 3.0) { indicator.setText("●  Normal Load  (max 19 CH)"); indicator.setTextFill(Color.web(GLD())); }
                else if (g <= 4.0) { indicator.setText("★  Overload  (max 21 CH)");    indicator.setTextFill(Color.web(ACC())); }
                else               { indicator.setText("GPA > 4.0 ?");                 indicator.setTextFill(Color.web(DNG())); }
            } catch (NumberFormatException ex) {
                indicator.setText("─"); indicator.setTextFill(Color.web(TSEC()));
            }
        });

        Button next = nextBtn("Continue →");
        Button back = backBtn();
        back.setOnAction(e -> showStep2());
        next.setOnAction(e -> {
            try {
                double g = Double.parseDouble(gpaField.getText().trim());
                if (g < 0.0 || g > 4.0) throw new NumberFormatException();
                showStep4();
            } catch (NumberFormatException ex) {
                shakeNode(next);
                showInlineError(content, "GPA must be a number between 0.0 and 4.0.");
            }
        });

        HBox nav = new HBox(12, back, next);
        nav.setAlignment(Pos.CENTER);
        content.getChildren().addAll(
            bigTitle("Your GPA"),
            subLabel("Enter your current cumulative GPA (0.0 – 4.0).\nThis determines your course load capacity."),
            labeledField("GPA", gpaField, "e.g. 3.2"),
            indicator, nav
        );
        fadeIn(content);
        primaryStage.setScene(new Scene(buildShell(content, 2)));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  STEP 4 – Year & Semester
    // ══════════════════════════════════════════════════════════════════════════
    private void showStep4() {
        VBox content = new VBox(24);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40, 100, 40, 100));

        yearGroup     = new ToggleGroup();
        semesterGroup = new ToggleGroup();

        HBox yearRow = new HBox(12);
        yearRow.setAlignment(Pos.CENTER);
        for (int i = 1; i <= 4; i++) {
            RadioButton rb = new RadioButton();
            rb.setUserData(String.valueOf(i));
            rb.setToggleGroup(yearGroup);
            if (i == 1) rb.setSelected(true);
            yearRow.getChildren().add(wrapRadio(rb, "Year " + i, ordinal(i) + " Year"));
        }

        HBox semRow = new HBox(16);
        semRow.setAlignment(Pos.CENTER);
        String[] semLabels = {"1st Semester", "2nd Semester"};
        for (int i = 1; i <= 2; i++) {
            RadioButton rb = new RadioButton();
            rb.setUserData(String.valueOf(i));
            rb.setToggleGroup(semesterGroup);
            if (i == 1) rb.setSelected(true);
            semRow.getChildren().add(wrapRadio(rb, "Sem " + i, semLabels[i - 1]));
        }

        Button next = nextBtn("Continue →");
        Button back = backBtn();
        back.setOnAction(e -> showStep3());
        next.setOnAction(e -> { loadCoursesForMajor(); showStep5(); });

        HBox nav = new HBox(12, back, next);
        nav.setAlignment(Pos.CENTER);
        content.getChildren().addAll(
            bigTitle("Academic Standing"),
            subLabel("Select your current year and semester."),
            sectionLabel("ACADEMIC YEAR"), yearRow,
            sectionLabel("SEMESTER"), semRow, nav
        );
        fadeIn(content);
        primaryStage.setScene(new Scene(buildShell(content, 3)));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  STEP 5 – Course Checklist
    // ══════════════════════════════════════════════════════════════════════════
    private void showStep5() {
        String yearStr = yearGroup.getSelectedToggle().getUserData().toString().replaceAll("[^0-9]", "");
        String semStr  = semesterGroup.getSelectedToggle().getUserData().toString().replaceAll("[^0-9]", "");
        int studentYear = Integer.parseInt(yearStr);
        int studentSem  = Integer.parseInt(semStr);

        List<int[]> pastSemesters = new ArrayList<>();
        for (int y = 1; y <= 4; y++)
            for (int s = 1; s <= 2; s++)
                pastSemesters.add(new int[]{y, s});

        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: " + BG() + ";");

        VBox hdrBox = new VBox(6);
        hdrBox.setPadding(new Insets(30, 40, 20, 40));
        hdrBox.getChildren().addAll(bigTitle("Completed Courses"), subLabel("Check every course you have already passed."));
        content.getChildren().add(hdrBox);

        VBox listContainer = new VBox(18);
        listContainer.setPadding(new Insets(10, 40, 20, 40));

        if (pastSemesters.isEmpty()) {
            Label none = new Label("No courses available to display.");
            none.setTextFill(Color.web(TSEC()));
            none.setFont(Font.font(F1(), 13));
            listContainer.getChildren().add(none);
        } else {
            for (int[] ys : pastSemesters) {
                int y = ys[0], s = ys[1];
                List<Course> semCourses = getCoursesForSemester(y, s);
                if (semCourses.isEmpty()) continue;

                Label semHeader = new Label("  Year " + y + "  ·  Semester " + s);
                semHeader.setFont(Font.font(F1(), FontWeight.BOLD, 12));
                semHeader.setTextFill(Color.web(ACC2()));
                semHeader.setStyle("-fx-background-color: " + SURF() + ";"
                        + "-fx-border-color: " + ACC2() + "; -fx-border-width: 0 0 0 3; -fx-padding: 8 14;");
                semHeader.setMaxWidth(Double.MAX_VALUE);

                // Create a final list to hold checkboxes for this semester
                List<CheckBox> rowBoxes = new ArrayList<>();
                Label semesterCHLabel = new Label("0 CH selected");
                semesterCHLabel.setFont(Font.font(F1(), 10));
                semesterCHLabel.setTextFill(Color.web(GLD()));

                GridPane grid = new GridPane();
                grid.setHgap(16); grid.setVgap(8);
                grid.setPadding(new Insets(10, 10, 10, 16));
                grid.setStyle("-fx-background-color: " + CARD() + ";"
                        + "-fx-border-color: " + BORD() + "; -fx-border-radius: 6; -fx-background-radius: 6;");

                int col = 0, row = 0;
                for (Course c : semCourses) {
                    CheckBox cb = new CheckBox(c.getCode() + "  –  " + c.getName() + "  [" + c.getCreditHours() + " CH]");
                    styleCheckBox(cb);
                    cb.setTextFill(Color.web(TPRI()));
                    cb.setFont(Font.font(F1(), 12));
                    cb.setUserData(c.getCode());
                    if (checkedCodes.contains(c.getCode())) cb.setSelected(true);

                    // Create final references for the listener
                    final List<CheckBox> finalRowBoxes = rowBoxes;
                    cb.selectedProperty().addListener((o, old, nw) -> {
                        if (nw) checkedCodes.add(c.getCode()); else checkedCodes.remove(c.getCode());
                        updateSemesterCHLabel(semesterCHLabel, finalRowBoxes);
                    });
                    rowBoxes.add(cb);
                    grid.add(cb, col, row);
                    col++; if (col == 2) { col = 0; row++; }
                }

                // Create Select All button with proper scope
                CheckBox selectAll = new CheckBox("Select All");
                styleCheckBox(selectAll);
                selectAll.setFont(Font.font(F1(), 11));
                selectAll.setTextFill(Color.web(TSEC()));
                selectAll.setStyle(selectAll.getStyle() + "; -fx-font-weight: bold;");

                // Create Deselect All button
                Button deselectAllBtn = new Button("Deselect All");
                deselectAllBtn.setFont(Font.font(F1(), 10));
                deselectAllBtn.setTextFill(Color.web(TSEC()));
                deselectAllBtn.setStyle("-fx-background-color: transparent; -fx-border-color: " + BORD() + 
                        "; -fx-border-radius: 4; -fx-padding: 4 10; -fx-cursor: hand;");
                deselectAllBtn.setOnMouseEntered(e -> deselectAllBtn.setStyle("-fx-background-color: " + SURF() + 
                        "; -fx-border-color: " + DNG() + "; -fx-border-radius: 4; -fx-padding: 4 10; -fx-cursor: hand;"));
                deselectAllBtn.setOnMouseExited(e -> deselectAllBtn.setStyle("-fx-background-color: transparent; -fx-border-color: " + BORD() + 
                        "; -fx-border-radius: 4; -fx-padding: 4 10; -fx-cursor: hand;"));

                // Create final reference for handlers
                final List<CheckBox> finalRowBoxes = rowBoxes;

                // Use a flag to prevent cascading updates
                final boolean[] updatingSelectAll = {false};

                // Select All action handler
                selectAll.setOnAction(e -> {
                    updatingSelectAll[0] = true;
                    boolean selectState = selectAll.isSelected();
                    for (CheckBox cb : finalRowBoxes) {
                        cb.setSelected(selectState);
                    }
                    updatingSelectAll[0] = false;
                    updateSemesterCHLabel(semesterCHLabel, finalRowBoxes);
                });

                // Deselect All button action
                deselectAllBtn.setOnAction(e -> {
                    updatingSelectAll[0] = true;
                    selectAll.setSelected(false);
                    for (CheckBox cb : finalRowBoxes) {
                        cb.setSelected(false);
                    }
                    updatingSelectAll[0] = false;
                    updateSemesterCHLabel(semesterCHLabel, finalRowBoxes);
                });

                // Update Select All checkbox when individual checkboxes change
                for (CheckBox cb : finalRowBoxes) {
                    cb.selectedProperty().addListener((o, old, nw) -> {
                        if (!updatingSelectAll[0]) {
                            long selected = finalRowBoxes.stream().filter(CheckBox::isSelected).count();
                            selectAll.setSelected(selected == finalRowBoxes.size());
                        }
                    });
                }

                // Initialize the semester CH label
                updateSemesterCHLabel(semesterCHLabel, finalRowBoxes);

                // Control panel with Select All, Deselect All, and credit hour display
                HBox controlPanel = new HBox(14);
                controlPanel.setAlignment(Pos.CENTER_LEFT);
                controlPanel.setPadding(new Insets(8, 10, 8, 10));
                controlPanel.getChildren().addAll(selectAll, deselectAllBtn, new Label("│"), semesterCHLabel);

                listContainer.getChildren().addAll(semHeader, controlPanel, grid);
            }
        }

        ScrollPane scroll = new ScrollPane(listContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG() + "; -fx-background-color: " + BG() + "; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        content.getChildren().add(scroll);

        Button back   = backBtn();
        back.setOnAction(e -> showStep4());
        Button submit = nextBtn("View Recommendations →");
        submit.setStyle(submit.getStyle().replace(ACC2(), ACC()));
        submit.setOnAction(e -> showResults());

        HBox nav = new HBox(12, back, submit);
        nav.setAlignment(Pos.CENTER);
        nav.setPadding(new Insets(14, 40, 20, 40));
        nav.setStyle("-fx-background-color: " + SURF() + "; -fx-border-color: " + BORD() + "; -fx-border-width: 1 0 0 0;");
        content.getChildren().add(nav);

        fadeIn(content);
        primaryStage.setScene(new Scene(buildShell(new StackPane(content), 4)));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  RESULTS PAGE
    // ══════════════════════════════════════════════════════════════════════════
    private void showResults() {
        String yearStr = yearGroup.getSelectedToggle().getUserData().toString().replaceAll("[^0-9]", "");
        String semStr  = semesterGroup.getSelectedToggle().getUserData().toString().replaceAll("[^0-9]", "");
        int year = Integer.parseInt(yearStr);
        int sem  = Integer.parseInt(semStr);

        String major = ((RadioButton) majorGroup.getSelectedToggle()).getUserData().toString();
        String track = ((RadioButton) trackGroup.getSelectedToggle()).getUserData().toString();
        double gpa   = Double.parseDouble(gpaField.getText().trim());
        String name  = nameField.getText().trim();
        String id    = idField.getText().trim();

        Student student;
        try {
            student = new Student(id, name, gpa, sem, checkedCodes, year, major, track);
        } catch (Exception ex) {
            showAlert("Error building student profile: " + ex.getMessage());
            return;
        }

        List<Course> available = major.equals("AI")
                ? new AdvisorService_AI(allCourses).getAvailableCourses(student)
                : new AdvisorService_CS(allCourses).getAvailableCourses(student);
        List<RecommendationBlock> recs = new RecommendationService(allCourses).getSemesterRecommendation(student);
        String loadType = LoadService.getLoadType(gpa);
        int    maxCH    = LoadService.getMaxCreditHours(gpa);

        BorderPane shell = new BorderPane();
        shell.setStyle("-fx-background-color: " + BG() + ";");

        // ── Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 30, 18, 30));
        header.setStyle("-fx-background-color: " + SURF() + "; -fx-border-color: " + BORD() + "; -fx-border-width: 0 0 1 0;");

        Label logo = new Label("◈ ACADEMIC ADVISOR");
        logo.setFont(Font.font(F1(), FontWeight.BOLD, 15));
        logo.setTextFill(Color.web(ACC()));

        Region hsp = new Region();
        HBox.setHgrow(hsp, Priority.ALWAYS);

        Button themeBtn = makeThemeBtn();
        themeBtn.setOnAction(e -> { darkMode = !darkMode; showResults(); });

        Button restart = new Button("↺  Start Over");
        restart.setFont(Font.font(F1(), FontWeight.BOLD, 12));
        restart.setTextFill(Color.web(TSEC()));
        String rbBase = "-fx-border-radius: 6; -fx-padding: 6 14; -fx-cursor: hand;";
        restart.setStyle("-fx-background-color: transparent; -fx-border-color: " + BORD() + ";" + rbBase);
        restart.setOnMouseEntered(e -> restart.setStyle("-fx-background-color: " + SURF() + "; -fx-border-color: " + ACC2() + ";" + rbBase));
        restart.setOnMouseExited(e  -> restart.setStyle("-fx-background-color: transparent; -fx-border-color: " + BORD() + ";" + rbBase));
        restart.setOnAction(e -> {
            checkedCodes.clear();
            gpaField.clear(); nameField.clear(); idField.clear();
            majorGroup = new ToggleGroup(); trackGroup = new ToggleGroup();
            yearGroup  = new ToggleGroup(); semesterGroup = new ToggleGroup();
            showStep1();
        });

        header.getChildren().addAll(logo, hsp, themeBtn, new Label("  "), restart);

        // ── Student badge
        HBox badge = new HBox(30);
        badge.setAlignment(Pos.CENTER_LEFT);
        badge.setPadding(new Insets(20, 30, 16, 30));
        badge.setStyle("-fx-background-color: " + SURF() + "; -fx-border-color: " + BORD() + "; -fx-border-width: 0 0 1 0;");
        badge.getChildren().addAll(
            badgeField("NAME",  name),
            badgeField("ID",    id),
            badgeField("MAJOR", major),
            badgeField("TRACK", track),
            badgeField("GPA",   String.valueOf(gpa)),
            badgeField("YEAR",  "Y" + year + "·S" + sem),
            colorBadge("LOAD",  loadType, maxCH + " CH max",
                loadType.equals("Half Load") ? DNG() : loadType.equals("Normal Load") ? GLD() : ACC())
        );

        shell.setTop(new VBox(0, header, badge));

        // ── Custom tab toggle buttons
        StackPane contentArea = new StackPane();
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        ScrollPane recPane   = buildRecommendationTab(recs, maxCH);
        ScrollPane availPane = buildAvailableTab(available);
        contentArea.getChildren().addAll(availPane, recPane);

        Button recBtn   = styledTabBtn("  ★  Semester Recommendation", true);
        Button availBtn = styledTabBtn("  ◎  All Available Courses",   false);

        recBtn.setOnAction(e   -> { recPane.toFront();   applyTabActive(recBtn, true);   applyTabActive(availBtn, false); });
        availBtn.setOnAction(e -> { availPane.toFront(); applyTabActive(availBtn, true); applyTabActive(recBtn,   false); });
        recBtn.setOnMouseEntered(e   -> { if (!isTabActive(recBtn))   applyTabHover(recBtn);   });
        recBtn.setOnMouseExited(e    -> { if (!isTabActive(recBtn))   applyTabActive(recBtn,   false); });
        availBtn.setOnMouseEntered(e -> { if (!isTabActive(availBtn)) applyTabHover(availBtn); });
        availBtn.setOnMouseExited(e  -> { if (!isTabActive(availBtn)) applyTabActive(availBtn, false); });

        HBox tabBar = new HBox(14, recBtn, availBtn);
        tabBar.setAlignment(Pos.CENTER_LEFT);
        tabBar.setPadding(new Insets(14, 30, 14, 30));
        tabBar.setStyle("-fx-background-color: " + SURF() + "; -fx-border-color: " + BORD() + "; -fx-border-width: 0 0 1 0;");

        VBox centerBox = new VBox(0, tabBar, contentArea);
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        shell.setCenter(centerBox);

        fadeIn(shell);
        primaryStage.setScene(new Scene(shell, 820, 700));
    }

    // ─── Recommendation tab ───────────────────────────────────────────────────
    private ScrollPane buildRecommendationTab(List<RecommendationBlock> recs, int maxCH) {
        VBox box = new VBox(20);
        box.setPadding(new Insets(24, 30, 30, 30));
        box.setStyle("-fx-background-color: " + BG() + ";");

        if (recs.isEmpty()) {
            Label msg = new Label("No recommendations could be generated.\nCheck that your completed courses and GPA are correct.");
            msg.setTextFill(Color.web(TSEC()));
            msg.setFont(Font.font(F1(), 13));
            box.getChildren().add(msg);
        } else {
            for (RecommendationBlock block : recs) {
                String tagColor = block.getType().equals("FIXED") ? ACC() : GLD();

                Label tag = new Label("  " + block.getType() + "  ");
                tag.setFont(Font.font(F1(), FontWeight.BOLD, 10));
                tag.setTextFill(Color.web(tagColor));
                tag.setStyle("-fx-background-color: " + tagColor + "22; -fx-border-color: " + tagColor
                        + "; -fx-border-radius: 3; -fx-padding: 2 8;");

                Label msg = new Label(block.getMessage());
                msg.setFont(Font.font(F1(), FontWeight.BOLD, 15));
                msg.setTextFill(Color.web(TPRI()));

                int blockCH = block.getCourses().stream().mapToInt(Course::getCreditHours).sum();
                Label chCount = new Label(blockCH + " CH");
                chCount.setFont(Font.font(F1(), 12));
                chCount.setTextFill(Color.web(TSEC()));

                Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
                HBox blockHeader = new HBox(10, tag, msg, sp, chCount);
                blockHeader.setAlignment(Pos.CENTER_LEFT);

                VBox courses = new VBox(6);
                courses.setStyle("-fx-background-color: " + CARD() + "; -fx-border-color: " + BORD()
                        + "; -fx-border-radius: 8; -fx-padding: 12;");
                for (Course c : block.getCourses()) courses.getChildren().add(courseRow(c, tagColor));

                box.getChildren().addAll(blockHeader, courses);
            }
        }

        ScrollPane sp = new ScrollPane(box);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: " + BG() + "; -fx-background-color: " + BG() + "; -fx-border-color: transparent;");
        return sp;
    }

    // ─── Available tab ────────────────────────────────────────────────────────
    private ScrollPane buildAvailableTab(List<Course> available) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(24, 30, 30, 30));
        box.setStyle("-fx-background-color: " + BG() + ";");

        Label header = new Label(available.size() + " courses available to you");
        header.setFont(Font.font(F1(), FontWeight.BOLD, 14));
        header.setTextFill(Color.web(TSEC()));
        box.getChildren().add(header);

        if (available.isEmpty()) {
            Label none = new Label("No courses available — all prerequisites may be pending.");
            none.setTextFill(Color.web(TSEC()));
            box.getChildren().add(none);
        } else {
            Map<String, List<Course>> grouped = new LinkedHashMap<>();
            for (Course c : available)
                grouped.computeIfAbsent(c.getCategory(), k -> new ArrayList<>()).add(c);

            for (Map.Entry<String, List<Course>> entry : grouped.entrySet()) {
                Label catLabel = new Label("  " + entry.getKey().replace("_", " "));
                catLabel.setFont(Font.font(F1(), FontWeight.BOLD, 11));
                catLabel.setTextFill(Color.web(ACC2()));
                catLabel.setStyle("-fx-background-color: " + ACC2() + "18; -fx-border-color: " + ACC2()
                        + "; -fx-border-width: 0 0 0 2; -fx-padding: 5 10;");
                catLabel.setMaxWidth(Double.MAX_VALUE);

                VBox catBox = new VBox(5);
                catBox.setStyle("-fx-background-color: " + CARD() + "; -fx-border-color: " + BORD()
                        + "; -fx-border-radius: 6; -fx-padding: 10;");
                for (Course c : entry.getValue()) catBox.getChildren().add(courseRow(c, ACC2()));

                box.getChildren().addAll(catLabel, catBox);
            }
        }

        ScrollPane sp = new ScrollPane(box);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: " + BG() + "; -fx-background-color: " + BG() + "; -fx-border-color: transparent;");
        return sp;
    }

    // ─── Single course row ────────────────────────────────────────────────────
    private HBox courseRow(Course c, String accentColor) {
        Label code = new Label(c.getCode());
        code.setFont(Font.font(F1(), FontWeight.BOLD, 12));
        code.setTextFill(Color.web(accentColor));
        code.setMinWidth(90);

        Label nameL = new Label(c.getName());
        nameL.setFont(Font.font(F2(), 13));
        nameL.setTextFill(Color.web(TPRI()));
        HBox.setHgrow(nameL, Priority.ALWAYS);

        Label ch = new Label(c.getCreditHours() + " CH");
        ch.setFont(Font.font(F1(), 11));
        ch.setTextFill(Color.web(TSEC()));
        ch.setStyle("-fx-background-color: " + SURF() + "; -fx-padding: 2 8; -fx-border-radius: 3;");

        HBox row = new HBox(12, code, nameL, ch);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(7, 10, 7, 10));
        row.setStyle("-fx-background-color: transparent; -fx-border-radius: 5;");
        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: " + accentColor + "15; -fx-border-radius: 5;"));
        row.setOnMouseExited(e  -> row.setStyle("-fx-background-color: transparent; -fx-border-radius: 5;"));
        return row;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DATA HELPERS
    // ══════════════════════════════════════════════════════════════════════════
    private void loadCoursesForMajor() {
        if (majorGroup.getSelectedToggle() != null) {
            String major = ((RadioButton) majorGroup.getSelectedToggle()).getUserData().toString();
            String path  = major.equals("AI") ? "Data/AI_courses.json" : "Data/CS_courses.json";
            allCourses   = new LoadData().loadCourses(path);
        }
    }

    private List<Course> getCoursesForSemester(int year, int semester) {
        List<Course> result = new ArrayList<>();
        for (Course c : allCourses)
            if (c.getYear() == year && c.getSemester() == semester)
                result.add(c);
        return result;
    }

    private void updateSemesterCHLabel(Label chLabel, List<CheckBox> rowBoxes) {
        int totalCH = 0;
        for (CheckBox cb : rowBoxes) {
            if (cb.isSelected()) {
                // Extract course code from checkbox text
                String courseCode = cb.getUserData().toString();
                for (Course c : allCourses) {
                    if (c.getCode().equals(courseCode)) {
                        totalCH += c.getCreditHours();
                        break;
                    }
                }
            }
        }
        chLabel.setText(totalCH + " CH selected");
        chLabel.setTextFill(Color.web(totalCH > 0 ? GLD() : TSEC()));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  UI COMPONENT HELPERS
    // ══════════════════════════════════════════════════════════════════════════
    private Label bigTitle(String text) {
        Label l = new Label(text);
        l.setFont(Font.font(F2(), FontWeight.BOLD, 28));
        l.setTextFill(Color.web(TPRI()));
        return l;
    }
    private Label subLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font(F2(), 14));
        l.setTextFill(Color.web(TSEC()));
        l.setWrapText(true);
        return l;
    }
    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font(F1(), FontWeight.BOLD, 11));
        l.setTextFill(Color.web(TSEC()));
        return l;
    }

    private VBox labeledField(String label, TextField field, String prompt) {
        Label lbl = new Label(label.toUpperCase());
        lbl.setFont(Font.font(F1(), FontWeight.BOLD, 11));
        lbl.setTextFill(Color.web(TSEC()));

        field.setPromptText(prompt);
        field.setFont(Font.font(F2(), 14));
        field.setMaxWidth(Double.MAX_VALUE);
        String base = "-fx-background-color: " + CARD() + "; -fx-border-radius: 7; -fx-background-radius: 7;"
                    + "-fx-text-fill: " + TPRI() + "; -fx-prompt-text-fill: " + TSEC() + "; -fx-padding: 10 14;";
        field.setStyle(base + "-fx-border-color: " + BORD() + ";");
        field.focusedProperty().addListener((o, old, focused) ->
            field.setStyle(base + "-fx-border-color: " + (focused ? ACC2() : BORD()) + ";"));

        VBox box = new VBox(6, lbl, field);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private RadioButton styledRadio(String code, String full, ToggleGroup group) {
        RadioButton rb = new RadioButton(full);
        rb.setUserData(code);
        rb.setToggleGroup(group);
        return rb;
    }

    private VBox wrapRadio(RadioButton rb, String code, String full) {
        rb.setUserData(code);

        Label codeLabel = new Label(code);
        codeLabel.setFont(Font.font(F1(), FontWeight.BOLD, 14));
        codeLabel.setTextFill(Color.web(TPRI()));

        Label fullLabel = new Label(full);
        fullLabel.setFont(Font.font(F2(), 11));
        fullLabel.setTextFill(Color.web(TSEC()));

        rb.setStyle("-fx-text-fill: transparent; -fx-padding: 0; -fx-background-color: transparent;");

        VBox card = new VBox(4, codeLabel, fullLabel, rb);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(14, 20, 14, 20));
        card.setMinWidth(120);
        card.setStyle("-fx-background-color: " + CARD() + "; -fx-border-color: " + BORD() + ";"
                + "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");

        Runnable updateStyle = () -> {
            boolean sel = rb.isSelected();
            card.setStyle("-fx-background-color: " + (sel ? ACC2() + "18" : CARD()) + ";"
                    + "-fx-border-color: " + (sel ? ACC2() : BORD()) + ";"
                    + "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
            codeLabel.setTextFill(Color.web(sel ? ACC2() : TPRI()));
        };
        rb.selectedProperty().addListener((o, old, nw) -> updateStyle.run());
        card.setOnMouseClicked(e -> { rb.setSelected(true); updateStyle.run(); });
        card.setOnMouseEntered(e -> {
            if (!rb.isSelected())
                card.setStyle("-fx-background-color: " + SURF() + "; -fx-border-color: " + ACC2() + "77;"
                        + "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
        });
        card.setOnMouseExited(e -> updateStyle.run());
        return card;
    }

    private Button nextBtn(String label) {
        Button b = new Button(label);
        b.setFont(Font.font(F1(), FontWeight.BOLD, 13));
        b.setTextFill(Color.web("#FFFFFF"));
        b.setStyle("-fx-background-color: " + ACC2() + "; -fx-background-radius: 8; -fx-padding: 10 28; -fx-cursor: hand;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: " + BHOV() + "; -fx-background-radius: 8; -fx-padding: 10 28; -fx-cursor: hand;"));
        b.setOnMouseExited(e  -> b.setStyle("-fx-background-color: " + ACC2() + "; -fx-background-radius: 8; -fx-padding: 10 28; -fx-cursor: hand;"));
        return b;
    }
    private Button backBtn() {
        Button b = new Button("← Back");
        b.setFont(Font.font(F1(), FontWeight.BOLD, 13));
        b.setTextFill(Color.web(TSEC()));
        b.setStyle("-fx-background-color: transparent; -fx-border-color: " + BORD() + "; -fx-border-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: " + SURF() + "; -fx-border-color: " + TSEC() + "; -fx-border-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;"));
        b.setOnMouseExited(e  -> b.setStyle("-fx-background-color: transparent; -fx-border-color: " + BORD() + "; -fx-border-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;"));
        return b;
    }
    private void styleCheckBox(CheckBox cb) {
        cb.setStyle("-fx-text-fill: " + TPRI() + "; -fx-cursor: hand;");
    }

    private HBox badgeField(String label, String value) {
        Label lbl = new Label(label); lbl.setFont(Font.font(F1(), 9));                lbl.setTextFill(Color.web(TSEC()));
        Label val = new Label(value); val.setFont(Font.font(F1(), FontWeight.BOLD, 13)); val.setTextFill(Color.web(TPRI()));
        return new HBox(new VBox(2, lbl, val));
    }
    private HBox colorBadge(String label, String value, String sub, String color) {
        Label lbl = new Label(label); lbl.setFont(Font.font(F1(), 9));                lbl.setTextFill(Color.web(TSEC()));
        Label val = new Label(value); val.setFont(Font.font(F1(), FontWeight.BOLD, 13)); val.setTextFill(Color.web(color));
        Label s   = new Label(sub);   s.setFont(Font.font(F1(), 10));                 s.setTextFill(Color.web(color));
        return new HBox(new VBox(2, lbl, val, s));
    }

    // ── Tab button helpers ────────────────────────────────────────────────────
    private Button styledTabBtn(String label, boolean active) {
        Button b = new Button(label);
        b.setFont(Font.font(F1(), FontWeight.BOLD, 13));
        b.setMinWidth(230); b.setPrefHeight(40);
        b.setUserData(active ? "active" : "inactive");
        applyTabActive(b, active);
        return b;
    }
    private void applyTabActive(Button b, boolean active) {
        b.setUserData(active ? "active" : "inactive");
        if (active) {
            b.setStyle("-fx-background-color: " + ACC2() + "; -fx-text-fill: #FFFFFF;"
                    + "-fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand; -fx-padding: 10 24;");
        } else {
            b.setStyle("-fx-background-color: " + CARD() + "; -fx-text-fill: " + TSEC() + ";"
                    + "-fx-background-radius: 8; -fx-border-color: " + BORD() + ";"
                    + "-fx-border-radius: 8; -fx-cursor: hand; -fx-padding: 10 24;");
        }
    }
    private void applyTabHover(Button b) {
        b.setStyle("-fx-background-color: " + SURF() + "; -fx-text-fill: " + TPRI() + ";"
                + "-fx-background-radius: 8; -fx-border-color: " + ACC2() + ";"
                + "-fx-border-radius: 8; -fx-cursor: hand; -fx-padding: 10 24;");
    }
    private boolean isTabActive(Button b) { return "active".equals(b.getUserData()); }

    // ══════════════════════════════════════════════════════════════════════════
    //  ANIMATION & UTILITY
    // ══════════════════════════════════════════════════════════════════════════
    private void fadeIn(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(300), node);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), node);
        tt.setFromY(20); tt.setToY(0); tt.play();
    }
    private void shakeNode(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(60), node);
        tt.setFromX(0); tt.setByX(8); tt.setCycleCount(6); tt.setAutoReverse(true); tt.play();
    }
    private void showInlineError(VBox container, String msg) {
        container.getChildren().removeIf(n -> "error".equals(n.getUserData()));
        Label err = new Label("⚠  " + msg);
        err.setUserData("error");
        err.setFont(Font.font(F1(), 12));
        err.setTextFill(Color.web(DNG()));
        err.setStyle("-fx-background-color: " + DNG() + "18; -fx-border-color: " + DNG() + "; -fx-border-radius: 5; -fx-padding: 6 12;");
        container.getChildren().add(err);
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> container.getChildren().remove(err));
        pause.play();
    }
    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle("Error"); a.showAndWait();
    }
    private String ordinal(int n) {
        return switch (n) { case 1 -> "1st"; case 2 -> "2nd"; case 3 -> "3rd"; default -> n + "th"; };
    }

    public static void main(String[] args) { launch(); }
}
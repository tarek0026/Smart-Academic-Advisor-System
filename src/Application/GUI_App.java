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

public class GUI_App extends Application {

    // ─── PALETTE ──────────────────────────────────────────────────────────────
    private static final String BG        = "#0D1117";
    private static final String SURFACE   = "#161B22";
    private static final String CARD      = "#1C2128";
    private static final String BORDER    = "#30363D";
    private static final String ACCENT    = "#2EA043";
    private static final String ACCENT2   = "#388BFD";
    private static final String TEXT_PRI  = "#E6EDF3";
    private static final String TEXT_SEC  = "#8B949E";
    private static final String DANGER    = "#F85149";
    private static final String GOLD      = "#D29922";

    // ─── STATE ────────────────────────────────────────────────────────────────
    private Stage primaryStage;

    // Step 1 fields
    private TextField nameField = new TextField();
    private TextField idField   = new TextField();

    // Step 2 fields
    private ToggleGroup majorGroup  = new ToggleGroup();
    private ToggleGroup trackGroup  = new ToggleGroup();

    // Step 3 fields
    private TextField gpaField = new TextField();

    // Step 4 fields
    private ToggleGroup yearGroup     = new ToggleGroup();
    private ToggleGroup semesterGroup = new ToggleGroup();

    // Step 5 checkboxes: map from (year, semester) label → list of (Course, CheckBox)
    private List<Course>    allCourses   = new ArrayList<>();
    private Set<String>     checkedCodes = new LinkedHashSet<>();

    // Current step tracking for progress bar
    private int currentStep = 0;
    private Label[] stepLabels = new Label[5];

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

    // ══════════════════════════════════════════════════════════════════════════
    //  CHROME – shared outer shell
    // ══════════════════════════════════════════════════════════════════════════
    private BorderPane buildShell(Node content, int step) {
        currentStep = step;

        BorderPane shell = new BorderPane();
        shell.setStyle("-fx-background-color: " + BG + ";");

        // ── Top header bar
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 30, 18, 30));
        header.setStyle("-fx-background-color: " + SURFACE + ";"
                + "-fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");

        Label logo = new Label("◈ ACADEMIC ADVISOR");
        logo.setFont(Font.font("Courier New", FontWeight.BOLD, 15));
        logo.setTextFill(Color.web(ACCENT));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label stepCounter = new Label("STEP " + (step + 1) + " / 5");
        stepCounter.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
        stepCounter.setTextFill(Color.web(TEXT_SEC));

        header.getChildren().addAll(logo, spacer, stepCounter);

        // ── Progress dots
        HBox progress = new HBox(12);
        progress.setAlignment(Pos.CENTER);
        progress.setPadding(new Insets(16, 30, 8, 30));
        String[] stepNames = {"Identity", "Major & Track", "GPA", "Year & Semester", "Courses"};
        for (int i = 0; i < 5; i++) {
            VBox dot = new VBox(5);
            dot.setAlignment(Pos.CENTER);
            Circle c = new Circle(7);
            if (i < step) {
                c.setFill(Color.web(ACCENT));
                c.setStroke(Color.web(ACCENT));
            } else if (i == step) {
                c.setFill(Color.web(ACCENT2));
                c.setStroke(Color.web(ACCENT2));
                ScaleTransition pulse = new ScaleTransition(Duration.millis(900), c);
                pulse.setFromX(1); pulse.setToX(1.3);
                pulse.setFromY(1); pulse.setToY(1.3);
                pulse.setAutoReverse(true);
                pulse.setCycleCount(Animation.INDEFINITE);
                pulse.play();
            } else {
                c.setFill(Color.web(SURFACE));
                c.setStroke(Color.web(BORDER));
                c.setStrokeWidth(1.5);
            }
            Label name = new Label(stepNames[i]);
            name.setFont(Font.font("Courier New", 9));
            name.setTextFill(i == step ? Color.web(ACCENT2) : Color.web(TEXT_SEC));
            dot.getChildren().addAll(c, name);
            progress.getChildren().add(dot);

            if (i < 4) {
                Rectangle line = new Rectangle(40, 1);
                line.setFill(i < step ? Color.web(ACCENT) : Color.web(BORDER));
                line.setTranslateY(-8);
                progress.getChildren().add(line);
            }
        }

        VBox top = new VBox(0, header, progress);
        shell.setTop(top);
        shell.setCenter(content);
        return shell;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  STEP 1 – Name & ID
    // ══════════════════════════════════════════════════════════════════════════
    private void showStep1() {
        VBox content = new VBox(28);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50, 120, 40, 120));

        Label title = bigTitle("Who are you?");
        Label sub   = subLabel("Enter your name and student ID to get started.");

        VBox nameBox = labeledField("Full Name", nameField, "e.g. Ahmed Al-Rashidi");
        VBox idBox   = labeledField("Student ID", idField, "e.g. 241001750");

        Button next = nextBtn("Continue →");
        next.setOnAction(e -> {
            if (nameField.getText().isBlank() || idField.getText().isBlank()) {
                shakeNode(next);
                showInlineError(content, "Please fill in both fields.");
                return;
            }
            showStep2();
        });

        content.getChildren().addAll(title, sub, nameBox, idBox, next);
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

        Label title = bigTitle("Your Major");
        Label sub   = subLabel("Select your department and specialization track.");

        // Major selector (CS only for now — extend later)
        Label majLabel = sectionLabel("MAJOR");
        HBox majorRow = new HBox(16);
        majorRow.setAlignment(Pos.CENTER);
        String[] majors = {"CS", "AI"};
        String[] majorFull = {"Computer Science", "Artificial Intelligence"};
        for (int i = 0; i < majors.length; i++) {
            RadioButton rb = styledRadio(majors[i], majorFull[i], majorGroup);
            if (i == 0) rb.setSelected(true);
            HBox.setHgrow(rb.getParent() == null ? new Region() : (Region) rb.getParent(), Priority.ALWAYS);
            majorRow.getChildren().add(wrapRadio(rb, majors[i], majorFull[i]));
        }

        // Track selector
        Label trackLabel = sectionLabel("TRACK");

        HBox trackRow = new HBox(12);
        trackRow.setAlignment(Pos.CENTER);

        String[] csTracks = {"BIGDATA", "MEDIA", "GENERAL"};
        String[] csTrackFull = {"Big Data", "Media", "General"};
        String[] aiTracks = {"AI"};
        String[] aiTrackFull = {"AI Track"};

        // Populate tracks based on selected major
        Runnable updateTracks = () -> {
            trackRow.getChildren().clear();
            trackGroup = new ToggleGroup();
            String major = ((RadioButton) majorGroup.getSelectedToggle()).getUserData().toString();
            String[] tracks = major.equals("AI") ? aiTracks : csTracks;
            String[] fullNames = major.equals("AI") ? aiTrackFull : csTrackFull;
            for (int i = 0; i < tracks.length; i++) {
                RadioButton rb = new RadioButton();
                rb.setUserData(tracks[i]);
                rb.setToggleGroup(trackGroup);
                if (i == 0) rb.setSelected(true);
                trackRow.getChildren().add(wrapRadio(rb, tracks[i], fullNames[i]));
            }
        };

        majorGroup.selectedToggleProperty().addListener((o, old, nw) -> updateTracks.run());
        updateTracks.run(); // initial

        Button next = nextBtn("Continue →");
        Button back = backBtn();
        back.setOnAction(e -> showStep1());
        next.setOnAction(e -> showStep3());

        HBox nav = new HBox(12, back, next);
        nav.setAlignment(Pos.CENTER);

        content.getChildren().addAll(title, sub, majLabel, majorRow, trackLabel, trackRow, nav);
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

        Label title = bigTitle("Your GPA");
        Label sub   = subLabel("Enter your current cumulative GPA (0.0 – 4.0).\nThis determines your course load capacity.");

        VBox gpaBox = labeledField("GPA", gpaField, "e.g. 3.2");

        // Dynamic GPA indicator
        Label indicator = new Label("─");
        indicator.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        indicator.setTextFill(Color.web(TEXT_SEC));

        gpaField.textProperty().addListener((o, old, nw) -> {
            try {
                double g = Double.parseDouble(nw);
                if (g < 2.0)       { indicator.setText("⚠  Half Load  (max 12 CH)"); indicator.setTextFill(Color.web(DANGER)); }
                else if (g <= 3.0) { indicator.setText("●  Normal Load  (max 19 CH)"); indicator.setTextFill(Color.web(GOLD)); }
                else if (g <= 4.0) { indicator.setText("★  Overload  (max 21 CH)"); indicator.setTextFill(Color.web(ACCENT)); }
                else               { indicator.setText("GPA > 4.0 ?"); indicator.setTextFill(Color.web(DANGER)); }
            } catch (NumberFormatException ex) {
                indicator.setText("─"); indicator.setTextFill(Color.web(TEXT_SEC));
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

        content.getChildren().addAll(title, sub, gpaBox, indicator, nav);
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

        Label title = bigTitle("Academic Standing");
        Label sub   = subLabel("Select your current year and semester.");

        Label yearLabel = sectionLabel("ACADEMIC YEAR");
        HBox yearRow = new HBox(12);
        yearRow.setAlignment(Pos.CENTER);
        for (int i = 1; i <= 4; i++) {
            RadioButton rb = new RadioButton();
            rb.setUserData(String.valueOf(i));
            rb.setToggleGroup(yearGroup);
            if (i == 1) rb.setSelected(true);
            yearRow.getChildren().add(wrapRadio(rb, "Year " + i, ordinal(i) + " Year"));
        }

        Label semLabel = sectionLabel("SEMESTER");
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
        next.setOnAction(e -> {
            loadCoursesForMajor();
            showStep5();
        });

        HBox nav = new HBox(12, back, next);
        nav.setAlignment(Pos.CENTER);

        content.getChildren().addAll(title, sub, yearLabel, yearRow, semLabel, semRow, nav);
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

        // Build list of (year, semester) pairs that come BEFORE student's current position
        List<int[]> pastSemesters = new ArrayList<>();
        for (int y = 1; y <= studentYear; y++) {
            for (int s = 1; s <= 2; s++) {
                if (y == studentYear && s >= studentSem) break;
                pastSemesters.add(new int[]{y, s});
            }
        }

        // Outer layout
        VBox content = new VBox(0);
        content.setStyle("-fx-background-color: " + BG + ";");

        // Top header inside content
        VBox headerBox = new VBox(6);
        headerBox.setPadding(new Insets(30, 40, 20, 40));
        Label title = bigTitle("Completed Courses");
        Label sub   = subLabel("Check every course you have already passed.");
        headerBox.getChildren().addAll(title, sub);
        content.getChildren().add(headerBox);

        // Scrollable checklist area
        VBox listContainer = new VBox(18);
        listContainer.setPadding(new Insets(10, 40, 20, 40));

        if (pastSemesters.isEmpty()) {
            Label none = new Label("No previous semesters — you are in Year 1, Semester 1.");
            none.setTextFill(Color.web(TEXT_SEC));
            none.setFont(Font.font("Courier New", 13));
            listContainer.getChildren().add(none);
        } else {
            for (int[] ys : pastSemesters) {
                int y = ys[0], s = ys[1];
                List<Course> semCourses = getCoursesForSemester(y, s);
                if (semCourses.isEmpty()) continue;

                // Semester section header
                Label semHeader = new Label("  Year " + y + "  ·  Semester " + s);
                semHeader.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
                semHeader.setTextFill(Color.web(ACCENT2));
                semHeader.setStyle("-fx-background-color: " + SURFACE + ";"
                        + "-fx-border-color: " + ACCENT2 + "; -fx-border-width: 0 0 0 3;"
                        + "-fx-padding: 8 14;");
                semHeader.setMaxWidth(Double.MAX_VALUE);

                // "Select All" toggle for this semester
                CheckBox selectAll = new CheckBox("Select All");
                styleCheckBox(selectAll);
                selectAll.setFont(Font.font("Courier New", 11));
                selectAll.setTextFill(Color.web(TEXT_SEC));

                // Course checkboxes
                List<CheckBox> rowBoxes = new ArrayList<>();
                GridPane grid = new GridPane();
                grid.setHgap(16);
                grid.setVgap(8);
                grid.setPadding(new Insets(10, 10, 10, 16));
                grid.setStyle("-fx-background-color: " + CARD + ";"
                        + "-fx-border-color: " + BORDER + "; -fx-border-radius: 6;"
                        + "-fx-background-radius: 6;");

                int col = 0, row = 0;
                for (Course c : semCourses) {
                    CheckBox cb = new CheckBox(c.getCode() + "  –  " + c.getName()
                            + "  [" + c.getCreditHours() + " CH]");
                    styleCheckBox(cb);
                    cb.setTextFill(Color.web(TEXT_PRI));
                    cb.setFont(Font.font("Courier New", 12));
                    cb.setUserData(c.getCode());
                    if (checkedCodes.contains(c.getCode())) cb.setSelected(true);

                    cb.selectedProperty().addListener((o, old, nw) -> {
                        if (nw) checkedCodes.add(c.getCode());
                        else    checkedCodes.remove(c.getCode());
                        long selected = rowBoxes.stream().filter(CheckBox::isSelected).count();
                        selectAll.setSelected(selected == rowBoxes.size());
                    });

                    rowBoxes.add(cb);
                    grid.add(cb, col, row);
                    col++;
                    if (col == 2) { col = 0; row++; }
                }

                selectAll.setOnAction(e -> {
                    boolean sel = selectAll.isSelected();
                    rowBoxes.forEach(cb -> cb.setSelected(sel));
                });

                HBox selAllRow = new HBox(selectAll);
                selAllRow.setAlignment(Pos.CENTER_RIGHT);
                selAllRow.setPadding(new Insets(0, 10, 4, 0));

                listContainer.getChildren().addAll(semHeader, selAllRow, grid);
            }
        }

        ScrollPane scroll = new ScrollPane(listContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + BG + "; -fx-background-color: " + BG + ";"
                + "-fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        content.getChildren().add(scroll);

        // Bottom nav
        Button back   = backBtn();
        back.setOnAction(e -> showStep4());
        Button submit = nextBtn("View Recommendations →");
        submit.setStyle(submit.getStyle().replace(ACCENT2, ACCENT));
        submit.setOnAction(e -> showResults());

        HBox nav = new HBox(12, back, submit);
        nav.setAlignment(Pos.CENTER);
        nav.setPadding(new Insets(14, 40, 20, 40));
        nav.setStyle("-fx-background-color: " + SURFACE + ";"
                + "-fx-border-color: " + BORDER + "; -fx-border-width: 1 0 0 0;");
        content.getChildren().add(nav);

        fadeIn(content);
        primaryStage.setScene(new Scene(buildShell(new StackPane(content), 4)));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  RESULTS PAGE
    // ══════════════════════════════════════════════════════════════════════════
    private void showResults() {
        // ── Build student
        String yearStr = yearGroup.getSelectedToggle().getUserData().toString().replaceAll("[^0-9]", "");
        String semStr  = semesterGroup.getSelectedToggle().getUserData().toString().replaceAll("[^0-9]", "");

        int year = Integer.parseInt(yearStr);
        int sem  = Integer.parseInt(semStr);
        
        String major = ((RadioButton) majorGroup.getSelectedToggle()).getUserData().toString();
        String track = ((RadioButton) trackGroup.getSelectedToggle()).getUserData().toString();
        double gpa   = Double.parseDouble(gpaField.getText().trim());
        //int year     = Integer.parseInt(yearGroup.getSelectedToggle().getUserData().toString());
        //int sem      = Integer.parseInt(semesterGroup.getSelectedToggle().getUserData().toString());
        String name  = nameField.getText().trim();
        String id    = idField.getText().trim();

        Student student;
        try {
            student = new Student(id, name, gpa, sem, checkedCodes, year, major, track);
        } catch (Exception ex) {
            showAlert("Error building student profile: " + ex.getMessage());
            return;
        }

        // ── Call services
        List<Course> available;
        if (major.equals("AI")) {
            available = new AdvisorService_AI(allCourses).getAvailableCourses(student);
        } else {
            available = new AdvisorService_CS(allCourses).getAvailableCourses(student);
        }

        List<RecommendationBlock> recs =
                new RecommendationService(allCourses).getSemesterRecommendation(student);

        String loadType = LoadService.getLoadType(gpa);
        int    maxCH    = LoadService.getMaxCreditHours(gpa);

        // ── Build results UI
        BorderPane shell = new BorderPane();
        shell.setStyle("-fx-background-color: " + BG + ";");

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 30, 18, 30));
        header.setStyle("-fx-background-color: " + SURFACE + ";"
                + "-fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");

        Label logo = new Label("◈ ACADEMIC ADVISOR");
        logo.setFont(Font.font("Courier New", FontWeight.BOLD, 15));
        logo.setTextFill(Color.web(ACCENT));

        Region hsp = new Region();
        HBox.setHgrow(hsp, Priority.ALWAYS);

        Button restart = new Button("↺  Start Over");
        restart.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
        restart.setTextFill(Color.web(TEXT_SEC));
        restart.setStyle("-fx-background-color: transparent; -fx-border-color: " + BORDER + ";"
                + "-fx-border-radius: 6; -fx-padding: 6 14; -fx-cursor: hand;");
        restart.setOnMouseEntered(e -> restart.setStyle(
                "-fx-background-color: " + SURFACE + "; -fx-border-color: " + ACCENT2 + ";"
                + "-fx-border-radius: 6; -fx-padding: 6 14; -fx-cursor: hand;"));
        restart.setOnMouseExited(e -> restart.setStyle(
                "-fx-background-color: transparent; -fx-border-color: " + BORDER + ";"
                + "-fx-border-radius: 6; -fx-padding: 6 14; -fx-cursor: hand;"));
        restart.setOnAction(e -> {
            checkedCodes.clear();
            gpaField.clear(); nameField.clear(); idField.clear();
            majorGroup = new ToggleGroup(); trackGroup = new ToggleGroup();
            yearGroup = new ToggleGroup(); semesterGroup = new ToggleGroup();
            showStep1();
        });

        header.getChildren().addAll(logo, hsp, restart);

        // Student badge
        HBox badge = new HBox(30);
        badge.setAlignment(Pos.CENTER_LEFT);
        badge.setPadding(new Insets(20, 30, 16, 30));
        badge.setStyle("-fx-background-color: " + SURFACE + ";"
                + "-fx-border-color: " + BORDER + "; -fx-border-width: 0 0 1 0;");

        badge.getChildren().addAll(
            badgeField("NAME",    name),
            badgeField("ID",      id),
            badgeField("MAJOR",   major),
            badgeField("TRACK",   track),
            badgeField("GPA",     String.valueOf(gpa)),
            badgeField("YEAR",    "Y" + year + "·S" + sem),
            colorBadge("LOAD",    loadType, maxCH + " CH max", loadType.equals("Half Load") ? DANGER : loadType.equals("Normal Load") ? GOLD : ACCENT)
        );

        VBox topBar = new VBox(0, header, badge);
        shell.setTop(topBar);

        // ── Tabs
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color: " + BG + "; -fx-tab-min-width: 200;");

        // Tab 1 – Recommended
        Tab recTab = new Tab("  ★  Semester Recommendation  ");
        recTab.setStyle("-fx-background-color: " + CARD + ";");
        recTab.setContent(buildRecommendationTab(recs, maxCH));
        tabs.getTabs().add(recTab);

        // Tab 2 – Available
        Tab availTab = new Tab("  ◎  All Available Courses  ");
        availTab.setContent(buildAvailableTab(available));
        tabs.getTabs().add(availTab);

        shell.setCenter(tabs);

        fadeIn(shell);
        primaryStage.setScene(new Scene(shell, 820, 700));
    }

    // ─── Recommendation tab ───────────────────────────────────────────────────
    private ScrollPane buildRecommendationTab(List<RecommendationBlock> recs, int maxCH) {
        VBox box = new VBox(20);
        box.setPadding(new Insets(24, 30, 30, 30));
        box.setStyle("-fx-background-color: " + BG + ";");

        if (recs.isEmpty()) {
            Label msg = new Label("No recommendations could be generated.\nCheck that your completed courses and GPA are correct.");
            msg.setTextFill(Color.web(TEXT_SEC));
            msg.setFont(Font.font("Courier New", 13));
            box.getChildren().add(msg);
        } else {
            for (RecommendationBlock block : recs) {
                // Block header
                String tagColor = block.getType().equals("FIXED") ? ACCENT : GOLD;
                HBox blockHeader = new HBox(10);
                blockHeader.setAlignment(Pos.CENTER_LEFT);

                Label tag = new Label("  " + block.getType() + "  ");
                tag.setFont(Font.font("Courier New", FontWeight.BOLD, 10));
                tag.setTextFill(Color.web(tagColor));
                tag.setStyle("-fx-background-color: " + tagColor + "22;"
                        + "-fx-border-color: " + tagColor + "; -fx-border-radius: 3;"
                        + "-fx-padding: 2 8;");

                Label msg = new Label(block.getMessage());
                msg.setFont(Font.font("Courier New", FontWeight.BOLD, 15));
                msg.setTextFill(Color.web(TEXT_PRI));

                // CH counter
                int blockCH = block.getCourses().stream().mapToInt(Course::getCreditHours).sum();
                Label chCount = new Label(blockCH + " CH");
                chCount.setFont(Font.font("Courier New", 12));
                chCount.setTextFill(Color.web(TEXT_SEC));

                Region sp = new Region();
                HBox.setHgrow(sp, Priority.ALWAYS);
                blockHeader.getChildren().addAll(tag, msg, sp, chCount);

                // Course rows
                VBox courses = new VBox(6);
                courses.setStyle("-fx-background-color: " + CARD + ";"
                        + "-fx-border-color: " + BORDER + "; -fx-border-radius: 8;"
                        + "-fx-padding: 12;");

                for (Course c : block.getCourses()) {
                    HBox row = courseRow(c, tagColor);
                    courses.getChildren().add(row);
                }

                box.getChildren().addAll(blockHeader, courses);
            }
        }

        ScrollPane sp = new ScrollPane(box);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: " + BG + "; -fx-background-color: " + BG + "; -fx-border-color: transparent;");
        return sp;
    }

    // ─── Available tab ────────────────────────────────────────────────────────
    private ScrollPane buildAvailableTab(List<Course> available) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(24, 30, 30, 30));
        box.setStyle("-fx-background-color: " + BG + ";");

        Label header = new Label(available.size() + " courses available to you");
        header.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        header.setTextFill(Color.web(TEXT_SEC));
        box.getChildren().add(header);

        if (available.isEmpty()) {
            Label none = new Label("No courses available — all prerequisites may be pending.");
            none.setTextFill(Color.web(TEXT_SEC));
            box.getChildren().add(none);
        } else {
            // Group by category
            Map<String, List<Course>> grouped = new LinkedHashMap<>();
            for (Course c : available) {
                grouped.computeIfAbsent(c.getCategory(), k -> new ArrayList<>()).add(c);
            }
            for (Map.Entry<String, List<Course>> entry : grouped.entrySet()) {
                Label catLabel = new Label("  " + entry.getKey().replace("_", " "));
                catLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 11));
                catLabel.setTextFill(Color.web(ACCENT2));
                catLabel.setStyle("-fx-background-color: " + ACCENT2 + "18;"
                        + "-fx-border-color: " + ACCENT2 + "; -fx-border-width: 0 0 0 2;"
                        + "-fx-padding: 5 10;");
                catLabel.setMaxWidth(Double.MAX_VALUE);
                box.getChildren().add(catLabel);

                VBox catBox = new VBox(5);
                catBox.setStyle("-fx-background-color: " + CARD + ";"
                        + "-fx-border-color: " + BORDER + "; -fx-border-radius: 6; -fx-padding: 10;");
                for (Course c : entry.getValue()) {
                    catBox.getChildren().add(courseRow(c, ACCENT2));
                }
                box.getChildren().add(catBox);
            }
        }

        ScrollPane sp = new ScrollPane(box);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: " + BG + "; -fx-background-color: " + BG + "; -fx-border-color: transparent;");
        return sp;
    }

    // ─── Single course row ────────────────────────────────────────────────────
    private HBox courseRow(Course c, String accentColor) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(7, 10, 7, 10));
        row.setStyle("-fx-background-color: transparent; -fx-border-radius: 5;");

        Label code = new Label(c.getCode());
        code.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
        code.setTextFill(Color.web(accentColor));
        code.setMinWidth(90);

        Label nameL = new Label(c.getName());
        nameL.setFont(Font.font("Segoe UI", 13));
        nameL.setTextFill(Color.web(TEXT_PRI));
        HBox.setHgrow(nameL, Priority.ALWAYS);

        Label ch = new Label(c.getCreditHours() + " CH");
        ch.setFont(Font.font("Courier New", 11));
        ch.setTextFill(Color.web(TEXT_SEC));
        ch.setStyle("-fx-background-color: " + SURFACE + "; -fx-padding: 2 8; -fx-border-radius: 3;");

        row.getChildren().addAll(code, nameL, ch);

        row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: " + accentColor + "15; -fx-border-radius: 5;"));
        row.setOnMouseExited(e  -> row.setStyle("-fx-background-color: transparent; -fx-border-radius: 5;"));
        return row;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DATA HELPERS
    // ══════════════════════════════════════════════════════════════════════════
    private void loadCoursesForMajor() {
        String major = ((RadioButton) majorGroup.getSelectedToggle()).getUserData().toString();
        String path  = major.equals("AI") ? "C:/Users/HP/Downloads/Abdalla/College/Advanced Programming/Project/Smart-Academic-Advisor-System - Copy/Data/AI_courses.json" : "C:/Users/HP/Downloads/Abdalla/College/Advanced Programming/Project/Smart-Academic-Advisor-System - Copy/Data/CS_courses.json";
        LoadData loader = new LoadData();
        allCourses = loader.loadCourses(path);
    }

    private List<Course> getCoursesForSemester(int year, int semester) {
        List<Course> result = new ArrayList<>();
        for (Course c : allCourses) {
            if (c.getYear() == year && c.getSemester() == semester) {
                result.add(c);
            }
        }
        return result;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  UI COMPONENT HELPERS
    // ══════════════════════════════════════════════════════════════════════════

    private Label bigTitle(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        l.setTextFill(Color.web(TEXT_PRI));
        return l;
    }

    private Label subLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Segoe UI", 14));
        l.setTextFill(Color.web(TEXT_SEC));
        l.setWrapText(true);
        return l;
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Courier New", FontWeight.BOLD, 11));
        l.setTextFill(Color.web(TEXT_SEC));
        return l;
    }

    private VBox labeledField(String label, TextField field, String prompt) {
        Label lbl = new Label(label.toUpperCase());
        lbl.setFont(Font.font("Courier New", FontWeight.BOLD, 11));
        lbl.setTextFill(Color.web(TEXT_SEC));

        field.setPromptText(prompt);
        field.setFont(Font.font("Segoe UI", 14));
        field.setMaxWidth(Double.MAX_VALUE);
        field.setStyle(
                "-fx-background-color: " + CARD + ";"
                + "-fx-border-color: " + BORDER + "; -fx-border-radius: 7;"
                + "-fx-text-fill: " + TEXT_PRI + "; -fx-prompt-text-fill: " + TEXT_SEC + ";"
                + "-fx-padding: 10 14; -fx-background-radius: 7;");

        field.focusedProperty().addListener((o, old, focused) -> {
            if (focused) field.setStyle(
                    "-fx-background-color: " + CARD + ";"
                    + "-fx-border-color: " + ACCENT2 + "; -fx-border-radius: 7;"
                    + "-fx-text-fill: " + TEXT_PRI + "; -fx-prompt-text-fill: " + TEXT_SEC + ";"
                    + "-fx-padding: 10 14; -fx-background-radius: 7;");
            else field.setStyle(
                    "-fx-background-color: " + CARD + ";"
                    + "-fx-border-color: " + BORDER + "; -fx-border-radius: 7;"
                    + "-fx-text-fill: " + TEXT_PRI + "; -fx-prompt-text-fill: " + TEXT_SEC + ";"
                    + "-fx-padding: 10 14; -fx-background-radius: 7;");
        });

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

        VBox card = new VBox(4);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(14, 20, 14, 20));
        card.setMinWidth(120);
        card.setStyle("-fx-background-color: " + CARD + "; -fx-border-color: " + BORDER + ";"
                + "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");

        Label codeLabel = new Label(code);
        codeLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        codeLabel.setTextFill(Color.web(TEXT_PRI));

        Label fullLabel = new Label(full);
        fullLabel.setFont(Font.font("Segoe UI", 11));
        fullLabel.setTextFill(Color.web(TEXT_SEC));

        rb.setStyle("-fx-text-fill: transparent; -fx-padding: 0; -fx-background-color: transparent;");
        card.getChildren().addAll(codeLabel, fullLabel, rb);

        // Hover & selection highlight
        Runnable updateStyle = () -> {
            boolean selected = rb.isSelected();
            String border = selected ? ACCENT2 : BORDER;
            String bg     = selected ? ACCENT2 + "18" : CARD;
            card.setStyle("-fx-background-color: " + bg + "; -fx-border-color: " + border + ";"
                    + "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
            codeLabel.setTextFill(Color.web(selected ? ACCENT2 : TEXT_PRI));
        };

        rb.selectedProperty().addListener((o, old, nw) -> updateStyle.run());
        card.setOnMouseClicked(e -> { rb.setSelected(true); updateStyle.run(); });
        card.setOnMouseEntered(e -> {
            if (!rb.isSelected())
                card.setStyle("-fx-background-color: " + SURFACE + "; -fx-border-color: " + ACCENT2 + "77;"
                        + "-fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
        });
        card.setOnMouseExited(e -> updateStyle.run());

        return card;
    }

    private Button nextBtn(String label) {
        Button b = new Button(label);
        b.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        b.setTextFill(Color.web("#FFFFFF"));
        b.setStyle("-fx-background-color: " + ACCENT2 + "; -fx-background-radius: 8;"
                + "-fx-padding: 10 28; -fx-cursor: hand;");
        b.setOnMouseEntered(e -> b.setStyle(
                "-fx-background-color: #5299e0; -fx-background-radius: 8; -fx-padding: 10 28; -fx-cursor: hand;"));
        b.setOnMouseExited(e -> b.setStyle(
                "-fx-background-color: " + ACCENT2 + "; -fx-background-radius: 8; -fx-padding: 10 28; -fx-cursor: hand;"));
        return b;
    }

    private Button backBtn() {
        Button b = new Button("← Back");
        b.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        b.setTextFill(Color.web(TEXT_SEC));
        b.setStyle("-fx-background-color: transparent; -fx-border-color: " + BORDER + ";"
                + "-fx-border-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;");
        b.setOnMouseEntered(e -> b.setStyle(
                "-fx-background-color: " + SURFACE + "; -fx-border-color: " + TEXT_SEC + ";"
                + "-fx-border-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;"));
        b.setOnMouseExited(e -> b.setStyle(
                "-fx-background-color: transparent; -fx-border-color: " + BORDER + ";"
                + "-fx-border-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;"));
        return b;
    }

    private void styleCheckBox(CheckBox cb) {
        cb.setStyle("-fx-text-fill: " + TEXT_PRI + "; -fx-cursor: hand;");
    }

    private HBox badgeField(String label, String value) {
        VBox v = new VBox(2);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Courier New", 9));
        lbl.setTextFill(Color.web(TEXT_SEC));
        Label val = new Label(value);
        val.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        val.setTextFill(Color.web(TEXT_PRI));
        v.getChildren().addAll(lbl, val);
        return new HBox(v);
    }

    private HBox colorBadge(String label, String value, String sub, String color) {
        VBox v = new VBox(2);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Courier New", 9));
        lbl.setTextFill(Color.web(TEXT_SEC));
        Label val = new Label(value);
        val.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
        val.setTextFill(Color.web(color));
        Label s = new Label(sub);
        s.setFont(Font.font("Courier New", 10));
        s.setTextFill(Color.web(color));
        v.getChildren().addAll(lbl, val, s);
        return new HBox(v);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ANIMATION & UTILITY
    // ══════════════════════════════════════════════════════════════════════════
    private void fadeIn(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(300), node);
        ft.setFromValue(0); ft.setToValue(1);
        ft.play();
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), node);
        tt.setFromY(20); tt.setToY(0);
        tt.play();
    }

    private void shakeNode(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(60), node);
        tt.setFromX(0); tt.setByX(8);
        tt.setCycleCount(6); tt.setAutoReverse(true);
        tt.play();
    }

    private void showInlineError(VBox container, String msg) {
        container.getChildren().removeIf(n -> "error".equals(n.getUserData()));
        Label err = new Label("⚠  " + msg);
        err.setUserData("error");
        err.setFont(Font.font("Courier New", 12));
        err.setTextFill(Color.web(DANGER));
        err.setStyle("-fx-background-color: " + DANGER + "18; -fx-border-color: " + DANGER + ";"
                + "-fx-border-radius: 5; -fx-padding: 6 12;");
        container.getChildren().add(err);
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> container.getChildren().remove(err));
        pause.play();
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle("Error");
        a.showAndWait();
    }

    private String ordinal(int n) {
        return switch (n) { case 1 -> "1st"; case 2 -> "2nd"; case 3 -> "3rd"; default -> n + "th"; };
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MAIN
    // ══════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        launch();
    }
}

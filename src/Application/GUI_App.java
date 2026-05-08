package Application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GUI_App extends Application {

    @Override
    public void start(Stage stage) {

        Label title = new Label("SMART ACADEMIC ADVISOR SYSTEM");

        Label text = new Label("JavaFX is Working Successfully!");

        VBox root = new VBox();

        root.setSpacing(20);

        root.getChildren().addAll(title, text);

        Scene scene = new Scene(root, 600, 300);

        stage.setTitle("Smart Academic Advisor");

        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {

        launch();
    }
}

//how to run
//first u have to wite in terminal    cd Smart-Academic-Advisor-System
//then complie 1
// javac --module-path "D:/javafx-sdk-17.0.19/lib" --add-modules
// javafx.controls,javafx.fxml -d bin src/Application/GUI_App.java
// then run
// & "C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot\bin\java.exe"
// --module-path "D:/javafx-sdk-17.0.19/lib" --add-modules
// javafx.controls,javafx.fxml -cp bin Application.GUI_App
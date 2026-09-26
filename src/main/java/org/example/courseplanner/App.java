package org.example.courseplanner;

import org.example.courseplanner.view.PrerequisiteView;
import org.example.courseplanner.view.CourseView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.example.courseplanner.database.DatabaseConnection;



public class App extends Application {

    private Label centerLabel;

    @Override
    public void start(Stage primaryStage) {
        DatabaseConnection.initializeDatabase();


        BorderPane root = new BorderPane();

        // Top section: application header
        Label headerLabel = new Label("Course Prerequisite Planner");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        VBox topBox = new VBox(headerLabel);
        topBox.setPadding(new Insets(15));
        root.setTop(topBox);

        // Left section: navigation menu with buttons
        Button coursesButton = new Button("Courses");
        Button prerequisitesButton = new Button("Prerequisites");
        Button planningButton = new Button("Course Planning");
        Button progressButton = new Button("Student Progress");

        coursesButton.setMaxWidth(Double.MAX_VALUE);
        prerequisitesButton.setMaxWidth(Double.MAX_VALUE);
        planningButton.setMaxWidth(Double.MAX_VALUE);
        progressButton.setMaxWidth(Double.MAX_VALUE);

        coursesButton.setOnAction(e -> root.setCenter(new CourseView()));
        prerequisitesButton.setOnAction(e -> root.setCenter(new PrerequisiteView()));
        planningButton.setOnAction(e -> centerLabel.setText("Course Planning section selected"));
        progressButton.setOnAction(e -> centerLabel.setText("Student Progress section selected"));

        VBox leftBox = new VBox(10, coursesButton, prerequisitesButton, planningButton, progressButton);
        leftBox.setPadding(new Insets(15));
        leftBox.setPrefWidth(180);
        leftBox.prefHeightProperty().bind(root.heightProperty());
        root.setLeft(leftBox);

        // Center section: main content area
        centerLabel = new Label("Main Content Area");
        VBox centerBox = new VBox(centerLabel);
        centerBox.setPadding(new Insets(15));
        centerBox.prefWidthProperty().bind(root.widthProperty().subtract(leftBox.getPrefWidth()));
        centerBox.prefHeightProperty().bind(root.heightProperty());
        root.setCenter(centerBox);

        Scene scene = new Scene(root, 800, 500);

        primaryStage.setTitle("Course Prerequisite Planner");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
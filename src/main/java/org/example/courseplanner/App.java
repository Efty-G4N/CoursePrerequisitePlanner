package org.example.courseplanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label welcomeLabel = new Label("Course Prerequisite Planner - Setup Successful!");

        StackPane root = new StackPane();
        root.getChildren().add(welcomeLabel);

        Scene scene = new Scene(root, 500, 300);

        primaryStage.setTitle("Course Prerequisite Planner");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
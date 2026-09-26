package org.example.courseplanner.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.courseplanner.dao.CourseDAO;
import org.example.courseplanner.graph.BreadthFirstSearch;
import org.example.courseplanner.graph.CourseGraph;
import org.example.courseplanner.model.Course;

import java.util.List;

public class PlanningView extends VBox {

    private final CourseDAO courseDAO;
    private List<Course> allCourses;

    private final ComboBox<Course> targetCourseComboBox;
    private final ListView<String> directPrerequisitesList;
    private final ListView<String> completePathList;

    public PlanningView() {
        courseDAO = new CourseDAO();

        targetCourseComboBox = new ComboBox<>();
        directPrerequisitesList = new ListView<>();
        completePathList = new ListView<>();

        loadCourseOptions();

        GridPane formGrid = buildForm();

        Label directLabel = new Label("Direct Prerequisites:");
        Label completeLabel = new Label("Complete Prerequisite Path:");

        VBox resultsBox = new VBox(10, directLabel, directPrerequisitesList, completeLabel, completePathList);

        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.getChildren().addAll(formGrid, resultsBox);
    }

    private GridPane buildForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 0, 10, 0));

        Button findButton = new Button("Find Prerequisites");
        findButton.setOnAction(e -> handleFindPrerequisites());

        HBox row = new HBox(10, targetCourseComboBox, findButton);

        grid.add(new Label("Target Course:"), 0, 0);
        grid.add(row, 1, 0);

        return grid;
    }

    private void loadCourseOptions() {
        allCourses = courseDAO.getAllCourses();
        ObservableList<Course> courseOptions = FXCollections.observableArrayList(allCourses);
        targetCourseComboBox.setItems(courseOptions);
    }

    private void handleFindPrerequisites() {
        Course targetCourse = targetCourseComboBox.getValue();

        if (targetCourse == null) {
            showAlert("Selection Error", "Please select a target course.");
            return;
        }

        CourseGraph courseGraph = new CourseGraph();

        // Direct prerequisites: one step back in the reverse graph
        List<Integer> directIds = courseGraph.getDirectPrerequisites(targetCourse.getId());
        ObservableList<String> directDisplay = FXCollections.observableArrayList();
        for (int id : directIds) {
            directDisplay.add(findCourseCode(id));
        }
        directPrerequisitesList.setItems(directDisplay);

        // Complete prerequisite path: full traversal using BFS on the reverse graph
        BreadthFirstSearch bfs = new BreadthFirstSearch(courseGraph.getReverseAdjacencyList());
        List<Integer> fullPathIds = bfs.traverse(targetCourse.getId());

        ObservableList<String> completeDisplay = FXCollections.observableArrayList();
        for (int id : fullPathIds) {
            if (id != targetCourse.getId()) {
                completeDisplay.add(findCourseCode(id));
            }
        }
        completePathList.setItems(completeDisplay);

        if (completeDisplay.isEmpty()) {
            completeDisplay.add("No prerequisites required.");
            completePathList.setItems(completeDisplay);
        }
    }

    private String findCourseCode(int courseId) {
        for (Course c : allCourses) {
            if (c.getId() == courseId) {
                return c.getCourseCode() + " - " + c.getCourseName();
            }
        }
        return "Unknown course";
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
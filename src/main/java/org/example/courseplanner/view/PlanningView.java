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
import org.example.courseplanner.graph.TopologicalSort;
import org.example.courseplanner.model.Course;

import java.util.ArrayList;
import java.util.List;

public class PlanningView extends VBox {

    private final CourseDAO courseDAO;
    private List<Course> allCourses;

    private final ComboBox<Course> targetCourseComboBox;
    private final ListView<String> directPrerequisitesList;
    private final ListView<String> completePathList;
    private final ListView<String> courseOrderList;
    private final Label orderStatusLabel;

    public PlanningView() {
        courseDAO = new CourseDAO();

        targetCourseComboBox = new ComboBox<>();
        directPrerequisitesList = new ListView<>();
        completePathList = new ListView<>();
        courseOrderList = new ListView<>();
        orderStatusLabel = new Label();

        loadCourseOptions();

        GridPane formGrid = buildForm();

        Label directLabel = new Label("Direct Prerequisites:");
        Label completeLabel = new Label("Complete Prerequisite Path:");
        Label orderLabel = new Label("Suggested Full Course Order:");

        Button generateOrderButton = new Button("Generate Full Course Order");
        generateOrderButton.setOnAction(e -> handleGenerateOrder());

        VBox resultsBox = new VBox(10,
                directLabel, directPrerequisitesList,
                completeLabel, completePathList,
                generateOrderButton, orderStatusLabel,
                orderLabel, courseOrderList);

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

        List<Integer> directIds = courseGraph.getDirectPrerequisites(targetCourse.getId());
        ObservableList<String> directDisplay = FXCollections.observableArrayList();
        for (int id : directIds) {
            directDisplay.add(findCourseCode(id));
        }
        directPrerequisitesList.setItems(directDisplay);

        BreadthFirstSearch bfs = new BreadthFirstSearch(courseGraph.getReverseAdjacencyList());
        List<Integer> fullPathIds = bfs.traverse(targetCourse.getId());

        ObservableList<String> completeDisplay = FXCollections.observableArrayList();
        for (int id : fullPathIds) {
            if (id != targetCourse.getId()) {
                completeDisplay.add(findCourseCode(id));
            }
        }
        if (completeDisplay.isEmpty()) {
            completeDisplay.add("No prerequisites required.");
        }
        completePathList.setItems(completeDisplay);
    }

    private void handleGenerateOrder() {
        loadCourseOptions(); // refresh in case courses changed
        CourseGraph courseGraph = new CourseGraph();

        List<Integer> allCourseIds = new ArrayList<>();
        for (Course c : allCourses) {
            allCourseIds.add(c.getId());
        }

        TopologicalSort topologicalSort = new TopologicalSort();
        List<Integer> order = topologicalSort.sort(courseGraph.getAdjacencyList(), allCourseIds);

        ObservableList<String> orderDisplay = FXCollections.observableArrayList();
        for (int id : order) {
            orderDisplay.add(findCourseCode(id));
        }
        courseOrderList.setItems(orderDisplay);

        if (order.size() < allCourseIds.size()) {
            orderStatusLabel.setText("Warning: a cycle was detected. A complete valid order is not possible.");
            orderStatusLabel.setStyle("-fx-text-fill: red;");
        } else {
            orderStatusLabel.setText("A valid course order was generated successfully.");
            orderStatusLabel.setStyle("-fx-text-fill: green;");
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
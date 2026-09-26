package org.example.courseplanner.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import org.example.courseplanner.dao.CourseDAO;
import org.example.courseplanner.dao.PrerequisiteDAO;
import org.example.courseplanner.graph.CourseGraph;
import org.example.courseplanner.graph.CycleDetector;
import org.example.courseplanner.model.Course;
import org.example.courseplanner.model.Prerequisite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrerequisiteView extends VBox {

    private final CourseDAO courseDAO;
    private final PrerequisiteDAO prerequisiteDAO;

    private final ComboBox<Course> courseComboBox;
    private final ComboBox<Course> prerequisiteComboBox;
    private final TableView<PrerequisiteDisplay> tableView;
    private final Label cycleStatusLabel;

    private List<Course> allCourses;

    public PrerequisiteView() {
        courseDAO = new CourseDAO();
        prerequisiteDAO = new PrerequisiteDAO();

        courseComboBox = new ComboBox<>();
        prerequisiteComboBox = new ComboBox<>();
        tableView = new TableView<>();
        cycleStatusLabel = new Label();

        loadCourseOptions();
        setupTable();
        loadPrerequisiteData();

        GridPane formGrid = buildForm();

        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.getChildren().addAll(formGrid, cycleStatusLabel, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);
    }

    private GridPane buildForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 0, 10, 0));

        Button addButton = new Button("Add Prerequisite");
        addButton.setOnAction(e -> handleAddPrerequisite());

        Button removeButton = new Button("Remove Selected");
        removeButton.setOnAction(e -> handleRemovePrerequisite());

        Button checkCyclesButton = new Button("Check Entire Graph for Cycles");
        checkCyclesButton.setOnAction(e -> handleCheckCycles());

        HBox buttonBox = new HBox(10, addButton, removeButton, checkCyclesButton);

        grid.add(new Label("Course:"), 0, 0);
        grid.add(courseComboBox, 1, 0);
        grid.add(new Label("Requires Prerequisite:"), 0, 1);
        grid.add(prerequisiteComboBox, 1, 1);
        grid.add(buttonBox, 1, 2);

        return grid;
    }

    private void loadCourseOptions() {
        allCourses = courseDAO.getAllCourses();
        ObservableList<Course> courseOptions = FXCollections.observableArrayList(allCourses);
        courseComboBox.setItems(courseOptions);
        prerequisiteComboBox.setItems(courseOptions);
    }

    private void setupTable() {
        TableColumn<PrerequisiteDisplay, String> courseColumn = new TableColumn<>("Course");
        courseColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().courseText()));

        TableColumn<PrerequisiteDisplay, String> prerequisiteColumn = new TableColumn<>("Requires");
        prerequisiteColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().prerequisiteText()));

        tableView.getColumns().add(courseColumn);
        tableView.getColumns().add(prerequisiteColumn);
    }

    private void loadPrerequisiteData() {
        List<Prerequisite> prerequisites = prerequisiteDAO.getAllPrerequisites();
        ObservableList<PrerequisiteDisplay> displayList = FXCollections.observableArrayList();

        for (Prerequisite p : prerequisites) {
            String courseText = findCourseCode(p.getCourseId());
            String prerequisiteText = findCourseCode(p.getPrerequisiteCourseId());
            displayList.add(new PrerequisiteDisplay(p.getId(), courseText, prerequisiteText));
        }

        tableView.setItems(displayList);
    }

    private String findCourseCode(int courseId) {
        for (Course c : allCourses) {
            if (c.getId() == courseId) {
                return c.getCourseCode();
            }
        }
        return "Unknown";
    }

    private void handleAddPrerequisite() {
        Course selectedCourse = courseComboBox.getValue();
        Course selectedPrerequisite = prerequisiteComboBox.getValue();

        if (selectedCourse == null || selectedPrerequisite == null) {
            showAlert("Selection Error", "Please select both a course and its prerequisite.");
            return;
        }

        if (selectedCourse.getId() == selectedPrerequisite.getId()) {
            showAlert("Invalid Selection", "A course cannot be its own prerequisite.");
            return;
        }

        // Simulate adding this edge on a copy of the graph to check for a cycle
        // BEFORE actually writing it to the database.
        CourseGraph courseGraph = new CourseGraph();
        Map<Integer, List<Integer>> testGraph = copyAdjacencyList(courseGraph.getAdjacencyList());
        testGraph.putIfAbsent(selectedPrerequisite.getId(), new ArrayList<>());
        testGraph.get(selectedPrerequisite.getId()).add(selectedCourse.getId());

        CycleDetector cycleDetector = new CycleDetector(testGraph);
        if (cycleDetector.hasCycle()) {
            String cycleDescription = describeCycle(cycleDetector.getCyclePath());
            showAlert("Cycle Detected",
                    "Adding this prerequisite would create a circular dependency:\n" + cycleDescription);
            return;
        }

        prerequisiteDAO.addPrerequisite(selectedCourse.getId(), selectedPrerequisite.getId());

        courseComboBox.setValue(null);
        prerequisiteComboBox.setValue(null);
        loadPrerequisiteData();
    }

    private Map<Integer, List<Integer>> copyAdjacencyList(Map<Integer, List<Integer>> original) {
        Map<Integer, List<Integer>> copy = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : original.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }

    private String describeCycle(List<Integer> cycleIds) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cycleIds.size(); i++) {
            sb.append(findCourseCode(cycleIds.get(i)));
            if (i < cycleIds.size() - 1) {
                sb.append(" -> ");
            }
        }
        return sb.toString();
    }

    private void handleCheckCycles() {
        CourseGraph courseGraph = new CourseGraph();
        CycleDetector cycleDetector = new CycleDetector(courseGraph.getAdjacencyList());

        if (cycleDetector.hasCycle()) {
            String cycleDescription = describeCycle(cycleDetector.getCyclePath());
            cycleStatusLabel.setText("Cycle detected: " + cycleDescription);
            cycleStatusLabel.setStyle("-fx-text-fill: red;");
        } else {
            cycleStatusLabel.setText("No cycles detected. The prerequisite graph is valid.");
            cycleStatusLabel.setStyle("-fx-text-fill: green;");
        }
    }

    private void handleRemovePrerequisite() {
        PrerequisiteDisplay selected = tableView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Selection Error", "Please select a prerequisite relationship to remove.");
            return;
        }

        prerequisiteDAO.deletePrerequisite(selected.id());
        loadPrerequisiteData();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private record PrerequisiteDisplay(int id, String courseText, String prerequisiteText) {
    }
}
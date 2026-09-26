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
import org.example.courseplanner.dao.CourseDAO;
import org.example.courseplanner.dao.PrerequisiteDAO;
import org.example.courseplanner.model.Course;
import org.example.courseplanner.model.Prerequisite;

import java.util.List;

public class PrerequisiteView extends VBox {

    private final CourseDAO courseDAO;
    private final PrerequisiteDAO prerequisiteDAO;

    private final ComboBox<Course> courseComboBox;
    private final ComboBox<Course> prerequisiteComboBox;
    private final TableView<PrerequisiteDisplay> tableView;

    private List<Course> allCourses;

    public PrerequisiteView() {
        courseDAO = new CourseDAO();
        prerequisiteDAO = new PrerequisiteDAO();

        courseComboBox = new ComboBox<>();
        prerequisiteComboBox = new ComboBox<>();
        tableView = new TableView<>();

        loadCourseOptions();
        setupTable();
        loadPrerequisiteData();

        GridPane formGrid = buildForm();

        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.getChildren().addAll(formGrid, tableView);
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

        HBox buttonBox = new HBox(10, addButton, removeButton);

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

        prerequisiteDAO.addPrerequisite(selectedCourse.getId(), selectedPrerequisite.getId());

        courseComboBox.setValue(null);
        prerequisiteComboBox.setValue(null);
        loadPrerequisiteData();
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

    // A small helper record to hold human-readable table row data
    private record PrerequisiteDisplay(int id, String courseText, String prerequisiteText) {
    }
}
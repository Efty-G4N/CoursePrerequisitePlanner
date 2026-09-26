package org.example.courseplanner.view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.example.courseplanner.dao.CourseDAO;
import org.example.courseplanner.model.Course;

public class CourseView extends VBox {

    private final CourseDAO courseDAO;
    private final TableView<Course> tableView;

    private final TextField codeField;
    private final TextField nameField;
    private final TextField creditsField;

    public CourseView() {
        courseDAO = new CourseDAO();
        tableView = new TableView<>();

        codeField = new TextField();
        codeField.setPromptText("Course Code");

        nameField = new TextField();
        nameField.setPromptText("Course Name");

        creditsField = new TextField();
        creditsField.setPromptText("Credits");

        setupTable();
        loadCourseData();

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

        Button addButton = new Button("Add Course");
        addButton.setOnAction(e -> handleAddCourse());

        grid.add(new Label("Course Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Course Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Credits:"), 0, 2);
        grid.add(creditsField, 1, 2);
        grid.add(addButton, 1, 3);

        return grid;
    }

    private void setupTable() {
        TableColumn<Course, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()).asObject());

        TableColumn<Course, String> codeColumn = new TableColumn<>("Course Code");
        codeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCourseCode()));

        TableColumn<Course, String> nameColumn = new TableColumn<>("Course Name");
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCourseName()));

        TableColumn<Course, Integer> creditsColumn = new TableColumn<>("Credits");
        creditsColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCredits()).asObject());

        tableView.getColumns().add(idColumn);
        tableView.getColumns().add(codeColumn);
        tableView.getColumns().add(nameColumn);
        tableView.getColumns().add(creditsColumn);
    }

    private void loadCourseData() {
        ObservableList<Course> courseList = FXCollections.observableArrayList(courseDAO.getAllCourses());
        tableView.setItems(courseList);
    }

    private void handleAddCourse() {
        String code = codeField.getText().trim();
        String name = nameField.getText().trim();
        String creditsText = creditsField.getText().trim();

        if (code.isEmpty() || name.isEmpty() || creditsText.isEmpty()) {
            showAlert("Input Error", "All fields must be filled in.");
            return;
        }

        int credits;
        try {
            credits = Integer.parseInt(creditsText);
        } catch (NumberFormatException ex) {
            showAlert("Input Error", "Credits must be a valid number.");
            return;
        }

        Course newCourse = new Course(0, code, name, credits);
        courseDAO.addCourse(newCourse);

        codeField.clear();
        nameField.clear();
        creditsField.clear();

        loadCourseData();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
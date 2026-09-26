package org.example.courseplanner.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.courseplanner.dao.CompletedCourseDAO;
import org.example.courseplanner.dao.CourseDAO;
import org.example.courseplanner.dao.StudentDAO;
import org.example.courseplanner.graph.CourseGraph;
import org.example.courseplanner.model.Course;
import org.example.courseplanner.model.Student;

import java.util.ArrayList;
import java.util.List;

public class ProgressView extends VBox {

    private final CourseDAO courseDAO;
    private final StudentDAO studentDAO;
    private final CompletedCourseDAO completedCourseDAO;

    private List<Course> allCourses;
    private final ComboBox<Student> studentComboBox;
    private final TextField studentCodeField;
    private final TextField studentNameField;

    private final ListView<Course> notCompletedList;
    private final ListView<Course> completedList;
    private final ListView<String> availableCoursesList;

    public ProgressView() {
        courseDAO = new CourseDAO();
        studentDAO = new StudentDAO();
        completedCourseDAO = new CompletedCourseDAO();

        studentComboBox = new ComboBox<>();
        studentCodeField = new TextField();
        studentCodeField.setPromptText("Roll (e.g., 2307001)");
        studentNameField = new TextField();
        studentNameField.setPromptText("Name");

        notCompletedList = new ListView<>();
        notCompletedList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        completedList = new ListView<>();
        completedList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        availableCoursesList = new ListView<>();

        allCourses = courseDAO.getAllCourses();
        loadStudentOptions();

        GridPane studentForm = buildStudentForm();

        Button markButton = new Button("Mark as Completed ->");
        markButton.setOnAction(e -> handleMarkCompleted());

        Button unmarkButton = new Button("<- Unmark");
        unmarkButton.setOnAction(e -> handleUnmarkCompleted());

        VBox markButtonsBox = new VBox(10, markButton, unmarkButton);

        Label notCompletedLabel = new Label("Not Completed:");
        Label completedLabel = new Label("Completed Courses:");

        VBox notCompletedBox = new VBox(5, notCompletedLabel, notCompletedList);
        VBox completedBox = new VBox(5, completedLabel, completedList);

        HBox listsRow = new HBox(10, notCompletedBox, markButtonsBox, completedBox);

        Button showAvailableButton = new Button("Show Available Courses");
        showAvailableButton.setOnAction(e -> handleShowAvailable());

        Label availableLabel = new Label("Available Courses (prerequisites met):");

        VBox availableBox = new VBox(10, showAvailableButton, availableLabel, availableCoursesList);

        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.getChildren().addAll(studentForm, listsRow, availableBox);
    }

    private GridPane buildStudentForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 0, 10, 0));

        Button addStudentButton = new Button("Add Student");
        addStudentButton.setOnAction(e -> handleAddStudent());

        Button loadButton = new Button("Load Progress");
        loadButton.setOnAction(e -> loadCourseLists());

        HBox addRow = new HBox(10, studentCodeField, studentNameField, addStudentButton);

        grid.add(new Label("Select Student:"), 0, 0);
        grid.add(studentComboBox, 1, 0);
        grid.add(loadButton, 2, 0);
        grid.add(new Label("New Student:"), 0, 1);
        grid.add(addRow, 1, 1);

        return grid;
    }

    private void loadStudentOptions() {
        List<Student> students = studentDAO.getAllStudents();
        ObservableList<Student> studentOptions = FXCollections.observableArrayList(students);
        studentComboBox.setItems(studentOptions);
    }

    private void handleAddStudent() {
        String code = studentCodeField.getText().trim();
        String name = studentNameField.getText().trim();

        if (code.isEmpty() || name.isEmpty()) {
            showAlert("Input Error", "Please enter both student code and name.");
            return;
        }

        studentDAO.addStudent(new Student(0, name, code));

        studentCodeField.clear();
        studentNameField.clear();
        loadStudentOptions();
    }

    private void loadCourseLists() {
        Student selectedStudent = studentComboBox.getValue();

        if (selectedStudent == null) {
            showAlert("Selection Error", "Please select a student first.");
            return;
        }

        allCourses = courseDAO.getAllCourses();
        List<Integer> completedIds = completedCourseDAO.getCompletedCourseIds(selectedStudent.getId());

        ObservableList<Course> notCompleted = FXCollections.observableArrayList();
        ObservableList<Course> completed = FXCollections.observableArrayList();

        for (Course c : allCourses) {
            if (completedIds.contains(c.getId())) {
                completed.add(c);
            } else {
                notCompleted.add(c);
            }
        }

        notCompletedList.setItems(notCompleted);
        completedList.setItems(completed);
        availableCoursesList.getItems().clear();
    }

    private void handleMarkCompleted() {
        Student selectedStudent = studentComboBox.getValue();
        if (selectedStudent == null) {
            showAlert("Selection Error", "Please select a student first.");
            return;
        }

        List<Course> selectedCourses = new ArrayList<>(notCompletedList.getSelectionModel().getSelectedItems());
        if (selectedCourses.isEmpty()) {
            showAlert("Selection Error", "Please select at least one course from 'Not Completed'.");
            return;
        }

        for (Course c : selectedCourses) {
            completedCourseDAO.markCompleted(selectedStudent.getId(), c.getId());
        }

        loadCourseLists();
    }

    private void handleUnmarkCompleted() {
        Student selectedStudent = studentComboBox.getValue();
        if (selectedStudent == null) {
            showAlert("Selection Error", "Please select a student first.");
            return;
        }

        List<Course> selectedCourses = new ArrayList<>(completedList.getSelectionModel().getSelectedItems());
        if (selectedCourses.isEmpty()) {
            showAlert("Selection Error", "Please select at least one course from 'Completed'.");
            return;
        }

        for (Course c : selectedCourses) {
            completedCourseDAO.unmarkCompleted(selectedStudent.getId(), c.getId());
        }

        loadCourseLists();
    }

    private void handleShowAvailable() {
        Student selectedStudent = studentComboBox.getValue();
        if (selectedStudent == null) {
            showAlert("Selection Error", "Please select a student first.");
            return;
        }

        List<Integer> completedIds = completedCourseDAO.getCompletedCourseIds(selectedStudent.getId());
        CourseGraph courseGraph = new CourseGraph();

        ObservableList<String> availableDisplay = FXCollections.observableArrayList();

        for (Course c : allCourses) {
            if (completedIds.contains(c.getId())) {
                continue; // already completed, skip
            }

            List<Integer> directPrereqs = courseGraph.getDirectPrerequisites(c.getId());
            if (completedIds.containsAll(directPrereqs)) {
                availableDisplay.add(c.getCourseCode() + " - " + c.getCourseName());
            }
        }

        if (availableDisplay.isEmpty()) {
            availableDisplay.add("No courses currently available.");
        }

        availableCoursesList.setItems(availableDisplay);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
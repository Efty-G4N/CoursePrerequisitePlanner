package org.example.courseplanner.view;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.courseplanner.concurrency.AppExecutor;
import org.example.courseplanner.dao.CourseDAO;
import org.example.courseplanner.model.Course;
import org.example.courseplanner.api.BookApiService;
import org.example.courseplanner.api.BookDoc;
import org.example.courseplanner.api.BookSearchResult;

import java.util.List;

public class CourseView extends VBox {

    private final CourseDAO courseDAO;
    private final TableView<Course> tableView;
    private final ProgressIndicator progressIndicator;
    private final BookApiService bookApiService;
    private final Button findBookButton;
    private final ListView<String> bookResultsList;
    private final ProgressIndicator bookProgressIndicator;

    private final TextField codeField;
    private final TextField nameField;
    private final TextField creditsField;

    private Course selectedCourse;

    public CourseView() {
        courseDAO = new CourseDAO();
        tableView = new TableView<>();

        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        bookApiService = new BookApiService();

        findBookButton = new Button("Find Reference Book");
        findBookButton.setOnAction(e -> handleFindReferenceBook());

        bookResultsList = new ListView<>();

        bookProgressIndicator = new ProgressIndicator();
        bookProgressIndicator.setVisible(false);

        codeField = new TextField();
        codeField.setPromptText("Course Code");

        nameField = new TextField();
        nameField.setPromptText("Course Name");

        creditsField = new TextField();
        creditsField.setPromptText("Credits");

        setupTable();
        loadCourseData();
        setupRowSelectionListener();

        GridPane formGrid = buildForm();

        Label bookSectionLabel = new Label("Reference Book (select a course above, then click Find):");
        HBox bookButtonBox = new HBox(10, findBookButton, bookProgressIndicator);
        VBox bookSection = new VBox(5, bookSectionLabel, bookButtonBox, bookResultsList);

        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.getChildren().addAll(formGrid, progressIndicator, tableView, bookSection);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        VBox.setVgrow(bookResultsList, Priority.ALWAYS);
    }

    private GridPane buildForm() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 0, 10, 0));

        Button addButton = new Button("Add Course");
        addButton.setOnAction(e -> handleAddCourse());

        Button updateButton = new Button("Update Course");
        updateButton.setOnAction(e -> handleUpdateCourse());

        Button deleteButton = new Button("Delete Course");
        deleteButton.setOnAction(e -> handleDeleteCourse());

        Button clearButton = new Button("Clear Form");
        clearButton.setOnAction(e -> clearForm());

        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton, clearButton);

        grid.add(new Label("Course Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Course Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Credits:"), 0, 2);
        grid.add(creditsField, 1, 2);
        grid.add(buttonBox, 1, 3);

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

        TableColumn<Course, Double> creditsColumn = new TableColumn<>("Credits");
        creditsColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getCredits()).asObject());

        tableView.getColumns().add(idColumn);
        tableView.getColumns().add(codeColumn);
        tableView.getColumns().add(nameColumn);
        tableView.getColumns().add(creditsColumn);

        // make columns share the table's full width proportionally,
        // instead of leaving empty space on the right
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        idColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.10));
        codeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.20));
        nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.50));
        creditsColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.20));
    }

    private void setupRowSelectionListener() {
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedCourse = newValue;
                codeField.setText(newValue.getCourseCode());
                nameField.setText(newValue.getCourseName());
                creditsField.setText(String.valueOf(newValue.getCredits()));
            }
        });
    }

    private void loadCourseData() {
        progressIndicator.setVisible(true);

        Task<List<Course>> loadCoursesTask = new Task<>() {
            @Override
            protected List<Course> call() {
                return courseDAO.getAllCourses();
            }
        };

        loadCoursesTask.setOnSucceeded(event -> {
            // this runs back on the JavaFX thread, safe to update UI here
            List<Course> courses = loadCoursesTask.getValue();
            ObservableList<Course> courseList = FXCollections.observableArrayList(courses);
            tableView.setItems(courseList);
            progressIndicator.setVisible(false);
        });

        loadCoursesTask.setOnFailed(event -> {
            progressIndicator.setVisible(false);
            showAlert("Load Error", "Failed to load courses: " + loadCoursesTask.getException().getMessage());
        });

        AppExecutor.getExecutorService().submit(loadCoursesTask);
    }

    private void handleFindReferenceBook() {
        if (selectedCourse == null) {
            showAlert("Selection Error", "Please select a course from the table first.");
            return;
        }

        String courseName = selectedCourse.getCourseName();
        bookResultsList.getItems().clear();
        bookProgressIndicator.setVisible(true);

        Task<BookSearchResult> searchTask = new Task<>() {
            @Override
            protected BookSearchResult call() throws Exception {
                // this runs on a background thread — the real network call happens here
                return bookApiService.searchBooks(courseName);
            }
        };

        searchTask.setOnSucceeded(event -> {
            BookSearchResult result = searchTask.getValue();
            bookProgressIndicator.setVisible(false);

            if (result.getDocs() == null || result.getDocs().isEmpty()) {
                bookResultsList.getItems().add("No books found for: " + courseName);
                return;
            }

            for (BookDoc doc : result.getDocs()) {
                bookResultsList.getItems().add(doc.toDisplayString());
            }
        });

        searchTask.setOnFailed(event -> {
            bookProgressIndicator.setVisible(false);
            showAlert("Network Error", "Failed to fetch book data: " + searchTask.getException().getMessage());
        });

        AppExecutor.getExecutorService().submit(searchTask);
    }

    private void handleAddCourse() {
        String code = codeField.getText().trim();
        String name = nameField.getText().trim();
        String creditsText = creditsField.getText().trim();

        if (code.isEmpty() || name.isEmpty() || creditsText.isEmpty()) {
            showAlert("Input Error", "All fields must be filled in.");
            return;
        }

        double credits;
        try {
            credits = Double.parseDouble(creditsText);
        } catch (NumberFormatException ex) {
            showAlert("Input Error", "Credits must be a valid number (e.g., 3 or 1.5).");
            return;
        }

        Course newCourse = new Course(0, code, name, credits);
        boolean success = courseDAO.addCourse(newCourse);

        if (!success) {
            showAlert("Add Error",
                    "Could not add the course. A course with code '" + code + "' may already exist.");
            return;
        }

        clearForm();
        loadCourseData();
    }

    private void handleUpdateCourse() {
        if (selectedCourse == null) {
            showAlert("Selection Error", "Please select a course from the table to update.");
            return;
        }

        String code = codeField.getText().trim();
        String name = nameField.getText().trim();
        String creditsText = creditsField.getText().trim();

        if (code.isEmpty() || name.isEmpty() || creditsText.isEmpty()) {
            showAlert("Input Error", "All fields must be filled in.");
            return;
        }

        double credits;
        try {
            credits = Double.parseDouble(creditsText);
        } catch (NumberFormatException ex) {
            showAlert("Input Error", "Credits must be a valid number (e.g., 3 or 1.5).");
            return;
        }

        selectedCourse.setCourseCode(code);
        selectedCourse.setCourseName(name);
        selectedCourse.setCredits(credits);

        courseDAO.updateCourse(selectedCourse);

        clearForm();
        loadCourseData();
    }

    private void handleDeleteCourse() {
        if (selectedCourse == null) {
            showAlert("Selection Error", "Please select a course from the table to delete.");
            return;
        }

        courseDAO.deleteCourse(selectedCourse.getId());

        clearForm();
        loadCourseData();
    }

    private void clearForm() {
        codeField.clear();
        nameField.clear();
        creditsField.clear();
        selectedCourse = null;
        tableView.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
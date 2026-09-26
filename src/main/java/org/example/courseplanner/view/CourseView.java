package org.example.courseplanner.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import org.example.courseplanner.dao.CourseDAO;
import org.example.courseplanner.model.Course;

public class CourseView extends VBox {

    private final CourseDAO courseDAO;
    private final TableView<Course> tableView;

    public CourseView() {
        courseDAO = new CourseDAO();
        tableView = new TableView<>();

        setupTable();
        loadCourseData();

        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.getChildren().add(tableView);
    }

    private void setupTable() {
        TableColumn<Course, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());

        TableColumn<Course, String> codeColumn = new TableColumn<>("Course Code");
        codeColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCourseCode()));

        TableColumn<Course, String> nameColumn = new TableColumn<>("Course Name");
        nameColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCourseName()));

        TableColumn<Course, Integer> creditsColumn = new TableColumn<>("Credits");
        creditsColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCredits()).asObject());

        tableView.getColumns().add(idColumn);
        tableView.getColumns().add(codeColumn);
        tableView.getColumns().add(nameColumn);
        tableView.getColumns().add(creditsColumn);
    }

    private void loadCourseData() {
        ObservableList<Course> courseList = FXCollections.observableArrayList(courseDAO.getAllCourses());
        tableView.setItems(courseList);
    }
}
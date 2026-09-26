package org.example.courseplanner.dao;

import org.example.courseplanner.database.DatabaseConnection;
import org.example.courseplanner.model.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    // CREATE: adds a new course to the database
    public void addCourse(Course course) {
        String sql = "INSERT INTO courses (course_code, course_name, credits) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, course.getCourseCode());
            statement.setString(2, course.getCourseName());
            statement.setDouble(3, course.getCredits());

            statement.executeUpdate();
            System.out.println("Course added: " + course.getCourseCode());

        } catch (SQLException e) {
            System.out.println("Error adding course: " + e.getMessage());
        }
    }

    // READ: retrieves all courses from the database
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String courseCode = resultSet.getString("course_code");
                String courseName = resultSet.getString("course_name");
                double credits = resultSet.getDouble("credits");

                Course course = new Course(id, courseCode, courseName, credits);
                courses.add(course);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving courses: " + e.getMessage());
        }

        return courses;
    }

    // UPDATE: modifies an existing course's details
    public void updateCourse(Course course) {
        String sql = "UPDATE courses SET course_code = ?, course_name = ?, credits = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, course.getCourseCode());
            statement.setString(2, course.getCourseName());
            statement.setDouble(3, course.getCredits());
            statement.setInt(4, course.getId());

            int rowsAffected = statement.executeUpdate();
            System.out.println("Rows updated: " + rowsAffected);

        } catch (SQLException e) {
            System.out.println("Error updating course: " + e.getMessage());
        }
    }

    // DELETE: removes a course from the database by its id
    public void deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, courseId);

            int rowsAffected = statement.executeUpdate();
            System.out.println("Rows deleted: " + rowsAffected);

        } catch (SQLException e) {
            System.out.println("Error deleting course: " + e.getMessage());
        }
    }
}
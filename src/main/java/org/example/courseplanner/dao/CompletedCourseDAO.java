package org.example.courseplanner.dao;

import org.example.courseplanner.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompletedCourseDAO {

    public void markCompleted(int studentId, int courseId) {
        String sql = "INSERT INTO completed_courses (student_id, course_id) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);
            statement.setInt(2, courseId);

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error marking course completed: " + e.getMessage());
        }
    }

    public void unmarkCompleted(int studentId, int courseId) {
        String sql = "DELETE FROM completed_courses WHERE student_id = ? AND course_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);
            statement.setInt(2, courseId);

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error unmarking course: " + e.getMessage());
        }
    }

    public List<Integer> getCompletedCourseIds(int studentId) {
        List<Integer> courseIds = new ArrayList<>();
        String sql = "SELECT course_id FROM completed_courses WHERE student_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    courseIds.add(resultSet.getInt("course_id"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving completed courses: " + e.getMessage());
        }

        return courseIds;
    }
}
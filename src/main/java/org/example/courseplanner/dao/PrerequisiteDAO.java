package org.example.courseplanner.dao;

import org.example.courseplanner.database.DatabaseConnection;
import org.example.courseplanner.model.Prerequisite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrerequisiteDAO {

    // CREATE: adds a new prerequisite relationship
    public void addPrerequisite(int courseId, int prerequisiteCourseId) {
        String sql = "INSERT INTO prerequisites (course_id, prerequisite_course_id) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, courseId);
            statement.setInt(2, prerequisiteCourseId);

            statement.executeUpdate();
            System.out.println("Prerequisite added.");

        } catch (SQLException e) {
            System.out.println("Error adding prerequisite: " + e.getMessage());
        }
    }

    // Checks whether this exact prerequisite relationship already exists,
// so we can prevent adding the same pair twice.
    public boolean prerequisiteExists(int courseId, int prerequisiteCourseId) {
        String sql = "SELECT COUNT(*) FROM prerequisites WHERE course_id = ? AND prerequisite_course_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, courseId);
            statement.setInt(2, prerequisiteCourseId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error checking existing prerequisite: " + e.getMessage());
        }

        return false;
    }

    // READ: retrieves all prerequisite relationships
    public List<Prerequisite> getAllPrerequisites() {
        List<Prerequisite> prerequisites = new ArrayList<>();
        String sql = "SELECT * FROM prerequisites";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int courseId = resultSet.getInt("course_id");
                int prerequisiteCourseId = resultSet.getInt("prerequisite_course_id");

                prerequisites.add(new Prerequisite(id, courseId, prerequisiteCourseId));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving prerequisites: " + e.getMessage());
        }

        return prerequisites;
    }

    // DELETE: removes a prerequisite relationship by its id
    public void deletePrerequisite(int prerequisiteId) {
        String sql = "DELETE FROM prerequisites WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, prerequisiteId);

            int rowsAffected = statement.executeUpdate();
            System.out.println("Rows deleted: " + rowsAffected);

        } catch (SQLException e) {
            System.out.println("Error deleting prerequisite: " + e.getMessage());
        }
    }
}
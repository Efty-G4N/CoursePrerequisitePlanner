package org.example.courseplanner.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:course_planner.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        String createCoursesTable = """
                CREATE TABLE IF NOT EXISTS courses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    course_code TEXT NOT NULL UNIQUE,
                    course_name TEXT NOT NULL,
                    credits INTEGER NOT NULL
                );
                """;

        String createPrerequisitesTable = """
                CREATE TABLE IF NOT EXISTS prerequisites (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    course_id INTEGER NOT NULL,
                    prerequisite_course_id INTEGER NOT NULL,
                    FOREIGN KEY (course_id) REFERENCES courses(id),
                    FOREIGN KEY (prerequisite_course_id) REFERENCES courses(id)
                );
                """;

        String createStudentsTable = """
                CREATE TABLE IF NOT EXISTS students (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_code TEXT NOT NULL UNIQUE,
                    student_name TEXT NOT NULL
                );
                """;

        String createCompletedCoursesTable = """
                CREATE TABLE IF NOT EXISTS completed_courses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_id INTEGER NOT NULL,
                    course_id INTEGER NOT NULL,
                    FOREIGN KEY (student_id) REFERENCES students(id),
                    FOREIGN KEY (course_id) REFERENCES courses(id)
                );
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(createCoursesTable);
            statement.execute(createPrerequisitesTable);
            statement.execute(createStudentsTable);
            statement.execute(createCompletedCoursesTable);

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            System.out.println("Database initialization failed: " + e.getMessage());
        }
    }
}
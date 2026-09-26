package org.example.courseplanner.model;

public class Student implements Identifiable {

    private int id;
    private String studentName;
    private String studentCode;

    public Student(int id, String studentName, String studentCode) {
        this.id = id;
        this.studentName = studentName;
        this.studentCode = studentCode;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    @Override
    public String toString() {
        return studentCode + " - " + studentName;
    }
}
package org.example.courseplanner.model;

public class Prerequisite {

    private int id;
    private int courseId;
    private int prerequisiteCourseId;

    public Prerequisite(int id, int courseId, int prerequisiteCourseId) {
        this.id = id;
        this.courseId = courseId;
        this.prerequisiteCourseId = prerequisiteCourseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getPrerequisiteCourseId() {
        return prerequisiteCourseId;
    }

    public void setPrerequisiteCourseId(int prerequisiteCourseId) {
        this.prerequisiteCourseId = prerequisiteCourseId;
    }

    @Override
    public String toString() {
        return "Course " + courseId + " requires Course " + prerequisiteCourseId;
    }
}
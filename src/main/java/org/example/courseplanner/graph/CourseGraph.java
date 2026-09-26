package org.example.courseplanner.graph;

import org.example.courseplanner.dao.PrerequisiteDAO;
import org.example.courseplanner.model.Prerequisite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseGraph {

    // forward: key = a course id, value = list of course ids that require it (dependents)
    private final Map<Integer, List<Integer>> adjacencyList;

    // reverse: key = a course id, value = list of course ids that are its direct prerequisites
    private final Map<Integer, List<Integer>> reverseAdjacencyList;

    public CourseGraph() {
        adjacencyList = new HashMap<>();
        reverseAdjacencyList = new HashMap<>();
        buildGraph();
    }

    private void buildGraph() {
        PrerequisiteDAO prerequisiteDAO = new PrerequisiteDAO();
        List<Prerequisite> prerequisites = prerequisiteDAO.getAllPrerequisites();

        for (Prerequisite p : prerequisites) {
            int prerequisiteCourseId = p.getPrerequisiteCourseId();
            int courseId = p.getCourseId();

            addForwardEdge(prerequisiteCourseId, courseId);
            addReverseEdge(courseId, prerequisiteCourseId);
        }
    }

    private void addForwardEdge(int fromCourseId, int toCourseId) {
        adjacencyList.putIfAbsent(fromCourseId, new ArrayList<>());
        adjacencyList.get(fromCourseId).add(toCourseId);
    }

    private void addReverseEdge(int fromCourseId, int toCourseId) {
        reverseAdjacencyList.putIfAbsent(fromCourseId, new ArrayList<>());
        reverseAdjacencyList.get(fromCourseId).add(toCourseId);
    }

    // returns the list of course ids that directly depend on the given course
    public List<Integer> getDependents(int courseId) {
        return adjacencyList.getOrDefault(courseId, new ArrayList<>());
    }

    // returns the list of course ids that are direct prerequisites of the given course
    public List<Integer> getDirectPrerequisites(int courseId) {
        return reverseAdjacencyList.getOrDefault(courseId, new ArrayList<>());
    }

    public Map<Integer, List<Integer>> getAdjacencyList() {
        return adjacencyList;
    }

    public Map<Integer, List<Integer>> getReverseAdjacencyList() {
        return reverseAdjacencyList;
    }
}
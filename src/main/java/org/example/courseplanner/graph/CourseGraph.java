package org.example.courseplanner.graph;

import org.example.courseplanner.dao.PrerequisiteDAO;
import org.example.courseplanner.model.Prerequisite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseGraph {

    // adjacency list: key = a course id, value = list of course ids that require it as a prerequisite
    private final Map<Integer, List<Integer>> adjacencyList;

    public CourseGraph() {
        adjacencyList = new HashMap<>();
        buildGraph();
    }

    // reads all prerequisite relationships from the database and builds the graph
    private void buildGraph() {
        PrerequisiteDAO prerequisiteDAO = new PrerequisiteDAO();
        List<Prerequisite> prerequisites = prerequisiteDAO.getAllPrerequisites();

        for (Prerequisite p : prerequisites) {
            int prerequisiteCourseId = p.getPrerequisiteCourseId();
            int courseId = p.getCourseId();

            addEdge(prerequisiteCourseId, courseId);
        }
    }

    // adds a directed edge from 'fromCourseId' to 'toCourseId'
    private void addEdge(int fromCourseId, int toCourseId) {
        adjacencyList.putIfAbsent(fromCourseId, new ArrayList<>());
        adjacencyList.get(fromCourseId).add(toCourseId);
    }

    // returns the list of course ids that directly depend on the given course
    public List<Integer> getDependents(int courseId) {
        return adjacencyList.getOrDefault(courseId, new ArrayList<>());
    }

    // returns all course ids that appear in the graph (as either a prerequisite or a dependent)
    public Map<Integer, List<Integer>> getAdjacencyList() {
        return adjacencyList;
    }
}
package org.example.courseplanner.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class GraphTraversal {

    protected Map<Integer, List<Integer>> graphData;

    public GraphTraversal(Map<Integer, List<Integer>> graphData) {
        this.graphData = graphData;
    }

    public List<Integer> traverse(int startCourseId) {
        List<Integer> visitedOrder = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        initializeStructure();
        addToStructure(startCourseId);
        visited.add(startCourseId);

        while (!isStructureEmpty()) {
            int current = removeFromStructure();
            visitedOrder.add(current);

            List<Integer> neighbors = graphData.getOrDefault(current, new ArrayList<>());
            for (int neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    addToStructure(neighbor);
                }
            }
        }

        return visitedOrder;
    }

    protected abstract void initializeStructure();

    protected abstract void addToStructure(int courseId);

    protected abstract int removeFromStructure();

    protected abstract boolean isStructureEmpty();
}
package org.example.courseplanner.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CycleDetector {

    private final Map<Integer, List<Integer>> adjacencyList;
    private final Set<Integer> visited = new HashSet<>();
    private final Set<Integer> recursionStack = new HashSet<>();
    private final List<Integer> path = new ArrayList<>();
    private List<Integer> cyclePath = null;

    public CycleDetector(Map<Integer, List<Integer>> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    public boolean hasCycle() {
        for (int node : adjacencyList.keySet()) {
            if (!visited.contains(node)) {
                if (dfs(node)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dfs(int node) {
        visited.add(node);
        recursionStack.add(node);
        path.add(node);

        List<Integer> neighbors = adjacencyList.getOrDefault(node, new ArrayList<>());
        for (int neighbor : neighbors) {
            if (!visited.contains(neighbor)) {
                if (dfs(neighbor)) {
                    return true;
                }
            } else if (recursionStack.contains(neighbor)) {
                int startIndex = path.indexOf(neighbor);
                cyclePath = new ArrayList<>(path.subList(startIndex, path.size()));
                cyclePath.add(neighbor);
                return true;
            }
        }

        path.remove(path.size() - 1);
        recursionStack.remove(node);
        return false;
    }

    public List<Integer> getCyclePath() {
        return cyclePath;
    }
}
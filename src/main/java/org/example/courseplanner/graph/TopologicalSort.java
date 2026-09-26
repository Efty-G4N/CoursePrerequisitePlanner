package org.example.courseplanner.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class TopologicalSort {

    // returns a valid course-taking order.
    // if the returned list is smaller than allCourseIds, a cycle exists in the graph.
    public List<Integer> sort(Map<Integer, List<Integer>> adjacencyList, List<Integer> allCourseIds) {
        Map<Integer, Integer> inDegree = new HashMap<>();

        // start every course with in-degree 0
        for (int id : allCourseIds) {
            inDegree.put(id, 0);
        }

        // count how many prerequisites point to each course
        for (Map.Entry<Integer, List<Integer>> entry : adjacencyList.entrySet()) {
            for (int neighbor : entry.getValue()) {
                inDegree.put(neighbor, inDegree.getOrDefault(neighbor, 0) + 1);
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int id : allCourseIds) {
            if (inDegree.get(id) == 0) {
                queue.add(id);
            }
        }

        List<Integer> order = new ArrayList<>();

        while (!queue.isEmpty()) {
            int current = queue.poll();
            order.add(current);

            List<Integer> dependents = adjacencyList.getOrDefault(current, new ArrayList<>());
            for (int dependent : dependents) {
                inDegree.put(dependent, inDegree.get(dependent) - 1);
                if (inDegree.get(dependent) == 0) {
                    queue.add(dependent);
                }
            }
        }

        return order;
    }
}
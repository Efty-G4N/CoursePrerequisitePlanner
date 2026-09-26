package org.example.courseplanner.graph;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class BreadthFirstSearch extends GraphTraversal {

    private Deque<Integer> queue;

    public BreadthFirstSearch(Map<Integer, List<Integer>> graphData) {
        super(graphData);
    }

    @Override
    protected void initializeStructure() {
        queue = new ArrayDeque<>();
    }

    @Override
    protected void addToStructure(int courseId) {
        queue.addLast(courseId);
    }

    @Override
    protected int removeFromStructure() {
        return queue.removeFirst();
    }

    @Override
    protected boolean isStructureEmpty() {
        return queue.isEmpty();
    }
}
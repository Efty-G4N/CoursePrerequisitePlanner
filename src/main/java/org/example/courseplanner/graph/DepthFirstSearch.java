package org.example.courseplanner.graph;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class DepthFirstSearch extends GraphTraversal {

    private Deque<Integer> stack;

    public DepthFirstSearch(Map<Integer, List<Integer>> graphData) {
        super(graphData);
    }

    @Override
    protected void initializeStructure() {
        stack = new ArrayDeque<>();
    }

    @Override
    protected void addToStructure(int courseId) {
        stack.push(courseId);
    }

    @Override
    protected int removeFromStructure() {
        return stack.pop();
    }

    @Override
    protected boolean isStructureEmpty() {
        return stack.isEmpty();
    }
}
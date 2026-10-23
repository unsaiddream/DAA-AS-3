package graph;

import java.util.ArrayList;
import java.util.List;

public class Graph {

    private final int n;
    private final boolean directed;

    private final List<List<Edge>> adj;
    private final List<Edge> edges;

    public Graph(int n, boolean directed) {
        if (n < 0) {
            throw new IllegalArgumentException("invalid number of vertices");
        }

        this.n = n;
        this.directed = directed;

        adj = new ArrayList<>();
        edges = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int from, int to, int weight) {
        if (from < 0 || from >= n || to < 0 || to >= n) {
            throw new IllegalArgumentException("invalid vertex");
        }

        Edge edge = new Edge(from, to, weight);

        adj.get(from).add(edge);
        edges.add(edge);

        if (!directed) {
            adj.get(to).add(new Edge(to, from, weight));
        }
    }

    public int size() {
        return n;
    }

    public boolean isDirected() {
        return directed;
    }

    public List<Edge> neighbors(int vertex) {
        if (vertex < 0 || vertex >= n) {
            throw new IllegalArgumentException("invalid vertex");
        }
        return adj.get(vertex);
    }

    public List<Edge> getEdges() {
        return edges;
    }
}

import benchmark.GraphGenerator;
import graph.Graph;

public class Main {

    public static void main(String[] args) {

        int n = 1000;
        int m = 4000;

        Graph undirected = GraphGenerator.undirected(n, m);
        Graph directed = GraphGenerator.directed(n, m);
        Graph dag = GraphGenerator.dag(n, m);

        System.out.println(
                "Undirected edges: " + undirected.getEdges().size()
        );

        System.out.println(
                "Directed edges: " + directed.getEdges().size()
        );

        System.out.println(
                "DAG edges: " + dag.getEdges().size()
        );
    }
}

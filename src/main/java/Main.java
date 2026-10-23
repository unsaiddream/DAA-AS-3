
import algorithm.Kruskal;
import graph.Edge;
import graph.Graph;
import graph.GraphReader;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args)
            throws FileNotFoundException {

        Graph graph = GraphReader.readFromFile("data/connected.txt");

        Kruskal.Result result = Kruskal.findMST(graph);

        System.out.println("MST total weight: " + result.totalWeight);
        System.out.println("Components: " + result.components);
        System.out.println("MST edges:");

        for (Edge edge : result.mstEdges) {
            System.out.println(
                    edge.from + " - " + edge.to +
                            " (weight: " + edge.weight + ")"
            );
        }

        System.out.println("DSU operations: " + result.dsuOps);
        System.out.println("Edge scans: " + result.edgeScans);
        System.out.println("Comparisons: " + result.comparisons);
    }
}

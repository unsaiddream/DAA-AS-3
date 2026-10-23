
import algorithm.Kahn;
import graph.Graph;
import graph.GraphReader;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args)
            throws FileNotFoundException {

        Graph graph = GraphReader.readFromFile("data/dag.txt");

        Kahn.Result result = Kahn.topologicalSort(graph);

        System.out.println("Has cycle: " + result.hasCycle);
        System.out.println("Topological order: " + result.order);
        System.out.println("Edge scans: " + result.edgeScans);

        if (!result.hasCycle) {
            System.out.println(
                    "Valid order: " +
                            Kahn.isValidOrder(graph, result.order)
            );
        }
    }
}

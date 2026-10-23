
import algorithm.DagShortestPath;
import graph.Graph;
import graph.GraphReader;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args)
            throws FileNotFoundException {

        Graph graph = GraphReader.readFromFile("data/dag.txt");

        DagShortestPath.Result result =
                DagShortestPath.shortestPaths(graph);

        for (int i = 0; i < graph.size(); i++) {
            System.out.println(
                    "Vertex " + i +
                            ": distance = " + result.distance[i] +
                            ", path = " + result.getPath(i)
            );
        }

        System.out.println(
                "Total distance: " + result.totalDistance
        );

        System.out.println(
                "Edge scans: " + result.edgeScans
        );
    }
}

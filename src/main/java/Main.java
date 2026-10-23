
import algorithm.Kosaraju;
import graph.Graph;
import graph.GraphReader;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args)
            throws FileNotFoundException {

        Graph graph = GraphReader.readFromFile("data/cyclic.txt");

        Kosaraju.Result result = Kosaraju.findSCC(graph);

        System.out.println("SCC count: " + result.componentCount);

        for (int i = 0; i < graph.size(); i++) {
            System.out.println(
                    "Vertex " + i + " → Component " + result.component[i]
            );
        }

        System.out.println("Edge scans: " + result.edgeScans);
        System.out.println("Max depth: " + result.maxDepth);
    }
}

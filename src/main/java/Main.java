
import algorithm.Kruskal;
import algorithm.Prim;
import graph.Graph;
import graph.GraphReader;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args)
            throws FileNotFoundException {

        Graph graph = GraphReader.readFromFile("data/connected.txt");

        Kruskal.Result kruskal = Kruskal.findMST(graph);
        Prim.Result prim = Prim.findMST(graph);

        System.out.println("Kruskal weight: " + kruskal.totalWeight);
        System.out.println("Prim weight: " + prim.totalWeight);

        System.out.println("Kruskal components: " + kruskal.components);
        System.out.println("Prim components: " + prim.components);

        System.out.println("Kruskal edges: " + kruskal.mstEdges.size());
        System.out.println("Prim edges: " + prim.mstEdges.size());

        if (kruskal.totalWeight != prim.totalWeight) {
            throw new IllegalStateException(
                    "MST weights do not match!"
            );
        }

        if (kruskal.components != prim.components) {
            throw new IllegalStateException(
                    "Component counts do not match!"
            );
        }

        System.out.println("MST validation passed!");
    }
}

import graph.Graph;
import graph.Edge;
import graph.GraphReader;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args)
            throws FileNotFoundException {

        Graph graph = GraphReader.readFromFile("data/connected.txt");

        for (int i = 0; i < graph.size(); i++) {
            System.out.print(i + ": ");

            for (Edge edge : graph.neighbors(i)) {
                System.out.print(
                        "(" + edge.to + ", " + edge.weight + ") "
                );
            }

            System.out.println();
        }
    }
}
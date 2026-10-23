package graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GraphReader {

    public static Graph readFromFile(String filename)
            throws FileNotFoundException {

        Scanner scanner = new Scanner(new File(filename));

        try {
            int n = scanner.nextInt();
            int m = scanner.nextInt();
            String type = scanner.next();

            if (!type.equals("directed") &&
                    !type.equals("undirected")) {
                throw new IllegalArgumentException("Invalid graph type");
            }

            boolean directed = type.equals("directed");

            Graph graph = new Graph(n, directed);

            for (int i = 0; i < m; i++) {
                int u = scanner.nextInt();
                int v = scanner.nextInt();
                int w = scanner.nextInt();

                graph.addEdge(u, v, w);
            }

            return graph;

        } finally {
            scanner.close();
        }
    }
}
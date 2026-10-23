package benchmark;

import graph.Graph;

import java.util.Random;


public class GraphGenerator {

    private static final int SEED = 42;

    private static int[] shuffled(int n, Random random) {
        int[] array = new int[n];

        for (int i = 0; i < n; i++) {
            array[i] = i;
        }

        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);

            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }

        return array;
    }

    public static Graph undirected(int n, int m) {
        validate(n, m, true);

        Random random = new Random(SEED);
        Graph graph = new Graph(n, false);

        int[] perm = shuffled(n, random);

        // Generate a random spanning tree
        for (int i = 1; i < n; i++) {
            int a = perm[random.nextInt(i)];
            int b = perm[i];
            int weight = 1 + random.nextInt(1000);

            graph.addEdge(a, b, weight);
        }

        // Add remaining random edges
        for (int i = n - 1; i < m; i++) {
            int a = random.nextInt(n);
            int b = random.nextInt(n);

            while (b == a) {
                b = random.nextInt(n);
            }

            int weight = 1 + random.nextInt(1000);
            graph.addEdge(a, b, weight);
        }

        return graph;
    }

    public static Graph directed(int n, int m) {
        validate(n, m, false);

        Random random = new Random(SEED);
        Graph graph = new Graph(n, true);

        for (int i = 0; i < m; i++) {
            int a = random.nextInt(n);
            int b = random.nextInt(n);

            while (b == a) {
                b = random.nextInt(n);
            }

            int weight = 1 + random.nextInt(1000);
            graph.addEdge(a, b, weight);
        }

        return graph;
    }


    public static Graph dag(int n, int m) {
        validate(n, m, false);

        Random random = new Random(SEED);
        Graph graph = new Graph(n, true);

        int[] rank = shuffled(n, random);

        for (int i = 0; i < m; i++) {
            int a = random.nextInt(n);
            int b = random.nextInt(n);

            while (b == a) {
                b = random.nextInt(n);
            }

            if (rank[a] > rank[b]) {
                int temp = a;
                a = b;
                b = temp;
            }

            int weight = 1 + random.nextInt(1000);

            graph.addEdge(a, b, weight);
        }

        return graph;
    }


    private static void validate(int n, int m, boolean connected) {
        if (n < 2 || m < 0 || (connected && m < n - 1)) {
            throw new IllegalArgumentException(
                    "Invalid graph size or edge count"
            );
        }
    }
}

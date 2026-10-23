
import algorithm.DSU;

public class Main {
    public static void main(String[] args) {

        DSU dsu = DSU.makeSet(5);

        System.out.println("Initial components: " + dsu.components());

        dsu.union(0, 1);
        dsu.union(1, 2);
        dsu.union(3, 4);

        System.out.println("Components after unions: " + dsu.components());

        System.out.println("Root of 0: " + dsu.find(0));
        System.out.println("Root of 2: " + dsu.find(2));

        dsu.union(2, 3);

        System.out.println("Final components: " + dsu.components());

        System.out.println("Find calls: " + dsu.getFindCalls());
        System.out.println("Successful unions: " + dsu.getSuccessfulUnions());
    }
}

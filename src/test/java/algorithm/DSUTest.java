
package algorithm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

public class DSUTest {

    @Test
    void testInitialComponents() {
        DSU dsu = DSU.makeSet(5);

        assertEquals(5, dsu.components());
    }

    @Test
    void testUnion() {
        DSU dsu = DSU.makeSet(5);

        assertTrue(dsu.union(0, 1));
        assertTrue(dsu.union(1, 2));

        assertEquals(dsu.find(0), dsu.find(2));
        assertEquals(3, dsu.components());
    }

    @Test
    void testDuplicateUnion() {
        DSU dsu = DSU.makeSet(3);

        assertTrue(dsu.union(0, 1));
        assertFalse(dsu.union(0, 1));

        assertEquals(2, dsu.components());
        assertEquals(1, dsu.getSuccessfulUnions());
    }

    @Test
    void testIsolatedVertices() {
        DSU dsu = DSU.makeSet(4);

        assertNotEquals(dsu.find(0), dsu.find(3));
        assertEquals(4, dsu.components());
    }

    @Test
    void testRandomUnionsAgainstReference() {
        int n = 100;
        Random random = new Random(42);

        DSU dsu = DSU.makeSet(n);

        int[] reference = new int[n];

        for (int i = 0; i < n; i++) {
            reference[i] = i;
        }

        for (int step = 0; step < 500; step++) {
            int a = random.nextInt(n);
            int b = random.nextInt(n);

            int oldA = reference[a];
            int oldB = reference[b];

            dsu.union(a, b);

            if (oldA != oldB) {
                for (int i = 0; i < n; i++) {
                    if (reference[i] == oldB) {
                        reference[i] = oldA;
                    }
                }
            }

            int referenceComponents = 0;

            for (int i = 0; i < n; i++) {
                boolean firstOccurrence = true;

                for (int j = 0; j < i; j++) {
                    if (reference[i] == reference[j]) {
                        firstOccurrence = false;
                        break;
                    }
                }

                if (firstOccurrence) {
                    referenceComponents++;
                }
            }

            assertEquals(referenceComponents, dsu.components());

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    assertEquals(
                            reference[i] == reference[j],
                            dsu.find(i) == dsu.find(j)
                    );
                }
            }
        }
    }
}

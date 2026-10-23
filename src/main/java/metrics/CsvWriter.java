
package metrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvWriter {

    public static void write(
            String filename,
            List<Metrics> results) throws IOException {

        Path path = Path.of(filename);

        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        try (var writer = Files.newBufferedWriter(path)) {

            writer.write(
                    "algorithm,graph,n,m,time_ms,dsu_ops," +
                            "edge_scans,comparisons,max_depth,result"
            );

            writer.newLine();

            for (Metrics metric : results) {
                writer.write(metric.toCsv());
                writer.newLine();
            }
        }
    }
}

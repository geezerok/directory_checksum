package rubiconproject.performance;

import org.junit.Test;
import rubiconproject.traverse.FileWalker;
import rubiconproject.util.FileWriteUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * @author serhii.holdun
 */
public class PerformanceTester {

    //TODO configure surefire plugin to skip performance tests
    private static final String TEST_DIR = "input/";

    private static final String OUTPUT_FILE_NAME = "results";

    @Test
    public void testFileWalker() throws IOException, NoSuchAlgorithmException {
        Path source = Paths.get(TEST_DIR).normalize().toAbsolutePath();
        time(() -> {
            Map<String, String> map = new FileWalker().processDirectory(source);
            FileWriteUtil.writeMapToFile(map, OUTPUT_FILE_NAME);
        });
    }

    /**
     * Times a {@link Runnable} instance.
     *
     * @param r {@link Runnable} object to time.
     * @return {@link Duration} object containing run-time length.
     */
    private Duration time(Runnable r) {
        Instant start = Instant.now();
        r.run();
        Duration dur = Duration.between(start, Instant.now());
        System.out.format("Completed in: %s%n", dur.toString());
        return dur;
    }
}

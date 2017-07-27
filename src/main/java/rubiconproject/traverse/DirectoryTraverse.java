package rubiconproject.traverse;

import java.nio.file.Path;
import java.util.Map;

/**
 * Directory traverse.
 *
 * @author serhii.holdun
 */
public interface DirectoryTraverse {

    /**
     * Traverse directory structure and and calculates hash for each file.
     *
     * @param dirPath path of the directory that should be processed
     * @return map of file names to hashes
     */
    Map<String, String> processDirectory(Path dirPath);
}

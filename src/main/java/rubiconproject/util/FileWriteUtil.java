package rubiconproject.util;

import org.apache.commons.lang3.StringUtils;
import rubiconproject.exception.HashFilesException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * utility class.
 * Contains methods to write data into file.
 *
 * @author serhii.holdun
 */
public class FileWriteUtil {

    /**
     * Writes fileNamesToHashesMap into file.
     *
     * @param fileNamesToHashesMap file names to hashes map
     * @param outputFile           output file
     */
    public static void writeMapToFile(Map<String, String> fileNamesToHashesMap, String outputFile) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFile))) {
            fileNamesToHashesMap.forEach((key, value) -> {
                // remove parents from the path
                // add indentations for each file, basing on it's level,
                String[] strArr = key.split(File.separator);
                String fileName = strArr[strArr.length - 1];
                String indentation = StringUtils.leftPad("", (strArr.length - 2) * 4, " ");
                try {
                    writer.write(indentation + fileName + " " + value + "\n");
                } catch (IOException e) {
                    throw new HashFilesException(e.getMessage(), e);
                }
            });
        } catch (IOException e) {
            throw new HashFilesException(e.getMessage(), e);
        }
    }
}

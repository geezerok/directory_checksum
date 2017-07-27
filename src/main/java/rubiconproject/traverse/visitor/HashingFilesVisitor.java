/*
 * Copyright © 2017 Rubicon Project, All rights reserved.
 */

package rubiconproject.traverse.visitor;

import rubiconproject.exception.HashFilesException;
import rubiconproject.util.HashUtil;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * File visitor implementation.
 * Calculates hash values for each visited file or directory.
 *
 * @author serhii.holdun
 */
public class HashingFilesVisitor extends SimpleFileVisitor<Path> {

    /**
     * Root folder path. Needed in order to relativize absolute file path.
     */
    private final String rootFolder;

    /**
     * Concurrent sorted map of file names to hashes.
     * Despite the overhead bringing by CAS it's still faster on insert/iterate operations,
     * comparing with TreeMap
     *
     */
    private Map<String, String> fileNameToHashMap = new ConcurrentSkipListMap<>();

    /**
     * Initializes a new instance of this exception.
     *
     * @param rootFolder root folder
     */
    public HashingFilesVisitor(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    /**
     * Calculates hash function for visited file, basing on its content.
     * Writes result into {@code fileNameToHashMap}.
     *
     * @param file  visited file path
     * @param attrs directory's basic attributes
     * @return file visit result
     */
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        String hash;
        try {
            hash = HashUtil.hashFileEfficient(file);
        } catch (IOException e) {
            throw new HashFilesException(e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            throw new HashFilesException("Wrong hash algorithm provided", e);
        }
        //relativize file path decrease memory consumed by map.
        // String replace is more efficient than Path.relativize()
        fileNameToHashMap.put(file.toString().replace(rootFolder, ""), hash);
        return CONTINUE;
    }

    /**
     * Calculates hash function for visited directory, basing on its child hashes.
     * Invoked for a directory after entries in the directory, and all of their
     * descendants, have been processed.
     *
     * @param dir visited directory path
     * @param exp I/O exception that prevented the file from being visited
     * @return file visit result
     */
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exp) {
        if (exp != null) {
            throw new HashFilesException(exp.getMessage(), exp);
        }

        //relativize file path decrease memory consumed by map.
        // String replace is more efficient than Path.relativize()
        fileNameToHashMap.put(dir.toString().replace(rootFolder, ""), HashUtil.hashDirectory(dir, fileNameToHashMap, rootFolder));
        return CONTINUE;
    }

    /**
     * Gets map of file names to hashes.
     *
     * @return map of file names to hashes
     */
    public Map<String, String> getFileNameToHashMap() {
        return fileNameToHashMap;
    }
}

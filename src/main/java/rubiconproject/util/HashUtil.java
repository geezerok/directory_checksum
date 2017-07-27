/*
 * Copyright © 2017 Rubicon Project, All rights reserved.
 */

package rubiconproject.util;

import rubiconproject.exception.HashFilesException;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Utility class.
 * Contains methods to calculate hash values for files and directories.
 *
 * @author serhii.holdun
 */
public class HashUtil {

    /**
     * Hash algorithm name.
     */
    private static final String HASH_ALGORITHM_NAME = "SHA-512";

    /**
     * File access mode.
     */
    private static final String FILE_ACCESS_MODE = "r";

    /**
     * Size of hash buffer. Used
     */
    private static final int HASH_BUFFER_SIZE = 16384;

    /**
     * Hexadecimal system radix
     */
    private static final int HEX_RADIX = 16;


    /**
     * Slow, but memory efficient method to calculate file hash value.
     * Uses {@code HASH_ALGORITHM_NAME} hash algorithm.
     * Doesn't load the entire file into memory, instead it reads file content by chunks.
     * Chunk size {@code HASH_BUFFER_SIZE}
     *
     * @param filePath path of the file to be hashed.
     * @return hash of the file
     * @throws NoSuchAlgorithmException if no implementation was found for specified algorithm
     * @throws IOException              if in some reason file can't be read (file not found, wrong permissions etc)
     */
    public static String hashFileEfficient(Path filePath) throws IOException, NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM_NAME);

        int buffSize = HASH_BUFFER_SIZE;
        byte[] buffer = new byte[buffSize];
        long read = 0;

        // calculate the hash of the whole file
        try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), FILE_ACCESS_MODE)) {
            long offset = file.length();
            int unitSize;
            while (read < offset) {
                unitSize = (int) (((offset - read) >= buffSize) ? buffSize
                        : (offset - read));
                file.read(buffer, 0, unitSize);
                messageDigest.update(buffer, 0, unitSize);
                read += unitSize;
            }
        }
        return new BigInteger(1, messageDigest.digest()).toString(HEX_RADIX);
    }

    /**
     * Fast, but memory hungry method to calculate file hash value.
     * Uses {@code HASH_ALGORITHM_NAME} hash algorithm.
     *
     * @param filePath path of the file to be hashed.
     * @return hash of the file
     * @throws NoSuchAlgorithmException if no implementation was found for specified algorithm
     * @throws IOException              if in some reason file can't be read (file not found, wrong permissions etc)
     */
    public static String hashFileFast(Path filePath) throws NoSuchAlgorithmException, IOException {
        File infile = filePath.toFile();
        MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM_NAME);
        FileInputStream fin = new FileInputStream(infile);
        byte data[] = new byte[(int) infile.length()];
        fin.read(data);
        fin.close();
        return new BigInteger(1, messageDigest.digest(data)).toString(HEX_RADIX);
    }

    /**
     * Method to calculate file hash value, basing on the hashes of its children.
     * Uses {@code HASH_ALGORITHM_NAME} hash algorithm.
     *
     * @param dir               directory to be hashed
     * @param fileNameToHashMap hash map, contains directory children and their hashes
     * @param rootFolder        root folder, used to calculate relative path for processed directory
     * @return directory hash calue
     */
    public static String hashDirectory(Path dir, Map<String, String> fileNameToHashMap, String rootFolder) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(HASH_ALGORITHM_NAME);
        } catch (NoSuchAlgorithmException e) {
            throw new HashFilesException(e.getMessage(), e);
        }
        try (Stream<Path> stream = Files.list(dir)) {
            stream.sorted().sequential().forEachOrdered(filePath -> {
                //relativize file path decrease memory consumed by map.
                // String replace is more efficient than Path.relativize()
                messageDigest.update(fileNameToHashMap.get(filePath.toString().replace(rootFolder, "")).getBytes());
            });
        } catch (IOException e) {
            throw new HashFilesException(e.getMessage(), e);
        }

        return new BigInteger(1, messageDigest.digest()).toString(HEX_RADIX);
    }
}

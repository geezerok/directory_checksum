/*
 * Copyright © 2017 Rubicon Project, All rights reserved.
 */
package rubiconproject;

import rubiconproject.traverse.FileWalker;
import rubiconproject.util.FileWriteUtil;

import java.nio.file.*;
import java.util.Map;

/**
 * Main class.
 *
 * @author serhii.holdun
 */
public class HashFiles {

    private static final String OUTPUT_FILE_NAME = "results";

    public static void main(String[] argv) {
        if (argv.length < 1) {
            System.err.println("one arguments expected: <path>");
            System.exit(1);
        }
        String inputDir = argv[0];
        try {
            Path source = Paths.get(inputDir).normalize().toAbsolutePath();
            System.out.println("input path: " + source.toString());
            Map<String, String> fileNameToItsHashMap = new FileWalker().processDirectory(source);
            FileWriteUtil.writeMapToFile(fileNameToItsHashMap, OUTPUT_FILE_NAME);
        } catch (Exception e) {
            fail(e);
        }

    }

    private static void fail(Exception e) {
        System.err.println(e.getMessage());
        e.printStackTrace();
        System.exit(1);
    }
}

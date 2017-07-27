/*
 * Copyright © 2017 Rubicon Project, All rights reserved.
 */

package rubiconproject.traverse;

import rubiconproject.exception.HashFilesException;
import rubiconproject.traverse.visitor.HashingFilesVisitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Implementation of {@link DirectoryTraverse}.
 * Uses {@code Files.walkFileTree} in order to traverse directory.
 *
 * @author serhii.holdun
 */
public class FileWalker implements DirectoryTraverse {

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> processDirectory(Path dirPath) {
        HashingFilesVisitor hashingFilesVisitor = new HashingFilesVisitor(dirPath.getParent().toString());
        try {
            Files.walkFileTree(dirPath, hashingFilesVisitor);
        } catch (IOException e) {
            throw new HashFilesException(e.getMessage(), e);
        }
        return hashingFilesVisitor.getFileNameToHashMap();
    }
}

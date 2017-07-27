/*
 * Copyright © 2017 Rubicon Project, All rights reserved.
 */

package rubiconproject.exception;

/**
 * Generic exception. Used as a superclass for all other exceptions.
 * Thrown if no child exception can be used.
 *
 * @author serhii.holdun
 */
public class HashFilesException extends RuntimeException {

    /**
     * Initializes a new instance of this exception.
     *
     * @param message error message
     * @param cause   exception cause
     */
    public HashFilesException(String message, Exception cause) {
        super(message, cause);
    }
}

package io.qdb.kvstore;

import java.io.IOException;

/**
 * Thrown when the directory for a kv store is being used by another process.
 */
public class DirLockedException extends IOException {

    public DirLockedException(String message) {
        super(message);
    }
}

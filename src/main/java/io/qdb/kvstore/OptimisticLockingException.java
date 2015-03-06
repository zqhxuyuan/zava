package io.qdb.kvstore;

/**
 * Thrown when updating an object that has been changed by someone else since being read.
 */
public class OptimisticLockingException extends KeyValueStoreException {

    public OptimisticLockingException(String message) {
        super(message);
    }
}

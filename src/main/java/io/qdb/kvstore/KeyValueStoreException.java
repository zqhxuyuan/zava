package io.qdb.kvstore;

public class KeyValueStoreException extends RuntimeException {

    public KeyValueStoreException(String message) {
        super(message);
    }

    public KeyValueStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyValueStoreException(Throwable cause) {
        super(cause);
    }
}

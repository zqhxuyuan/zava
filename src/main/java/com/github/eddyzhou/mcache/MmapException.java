package com.github.eddyzhou.mcache;

public class MmapException extends Exception {
    private static final long serialVersionUID = -8413715979049073156L;

    public MmapException(String msg) {
        super(msg);
    }

    public MmapException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
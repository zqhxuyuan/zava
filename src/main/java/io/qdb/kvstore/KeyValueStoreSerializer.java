package io.qdb.kvstore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Responsible for converting objects to/from streams. Note that this must be able to serialize
 * maps containing K and V instances and {@link StoreTx} instances which will reference K and V instances.
 */
public interface KeyValueStoreSerializer {
    public void serialize(Object value, boolean pretty, OutputStream out) throws IOException;
    public <T> T deserialize(InputStream in, Class<T> cls) throws IOException;
}

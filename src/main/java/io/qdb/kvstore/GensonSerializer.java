package io.qdb.kvstore;

import com.owlike.genson.Genson;
import com.owlike.genson.TransformationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Serializes to/from JSON using Genson.
 */
public class GensonSerializer implements KeyValueStoreSerializer {

    private final Genson genson;
    private final Genson gensonPretty;

    public GensonSerializer(Map<String, Class> aliases) {
        Genson.Builder b = new Genson.Builder().setSkipNull(true);
        b.addAlias("tx", StoreTx.class);
        for (Map.Entry<String, Class> e : aliases.entrySet()) b.addAlias(e.getKey(), e.getValue());
        genson = b.create();
        gensonPretty = b.useIndentation(true).create();
    }

    @Override
    public void serialize(Object value, boolean pretty, OutputStream out) throws IOException {
        try {
            if (pretty) {
                gensonPretty.serialize(value, out);
            } else {
                genson.serialize(value, out);
            }
        } catch (TransformationException e) {
            throw new IOException(e.toString(), e);
        }
    }

    @Override
    public <T> T deserialize(InputStream in, Class<T> cls) throws IOException {
        try {
            return genson.deserialize(in, cls);
        } catch (TransformationException e) {
            throw new IOException(e.toString(), e);
        }
    }
}

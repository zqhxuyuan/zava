package com.github.wangxuehui.rpc.serializer.jackson;

import java.io.IOException;

import com.github.wangxuehui.rpc.serializer.Deserializer;
import com.github.wangxuehui.rpc.serializer.Serializer;
import org.codehaus.jackson.map.ObjectMapper;

public class JacksonSerializer implements Serializer, Deserializer {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> T deserialize( final byte[] bytes , final Class<T> clazz ) {
        try {
            return mapper.readValue( bytes, clazz );
        }
        catch ( final IOException e ) {
            throw new IllegalStateException( e.getMessage() , e );
        }
    }

    @Override
    public <T> byte[] serialize( final T source ) {
        try {
            return mapper.writeValueAsBytes( source );
        }
        catch ( final IOException e ) {
            throw new IllegalStateException( e.getMessage() , e );
        }
    }
}

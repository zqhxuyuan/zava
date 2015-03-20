package com.github.wangxuehui.rpc.serializer;

public interface Serializer {
    <T> byte[] serialize( T source );
}

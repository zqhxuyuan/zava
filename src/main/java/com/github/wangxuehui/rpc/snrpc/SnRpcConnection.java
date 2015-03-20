package com.github.wangxuehui.rpc.snrpc;

import com.github.wangxuehui.rpc.snrpc.serializer.SnRpcRequest;
import com.github.wangxuehui.rpc.snrpc.serializer.SnRpcResponse;

/**
 * @author skyim E-mail:wxh64788665@gmail.com
 * 类说明
 */
public interface SnRpcConnection {

	public SnRpcResponse sendRequest(final SnRpcRequest request) throws Throwable ;
}

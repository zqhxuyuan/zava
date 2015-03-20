package com.github.wangxuehui.rpc.snrpc.serializer;

import com.github.wangxuehui.rpc.serializer.jdk.JdkObjectSerializer;
import com.github.wangxuehui.rpc.serializer.protostuff.ProtostuffSerializer;
import com.github.wangxuehui.rpc.snrpc.conf.SnRpcConfig;
import com.github.wangxuehui.rpc.serializer.fasterxml.FasterxmlSerializer;
import com.github.wangxuehui.rpc.serializer.jackson.JacksonSerializer;
import com.github.wangxuehui.rpc.serializer.kryo.KryoSerializer;
import com.github.wangxuehui.rpc.serializer.protobuf.ProtobufSerializer;
import com.github.wangxuehui.rpc.snrpc.util.Const;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author skyim E-mail:wxh64788665@gmail.com
 * 类说明
 */
public class SnRpcRequestEncoder  extends MessageToByteEncoder<SnRpcRequest>{

	@Override
	protected void encode(ChannelHandlerContext ctx, SnRpcRequest msg,
			ByteBuf out) throws Exception {

	   byte[] data =null;
	   
       SnRpcConfig snRpcConfig = SnRpcConfig.getInstance();
       String type = snRpcConfig.getProperty("snrpc.serializataion.type", "5");
       if(Const.SERIALIZATION_PROTOBUF.equals(type)){
    	    final ProtobufSerializer protobuf = new ProtobufSerializer();
    	    data  =  protobuf.serialize(msg);
       }else if(Const.SERIALIZATION_KRYO.equals(type)){
    	    final KryoSerializer kryo = new KryoSerializer();
    	    data  =  kryo.serialize(msg);
       }else if(Const.SERIALIZATION_PROTOSTUFF.equals(type)){
   	    final ProtostuffSerializer protostuff = new ProtostuffSerializer();
   	    	data  =  protostuff.serialize(msg);
       }else if(Const.SERIALIZATION_FASTERXML.equals(type)){
     	    final FasterxmlSerializer fastxml = new FasterxmlSerializer();
     	   data  =  fastxml.serialize(msg);
       }else if(Const.SERIALIZATION_JACKSON.equals(type)){
    	    final JacksonSerializer jackson = new JacksonSerializer();
    	    data  =  jackson.serialize(msg);
       }else if(Const.SERIALIZATION_JDK.equals(type)){
   	        final JdkObjectSerializer jdk = new JdkObjectSerializer();
   	        data  =  jdk.serialize(msg);
       }else {
    	   	final ProtobufSerializer protobuf = new ProtobufSerializer();
    	   	data  =  protobuf.serialize(msg);
       }
	   
       int dataLength = data.length;
		// TODO Auto-generated method stub
       out.writeInt(dataLength);
       out.writeBytes(data);
	}

	



}

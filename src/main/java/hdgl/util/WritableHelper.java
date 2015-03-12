package hdgl.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Writable;

public class WritableHelper {

	private static final org.apache.commons.logging.Log Log = LogFactory.getLog(WritableHelper.class);
	
	public static byte[] toBytes(Writable obj){
		try{
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(buffer);
			obj.write(out);
			out.close();
			return buffer.toByteArray();
		}catch (IOException e) {
			Log.fatal("This exception should never happen.", e);
			throw new RuntimeException("This exception should never happen.", e);
		}
	}
	
	public static void read(byte[] data, Writable obj){
		try{
			DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(data));
			obj.readFields(inputStream);
			inputStream.close();
		} catch (IOException e) {
			Log.fatal("This exception should never happen.", e);
			throw new RuntimeException("This exception should never happen.", e);
		}
	}
	
	public static <T extends Writable> T parse(byte[] data, Class<T> type){
		try{
			DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(data));
			T res = type.newInstance();
			res.readFields(inputStream);
			inputStream.close();
			return res;
		}catch (IllegalAccessException e) {
			Log.fatal("This exception should never happen.", e);
			throw new RuntimeException("This exception should never happen.", e);
		} catch (InstantiationException e) {
			Log.fatal("This exception should never happen.", e);
			throw new RuntimeException("This exception should never happen.", e);
		} catch (IOException e) {
			Log.fatal("This exception should never happen.", e);
			throw new RuntimeException("This exception should never happen.", e);
		}
	}

	public static byte[] toBytes(int number){
		byte[] data = new byte[4];
		data[0]=(byte) (number>>>24&0xff);
		data[1]=(byte) (number>>>16&0xff);
		data[2]=(byte) (number>>>8&0xff);
		data[3]=(byte) (number&0xff);
		return data;
	}
	
	public static byte[] toBytes(String str){
		return str.getBytes();
	}
	
	public static int parseInt(byte[] data){
		return (data[0]<<24)|(data[1]<<16)|(data[2]<<8)|(data[3]);
	}
	
	public static String parseString(byte[] data){
		return new String(data);
	}
	
	public static byte[] toBytes(float val){
		return toBytes(Float.floatToIntBits(val));
	}
	
	public static float parseFloat(byte[] data){
		return Float.intBitsToFloat((data[0]<<24)|(data[1]<<16)|(data[2]<<8)|(data[3]));
	}
}

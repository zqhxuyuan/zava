package hdgl.db.store.impl.hdfs.mapreduce;
import java.nio.ByteBuffer;


public class Label {
	private String attribute;
	private String value;
	
	public Label(String attr, String val){
		attribute = attr;
		value = val;
	}
	
	public String getAttr()
	{
		return attribute;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public String getString()
	{
		return attribute + "=" + value;
	}
	
	public int getBytes(ByteBuffer bb)
	{
		int ret = 0;
		byte[] b;
		b = attribute.getBytes();
		bb.putInt(b.length);
		bb.put(b);
		ret = ret + 4 + b.length;
		b = value.getBytes();
		bb.putInt(b.length);
		bb.put(b);
		ret = ret + 4 + b.length;
		return ret;
	}
}

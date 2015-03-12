package hdgl.db.query.condition;

import hdgl.db.exception.HdglException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public abstract class AbstractValue implements Writable {

	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object obj);
	
	public abstract boolean equalsTo(byte[] data);
	
	public abstract boolean lessThan(byte[] data);
	
	public abstract boolean largerThanOrEqualsTo(byte[] data);
	
	public abstract boolean lessThanOrEqualsTo(byte[] data);
	
	public abstract boolean largerThan(byte[] data);
	
	public abstract boolean lessThan(AbstractValue obj);
	
	public abstract boolean largerThan(AbstractValue obj);
	
	public boolean lessThanOrEqualTo(AbstractValue value){
		return lessThan(value)||equals(value);
	}
	
	public boolean largerThanOrEqualTo(AbstractValue value){
		return largerThan(value)||equals(value);
	}
	
	public abstract void readTail(DataInput in) throws IOException;
	
	public static void writeValue(DataOutput out, AbstractValue value) throws IOException{
		value.write(out);
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		in.readByte();
		readTail(in);
	}
	
	public static AbstractValue readValue(DataInput in) throws IOException{
		byte flag = in.readByte();
		AbstractValue value;
		switch (flag) {
		case FloatNumberValue.FLAG_BYTE:
			value = new FloatNumberValue();
			break;
		case IntNumberValue.FLAG_BYTE:
			value = new IntNumberValue();
			break;
		case StringValue.FLAG_BYTE:
			value = new StringValue();
			break;
		default:
			throw new HdglException("Unsupported value type: " + flag);
		}
		value.readTail(in);
		return value;
	}
}

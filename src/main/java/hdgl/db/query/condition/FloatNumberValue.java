package hdgl.db.query.condition;

import hdgl.util.WritableHelper;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FloatNumberValue extends AbstractValue {

	public static final byte FLAG_BYTE=-21;
	
	private float value;

	public float getValue() {
		return value;
	}

	public FloatNumberValue(float value) {
		this.value = value;
	}
	
	public FloatNumberValue(String string) {
		this.value = Float.parseFloat(string);
	}

	public FloatNumberValue() {
		
	}

	public static FloatNumberValue parse(String value) {
		return new FloatNumberValue(Float.parseFloat(value));
	}
	
	@Override
	public String toString() {
		return Double.toString(value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FloatNumberValue other = (FloatNumberValue) obj;
		if (Double.doubleToLongBits(value) != Double
				.doubleToLongBits(other.value))
			return false;
		return true;
	}
	
	@Override
	public boolean lessThan(AbstractValue obj) {
		if(obj instanceof IntNumberValue){
			return value<((IntNumberValue)obj).getValue();
		}else if(obj instanceof FloatNumberValue){
			return value<((FloatNumberValue)obj).getValue();
		}else{
			throw new ArithmeticException("Not comparable");
		}
	}

	@Override
	public boolean largerThan(AbstractValue obj) {
		if(obj instanceof IntNumberValue){
			return value>((IntNumberValue)obj).getValue();
		}else if(obj instanceof FloatNumberValue){
			return value>((FloatNumberValue)obj).getValue();
		}else{
			throw new ArithmeticException("Not comparable");
		}
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeByte(FLAG_BYTE);
		arg0.writeFloat(value);
	}

	@Override
	public void readTail(DataInput in) throws IOException {
		value = in.readFloat();
	}

	@Override
	public boolean equalsTo(byte[] data) {
		float d = WritableHelper.parseInt(data);
		return d == getValue();
	}

	@Override
	public boolean lessThan(byte[] data) {
		float d = WritableHelper.parseInt(data);
		return getValue() < d;
	}

	@Override
	public boolean largerThan(byte[] data) {
		float d = WritableHelper.parseInt(data);
		return getValue() > d;
	}

	@Override
	public boolean largerThanOrEqualsTo(byte[] data) {
		float d = WritableHelper.parseInt(data);
		return getValue() >= d;
	}

	@Override
	public boolean lessThanOrEqualsTo(byte[] data) {
		float d = WritableHelper.parseInt(data);
		return getValue() <= d;
	}
}

package hdgl.db.query.condition;

import hdgl.util.WritableHelper;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class StringValue extends AbstractValue {

	public static final byte FLAG_BYTE=-23;
	
	private String value;

	public StringValue(String value) {
		super();
		this.value = value;
	}

	public StringValue() {
	}

	public String getValue() {
		return value;
	}

	public static StringValue parse(String value){
		return new StringValue(value);
	}
	
	@Override
	public String toString() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		StringValue other = (StringValue) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	@Override
	public boolean lessThan(AbstractValue obj) {
		throw new ArithmeticException("Not comparable");
	}

	@Override
	public boolean largerThan(AbstractValue obj) {
		throw new ArithmeticException("Not comparable");
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeByte(FLAG_BYTE);
		arg0.writeUTF(value);
	}

	@Override
	public void readTail(DataInput in) throws IOException {
		value = in.readUTF();
	}

	@Override
	public boolean equalsTo(byte[] data) {
		String s = WritableHelper.parseString(data);
		return value.equalsIgnoreCase(s);
	}

	@Override
	public boolean lessThan(byte[] data) {
		String s = WritableHelper.parseString(data);
		return value.compareToIgnoreCase(s) < 0;
	}

	@Override
	public boolean largerThan(byte[] data) {
		String s = WritableHelper.parseString(data);
		return value.compareToIgnoreCase(s) > 0;
	}

	@Override
	public boolean largerThanOrEqualsTo(byte[] data) {
		String s = WritableHelper.parseString(data);
		return value.compareToIgnoreCase(s) >= 0;
	}

	@Override
	public boolean lessThanOrEqualsTo(byte[] data) {
		String s = WritableHelper.parseString(data);
		return value.compareToIgnoreCase(s) <= 0;
	}
}

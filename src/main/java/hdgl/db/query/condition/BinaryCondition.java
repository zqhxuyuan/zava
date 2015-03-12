package hdgl.db.query.condition;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class BinaryCondition extends AbstractCondition {

	public BinaryCondition() {
		
	}
	
	public String getLabel() {
		return label;
	}

	public AbstractValue getValue() {
		return value;
	}

	private String label;
	
	private AbstractValue value;

	public BinaryCondition(String label, AbstractValue value) {
		super();
		this.label = label;
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
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
		BinaryCondition other = (BinaryCondition) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	abstract byte getFlagByte();
	
	@Override
	public void readTail(DataInput input) throws IOException {
		label = input.readUTF();
		value = AbstractValue.readValue(input);
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeByte(getFlagByte());
		out.writeUTF(getLabel());
		getValue().write(out);
	}
	
}

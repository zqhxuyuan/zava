package hdgl.db.query.condition;

import hdgl.db.graph.Entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class OfType extends AbstractCondition {

	public static final byte FLAG_BYTE=-9;
	
	String type;

	public OfType() {
	}
	
	public OfType(String type) {
		super();
		this.type = type;
	}

	public String getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		OfType other = (OfType) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public boolean require(AbstractCondition other) {
		if(other instanceof NoRestriction){
			return true;
		}else if(other.equals(this)){
			return true;
//		}else if(other instanceof Conjunction){
//			for (AbstractCondition condition : ((Conjunction) other).getConditions()) {
//				if(!require(condition)){
//					return false;
//				}
//			}
//			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "ofType("+type+")";
	}

	@Override
	public boolean compatible(AbstractCondition other) {
		if(other instanceof OfType){
			return getType().equals(((OfType) other).getType());
		}else{
			return true;
		}
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.write(FLAG_BYTE);
		arg0.writeUTF(type);
	}

	@Override
	public void readTail(DataInput input) throws IOException {
		type = input.readUTF();
	}

	@Override
	public boolean test(Entity e) {
		String type = e.getType();
		return type.equalsIgnoreCase(getType());
	}
	
	
}

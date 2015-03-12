package hdgl.db.query.condition;

import hdgl.db.graph.Entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class Conjunction extends AbstractCondition {

	public static final byte FLAG_BYTE=-1;
	
	AbstractCondition[] conditions;

	
	
	public Conjunction(){
		
	}
	
	public Conjunction(AbstractCondition[] conditions) {
		super();
		this.conditions = conditions;
	}

	public AbstractCondition[] getConditions() {
		return conditions;
	}

	public void setConditions(AbstractCondition[] conditions) {
		this.conditions = conditions;
	}

	@Override
	public boolean require(AbstractCondition other) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean compatible(AbstractCondition other) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(conditions);
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
		Conjunction other = (Conjunction) obj;
		if (!Arrays.equals(conditions, other.conditions))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return Arrays.toString(conditions);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeByte(FLAG_BYTE);
		out.writeInt(conditions.length);
		for(AbstractCondition cond:conditions){
			cond.write(out);
		}
	}

	@Override
	public void readTail(DataInput input) throws IOException {
		int len = input.readInt();
		conditions = new AbstractCondition[len];
		for (int i = 0; i < conditions.length; i++) {
			conditions[i] = AbstractCondition.readAbstractCondition(input);
		}
	}

	@Override
	public boolean test(Entity e) {
		for(AbstractCondition cond:conditions){
			if(!cond.test(e)){
				return false;
			}
		}
		return true;
	}
	
}

package hdgl.db.query.condition;

import hdgl.db.graph.Entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class NoRestriction extends AbstractCondition {

	public static final byte FLAG_BYTE=-7;
	
	public static final NoRestriction I = new NoRestriction();
	
	public NoRestriction() {
	}
	
	@Override
	public boolean require(AbstractCondition other) {
		return other instanceof NoRestriction;
	}

	@Override
	public int hashCode() {
		return NoRestriction.class.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj!=null&&obj instanceof NoRestriction;
	}

	@Override
	public boolean compatible(AbstractCondition other) {
		return true;
	}

	@Override
	public String toString() {
		return "*";
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeByte(FLAG_BYTE);
	}

	@Override
	public void readTail(DataInput input) throws IOException {
		
	}

	@Override
	public boolean test(Entity e) {
		return true;
	}
}

package hdgl.db.query.condition;

import hdgl.db.exception.HdglException;
import hdgl.db.graph.Entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public abstract class AbstractCondition implements Writable{

	public static enum ConditionRelationship{
		Require, Sufficient, Equivalent, NotRelevant
	}
	
	/**
	 * Test whether "other" is a required condition of this. 
	 * meaning that if this is satisfied, "other" is satisfied certainly. 
	 * @param other
	 * @return
	 */
	public abstract boolean require(AbstractCondition other);
	
	/**
	 * Test whether this condition is compatible with "other"
	 * "Compatible" means "Can satisfied at a same time"
	 * @param other
	 * @return
	 */
	public abstract boolean compatible(AbstractCondition other);
	
	public ConditionRelationship relationship(AbstractCondition other){
		if(require(other)){
			if(other.require(this)){
				return ConditionRelationship.Equivalent;
			}else{
				return ConditionRelationship.Require;
			}
		}else{
			if(other.require(this)){
				return ConditionRelationship.Sufficient;
			}else{
				return ConditionRelationship.NotRelevant;
			}
		}
	}
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object obj);
	
	public abstract void readTail(DataInput input) throws IOException;
	
	public static void writeAbstractCondition(AbstractCondition cond, DataOutput out) throws IOException{
		cond.write(out);
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		in.readByte();
		readTail(in);
	}
	
	public abstract boolean test(Entity e);
	
	public static AbstractCondition readAbstractCondition(DataInput in) throws IOException{
		AbstractCondition cond;
		byte flagByte = in.readByte();
		switch (flagByte) {
		case Conjunction.FLAG_BYTE:
			cond = new Conjunction();
			break;
		case EqualTo.FLAG_BYTE:
			cond = new EqualTo();
			break;
		case LargerThan.FLAG_BYTE:
			cond = new LargerThan();
			break;
		case LargerThanOrEqualTo.FLAG_BYTE:
			cond = new LargerThanOrEqualTo();
			break;
		case LessThan.FLAG_BYTE:
			cond = new LessThan();
			break;
		case LessThanOrEqualTo.FLAG_BYTE:
			cond = new LessThanOrEqualTo();
			break;
		case NotEqualTo.FLAG_BYTE:
			cond = new NotEqualTo();
			break;
		case NoRestriction.FLAG_BYTE:
			cond = new NoRestriction();
			break;
		case OfType.FLAG_BYTE:
			cond = new OfType();
			break;
		default:
			throw new HdglException("Unsuppoorted condition type: " + flagByte);
		}
		cond.readTail(in);
		return cond;
	}
}

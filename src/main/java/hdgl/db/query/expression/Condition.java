package hdgl.db.query.expression;

import hdgl.db.query.condition.AbstractCondition;
import hdgl.db.query.condition.AbstractValue;
import hdgl.db.query.condition.EqualTo;
import hdgl.db.query.condition.LargerThan;
import hdgl.db.query.condition.LargerThanOrEqualTo;
import hdgl.db.query.condition.LessThan;
import hdgl.db.query.condition.LessThanOrEqualTo;
import hdgl.db.query.condition.NotEqualTo;
import hdgl.db.query.visitor.Visitor;

public class Condition extends Expression {
	
	String label;
	String op;
	AbstractValue value;
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getOp() {
		return op;
	}
	public void setOp(String op) {
		this.op = op;
	}
	public AbstractValue getValue() {
		return value;
	}
	public void setValue(AbstractValue value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "["+label+op+value+"]";
	}
	
	@Override
	public <TR, TA> TR accept(Visitor<TR, TA> visitor, TA... arguments) {
		return visitor.visitCondition(this, arguments);
	}
	
	public AbstractCondition getCondition(){
		if(op.equals("=")){
			return new EqualTo(getLabel(), getValue());
		}else if(op.equals("<>")){
			return new NotEqualTo(getLabel(), getValue());
		}else if(op.equals("<=")){
			return new LessThanOrEqualTo(getLabel(), getValue());
		}else if(op.equals("<")){
			return new LessThan(getLabel(), getValue());
		}else if(op.equals(">=")){
			return new LargerThanOrEqualTo(getLabel(), getValue());
		}else if(op.equals(">")){
			return new LargerThan(getLabel(), getValue());
		}else{
			throw new IllegalArgumentException(op);
		}
	}
}

package hdgl.db.query.expression;

import hdgl.db.query.visitor.Visitor;

public class Order extends Expression {

	String label;
	boolean isAscending;
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public boolean isAscending() {
		return isAscending;
	}
	public void setAscending(boolean isAscending) {
		this.isAscending = isAscending;
	}
	
	@Override
	public String toString() {
		return "["+(isAscending?"ASC:":"DESC:")+label+"]";
	}
	
	@Override
	public <TR, TA> TR accept(Visitor<TR, TA> visitor, TA... arguments) {
		return visitor.visitOrder(this, arguments);
	}
	
}

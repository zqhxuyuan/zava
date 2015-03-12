package hdgl.db.query.expression;

import hdgl.db.query.visitor.Visitor;

public class Concat extends Expression {

	Expression first;
	Expression second;
	
	public Concat(Expression first, Expression second) {
		super();
		this.first = first;
		this.second = second;
	}
	
	public Concat(){
		
	}
	
	public Expression getFirst() {
		return first;
	}
	public void setFirst(Expression first) {
		this.first = first;
	}
	public Expression getSecond() {
		return second;
	}
	public void setSecond(Expression second) {
		this.second = second;
	}
	
	@Override
	public String toString() {
		return "("+first + " " + second+")";
	}
	
	@Override
	public <TR, TA> TR accept(Visitor<TR, TA> visitor, TA... arguments) {
		return visitor.visitConcat(this, arguments);
	}
	
	
	
}

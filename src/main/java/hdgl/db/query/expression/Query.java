package hdgl.db.query.expression;

import hdgl.db.query.visitor.Visitor;

public class Query extends Expression {

	Expression expression;
	EOF eof;
	
	public Query() {
		super();
	}
	
	public Query(Expression expression) {
		this();
		this.expression = expression;
		this.eof = new EOF();
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	
	public EOF getEOF(){
		return eof;
	}
	
	@Override
	public String toString() {
		return expression.toString();
	}

	@Override
	public <TR, TA> TR accept(Visitor<TR, TA> visitor, TA... arguments) {
		return visitor.visitQuery(this, arguments);
	}

}

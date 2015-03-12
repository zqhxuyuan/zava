package hdgl.db.query.expression;

public abstract class Quantifier extends Expression {

	protected Expression quantified;

	public Expression getQuantified() {
		return quantified;
	}

	public void setQuantified(Expression quantified) {
		this.quantified = quantified;
	}
	
}

package hdgl.db.query.expression;

import hdgl.db.query.visitor.Visitor;

public class AsteriskQuantifier extends Quantifier {
	
	public AsteriskQuantifier(){
		
	}
	
	public AsteriskQuantifier(Expression quantified){
		setQuantified(quantified);
	}
	
	@Override
	public String toString() {
		return quantified + "*";
	}

	@Override
	public <TR, TA> TR accept(Visitor<TR, TA> visitor, TA... arguments) {
		return visitor.visitAsteriskQuantifier(this, arguments);
	}

	

	
}

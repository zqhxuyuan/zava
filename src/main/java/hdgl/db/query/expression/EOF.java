package hdgl.db.query.expression;

import hdgl.db.query.visitor.Visitor;

public class EOF extends Entity { 
	
	@Override
	public <TR, TA> TR accept(Visitor<TR, TA> visitor, TA... arguments) {
		throw new RuntimeException("Cannot traverse a pseudo entity");
	}

	@Override
	public String toString() {
		return "#";
	}
	
}

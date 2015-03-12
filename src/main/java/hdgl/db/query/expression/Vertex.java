package hdgl.db.query.expression;

import hdgl.db.query.visitor.Visitor;

public class Vertex extends Entity {

	public static final Vertex UNRESTRICTED = new Vertex();
	
	@Override
	public String toString() {
		StringBuilder base=new StringBuilder(".");
		if(type!=null){
			base.append(type);
		}
		if(order!=null){
			base.append(order);
		}
		if(conditions!=null){
			for (Condition condition : conditions) {
				base.append(condition);
			}
		}
		return base.toString();
	}

	@Override
	public <TR, TA> TR accept(Visitor<TR, TA> visitor, TA... arguments) {
		return visitor.visitVertex(this, arguments);
	}

	
}

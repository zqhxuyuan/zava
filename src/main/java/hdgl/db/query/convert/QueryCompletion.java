package hdgl.db.query.convert;

import hdgl.db.query.expression.Expression;
import hdgl.db.query.visitor.CompleteQueryVisitor;

public class QueryCompletion {

	public static Expression complete(Expression expression){
		return CompleteQueryVisitor.complete(expression);
	}
	
}

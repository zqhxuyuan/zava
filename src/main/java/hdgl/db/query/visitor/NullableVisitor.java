package hdgl.db.query.visitor;

import hdgl.db.query.expression.AsteriskQuantifier;
import hdgl.db.query.expression.Concat;
import hdgl.db.query.expression.Condition;
import hdgl.db.query.expression.Edge;
import hdgl.db.query.expression.Entity;
import hdgl.db.query.expression.Expression;
import hdgl.db.query.expression.Order;
import hdgl.db.query.expression.Parallel;
import hdgl.db.query.expression.PlusQuantifier;
import hdgl.db.query.expression.Query;
import hdgl.db.query.expression.QuestionQuantifier;
import hdgl.db.query.expression.Vertex;
import hdgl.db.query.visitor.Visitor._void;

import java.util.HashMap;
import java.util.Map;

public class NullableVisitor implements Visitor<Boolean, _void> {

	Map<Entity, Integer> idsMap;
	Map<Expression, Boolean> nullable = new HashMap<Expression, Boolean>();
	
	public Map<Expression, Boolean> getNullableMap(){
		return nullable;
	}
	
	public NullableVisitor(Map<Entity, Integer> idsMap){
		this.idsMap = idsMap;
	}
	
	@Override
	public Boolean visitQuery(Query query, _void... arguments) {
		boolean n = query.getExpression().accept(this);
		nullable.put(query, n);
		nullable.put(query.getEOF(), false);
		return n;
	}

	@Override
	public Boolean visitVertex(Vertex vertex, _void... arguments) {
		nullable.put(vertex, false);
		return false;
	}

	@Override
	public Boolean visitEdge(Edge edge, _void... arguments) {
		nullable.put(edge, false);
		return false;
	}

	@Override
	public Boolean visitAsteriskQuantifier(AsteriskQuantifier quantifier,
			_void... arguments) {
		quantifier.getQuantified().accept(this);
		nullable.put(quantifier, true);
		return true;
	}

	@Override
	public Boolean visitQuestionQuantifier(QuestionQuantifier quantifier,
			_void... arguments) {
		quantifier.getQuantified().accept(this);
		nullable.put(quantifier, true);
		return true;
	}

	@Override
	public Boolean visitPlusQuantifier(PlusQuantifier quantifier,
			_void... arguments) {
		boolean n = quantifier.getQuantified().accept(this);
		nullable.put(quantifier, n);
		return n;
	}

	@Override
	public Boolean visitConcat(Concat concat, _void... arguments) {
		boolean n1=concat.getFirst().accept(this);
		boolean n2=concat.getSecond().accept(this);
		nullable.put(concat, n1 && n2);
		return n1 && n2;
	}

	@Override
	public Boolean visitCondition(Condition cond, _void... arguments) {
		return null;
	}

	@Override
	public Boolean visitOrder(Order order, _void... arguments) {
		return null;
	}

	@Override
	public Boolean visitParallel(Parallel parallel, _void... arguments) {
		boolean n1=parallel.getFirst().accept(this);
		boolean n2=parallel.getSecond().accept(this);
		nullable.put(parallel, n1 || n2);
		return n1 || n2;
	}

}

package hdgl.db.query.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import hdgl.db.query.expression.AsteriskQuantifier;
import hdgl.db.query.expression.Concat;
import hdgl.db.query.expression.Condition;
import hdgl.db.query.expression.Edge;
import hdgl.db.query.expression.Entity;
import hdgl.db.query.expression.Order;
import hdgl.db.query.expression.Parallel;
import hdgl.db.query.expression.PlusQuantifier;
import hdgl.db.query.expression.Query;
import hdgl.db.query.expression.QuestionQuantifier;
import hdgl.db.query.expression.Vertex;

import hdgl.db.query.visitor.Visitor._void;

public class IdentifyEntitiesVisitor implements Visitor<_void, _void> {

	ArrayList<Entity> ids = new ArrayList<Entity>();
	
	public Map<Integer, Entity> getIdMap(){
		Map<Integer, Entity> res = new HashMap<Integer, Entity>();
		for (int i = 0; i < ids.size(); i++) {
			res.put(i, ids.get(i));
		}
		return res;
	}
	
	public Map<Entity, Integer> getEntityMap(){
		Map<Entity, Integer> res = new HashMap<Entity, Integer>();
		for (int i = 0; i < ids.size(); i++) {
			res.put(ids.get(i), i);
		}
		return res;
	}
	
	@Override
	public _void visitQuery(Query query, _void... arguments) {
		query.getExpression().accept(this);
		ids.add(query.getEOF());
		return null;
	}

	@Override
	public _void visitVertex(Vertex vertex, _void... arguments) {
		ids.add(vertex);
		return null;
	}

	@Override
	public _void visitEdge(Edge edge, _void... arguments) {
		ids.add(edge);
		return null;
	}

	@Override
	public _void visitAsteriskQuantifier(AsteriskQuantifier quantifier,
			_void... arguments) {
		quantifier.getQuantified().accept(this);
		return null;
	}

	@Override
	public _void visitQuestionQuantifier(QuestionQuantifier quantifier,
			_void... arguments) {
		quantifier.getQuantified().accept(this);
		return null;
	}

	@Override
	public _void visitPlusQuantifier(PlusQuantifier quantifier,
			_void... arguments) {
		quantifier.getQuantified().accept(this);
		return null;
	}

	@Override
	public _void visitConcat(Concat concat, _void... arguments) {
		concat.getFirst().accept(this);
		concat.getSecond().accept(this);
		return null;
	}

	@Override
	public _void visitCondition(Condition cond, _void... arguments) {
		return null;
	}

	@Override
	public _void visitOrder(Order order, _void... arguments) {
		return null;
	}

	@Override
	public _void visitParallel(Parallel parallel, _void... arguments) {
		parallel.getFirst().accept(this);
		parallel.getSecond().accept(this);
		return null;
	}

	
	
}

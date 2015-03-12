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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LastPosVisitor implements Visitor<Set<Integer>, _void> {

	Map<Entity, Integer> idsMap;
	Map<Expression, Boolean> nullable;
	Map<Expression, Set<Integer>> lastpos = new HashMap<Expression, Set<Integer>>();
	
	public LastPosVisitor(Map<Entity, Integer> idsMap,
			Map<Expression, Boolean> nullable){
		this.idsMap = idsMap;
		this.nullable = nullable;
	}
	
	
	public Map<Expression, Set<Integer>> getLastPos(){
		return lastpos;
	}	
	
	
	@Override
	public Set<Integer> visitQuery(Query query, _void... arguments) {
		Set<Integer> n = new HashSet<Integer>();
		query.getExpression().accept(this);
		n.add(idsMap.get(query.getEOF()));
		lastpos.put(query, n);
		Set<Integer> hash = new HashSet<Integer>();
		hash.add(idsMap.get(query.getEOF()));
		lastpos.put(query.getEOF(), hash);
		return n;
	}

	@Override
	public Set<Integer> visitVertex(Vertex vertex, _void... arguments) {
		Set<Integer> set = new HashSet<Integer>();
		set.add(idsMap.get(vertex));
		lastpos.put(vertex, set);
		return set;
	}

	@Override
	public Set<Integer> visitEdge(Edge edge, _void... arguments) {
		Set<Integer> set = new HashSet<Integer>();
		set.add(idsMap.get(edge));
		lastpos.put(edge, set);
		return set;
	}

	@Override
	public Set<Integer> visitAsteriskQuantifier(AsteriskQuantifier quantifier,
			_void... arguments) {
		Set<Integer> n = new HashSet<Integer>();
		Set<Integer> sub = quantifier.getQuantified().accept(this);
		n.addAll(sub);
		lastpos.put(quantifier, n);
		return n;
	}

	@Override
	public Set<Integer> visitQuestionQuantifier(QuestionQuantifier quantifier,
			_void... arguments) {
		Set<Integer> n = new HashSet<Integer>();
		Set<Integer> sub = quantifier.getQuantified().accept(this);
		n.addAll(sub);
		lastpos.put(quantifier, n);
		return n;
	}

	@Override
	public Set<Integer> visitPlusQuantifier(PlusQuantifier quantifier,
			_void... arguments) {
		Set<Integer> n = new HashSet<Integer>();
		Set<Integer> sub = quantifier.getQuantified().accept(this);
		n.addAll(sub);
		lastpos.put(quantifier, n);
		return n;
	}

	@Override
	public Set<Integer> visitConcat(Concat concat, _void... arguments) {
		Set<Integer> n = new HashSet<Integer>();
		Set<Integer> first = concat.getFirst().accept(this);
		Set<Integer> last = concat.getSecond().accept(this);
		if(nullable.get(concat.getSecond())){
			n.addAll(first);
			n.addAll(last);
		}else{
			n.addAll(last);
		}
		lastpos.put(concat, n);
		return n;
	}

	@Override
	public Set<Integer> visitCondition(Condition cond, _void... arguments) {
		return null;
	}

	@Override
	public Set<Integer> visitOrder(Order order, _void... arguments) {
		return null;
	}

	@Override
	public Set<Integer> visitParallel(Parallel parallel, _void... arguments) {
		Set<Integer> n = new HashSet<Integer>();
		Set<Integer> first = parallel.getFirst().accept(this);
		Set<Integer> last = parallel.getSecond().accept(this);
		n.addAll(first);
		n.addAll(last);
		lastpos.put(parallel, n);
		return n;
	}
	
}

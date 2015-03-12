package hdgl.db.query.visitor;

import hdgl.db.query.expression.AsteriskQuantifier;
import hdgl.db.query.expression.Concat;
import hdgl.db.query.expression.Condition;
import hdgl.db.query.expression.Edge;
import hdgl.db.query.expression.Order;
import hdgl.db.query.expression.Parallel;
import hdgl.db.query.expression.PlusQuantifier;
import hdgl.db.query.expression.Quantifier;
import hdgl.db.query.expression.Query;
import hdgl.db.query.expression.QuestionQuantifier;
import hdgl.db.query.expression.Expression;
import hdgl.db.query.expression.Vertex;

public class CloneVisitor implements Visitor<Expression, Object> {

	@Override
	public Expression visitVertex(Vertex vertex, Object... args) {
		Vertex nVertex = new Vertex();
		Condition[] conditions = vertex.getConditions();
		Condition[] nConditions;
		if(conditions!=null){
			nConditions = new Condition[conditions.length];
			for(int i=0; i<conditions.length; i++){
				nConditions[i] = (Condition) conditions[i].accept(this);
			}
		}else{
			nConditions = null;
		}
		nVertex.setType(vertex.getType());
		nVertex.setConditions(nConditions);
		if(vertex.getOrder()!=null){
			nVertex.setOrder((Order) vertex.getOrder().accept(this));
		}
		return nVertex;
	}

	@Override
	public Expression visitEdge(Edge edge, Object... args) {
		Edge nEdge = new Edge();
		Condition[] conditions = edge.getConditions();
		Condition[] nConditions;
		if(conditions!=null){
			nConditions = new Condition[conditions.length];
			for(int i=0; i<conditions.length; i++){
				nConditions[i] = (Condition) conditions[i].accept(this);
			}
		}else{
			nConditions = null;
		}
		nEdge.setType(edge.getType());
		nEdge.setConditions(nConditions);
		if(edge.getOrder()!=null){
			nEdge.setOrder((Order) edge.getOrder().accept(this));
		}
		return nEdge;
	}

	@Override
	public Expression visitAsteriskQuantifier(AsteriskQuantifier quantifier, Object... args) {
		Quantifier n = new AsteriskQuantifier();
		n.setQuantified(quantifier.getQuantified().accept(this));
		return n;
	}

	@Override
	public Expression visitQuestionQuantifier(QuestionQuantifier quantifier, Object... args) {
		Quantifier n = new QuestionQuantifier();
		n.setQuantified(quantifier.getQuantified().accept(this));
		return n;
	}

	@Override
	public Expression visitPlusQuantifier(PlusQuantifier quantifier, Object... args) {
		Quantifier n = new PlusQuantifier();
		n.setQuantified(quantifier.getQuantified().accept(this));
		return n;
	}

	@Override
	public Expression visitConcat(Concat concat, Object... args) {
		Concat cat=new Concat();
		cat.setFirst(concat.getFirst().accept(this));
		cat.setSecond(concat.getSecond().accept(this));
		return cat;
	}

	@Override
	public Expression visitCondition(Condition cond, Object... args) {
		Condition condition = new Condition();
		condition.setLabel(cond.getLabel());
		condition.setOp(cond.getOp());
		condition.setValue(cond.getValue());
		return condition;
	}

	@Override
	public Expression visitOrder(Order order, Object... args) {
		Order nOrder = new Order();
		nOrder.setLabel(order.getLabel());
		nOrder.setAscending(order.isAscending());
		return nOrder;
	}

	@Override
	public Expression visitParallel(Parallel parallel, Object... args) {
		Parallel nParallel=new Parallel();
		nParallel.setFirst(parallel.getFirst().accept(this));
		nParallel.setSecond(parallel.getSecond().accept(this));
		return nParallel;
	}

	@Override
	public Expression visitQuery(Query query, Object... args) {
		return new Query(query.getExpression().accept(this));
	}

}

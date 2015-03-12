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

public class CompleteQueryVisitor implements Visitor<CompleteQueryVisitor.ExpressionWithFrom, CompleteQueryVisitor.PathForm> {
	
	public static Expression complete(Expression expression){
		CompleteQueryVisitor v = new CompleteQueryVisitor();
		ExpressionWithFrom r = expression.accept(v, PathForm.Vertex, PathForm.Vertex);
		assert PathForm.Vertex.compatible(r.getHead()) && PathForm.Vertex.compatible(r.getTail());
		return r.getExpression();
	}
	
	public static class ExpressionWithFrom{
		private PathForm head;
		private PathForm tail;
		private Expression expression;
		
		public ExpressionWithFrom(PathForm head, PathForm tail,
				Expression expression) {
			super();
			this.head = head;
			this.tail = tail;
			this.expression = expression;
		}
		public PathForm getHead() {
			return head;
		}
		public void setHead(PathForm head) {
			this.head = head;
		}
		public PathForm getTail() {
			return tail;
		}
		public void setTail(PathForm tail) {
			this.tail = tail;
		}
		public Expression getExpression() {
			return expression;
		}
		public void setExpression(Expression expression) {
			this.expression = expression;
		}
	}
	
	public static enum PathForm{
		Any, Vertex, Edge;
		
		public PathForm other(){
			switch (this) {
			case Any:
				return Any;
			case Vertex:
				return Edge;
			case Edge:
				return Vertex;
			default:
				throw new RuntimeException("Never should this exception happen");
			}
		}
		
		public boolean compatible(PathForm other){
			switch (this) {
			case Any:
				return true;
			case Vertex:
				return other==Vertex;
			case Edge:
				return other==Edge;
			default:
				throw new RuntimeException("Never should this exception happen");
			}
		}
	}

	@Override
	public ExpressionWithFrom visitQuery(Query query, PathForm... arguments) {
		ExpressionWithFrom inner = query.getExpression().accept(this, PathForm.Vertex, PathForm.Vertex);
		assert PathForm.Vertex.compatible(inner.getHead()) && PathForm.Vertex.compatible(inner.getTail());
		return new ExpressionWithFrom(PathForm.Vertex, PathForm.Vertex, 
				new Query(inner.getExpression()));
	}

	@Override
	public ExpressionWithFrom visitVertex(Vertex vertex, PathForm... arguments) {
		PathForm head = arguments[0];
		if (head == PathForm.Any)
			head = PathForm.Vertex;
		PathForm tail = arguments[1];
		if (tail == PathForm.Any)
			tail = PathForm.Vertex;
		Expression n = vertex.clone(Expression.class);
		if(head == PathForm.Edge){
			n = new Concat(Edge.UNRESTRICTED.clone(Expression.class), n);
		}
		if(tail==PathForm.Edge){
			n = new Concat(n, Edge.UNRESTRICTED.clone(Expression.class));
		}
		return new ExpressionWithFrom(head, tail, n);
	}

	@Override
	public ExpressionWithFrom visitEdge(Edge edge, PathForm... arguments) {
		PathForm head = arguments[0];
		if (head == PathForm.Any)
			head = PathForm.Edge;
		PathForm tail = arguments[1];
		if (tail == PathForm.Any)
			tail = PathForm.Edge;
		Expression n = edge.clone(Expression.class);
		if(head == PathForm.Vertex){
			n = new Concat(Vertex.UNRESTRICTED.clone(Expression.class), n);
		}
		if(tail==PathForm.Vertex){
			n = new Concat(n, Vertex.UNRESTRICTED.clone(Expression.class));
		}
		return new ExpressionWithFrom(head, tail, n);
	}

	
	
	private ExpressionWithFrom visitQuantifier(Quantifier quantifier,
			PathForm... arguments) {
		PathForm head = arguments[0];
		PathForm tail = arguments[1];
		ExpressionWithFrom subexpression = quantifier.getQuantified().accept(this, head, head.other());
		assert head.compatible(subexpression.getHead()) && head.other().compatible(subexpression.getTail());
		if(tail.compatible(subexpression.getTail())){
			return new ExpressionWithFrom(subexpression.getHead(), subexpression.getTail(), 
					new AsteriskQuantifier(subexpression.getExpression()));
		}else{
			if(tail == PathForm.Edge){
				return new ExpressionWithFrom(subexpression.getHead(), PathForm.Edge, 
							new Concat(
								new AsteriskQuantifier(subexpression.getExpression()),
								Edge.UNRESTRICTED.clone(Expression.class))
					   );
			}else if(tail == PathForm.Vertex){
				return new ExpressionWithFrom(subexpression.getHead(), PathForm.Vertex, 
						new Concat(
							new AsteriskQuantifier(subexpression.getExpression()),
							Vertex.UNRESTRICTED.clone(Expression.class))
				   );
			}else{
				throw new RuntimeException("Never should this exception happen.");
			}
		}
	}

	@Override
	public ExpressionWithFrom visitAsteriskQuantifier(
			AsteriskQuantifier quantifier, PathForm... arguments) {
		return visitQuantifier(quantifier, arguments);
	}
	
	@Override
	public ExpressionWithFrom visitQuestionQuantifier(QuestionQuantifier quantifier,
			PathForm... arguments) {
		return visitQuantifier(quantifier, arguments);
	}

	@Override
	public ExpressionWithFrom visitPlusQuantifier(PlusQuantifier quantifier,
			PathForm... arguments) {
		return visitQuantifier(quantifier, arguments);
	}

	@Override
	public ExpressionWithFrom visitConcat(Concat concat, PathForm... arguments) {
		PathForm head = arguments[0];
		PathForm tail = arguments[1];
		ExpressionWithFrom first = concat.getFirst().accept(this, head, PathForm.Any);
		assert head.compatible(first.getHead());
		ExpressionWithFrom second = concat.getSecond().accept(this, first.getTail().other(), tail);
		assert first.getTail().other().compatible(second.getHead()) && tail.compatible(second.getTail());
		return new ExpressionWithFrom(first.getHead(), second.getTail(),
				new Concat(first.getExpression(), second.getExpression()));
	}

	@Override
	public ExpressionWithFrom visitCondition(Condition cond, PathForm... arguments) {
		throw new RuntimeException("Never should this exception happen.");
	}

	@Override
	public ExpressionWithFrom visitOrder(Order order, PathForm... arguments) {
		throw new RuntimeException("Never should this exception happen.");
	}

	@Override
	public ExpressionWithFrom visitParallel(Parallel parallel, PathForm... arguments) {
		PathForm head = arguments[0];
		PathForm tail = arguments[1];
		ExpressionWithFrom first = parallel.getFirst().accept(this, head, tail);
		assert head.compatible(first.getHead()) && tail.compatible(first.getTail());
		ExpressionWithFrom second = parallel.getSecond().accept(this, first.getHead(), first.getTail());
		assert first.getHead().compatible(second.getHead()) && first.getTail().compatible(second.getTail());
		return new ExpressionWithFrom(first.getHead(), first.getTail(), 
				new Parallel(first.getExpression(), second.getExpression()));
	}
	
	
}

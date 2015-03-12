package hdgl.db.query.visitor;

import hdgl.db.query.expression.*;

public interface Visitor<TR, TA> {
	
	/**
	 * a place-holder class which indicates no input or output parameters
	 * _void is not instantiable so you can only use "null" for a _void;
	 * @author elm
	 *
	 */
	public static final class _void{		
		private _void(){
			throw new InstantiationError("_void is not instantiable.");			
		}	
	}
	
	public TR visitQuery(Query query, TA... arguments);
	
	public TR visitVertex(Vertex vertex, TA... arguments);
	
	public TR visitEdge(Edge edge, TA... arguments);
	
	public TR visitAsteriskQuantifier(AsteriskQuantifier quantifier, TA... arguments);
	
	public TR visitQuestionQuantifier(QuestionQuantifier quantifier, TA... arguments);
	
	public TR visitPlusQuantifier(PlusQuantifier quantifier, TA... arguments);
	
	public TR visitConcat(Concat concat, TA... arguments);
	
	public TR visitCondition(Condition cond, TA... arguments);
	
	public TR visitOrder(Order order, TA... arguments);
	
	public TR visitParallel(Parallel parallel, TA... arguments);
}

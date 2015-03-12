package hdgl.db.query.expression;

import hdgl.db.query.condition.AbstractValue;
import hdgl.db.query.visitor.CloneVisitor;
import hdgl.db.query.visitor.Visitor;

public abstract class Expression implements Cloneable {

	public abstract <TR, TA> TR accept(Visitor<TR, TA> visitor, TA... arguments);
	
	public static Entity buildEntity(String type, Order order, Condition[] conditions, String ofType) throws BadExpressionException{
		Entity e;
		if(type.equals(".")){
			e = new Vertex();
			e.setOrder(order);
			e.setConditions(conditions);
			e.setType(ofType);
		}else if(type.equals("-")){
			e = new Edge();
			e.setOrder(order);
			e.setConditions(conditions);
			e.setType(ofType);
		}else{
			throw new BadExpressionException();
		}		
		return e;
	}
	
	public static Expression buildParallel(Expression... selectors) throws BadExpressionException{
		if(selectors.length == 0){
			throw new BadExpressionException();
		}
		Expression basic = selectors[0];
		for(int i=1;i<selectors.length;i++){
			Parallel p = new Parallel();
			p.setFirst(basic);
			p.setSecond(selectors[i]);
			basic = p;
		}
		return basic;
	}
	
	public static Expression buildConcat(Expression... selectors) throws BadExpressionException{
		if(selectors.length == 0){
			throw new BadExpressionException();
		}
		Expression basic = selectors[0];
		for(int i=1;i<selectors.length;i++){
			Concat p = new Concat();
			p.setFirst(basic);
			p.setSecond(selectors[i]);
			basic = p;
		}
		return basic;
	}
	
	public static Quantifier buildQuantifier(String type, Expression quantified) throws BadExpressionException{
		Quantifier q;
		if(type.equals("?")){
			q = new QuestionQuantifier();
			q.setQuantified(quantified);
		}else if(type.equals("*")){
			q = new AsteriskQuantifier();
			q.setQuantified(quantified);
		}else if(type.equals("+")){
			q = new PlusQuantifier();
			q.setQuantified(quantified);
		}else{
			throw new BadExpressionException();
		}
		return q;
	}
	
	public static Condition buildCondition(String label, String op, AbstractValue value){
		Condition condition = new Condition();
		condition.setLabel(label);
		condition.setOp(op);
		condition.setValue(value);
		return condition;
	}
	
	public static Order buildOrder(String label, String order) throws BadExpressionException{
		boolean isAsc;
		if(order.equalsIgnoreCase("asc")||order.equals("<")||order.equals("<=")){
			isAsc = true;
		}else if(order.equalsIgnoreCase("desc")||order.equals(">")||order.equals(">=")){
			isAsc = false;
		}else{
			throw new BadExpressionException();
		}
		Order o = new Order();
		o.setLabel(label);
		o.setAscending(isAsc);
		return o;
	}
	
	public static Query buildQuery(Expression expression){
		return new Query(expression);
	}
	
	@Override
	public Object clone(){
		return this.accept(new CloneVisitor());
	}
	
	public <T> T clone(Class<T> type){
		return type.cast(this.clone());
	}
	
}

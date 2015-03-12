package hdgl.db.query.parser;

import java.util.ArrayList;

import hdgl.db.query.expression.BadExpressionException;
import hdgl.db.query.expression.Condition;
import hdgl.db.query.expression.Order;

public class Util {
	
	public static OrderAndConditions combineOrderAndConditions(Iterable<OrderAndCondition> list) throws BadExpressionException{
		Order order = null;
		ArrayList<Condition> conditions = new ArrayList<Condition>();
		for(OrderAndCondition e:list){
			if(e.getOrder()!=null){
				if(order!=null){
					throw new BadExpressionException();
				}
				order = e.getOrder();
			}
			if(e.condition!=null){
				conditions.add(e.getCondition());
			}
		}
		return new OrderAndConditions(order, conditions.toArray(new Condition[0]));
	}
	
	public static class OrderAndConditions{
		Order order;
		Condition[] conditions;
		public OrderAndConditions(Order order, Condition[] conditions) {
			super();
			this.order = order;
			this.conditions = conditions;
		}
		
		public Order getOrder() {
			return order;
		}
		public void setOrder(Order order) {
			this.order = order;
		}
		public Condition[] getConditions() {
			return conditions;
		}
		public void setConditions(Condition[] conditions) {
			this.conditions = conditions;
		}
	}
	
	public static class OrderAndCondition{
		Order order;
		Condition condition;
		
		public OrderAndCondition(Order order, Condition condition) {
			super();
			this.order = order;
			this.condition = condition;
		}
		
		public Order getOrder() {
			return order;
		}
		public void setOrder(Order order) {
			this.order = order;
		}
		public Condition getCondition() {
			return condition;
		}
		public void setCondition(Condition condition) {
			this.condition = condition;
		}
	}
	
}

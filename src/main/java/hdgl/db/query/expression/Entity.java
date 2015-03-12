package hdgl.db.query.expression;

import hdgl.db.query.condition.AbstractCondition;
import hdgl.db.query.condition.OfType;

public abstract class Entity extends Expression {

	protected Order order;
	protected Condition[] conditions;
	protected String type;
	
	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Condition[] getConditions() {
		if(conditions!=null){
			return conditions.clone();
		}else{
			return null;
		}
	}

	public void setConditions(Condition[] conditions) {
		this.conditions = conditions!=null?conditions.clone():null;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public AbstractCondition[] getAbstractConditions(){
		if(type!=null&&type.length()>0){
			AbstractCondition[] res = new AbstractCondition[conditions==null?1:conditions.length+1];
			res[0]=new OfType(getType());
			if(conditions!=null){
				for(int i=0;i<conditions.length;i++){
					res[i+1] = conditions[i].getCondition();
				}
			}
			return res;
		}else{
			AbstractCondition[] res = new AbstractCondition[conditions==null?0:conditions.length];
			if(conditions!=null){
				for(int i=0;i<conditions.length;i++){
					res[i] = conditions[i].getCondition();
				}
			}
			return res;
		}
	}
	
}

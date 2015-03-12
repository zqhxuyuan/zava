//package hdgl.db.query.visitor;
//
//import java.util.HashMap;
//
//import hdgl.db.query.expression.Expression;
//
//public abstract class LabelledVisitor<TR> implements Visitor<TR>{
//
//	
//	HashMap<String, HashMap<Expression, Object>> values = new HashMap<String, HashMap<Expression,Object>>();
//	
//	public void setLabel(Expression entity, String name, Object value){
//		if(!values.containsKey(name)){
//			HashMap<Expression, Object> val = new HashMap<Expression, Object>();
//			val.put(entity, value);
//			values.put(name, val);
//		}else{
//			values.get(name).put(entity, value);
//		}
//	}
//	
//	public Object getLabel(Expression entity, String name){
//		if(!values.containsKey(name)){
//			return null;
//		}else{
//			HashMap<Expression, Object> val = values.get(name);
//			if(!val.containsKey(entity)){
//				return null;
//			}else{
//				return val.get(entity);
//			}
//		}
//	}
//	
//	public <T> T getLabel(Expression entity, String name, Class<T> type){
//		return type.cast(getLabel(entity, name));
//	}
//	
//}

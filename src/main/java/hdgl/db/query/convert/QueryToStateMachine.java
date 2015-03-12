package hdgl.db.query.convert;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import hdgl.db.query.condition.AbstractCondition;
import hdgl.db.query.condition.NoRestriction;
import hdgl.db.query.expression.Edge;
import hdgl.db.query.expression.Entity;
import hdgl.db.query.expression.Expression;
import hdgl.db.query.expression.Query;
import hdgl.db.query.expression.Vertex;
import hdgl.db.query.stm.SimpleStateMachine;
import hdgl.db.query.stm.SimpleStateMachineWithData;
import hdgl.db.query.visitor.FirstPosVisitor;
import hdgl.db.query.visitor.FollowPosVisitor;
import hdgl.db.query.visitor.IdentifyEntitiesVisitor;
import hdgl.db.query.visitor.LastPosVisitor;
import hdgl.db.query.visitor.NullableVisitor;

public class QueryToStateMachine {

	static class SimpleState{
		
		Set<Integer> pos;
		
		Map<Entity, SimpleState> translates = new HashMap<Entity, QueryToStateMachine.SimpleState>();
		
		boolean isVertex;
		boolean isSuccess;
		
		public boolean isSuccess() {
			return isSuccess;
		}

		public boolean isVertex() {
			return isVertex;
		}

		public SimpleState(Set<Integer> pos, boolean isVertex, boolean isSuccess){
			this.pos = pos;
			this.isVertex = isVertex;
			this.isSuccess = isSuccess;
		}
		
		@Override
		public int hashCode() {
			return pos.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SimpleState other = (SimpleState) obj;
			if (pos == null) {
				if (other.pos != null)
					return false;
			} else if (!pos.equals(other.pos))
				return false;
			return true;
		}
		
		public Set<Integer> getPos() {
			return pos;
		}

		public Map<Entity, SimpleState> getTranslates() {
			return translates;
		}
		
		@Override
		public String toString() {
			return pos.toString();
		}
		
	}
	
	public static SimpleStateMachine convert(Expression query){
		IdentifyEntitiesVisitor visitor = new IdentifyEntitiesVisitor();
		query.accept(visitor);
		Map<Entity, Integer> idsMap = visitor.getEntityMap();
		Map<Integer, Entity> idsRevMap = visitor.getIdMap();
		Map<Integer, AbstractCondition[]> condMap = new HashMap<Integer, AbstractCondition[]>();
		for (Map.Entry<Integer, Entity> i : idsRevMap.entrySet()) {
			condMap.put(i.getKey(), i.getValue().getAbstractConditions());
		}
//		for(Map.Entry<Integer, Entity> i:visitor.getIdMap().entrySet()){
//			assert i.getKey().equals(idsMap.get(i.getValue()));
//			System.out.println(i.getKey()+": "+i.getValue());
//		}
		
		NullableVisitor visitor2 = new NullableVisitor(idsMap);
		query.accept(visitor2);
//		for(Map.Entry<Expression, Boolean> i:visitor2.getNullableMap().entrySet()){
//			System.out.println(i.getKey()+": "+i.getValue());
//		}
		Map<Expression, Boolean> nullable = visitor2.getNullableMap();
		
		FirstPosVisitor v3 = new FirstPosVisitor(idsMap, nullable);
		LastPosVisitor v4 = new LastPosVisitor(idsMap, nullable);		
		query.accept(v3);
		query.accept(v4);		
		Map<Expression, Set<Integer>> firstpos = v3.getFirstPos();
		Map<Expression, Set<Integer>> lastpos = v4.getLastPos();
		
//		for(Entry<Expression, Set<Integer>> i: firstpos.entrySet()){
//			System.out.println(i.getKey()+": "+i.getValue()+" - "+lastpos.get(i.getKey()));
//		}
		
		FollowPosVisitor v5 = new FollowPosVisitor(idsMap, idsRevMap, nullable, firstpos, lastpos);
		query.accept(v5);
		Map<Entity, Set<Integer>> followpos = v5.getFollowPos();
//		for(Entry<Entity, Set<Integer>> i: followpos.entrySet()){
//			System.out.println(idsMap.get(i.getKey())+": "+i.getValue());
//		}
		
		Set<AbstractCondition> vcs = new HashSet<AbstractCondition>();
		Set<AbstractCondition> ecs = new HashSet<AbstractCondition>();
		
		for(Entity e:idsMap.keySet()){
			if(e instanceof Vertex){
				for (AbstractCondition ac : e.getAbstractConditions()) {
					vcs.add(ac);
				}
			}else if(e instanceof Edge){
				for (AbstractCondition ac : e.getAbstractConditions()) {
					ecs.add(ac);
				}
			}
		}
		if(!vcs.contains(NoRestriction.I)){
			vcs.add(NoRestriction.I);
		}
		if(!ecs.contains(NoRestriction.I)){
			ecs.add(NoRestriction.I);
		}
		AbstractCondition[] valphabet = SortConditions.sortConditions(vcs.toArray(new AbstractCondition[0]));
		AbstractCondition[] ealphabet = SortConditions.sortConditions(ecs.toArray(new AbstractCondition[0]));
		
//		for (int i = 0; i < valphabet.length; i++) {
//			System.out.println("v["+i+"]\t"+valphabet[i]);
//		}
//		for (int i = 0; i < valphabet.length; i++) {
//			System.out.println("e["+i+"]\t"+ealphabet[i]);
//		}
		Set<SimpleState> openStates = new HashSet<SimpleState>();
		Set<SimpleState> closedStates = new HashSet<SimpleState>();
		Integer successPos = idsMap.get(((Query)query).getEOF());
		
		Set<Integer> start = firstpos.get(query);
		SimpleState startState = new SimpleState(start, true, false);
		openStates.add(startState);
		SimpleStateMachineWithData<SimpleState> stm = new SimpleStateMachineWithData<SimpleState>(ealphabet, valphabet);
		stm.addState(startState, startState.isSuccess());
		while(!openStates.isEmpty()){
			SimpleState one = openStates.iterator().next();
			openStates.remove(one);
			closedStates.add(one);			
			AbstractCondition[] alphabet = one.isVertex()?valphabet:ealphabet;
			for(int i=0; i<alphabet.length; i++){
				Set<Integer> next = new HashSet<Integer>();
				AbstractCondition input = alphabet[i];
				boolean isSuccess = false;
				//for(Entry<Integer, AbstractCondition[]> ec:condMap.entrySet()){
				for(Integer inpos:one.pos){
					AbstractCondition[] conds=condMap.get(inpos);
					
					boolean satisfy=true;
					for(AbstractCondition ac:conds){
						if(!input.require(ac)){
							satisfy = false;
							break;
						}
					}
					if(satisfy){
						Set<Integer> follows = followpos.get(idsRevMap.get(inpos));
						if(follows.contains(successPos)){
							for (Integer integer : follows) {
								if(!integer.equals(successPos)){
									next.add(integer);
								}
							}
							isSuccess = true;
						}else{
							next.addAll(follows);
						}
					}
				}
				if(next.size()>0||isSuccess){
					SimpleState nextState = new SimpleState(next, !one.isVertex(), isSuccess);
					int n;
					if((n=stm.findState(nextState))<0){
						openStates.add(nextState);
						n=stm.addState(nextState, nextState.isSuccess());
					}
					stm.addTransition(stm.findState(one), n, i, one.isVertex());
				}
			}
		}
		SimpleStateMachine rstm = stm.removeData();
		rstm.minimize();
		return rstm;
	}
}

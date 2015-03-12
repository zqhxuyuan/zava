package hdgl.db.query.stm;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import hdgl.db.query.condition.AbstractCondition;
import hdgl.db.query.condition.NoRestriction;
import hdgl.db.query.stm.StateMachine.Condition;
import hdgl.db.query.stm.StateMachine.State;
import hdgl.db.query.stm.StateMachine.Transition;
import hdgl.db.query.stm.StateMachine.TransitionType;

public class SimpleStateMachine {
	
	AbstractCondition[] edgeAlphabet;
	AbstractCondition[] vertexAlphabet;
	
	int maxstate=1;
	Set<Integer> states = new HashSet<Integer>();
	Map<Integer, int[]> transitions = new HashMap<Integer, int[]>();
	Map<Integer, Boolean> success = new HashMap<Integer, Boolean>();
	
	public SimpleStateMachine(AbstractCondition[] edgeAlphabet,
			AbstractCondition[] vertexAlphabet) {
		super();
		this.edgeAlphabet = edgeAlphabet;
		this.vertexAlphabet = vertexAlphabet;
	}
	
	public AbstractCondition[] getVertexAlphabet() {
		return vertexAlphabet;
	}
	public void setVertexAlphabet(AbstractCondition[] vertexAlphabet) {
		this.vertexAlphabet = vertexAlphabet;
	}
	public AbstractCondition[] getEdgeAlphabet() {
		return edgeAlphabet;
	}
	public void setEdgeAlphabet(AbstractCondition[] edgeAlphabet) {
		this.edgeAlphabet = edgeAlphabet;
	}
	
	public int getStates() {
		return states.size();
	}
	
	public int addState(boolean isSuccess) {
		int nstate = maxstate;
		transitions.put(nstate,new int[vertexAlphabet.length+edgeAlphabet.length]);
		success.put(nstate,isSuccess);
		states.add(nstate);
		maxstate++;
		return nstate;
	}
	
	public void addTransition(int inState, int toState, int input, boolean isVertex){
		if(!isVertex){
			input += vertexAlphabet.length;
		}		
		transitions.get(inState)[input] = toState;
	}
	
	public void print(PrintStream out){
		out.print("states\t");
		for (AbstractCondition ac : vertexAlphabet) {
			out.print(ac+"\t");
		}
		out.print("|");
		for (AbstractCondition ac : edgeAlphabet) {
			out.print("\t"+ac);
		}
		out.println();
		for(int i:states){
			out.print(i+(success.get(i)?"!":"")+"\t");
			for (int j = 0; j < vertexAlphabet.length; j++) {
				out.print(transitions.get(i)[j]+"\t");
			}
			out.print("|");
			for (int j = 0; j < edgeAlphabet.length; j++) {
				out.print("\t"+transitions.get(i)[j+vertexAlphabet.length]);
			}
			out.println();
		}		
	}
	
	public void minimize(){
		boolean doing = true;
		w:while (doing) {
			doing = false;
			for (Map.Entry<Integer, int[]> tr1 : transitions.entrySet()) {
				for (Map.Entry<Integer, int[]> tr2 : transitions.entrySet()) {
					if(!tr1.getKey().equals(tr2.getKey())){
						if(success.get(tr1.getKey()).equals(success.get(tr2.getKey())) &&
							Arrays.equals(tr1.getValue(), tr2.getValue())){
							compact(tr1.getKey(), tr2.getKey());
							doing = true;
							continue w;
						}
					}
				}
			}
		}		
	}
	
	void compact(int s1, int s2){
		for (Map.Entry<Integer, int[]> tr : transitions.entrySet()) {
			int[] r=tr.getValue();
			for (int i = 0; i < r.length; i++) {
				if(r[i]==s2)r[i]=s1;
			}
		}
		success.remove(s2);
		transitions.remove(s2);
		states.remove(s2);
	}
	
	public StateMachine buildStateMachine(){
		StateMachine stm = new StateMachine();
		Map<Integer, Integer> newStateIds = new HashMap<Integer, Integer>();
		Queue<Integer> states =new LinkedList<Integer>();
		Set<Integer> closedStateSet = new HashSet<Integer>();
		states.offer(-1);
		//newStateIds.put(-1, 1);
		
		while (states.size()>0) {
			int stateId = states.poll();
			closedStateSet.add(stateId);
			int newStateId;
			StateMachine.State state = stm.getState(newStateId = stm.addState());
			newStateIds.put(stateId, newStateId);
			List<StateMachine.Condition> conds = buildConditions(-stateId);
			for(Condition cond:conds){
				for(Transition t:cond.getTransitions()){
					int next = t.getToState();
					if(newStateIds.containsKey(next)){
						next = newStateIds.get(next);
						t.setToState(next);						
					}
					if(next<0 && !states.contains(next) && !closedStateSet.contains(next)){
						states.offer(next);
					}
				}
				state.addCondition(cond);
			}
		}	
		for(State s:stm.getStates()){
			for(Condition c:s.getConditions()){
				for(Transition t:c.getTransitions()){
					int next = t.getToState();
					if(next<0){
						t.setToState(newStateIds.get(next));
					}
				}
			}
		}
		
		return stm;
	}
	
	List<StateMachine.Condition> buildConditions(int startState){
		ArrayList<Condition> conditions = new ArrayList<StateMachine.Condition>();
		int[] trans = this.transitions.get(startState);
		
		int prevTrans = -1;
		AbstractCondition prevCondition = null;
		for(int i = 0;i < vertexAlphabet.length;i++){
			if(trans[i] != prevTrans || 
					!prevCondition.require(vertexAlphabet[i])){
				if(prevCondition!=null && prevTrans!=0){
					StateMachine.Condition cond= new StateMachine.Condition(prevCondition);
					for(Transition t:buildTransitions(prevTrans)){
						cond.addTransition(t);
					}
					conditions.add(cond);
				}
				prevTrans = trans[i];
				prevCondition = vertexAlphabet[i];
			}else{
				prevCondition = vertexAlphabet[i];
			}
		}
		if(prevCondition!=null&&prevTrans!=0){
			StateMachine.Condition cond= new StateMachine.Condition(prevCondition);
			for(Transition t:buildTransitions(prevTrans)){
				cond.addTransition(t);
			}
			conditions.add(cond);
		}
		return conditions;
	}
	
	List<StateMachine.Transition> buildTransitions(int startState){
		ArrayList<StateMachine.Transition> transitions = new ArrayList<StateMachine.Transition>();
		int[] trans = this.transitions.get(startState);
		
		if(success.get(startState)){
			StateMachine.Transition transition = new Transition(TransitionType.Success, NoRestriction.I, 0);
			transitions.add(transition);
		}
		
		int prevTrans = -1;
		AbstractCondition prevCondition = null;
		for(int i = 0;i < edgeAlphabet.length;i++){
			if(trans[i+vertexAlphabet.length] != prevTrans || 
					!prevCondition.require(edgeAlphabet[i])){
				if(prevCondition!=null && prevTrans!=0){
					StateMachine.Transition transition= new Transition(TransitionType.Out, prevCondition, -prevTrans);
					transitions.add(transition);
				}
				prevTrans = trans[i+vertexAlphabet.length];
				prevCondition = edgeAlphabet[i];
			}else{
				prevCondition = edgeAlphabet[i];
			}
		}
		if(prevCondition != null && prevTrans!=0){
			StateMachine.Transition transition = new Transition(TransitionType.Out, prevCondition, -prevTrans);
			transitions.add(transition);
		}
		return transitions;
	}
	
}

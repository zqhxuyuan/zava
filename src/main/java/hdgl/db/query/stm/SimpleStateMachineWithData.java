package hdgl.db.query.stm;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import hdgl.db.query.condition.AbstractCondition;

public class SimpleStateMachineWithData<T> {
	
	
	
	AbstractCondition[] edgeAlphabet;
	AbstractCondition[] vertexAlphabet;
	
	int states = 1;
	Map<Integer, int[]> transitions = new HashMap<Integer, int[]>();
	Map<Integer, T> stateData = new HashMap<Integer, T>();
	Map<Integer, Boolean> success = new HashMap<Integer, Boolean>();
	
	public SimpleStateMachineWithData(AbstractCondition[] edgeAlphabet,
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
		return states;
	}
	
	public int findState(T data){
		for (Map.Entry<Integer, T> dataEntry : stateData.entrySet()) {
			if(dataEntry.getValue().equals(data)){
				return dataEntry.getKey();
			}
		}
		return -1;
	}
	
	public int addState(T data, boolean isSuccess) {
		transitions.put(states,new int[vertexAlphabet.length+edgeAlphabet.length]);
		stateData.put(states,data);
		success.put(states,isSuccess);
		states++;
		return states - 1;
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
		for(int i=0;i<states;i++){
			out.print(i+(success.get(i)?"!":"")+"\t");
			for (int j = 0; j < vertexAlphabet.length; j++) {
				out.print(transitions.get(i)[j]+"\t");
			}
			out.print("|");
			for (int j = 0; j < edgeAlphabet.length; j++) {
				out.print("\t"+transitions.get(i)[j+vertexAlphabet.length]);
			}
			out.print("\t>\t"+stateData.get(i));
			out.println();
		}		
	}
	
	public SimpleStateMachine removeData() {
		SimpleStateMachine stm = new SimpleStateMachine(edgeAlphabet, vertexAlphabet);
		Map<Integer, Integer> newids = new HashMap<Integer, Integer>();
		for (Map.Entry<Integer, Boolean> states : success.entrySet()) {
			newids.put(states.getKey(), stm.addState(states.getValue()));
		}
		for(Map.Entry<Integer, int[]> t:transitions.entrySet()){
			int[] ts = t.getValue();
			for (int i = 0; i < ts.length; i++) {
				if(ts[i]>0){
					if(i<vertexAlphabet.length){
						stm.addTransition(newids.get(t.getKey()), newids.get(ts[i]), i, true);
					}else{
						stm.addTransition(newids.get(t.getKey()), newids.get(ts[i]), i-vertexAlphabet.length, false);
					}
				}
			}
		}
		return stm;
	}
	
}

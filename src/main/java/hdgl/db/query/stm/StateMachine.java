package hdgl.db.query.stm;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.io.Writable;

import hdgl.db.exception.HdglException;
import hdgl.db.query.condition.AbstractCondition;

public class StateMachine implements Writable {
	
	public static enum TransitionType{
		In, Out, Backtrack, Success;

		public static TransitionType read(DataInput in) throws IOException{
			byte i = in.readByte();
			switch (i) {
			case 1:
				return In;
			case 2:
				return Out;
			case 3:
				return Backtrack;
			case 4:
				return Success;
			default:
				throw new HdglException("Unsupported Transition Type: "+i);
			}
		}

		public static void write(TransitionType tt, DataOutput out) throws IOException {
			switch (tt) {
			case In:
				out.writeByte(1);
				break;
			case Out:
				out.writeByte(2);
				break;
			case Backtrack:
				out.writeByte(3);
				break;
			case Success:
				out.writeByte(4);
				break;
			default:
				throw new HdglException("Unsupported Transition Type: " + tt);
			}
		}
	}
	
	public static class Transition implements Writable{
		private TransitionType type;
		private AbstractCondition test;
		private int toState;
		
		public Transition() {
		}
		
		public Transition(TransitionType type, AbstractCondition test,
				int toState) {
			super();
			this.type = type;
			this.test = test;
			this.toState = toState;
		}
		public TransitionType getType() {
			return type;
		}
		public void setType(TransitionType type) {
			this.type = type;
		}
		public AbstractCondition getTest() {
			return test;
		}
		public void setTest(AbstractCondition test) {
			this.test = test;
		}
		public int getToState() {
			return toState;
		}
		public void setToState(int toState) {
			this.toState = toState;
		}
		@Override
		public void readFields(DataInput in) throws IOException {
			type = TransitionType.read(in);
			test = AbstractCondition.readAbstractCondition(in);
			toState = in.readInt();
		}
		
		@Override
		public void write(DataOutput out) throws IOException {
			TransitionType.write(type, out);
			test.write(out);
			out.writeInt(toState);
		}
	}
	
	public static class Condition implements Writable{
		
		private AbstractCondition test;
		private ArrayList<Transition> transitions = new ArrayList<StateMachine.Transition>();
		
		public int getSize(){
			return transitions.size();
		}
		
		public AbstractCondition getTest() {
			return test;
		}
		
		public void setTest(AbstractCondition test) {
			this.test = test;
		}
		
		public Transition getTransition(int transId) {
			return transitions.get(transId);
		}
		
		public Iterable<Transition> getTransitions(){
			return transitions;
		}
		
		public int addTransition(Transition transition) {
			this.transitions.add(transition);
			return transitions.size()-1;
		}
		
		public Condition() {
		}
		
		public Condition(AbstractCondition test) {
			super();
			this.test = test;			
		}

		@Override
		public void readFields(DataInput in) throws IOException {
			test = AbstractCondition.readAbstractCondition(in);
			int len=in.readInt();
			transitions.clear();
			for(int i=0;i<len;i++){
				Transition t = new Transition();
				t.readFields(in);
				transitions.add(t);
			}
		}

		@Override
		public void write(DataOutput out) throws IOException {
			test.write(out);
			out.writeInt(transitions.size());
			for(Transition t:transitions){
				t.write(out);
			}
		}
	}
	
	public static class State implements Writable{
		
		ArrayList<Condition> conditions = new ArrayList<Condition>();

		public int getSize(){
			return conditions.size();
		}
		
		public Condition getCondition(int condId){
			return conditions.get(condId);
		}
		
		public Iterable<Condition> getConditions(){
			return conditions;
		}

		public int addCondition(Condition cond) {
			this.conditions.add(cond);
			return conditions.size()-1;
		}

		@Override
		public void readFields(DataInput in) throws IOException {
			int len=in.readInt();
			conditions.clear();
			for(int i=0;i<len;i++){
				Condition condition = new Condition();
				condition.readFields(in);
				conditions.add(condition);
			}
		}

		@Override
		public void write(DataOutput out) throws IOException {
			out.writeInt(conditions.size());
			for(Condition c:conditions){
				c.write(out);
			}
		}
	}
	
	public int getStartState(){
		return 1;
	}
	
	int maxState = 1;
	Map<Integer, State> states = new HashMap<Integer, StateMachine.State>();
	
	public int addState(){
		int stateId = maxState;
		maxState++;
		states.put(stateId, new State());
		return stateId;
	}
	
	public State getState(int stateId){
		return states.get(stateId);
	}
	
	public Iterable<State> getStates(){
		return states.values();
	}
	
	public void print(PrintStream out){
		out.println("states\tconditions\ttransitions");
		for (Map.Entry<Integer, State> states : this.states.entrySet()) {
			out.print(states.getKey()+"\t");
			int condsize=states.getValue().getSize();
			for(int i=0;i<condsize;i++){
				Condition cond = states.getValue().getCondition(i);
				if(i>0){
					out.print("\t");
				}
				out.print(cond.getTest()+"\t\t");
				for(int j=0;j<cond.getSize();j++){
					Transition transition = cond.getTransition(j);
					if(j>0){
						out.print("\t\t\t");
					}
					out.println(transition.type + "[" + transition.test + "] -> "+transition.toState);
				}
				
			}
		}
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		states.clear();
		int len = in.readInt();
		for(int i=0;i<len;i++){
			int id = in.readInt();
			State state=new State();
			state.readFields(in);
			states.put(id, state);
		}
		maxState = len + 1;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		Set<Map.Entry<Integer, State>> states = this.states.entrySet();
		out.writeInt(states.size());
		for(Map.Entry<Integer, State> state:states){
			out.writeInt(state.getKey());
			state.getValue().write(out);
		}
	}
	
	@Override
	public String toString() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream w = new PrintStream(out);
		print(w);
		return new String(out.toByteArray());
	}
}

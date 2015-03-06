package com.interview.algorithms.graph;

import com.interview.basics.model.graph.Graph;
import com.interview.basics.model.graph.searcher.DFSearcher;
import com.interview.basics.model.graph.searcher.ProblemSolver;

import java.util.Stack;

public class C6_16_CycleFinder extends ProblemSolver {

	private String cycle;
	private Stack<Integer> path = new Stack<Integer>();


	public C6_16_CycleFinder(Graph g){
		super(g);
		searcher = new DFSearcher(g);
	}
	
	public void remove(int t) {
		path.remove((Integer)t);
	}


	@Override
	public void postProcess(int v) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void preProcess(int v) {
		path.add(v);
	}

	public boolean buildCycle(int s, int t) {
		if(path.contains(t)){
			StringBuilder builder = new StringBuilder();
			builder.append(t);
			while(s != t && s >= 0){
				builder.append("-" + s);
				s = searcher.getPrevious(s);
			}
			builder.append("-"+ s);
			cycle = builder.toString();
			this.isBreak = true;
			return true;
		} else {
			return false;
		}
	}

	public String getCycle() {
		return cycle;
	}
	
	@Override
	protected void clean() {
		path.clear();
		searcher.cleanPath();
		searcher.cleanMark();
	}

	

	
	

}

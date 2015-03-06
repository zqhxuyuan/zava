package com.interview.algorithms.graph;

import java.util.Stack;

import com.interview.basics.model.graph.searcher.DFSearcher;
import com.interview.basics.model.graph.Graph;
import com.interview.basics.model.graph.searcher.ProblemSolver;

/**
 * Run depth-first search. Return vertices in reverse postorder
 * @author stefanie
 *
 */
public class C6_6_TopologicalSorter extends ProblemSolver {
	Stack<Integer> stack = new Stack<Integer>();
	C6_16_CycleFinder cycleFinder ;

	public C6_6_TopologicalSorter(Graph g) {
		super(g);
		searcher = new DFSearcher(g);
		cycleFinder = new C6_16_CycleFinder(g);
		cycleFinder.solve();
	}

	@Override
	public void preProcess(int v) {
		
	}

	@Override
	public void postProcess(int v) {
		stack.add(v);
	}

	public String getOrder(){
		StringBuilder builder = new StringBuilder();
		while(!stack.isEmpty()){
			builder.append(stack.pop() + "-");
		}
		return builder.toString();
	}
	
	public boolean canSort(){
		return (cycleFinder.getCycle() == null) ? true : false;
	}
	
	public String why(){
		return cycleFinder.getCycle();
	}

	@Override
	protected void clean() {
//		if(searcher.allMarked()){
//			this.isBreak = true;
//		} //else {
//			stack.clear();
//			searcher.cleanPath();
//			searcher.cleanMark();
//		}
	}
}

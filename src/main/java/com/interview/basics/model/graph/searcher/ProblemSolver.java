package com.interview.basics.model.graph.searcher;

import com.interview.basics.model.graph.Graph;
import com.interview.basics.model.graph.searcher.Processor;
import com.interview.basics.model.graph.searcher.Searcher;

public abstract class ProblemSolver implements Processor{
	protected Searcher searcher;
	protected Graph g;
	public int count;
	public boolean isBreak;
	
	public ProblemSolver(Graph g){
		this.g = g;
		count = 1;
	}
	
	public void solve(){
		for (int v = 0; v < g.V; v++){
			if (!searcher.isMarked(v)){
				searcher.search(v, this);
				count++;
			}
			clean();
			if(isBreak){
				break;
			}
		}
	}

	protected void clean() {
		// TODO Auto-generated method stub
		
	}
}

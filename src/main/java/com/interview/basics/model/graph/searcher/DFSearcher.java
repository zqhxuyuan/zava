package com.interview.basics.model.graph.searcher;

import com.interview.algorithms.graph.C6_16_CycleFinder;
import com.interview.basics.model.graph.Graph;

public class DFSearcher extends Searcher{
	
	
	public DFSearcher(Graph g){
		super(g);
		init();
	}
	
	public void search(int s, Processor p){
		dfsInner(s, p);
	}

    protected void dfsInner(int s, Processor p){
		if(p != null){
			p.preProcess(s);
		}
		marked[s] = true;
		if(g.adj[s] != null){
			for(int t : g.adj[s]){
				if(isBreak) return;
				if(!marked[t]){
					edges[t] = s;
					dfsInner(t, p);
					if(p instanceof C6_16_CycleFinder){
						((C6_16_CycleFinder)p).remove(t);
					}
				} else {
					if(p instanceof C6_16_CycleFinder){
						isBreak = ((C6_16_CycleFinder)p).buildCycle(s, t);
					}
				}
			}
		}
		p.postProcess(s);
	}
	
}

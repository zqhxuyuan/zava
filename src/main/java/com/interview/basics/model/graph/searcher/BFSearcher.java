package com.interview.basics.model.graph.searcher;

import com.interview.basics.model.graph.Graph;

import java.util.ArrayDeque;
import java.util.Queue;


public class BFSearcher extends Searcher {
	protected Queue<Integer> queue;
	
	public BFSearcher(Graph g){
		super(g);
		init();
		queue = new ArrayDeque<Integer>();
	}
	
	public void search(int s, Processor p){
		queue.add(s);
		bfsInner(p);
	}
	
	protected void bfsInner(Processor p){
		while(!queue.isEmpty()){
			int s = queue.poll();
			if(marked[s]) continue;
			if(p != null){
				p.preProcess(s);
			}
			if(isBreak)		break;
			marked[s] = true;
			if(g.adj[s] != null){
				for(int t : g.adj[s]){
					if(!marked[t]){
						queue.add(t);
						edges[t] = s;
					}
				}
			}
		}
	}
}

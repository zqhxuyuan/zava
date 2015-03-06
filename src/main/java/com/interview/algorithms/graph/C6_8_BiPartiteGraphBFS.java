package com.interview.algorithms.graph;

import com.interview.basics.model.graph.searcher.BFSearcher;
import com.interview.basics.model.graph.Graph;
import com.interview.basics.model.graph.searcher.Processor;

public class C6_8_BiPartiteGraphBFS extends C6_8_BiPartiteGraph {
	public boolean isBiPartite = true;

	public C6_8_BiPartiteGraphBFS(Graph g) {
		super(g);
		searcher = new BFSearcher(g){
			private boolean flag = true;
			@Override
			public void bfsInner(Processor p) {
				while(!queue.isEmpty()){
					int s = queue.poll();
					if(s == -1){
						flag = !flag;
						if(!queue.isEmpty())	queue.add(-1);
						continue;
					}
					if(marked[s]) {
						if(flags[s] != flag) {
							isBiPartite = false;
							return;
						} else {
							continue;
						}
					}
					flags[s] = flag;
					marked[s] = true;
					if(g.adj[s] != null){
						for(int t : g.adj[s]){
							if(marked[t]) {
								if(flags[t] == flag) {
									isBiPartite = false;
									return;
								} else {
									continue;
								}
							}
							queue.add(t);
						}
					}
				}
			}
			@Override
			public void search(int s, Processor p) {
				queue.add(s);
				queue.add(-1);
				bfsInner(p);
			}
		};
	}

	@Override
	public boolean isBiPartite() {
		return isBiPartite;
	}


}

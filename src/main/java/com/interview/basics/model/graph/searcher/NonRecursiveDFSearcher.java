package com.interview.basics.model.graph.searcher;

import com.interview.basics.model.graph.Graph;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/15/14
 * Time: 2:30 PM
 */
public class NonRecursiveDFSearcher extends Searcher{
    Stack<Queue<Integer>> stack;

    public NonRecursiveDFSearcher(Graph g) {
        super(g);
        init();
        stack = new Stack<>();
    }

    @Override
    public void search(int s, Processor p) {
        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(s);
        stack.add(queue);
        dfsInner(p);
    }

    protected void dfsInner(Processor p) {
        while(!stack.isEmpty()){
            Queue<Integer> queue = stack.pop();
            while(!queue.isEmpty()){
                Integer s = queue.poll();
                if(marked[s]) continue;
                if(p != null){
                    p.preProcess(s);
                }
                if(isBreak)		break;
                marked[s] = true;
                if(g.adj[s] != null){
                    Queue<Integer> children = new ArrayDeque<>();
                    for(int t : g.adj[s]){
                        if(!marked[t]){
                            children.add(t);
                            edges[t] = s;
                        }
                    }
                    if(children.size() > 0) stack.push(children);
                }
            }

        }
    }
}

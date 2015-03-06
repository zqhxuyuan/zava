package com.interview.algorithms.graph;

import com.interview.basics.model.graph.WeightedGraph;
import com.interview.basics.search.ASearcher;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/12/14
 * Time: 11:12 AM
 */
class NodeState implements ASearcher.State<Integer> {
    int id = 0;
    WeightedGraph.Edge e;

    NodeState(int id, WeightedGraph.Edge e) {
        this.id = id;
        this.e = e;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public Integer key() {
        return id;
    }
}
public class C6_5_ShortestPath_ASearch extends ASearcher<Integer, NodeState, WeightedGraph> {

    public C6_5_ShortestPath_ASearch(WeightedGraph graph) {
        super(graph);
    }

    public Path pathTo(int s, int t){
        NodeState ss = new NodeState(s, null);
        NodeState ts = new NodeState(t, null);
        return pathTo(ss, ts);
    }

    @Override
    protected double heuristicEstimateDistance(NodeState c, NodeState t) {
        return 0;
    }

    @Override
    protected boolean isSame(NodeState s, NodeState t) {
        return s.id ==  t.id;
    }

    @Override
    protected NodeState[] nextState(NodeState s) {
        Set<WeightedGraph.Edge> edges = input.adj[s.id];
        if(edges == null) return null;
        NodeState[] states = new NodeState[edges.size()];
        int i = 0;
        for(WeightedGraph.Edge edge : edges){
            states[i++] = new NodeState(edge.t, edge);
        }
        return states;
    }

    @Override
    protected double gScore(Candidate c, NodeState t) {
        return c.cost + (t.e == null ? 0 : t.e.w);
    }

}

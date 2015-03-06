package com.interview.basics.search;

import com.interview.basics.model.collection.heap.BinaryArrayHeap;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/12/14
 * Time: 1:50 PM
 */

public abstract class ASearcher<T, S extends ASearcher.State<T>, Input> {
    public interface State<T> {
        public T key();
    }

    protected class Candidate implements Comparable<Candidate>{
        public S state;
        public double cost;

        @Override
        public int compareTo(Candidate candidate) {
            if(this.cost > candidate.cost) return 1;
            else if(this.cost == candidate.cost) return 0;
            else return -1;
        }

        Candidate(S state, double cost) {
            this.state = state;
            this.cost = cost;
        }
    }

    public class Path<T>{
        public Stack<T> path;
        public double weight;

        public Path(Stack<T> path, double weight) {
            this.path = path;
            this.weight = weight;
        }
    }

    protected boolean isDebug = false;
    protected Input input;
    protected Map<T, Double> gScore;
    protected Map<T, Double> hScore;
    protected Map<T, S> previous;

    public ASearcher(Input input){
        this.input = input;
    }

    private void prepare(){
        this.gScore = new HashMap<T, Double>();
        this.hScore = new HashMap<T, Double>();
        this.previous = new HashMap<T, S>();
    }

    private double fScore(State c){
        return gScore.get(c.key()) + hScore.get(c.key());
    }

    protected abstract double heuristicEstimateDistance(S c, S t);
    protected abstract boolean isSame(S s, S t);
    protected abstract S[] nextState(S s);
    protected abstract double gScore(Candidate c, S t);

    public Path<T> pathTo(S s, S t){
        Stack<T> path = new Stack<T>();
        if(s.key().equals(t.key())) return new Path(path, 0.0);
        double weight = search(s, t);
        if(weight != -1){
            for(S state = t; state != null && !state.equals(s); state = previous.get(state.key())){
                path.push(state.key());
            }
        }
        return new Path(path, weight);
    }

    public double search(S s, S t){
        prepare();
        Set<State> close = new HashSet<State>();
        BinaryArrayHeap<Candidate> open = new BinaryArrayHeap<Candidate>(BinaryArrayHeap.MIN_HEAD);
        gScore.put(s.key(), 0.0);
        hScore.put(s.key(), heuristicEstimateDistance(s, t));
        open.add(new Candidate(s, fScore(s)));

        while(open.size() != 0){
            Candidate c = open.pollHead();
            if(isDebug) System.out.printf("state: %s with score %2f\n", c.state.key(), c.cost);
            if(isSame(c.state, t))   return c.cost;
            if(!close.contains(c.state)){
                close.add(c.state);
                S[] nextStates = nextState(c.state);
                if(nextStates == null) continue;
                for(int i = 0; i < nextStates.length; i++){
                    S e = nextStates[i];
                    if(e == null) continue;
                    double ten = gScore(c, e);
                    if(! gScore.containsKey(e.key()) || gScore.get(e.key()) > ten){
                        gScore.put(e.key(), ten);
                        previous.put(e.key(), c.state);
                        hScore.put(e.key(), heuristicEstimateDistance(e, t));
                        Candidate nc = new Candidate(e, fScore(e));
                        open.add(nc);
                    }
                }
            }
        }
        return -1;
    }
}

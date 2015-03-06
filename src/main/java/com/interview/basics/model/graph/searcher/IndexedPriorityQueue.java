package com.interview.basics.model.graph.searcher;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class IndexedPriorityQueue<Key extends Comparable<Key>, Value extends Comparable<Value>> {
	PriorityQueue<IndexedNode> pq;
	Map<Key, IndexedNode> map;
	
	public IndexedPriorityQueue(){
		pq = new PriorityQueue<IndexedNode>();
		map = new HashMap<Key, IndexedNode>();
	}
	
	class IndexedNode implements Comparable<IndexedNode>{
		Key index;
		Value obj;
		public IndexedNode(Key index, Value obj){
			this.index = index;
			this.obj = obj;
		}
		@Override
		public int compareTo(IndexedNode node) {
			return this.obj.compareTo(node.obj);
		}
	}

	public void add(Key k, Value v) {
		IndexedNode node = new IndexedNode(k, v);
		pq.add(node);
		map.put(k, node);
	}

	public boolean isEmpty() {
		return pq.isEmpty();
	}

	public Key poll() {
		Key key = pq.poll().index;
		map.remove(key);
		return key;
	}

	public void update(Key k, Value v) {
		IndexedNode node = map.get(k);
		if(node != null){
			node.obj = v;
		}
	}

	public boolean contains(Key k) {
		return map.containsKey(k);
	}
}

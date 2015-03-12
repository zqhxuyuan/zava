package hdgl.db.query.convert;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import hdgl.db.query.condition.AbstractCondition;
import hdgl.db.query.condition.Conjunction;;

public class SortConditions {
	
	static <TK,TV> void addToMultiMap(Map<TK, Set<TV>> map,TK key, TV value){
		Set<TV> set;
		if(map.containsKey(key)){
			set=map.get(key);
		}else{
			set=new HashSet<TV>();
			map.put(key, set);
		}
		set.add(value);
	}
	
	public static AbstractCondition[] sortConditions(AbstractCondition[] in){
		
		Map<AbstractCondition, Set<AbstractCondition>> coverMap = new HashMap<AbstractCondition, Set<AbstractCondition>>();
		Set<AbstractCondition> all = new HashSet<AbstractCondition>();
		
		for(int i = 0; i<in.length; i++){
			all.add(in[i]);
			for (int j = i + 1; j < in.length; j++) {
				AbstractCondition c1 = in[i];
				AbstractCondition c2 = in[j];
				switch (c1.relationship(c2)) {
					case Require:
						addToMultiMap(coverMap, c2, c1);
						break;
					case Sufficient:
						addToMultiMap(coverMap, c1, c2);
						break;
					default:
						break;
				}
			}
		}
		
		Set<AbstractCondition> uniqueConditions = new HashSet<AbstractCondition>();
		for (AbstractCondition abstractCondition : in) {
			if(!coverMap.containsKey(abstractCondition)){
				uniqueConditions.add(abstractCondition);
			}
		}
		AbstractCondition[] unique = uniqueConditions.toArray(new AbstractCondition[0]);
		ArrayList<Set<AbstractCondition>> uniqueCombinedConditions = new ArrayList<Set<AbstractCondition>>();
		outer:for (Set<AbstractCondition> combineConditions : selection(unique)) {
			if(combineConditions.size()<=1){
				continue;
			}
			for(AbstractCondition c1:combineConditions){
				for(AbstractCondition c2:combineConditions){
					if(!c1.equals(c2)){
						if(!c1.compatible(c2)){
							continue outer;
						}
						if(c1.require(c2)||c2.require(c1)){
							continue outer;
						}
					}
				}
			}
			uniqueCombinedConditions.add(combineConditions);
		}
		AbstractCondition[] result2 = topologicalSort(all, coverMap, AbstractCondition.class);
		AbstractCondition[] result = new AbstractCondition[uniqueCombinedConditions.size()+result2.length];
		for (int i = 0; i < uniqueCombinedConditions.size(); i++) {
			result[i] = new Conjunction(uniqueCombinedConditions.get(i).toArray(new AbstractCondition[0]));
		}
		
		System.arraycopy(result2, 0, result, uniqueCombinedConditions.size(), result2.length);
		return result;	
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Set<T>[] selection(T[] arr){
		ArrayList<Set<T>> a = selectionInner(arr,0);
		Collections.sort(a, new Comparator<Set<T>>() {
			@Override
			public int compare(Set<T> o1, Set<T> o2) {
				return Integer.valueOf(o2.size()).compareTo(o1.size());
			}
		});
		return a.toArray(new Set[0]);
	}
	
	static <T> ArrayList<Set<T>> selectionInner(T[] arr, int startpos){
		if(startpos==arr.length-1){
			Set<T> c1=new HashSet<T>();
			c1.add(arr[startpos]);
			Set<T> c2=new HashSet<T>();			
			ArrayList<Set<T>> res = new ArrayList<Set<T>>();
			res.add(c1);
			res.add(c2);
			return res;
		}else{
			ArrayList<Set<T>> res = new ArrayList<Set<T>>();
			for(Set<T> subset: selectionInner(arr, startpos+1)){
				Set<T> c1=new HashSet<T>(subset);
				c1.add(arr[startpos]);
				Set<T> c2=new HashSet<T>(subset);	
				res.add(c1);
				res.add(c2);
			}
			return res;
		}
	}
	
	/**
	 * Map<T,T> has 
	 * @param cover
	 * @return
	 */
	
	static <T> T[] topologicalSort(Set<T> all, Map<T, Set<T>> cover, Class<T> type){
		ArrayList<T> result = new ArrayList<T>();
		ArrayList<T> removeList = new ArrayList<T>();
		while (!all.isEmpty()) {
			T found = null;
			for (T t : all) {
				if(!cover.keySet().contains(t)){
					found = t;
					break;
				}
			}
			if(found==null){
				throw new IllegalArgumentException("Graph with loops");
			}
			result.add(found);
			all.remove(found);
			removeList.clear();
			for (Map.Entry<T, Set<T>> t : cover.entrySet()) {
				t.getValue().remove(found);
				if(t.getValue().size()==0){
					removeList.add(t.getKey());
				}
			}
			for (T t : removeList) {
				cover.remove(t);
			}
		}
		@SuppressWarnings("unchecked")
		T[] foo=(T[]) Array.newInstance(type, 0);
		return result.toArray(foo);
	}
}

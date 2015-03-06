package com.interview.flag.g;

import com.interview.utils.ConsoleWriter;

import java.util.*;

/**
 * Created by stefanie on 1/28/15.
 */
public class G49_ListClustering<T> {
    
    public Collection<List<T>> clustering(T[] elements, List<T> list){
        HashMap<T, Boolean> members = new HashMap();
        for(int i = 0; i < elements.length; i++) members.put(elements[i], false);
        
        HashMap<T, List<T>> cluster = new HashMap();
        for(int i = 0; i < elements.length; i++){
            if(members.get(elements[i])) continue;
            
            members.put(elements[i], true);
            List<T> subset = new ArrayList();
            subset.add(elements[i]);

            Iterator<T> itr = list.iterator();
            while(itr.hasNext() && !itr.next().equals(elements[i]));
            while(itr.hasNext()){
                T next = itr.next();
                if(!members.containsKey(next)) break;
                if(members.get(next)){
                    subset.addAll(cluster.get(next));
                    cluster.remove(next);
                    break;
                } else {
                    subset.add(next);
                    members.put(next, true);
                }
            }
            cluster.put(elements[i], subset);
        }
        return cluster.values();
    }
    
    public static void main(String[] args){
        G49_ListClustering<Character> cluster = new G49_ListClustering();
        
        Character[] elements = new Character[]{'D', 'E', 'F', 'J', 'G', 'H', 'C'};
        List<Character> list = Arrays.asList(new Character[]{'A','B','C','D','E','F','G','H','I','J','K'});
        
        Collection<List<Character>> clusters = cluster.clustering(elements, list);
        for(List<Character> subset : clusters){
            ConsoleWriter.printCollection(subset);
        }
        
    }
}

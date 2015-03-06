package com.interview.flag.g;


import java.util.*;

/**
 * Created_By: stefanie
 * Date: 15-1-14
 * Time: 上午10:41
 */
public class G25_FindMapInConfusedDict {
    public HashMap<Character, Character> getMap(String[] dict){

        HashMap<Character, List<Character>> graph = new HashMap();
        HashMap<Character, Integer> indegrees = new HashMap();
        HashSet<Character> allChars = new HashSet();

        for(int i = 1; i < dict.length; i++){
            int firstDifferent = findFirstDifferent(dict[i - 1], dict[i]);
            if(firstDifferent != -1){
                char first = dict[i - 1].charAt(firstDifferent);
                char second = dict[i].charAt(firstDifferent);
                allChars.add(first);
                allChars.add(second);
                addEdgeGraph(graph, first, second);
                addInDegree(indegrees, second);
            }
        }

        List<Character> original = new ArrayList();
        Queue<Character> queue = new LinkedList();

        for(Character ch : allChars){
            if(!indegrees.containsKey(ch)) queue.add(ch);
            original.add(ch);
        }

        List<Character> confused = new ArrayList();
        while(!queue.isEmpty()){
            char ch = queue.poll();
            confused.add(ch);
            if(graph.containsKey(ch)){
                for(Character next : graph.get(ch)){
                    if(indegrees.get(next) > 1) indegrees.put(next, indegrees.get(next) - 1);
                    else queue.offer(next);
                }
            }
        }
        Collections.sort(original);

        HashMap<Character, Character> mapping = new HashMap();
        for(int i = 0; i < original.size(); i++){
            mapping.put(original.get(i), confused.get(i));
        }
        return mapping;
    }

    private void addEdgeGraph(HashMap<Character, List<Character>> graph, char from, char to){
        if(graph.containsKey(from)){
            graph.get(from).add(to);
        } else {
            List<Character> dependencies = new ArrayList();
            dependencies.add(to);
            graph.put(from, dependencies);
        }
    }

    private void addInDegree(HashMap<Character, Integer> indegrees, char to){
        if(indegrees.containsKey(to)){
            indegrees.put(to, indegrees.get(to) + 1);
        } else {
            indegrees.put(to, 1);
        }
    }

    private int findFirstDifferent(String s1, String s2){
        for(int i = 0; i < s1.length() && i < s2.length(); i++){
            if(s1.charAt(i) != s2.charAt(i)) return i;
        }
        return -1;
    }

    public static void main(String[] args){
        G25_FindMapInConfusedDict finder = new G25_FindMapInConfusedDict();
        String[] dict = new String[]{"wrt", "wrf", "er", "ett", "rftt","te","ba","bw","fw"};
        HashMap<Character, Character> mapping = finder.getMap(dict);
        for(Map.Entry<Character, Character> entry : mapping.entrySet()){
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}

package com.interview.books.question300;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created_By: stefanie
 * Date: 14-12-15
 * Time: 下午6:55
 */
public class TQ12_StringConjunction {
    public String conjoin(String[] strs, int M){
        //Build a graph based on can conjoin or not
        int[][] graph = new int[strs.length][strs.length];
        for(int i = 0; i < strs.length; i++)
            for(int j = 0; j < strs.length; j++)
                if(strs[i].substring(1).equals(strs[j].substring(0, M)))
                    graph[i][j] = 1;

        //find the longest path and build the result string
        List<Integer> path = longestPath(graph);
        StringBuilder builder = new StringBuilder();
        builder.append(strs[path.get(0)]);
        for(int i = 1; i < path.size(); i++){
            builder.append(strs[path.get(i)].charAt(M));
        }

        String result = builder.toString();
        //check if the result string can conjoin itself
        if(result.substring(0, M).equals(result.substring(result.length() - M))) return null;
        else return result;
    }

    public static List<Integer> longestPath(int[][] graph){
        List<Integer> longestPath = new ArrayList<Integer>();
        Set<Integer> visited = new HashSet<Integer>();
        //do dfs on every node haven't on any path.
        for(int i = 0; i < graph.length; i++){
            if(!visited.contains(i)){
                boolean[] _visited = new boolean[graph.length];
                _visited[i] = true;
                List<Integer> path = dfsFindPath(graph, i, _visited);
                if(path.size() > longestPath.size()) longestPath = path;
                visited.addAll(path);
            }
        }
        return longestPath;
    }

    public static List<Integer> dfsFindPath(int[][] graph, int node, boolean[] visited){
        List<Integer> maxPath = new ArrayList<Integer>();
        for(int i = 0; i < graph.length; i++){
            if(graph[node][i] == 1 && !visited[i])  {
                visited[i] = true;
                List<Integer> path = dfsFindPath(graph, i, visited);
                if(path.size() > maxPath.size())    maxPath = path;
                visited[i] = false;
            }
        }
        maxPath.add(0, node);
        return maxPath;
    }


    public static void main(String[] args){
        TQ12_StringConjunction conjuncter = new TQ12_StringConjunction();
        String[] strs = new String[] {"aaaab", "aaabb", "aabbb", "abbbb", "bbbbc"};
        String result = conjuncter.conjoin(strs, 4);
        System.out.println(result);   //aaaabbbbc

        strs = new String[] {"aaaab", "aaabb", "aabba", "abbaa", "bbaaa", "baaaa"};
        result = conjuncter.conjoin(strs, 4);
        //should found a cycle
        System.out.println(result);

        strs = new String[] {"dabbb","aaaab", "aaabb", "aabbb", "abbbb", "bbbbc"};
        result = conjuncter.conjoin(strs, 4);
        System.out.println(result);   //aaaabbbbc
    }
}

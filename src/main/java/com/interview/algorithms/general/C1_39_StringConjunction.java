package com.interview.algorithms.general;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created_By: stefanie
 * Date: 14-7-26
 * Time: 下午1:04
 */
public class C1_39_StringConjunction {

    public static String conjoin(int m, String[] strs){

        //Build a graph based on can conjoin or not
        int[][] graph = new int[strs.length][strs.length];
        for(int i = 0; i < strs.length; i++)
            for(int j = 0; j < strs.length; j++)
                if(strs[i].substring(1).equals(strs[j].substring(0, m)))
                    graph[i][j] = 1;

        //find the longest path and build the result string
        List<Integer> path = longestPath(graph);
        StringBuilder builder = new StringBuilder();
        builder.append(strs[path.get(0)]);
        for(int i = 1; i < path.size(); i++){
            builder.append(strs[path.get(i)].charAt(m));
        }
        String result = builder.toString();
        //check if the result string can conjoin itself
        if(result.substring(0, m).equals(result.substring(result.length() - m))) return null;
        else return result;
    }

    public static List<Integer> longestPath(int[][] graph){
        List<Integer> longestPath = new ArrayList<Integer>();
        Set<Integer> visited = new HashSet<Integer>();
        for(int i = 0; i < graph.length; i++){
            if(!visited.contains(i)){
                int[] v = new int[graph.length];
                v[i] = 1;
                List<Integer> path = findPath(graph, i, v);
                if(path.size() > longestPath.size()) longestPath = path;
                visited.addAll(path);
            }
        }
        return longestPath;
    }

    public static List<Integer> findPath(int[][] graph, int node, int[] visited){
        List<Integer> maxPath = new ArrayList<Integer>();
        for(int i = 0; i < graph.length; i++){
            if(graph[node][i] == 1 && visited[i] != 1)  {
                visited[i] = 1;
                List<Integer> path = findPath(graph, i, visited);
                if(path.size() > maxPath.size())    maxPath = path;
                visited[i] = 0;
            }
        }
        maxPath.add(0, node);
        return maxPath;
    }
}

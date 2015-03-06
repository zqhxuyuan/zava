package com.interview.books.leetcodeoj;

import java.util.*;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 下午10:26
 */
public class LOJ126_WordLadderII {
    //Solution:
    //  1. to find all shortest path solution, should use level-order traversal
    //  2. to build the path, need create a retrieval data structure, Node(String word, List<Node> prev, int depth)
    //  3. use a Map<String, Node> map to mark if a word is visited and tracking it's prev
    //  4. while(!queue.isEmpty() && !found) do traversal
    //  5. if(found) use backtracing(permutation) to find all the path.
    //Important:
    //  1. tracking depth in Node, only if (nextNode.depth == node.depth + 1) nextNode.prev.add(node);
    //  2. set found = true when found a solution, but need finish visiting this layer
    //  3. chars[i] = original; when permutation on char[i] to 'a' to 'z'
    //  4. when permutate to get path, clone path and add prev in until prev is start.
    class Node{
        String word;
        int depth = 0;
        List<Node> prev = new ArrayList();
        public Node(String word, int depth){
            this.word = word;
            this.depth = depth;
        }
    }
    List<List<String>> paths;
    public List<List<String>> findLadders(String start, String end, HashSet<String> dict) {
        paths = new ArrayList();
        if(start.equals(end)){
            List<String> path = new ArrayList();
            path.add(start);
            paths.add(path);
            return paths;
        }
        Queue<Node> queue = new LinkedList();
        Map<String, Node> visited = new HashMap();
        Node startNode = new Node(start, 0);
        queue.offer(startNode);
        visited.put(start, startNode);
        boolean found = false;
        while(!queue.isEmpty() && !found){
            int levelSize = queue.size();
            for(int k = 0; k < levelSize; k++){
                Node node = queue.poll();
                char[] chars = node.word.toCharArray();
                for(int i = 0; i < chars.length; i++){
                    char original = chars[i];
                    for(char ch = 'a'; ch <= 'z'; ch++){
                        if(ch == original) continue;
                        chars[i] = ch;
                        String next = String.valueOf(chars);

                        if(dict.contains(next)){
                            Node nextNode = visited.get(next);
                            if(nextNode != null) {
                                if (nextNode.depth == node.depth + 1) nextNode.prev.add(node);
                            } else {
                                nextNode = new Node(next, node.depth + 1);
                                nextNode.prev.add(node);
                                visited.put(next, nextNode);
                                if(next.equals(end)) found = true;
                                else queue.add(nextNode);
                            }
                        }
                    }
                    chars[i] = original;
                }
            }
        }
        if(found){
            List<String> path = new ArrayList();
            path.add(end);
            getPath(visited.get(end), path, start);
        }
        return paths;
    }

    public void getPath(Node node, List<String> path, String start){
        if(node.word.equals(start)){
            paths.add(path);
        }
        for (Node prev : node.prev) {
            List<String> current = new ArrayList<String>(path);
            current.add(0, prev.word);
            getPath(prev, current, start);
        }
    }

    public static void main(String[] args){
        HashSet<String> dict = new HashSet<>();
        dict.add("hot");
        dict.add("dotProduct");
        dict.add("dog");
        LOJ126_WordLadderII finder = new LOJ126_WordLadderII();
        List<List<String>> paths = finder.findLadders("hot", "hot", dict);
        for(List<String> path : paths){
            for(String word : path){
                System.out.print(word + ", ");
            }
            System.out.println();
        }
    }

}

package com.interview.books.leetcodeoj;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 下午9:58
 */
public class LOJ127_WordLadder {
    //do Level-Order traverse on the generation process, each step only change only one char
    //1. mark visited and check equals(end) when add into queue, not when poll from queue to avoid duplication element in queue.
    //2. for edge case: if(start.equals(end)) return 1
    //3. remember set char[i] = original after change char in position i
    //4. remember to length++ when after visit one layer.
    public int ladderLength(String start, String end, Set<String> dict) {
        if(start.equals(end)) return 1;
        Queue<String> queue = new LinkedList();
        Set<String> visited = new HashSet();
        queue.offer(start);
        visited.add(start);
        int length = 1;
        while(!queue.isEmpty()){
            int levelSize = queue.size();
            for(int k = 0; k < levelSize; k++){
                String word = queue.poll();
                char[] chars = word.toCharArray();
                for(int i = 0; i < chars.length; i++){
                    char original = chars[i];
                    for(char ch = 'a'; ch <= 'z'; ch++){
                        if(ch == original) continue;
                        chars[i] = ch;
                        String next = String.valueOf(chars);
                        if(!visited.contains(next) && dict.contains(next)){
                            if(next.equals(end)) return length + 1;
                            queue.offer(next);
                            visited.add(next);
                        }
                    }
                    chars[i] = original;
                }
            }
            length++;
        }
        return 0;
    }
}

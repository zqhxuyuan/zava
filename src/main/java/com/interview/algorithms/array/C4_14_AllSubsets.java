package com.interview.algorithms.array;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created_By: zouzhile
 * Date: 11/1/14
 * Time: 3:41 PM
 */
public class C4_14_AllSubsets {

    public Set<Set> findSubsets(Set<Integer> set) {
        Set<Set> subsets = new HashSet();
        if(set.size() == 1) {
            HashSet<Integer> result = new HashSet<Integer>();
            result.addAll(set);
            subsets.add(result);
            return subsets;
        }

        Iterator<Integer> itr = set.iterator();
        while(itr.hasNext()) {
            int value = itr.next();
            Set<Integer> clone = new HashSet<Integer>();
            clone.addAll(set);
            clone.remove(value);
            for(Set currentSet : this.findSubsets(clone)) {
                subsets.add(currentSet);
                HashSet<Integer> currentClone = new HashSet();
                currentClone.addAll(currentSet);
                currentClone.add(value);
                subsets.add(currentClone);
            }
        }
        return subsets;
    }

    public static void main(String[] args) {
        C4_14_AllSubsets finder = new C4_14_AllSubsets();
        Set<Integer> set = new HashSet<Integer>();
        set.add(1);
        set.add(2);
        set.add(3);
        for(Set subset : finder.findSubsets(set)) {
            Iterator<Integer> itr = subset.iterator();
            while(itr.hasNext())
                System.out.print(itr.next() + " ");
            System.out.println();
        }
    }
}

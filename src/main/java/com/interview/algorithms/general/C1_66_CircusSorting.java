package com.interview.algorithms.general;

import com.interview.basics.sort.QuickSorter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-10-19
 * Time: 上午11:38
 */
public class C1_66_CircusSorting {

    static class Person implements Comparable<Person>{
        int height;
        int weight;

        public Person(int weight, int height) {
            this.weight = weight;
            this.height = height;
        }

        @Override
        public int compareTo(Person o) {
            if(weight == o.weight){
                if(height == o.height) return 0;
                else if(height > o.height) return 1;
                else return -1;
            } else if(weight > o.weight) return 1;
            else return -1;
        }
    }

    public static List<Person> sort(Person[] persons){
        QuickSorter<Person> sorter = new QuickSorter<>();
        sorter.sort(persons);

        int[] opt = new int[persons.length];
        int[] pre = new int[persons.length];

        int max = 1;
        int maxEnd = 0;
        opt[0] = 1;
        pre[0] = -1;
        for(int i = 1; i < opt.length; i++){
            opt[i] = 1;
            for(int j = i - 1; j >= 0; j--){
                if(persons[j].height <= persons[i].height && opt[j] + 1 > opt[i]){
                    opt[i] = opt[j] + 1;
                    pre[i] = j;
                }
            }
            if(opt[i] > max){
                max = opt[i];
                maxEnd = i;
            }
        }

        List<Person> team = new ArrayList<>();
        while(maxEnd != -1){
            team.add(0, persons[maxEnd]);
            maxEnd = pre[maxEnd];
        }
        return team;
    }
}

package com.interview.algorithms.stackqueue;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/15/14
 * Time: 11:21 AM
 */

public class C7_6_HanoiUsingStack {
    static class Step{
        int disk;
        int from;
        int to;

        Step(int disk, int from, int to) {
            this.disk = disk;
            this.from = from;
            this.to = to;
        }

        public String toString(){
            return disk + " " + from + "-->" + to;
        }
    }

    static class Tower{
        Stack<Integer> disks;
        int index;

        public Tower(int index){
            this.index = index;
            this.disks = new Stack<>();
        }

        public void add(int disk){
            if(!disks.isEmpty() && disks.peek() <= disk)
                System.err.println("Wrong Place of Disk " + disk + " to tower " + this.index);
            else disks.push(disk);
        }

        public Step moveTopTo(Tower t){
            int disk = disks.pop();
            t.add(disk);
            return new Step(disk, this.index, t.index);
        }

        public void printState(){
            System.out.printf("The state of tower %d is: \n", this.index);
            for(int i = 0; i < disks.size(); i++){
                System.out.print(disks.get(i) + " ");
            }
            System.out.println("");
        }
    }

    int towerNumber = 3;
    Tower[] towers = new Tower[towerNumber];

    public C7_6_HanoiUsingStack(){
        for(int i = 0; i < towerNumber; i++) towers[i] = new Tower(i);
    }

    public List<Step> solve(int n){
        for (int i = n; i > 0; i--) towers[0].add(i);
        List<Step> steps = new ArrayList<>();
        moveDisks(n, towers[0], towers[2], towers[1], steps);
        return steps;
    }

    private void moveDisks(int n, Tower source, Tower dest, Tower buffer, List<Step> steps){
        if(n > 0){
            moveDisks(n - 1, source, buffer, dest, steps);
            steps.add(source.moveTopTo(dest));
            moveDisks(n - 1, buffer, dest, source, steps);
        }
    }
}

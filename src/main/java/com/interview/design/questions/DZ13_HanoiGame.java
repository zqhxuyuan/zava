package com.interview.design.questions;

import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 14-11-20
 * Time: 下午1:27
 */
public class DZ13_HanoiGame {
    static class Tower extends Stack<Integer>{
        int id;
        public Tower(int id){
            this.id = id;
        }
    }
    Tower[] towers;

    public DZ13_HanoiGame(int n){
        towers = new Tower[3];
        for(int i = 0; i < 3; i++) towers[i] = new Tower(i);
        for(int i = n; i > 0; i--) towers[0].push(i);
    }

    public void play(){
        move(towers[0], towers[2], towers[1], towers[0].size());
    }

    public void move(Tower src, Tower des, Tower buf, int size){
        if(size <= 0) return;
        if(size == 1) {
            int element = src.pop();
            System.out.printf("move %d from tower%d to tower%d\n", element, src.id, des.id);
            des.push(element);
        }
        else {
            move(src, buf, des, size - 1);
            int element = src.pop();
            System.out.printf("move %d from tower%d to tower%d\n", element, src.id, des.id);
            des.push(element);
            move(buf, des, src, size - 1);
        }
    }

    public static void main(String[] args){
        DZ13_HanoiGame hanoi = new DZ13_HanoiGame(3);
        hanoi.play();
    }

}

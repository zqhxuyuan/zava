package com.interview.flag.f;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created_By: stefanie
 * Date: 15-1-7
 * Time: 下午6:04
 */
public class F8_JumpOverRiver {
    class Payload {
        int offset, speed;
        Payload(int offset, int speed) { this.offset = offset; this.speed = speed; }
    }

    public int minSteps(int[] A) {
        if (A == null || A.length < 2) return 0;

        Queue<Payload> queue = new LinkedList();
        queue.offer(new Payload(0, 1));
        int steps = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Payload payload = queue.poll();
                int offset = payload.offset;
                int speed = payload.speed;
                if (offset >= A.length - 1) return steps;
                else if (A[offset] == 0) continue;
                else {
                    queue.offer(new Payload(offset + speed, speed));
                    queue.offer(new Payload(offset + speed + 1, speed + 1));
                }
            }
            steps++;
        }

        return -1; // can't be reached.
    }

    public static void main(String[] args){
        F8_JumpOverRiver jump = new F8_JumpOverRiver();
        int[] R = new int[]{1,1,1,0,1,1,0,0};
        System.out.println(jump.minSteps(R)); //3
    }
}

package com.interview.flag.g;

import com.interview.basics.sort.QuickSorter;
import com.interview.basics.sort.Sorter;
import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 15-1-18
 * Time: 下午1:31
 */
public class G11_TaskAssignment {
    static Sorter<Task> SORTER = new QuickSorter<Task>();

    static class Task implements Comparable<Task> {
        int number;
        Integer request;
        Integer output;

        Task(int number, Integer request, Integer output) {
            this.number = number;
            this.request = request;
            this.output = output;
        }

        @Override
        public int compareTo(Task task) {
            if(output == task.output){
                return request.compareTo(task.request);
            } else return output.compareTo(task.output);
        }
    }

    public static int[] assign(int available, int[] request, int[] output) {
        Task[] tasks = new Task[request.length];
        for (int i = 0; i < request.length; i++) tasks[i] = new Task(i, request[i], output[i]);
        SORTER.sort(tasks);

        int[] seq = new int[request.length];
        for (int i = 0; i < tasks.length; i++) {
            if (available < tasks[i].request) return new int[0];
            seq[i] = tasks[i].number;
            available -= tasks[i].output;
        }
        return seq;
    }

    public static void main(String[] args){
        G11_TaskAssignment assignment = new G11_TaskAssignment();
        int[] request = new int[]{8, 10};
        int[] output = new int[]{6, 5};

        int[] seq = assignment.assign(14, request, output);
        ConsoleWriter.printIntArray(seq);//1, 0
    }
}

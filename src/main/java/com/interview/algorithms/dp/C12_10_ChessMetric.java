package com.interview.algorithms.dp;

/**
 * Created_By: zouzhile
 * Date: 4/6/14
 * Time: 7:20 AM
 *

 Problem URL: http://community.topcoder.com/stat?c=problem_statement&pm=1592&rd=4482

 Suppose you had an n by n chess board and a super piece called a kingknight.
 Using only one move the kingknight denoted 'K' below can reach any of the spaces denoted 'X' or 'L' below:

 .......
 ..L.L..
 .LXXXL.
 ..XKX..
 .LXXXL.
 ..L.L..
 .......

 In other words, the kingknight can move either one space in any direction (vertical, horizontal or diagonally)
 or can make an 'L' shaped move. An 'L' shaped move involves moving 2 spaces horizontally then 1 space vertically
 or 2 spaces vertically then 1 space horizontally. In the drawing above, the 'L' shaped moves are marked with 'L's
 whereas the one space moves are marked with 'X's. In addition, a kingknight may never jump off the board.

 Given the size of the board, the start position of the kingknight and the end position of the kingknight,
 your method will return how many possible ways there are of getting from start to end in exactly numMoves moves.
 start and finish are int[]s each containing 2 elements. The first element will be the (0-based) row position and
 the second will be the (0-based) column position. Rows and columns will increment down and to the right respectively.
 The board itself will have rows and columns ranging from 0 to size-1 inclusive.

 Note, two ways of getting from start to end are distinct if their respective move sequences differ in any way.
 In addition, you are allowed to use spaces on the board (including start and finish) repeatedly during a particular
 path from start to finish. We will ensure that the total number of paths is less than or equal to 2^63-1
 (the upper bound for a long).
 */
public class C12_10_ChessMetric {

    class Counter {
        long count = 0;

        public void increase() {
            this.count ++ ;
        }

        public long value() {
            return this.count;
        }
    }

    public long howMany(int size, int[] start, int[] end, int numMoves) {
        Counter counter = new Counter();
        this.howMany(size, start, end, 0, numMoves, counter);
        return counter.value();
    }

    public void howMany(int size, int[] current, int[] end, int currentNumMoves, int numMoves, Counter counter) {
        if(current[0] < 0 || current[0] >= size || current[1] < 0 || current[1] >= size || currentNumMoves > numMoves)
            return;

        if(currentNumMoves > 0 && currentNumMoves == numMoves && current[0] == end[0] && current[1] == end[1]) {
            counter.increase();
        } else {
/*

 .......
 ..L.L..
 .LXXXL.
 ..XKX..
 .LXXXL.
 ..L.L..
 .......

 */
            // visit all X positions in clock-wise order
            this.howMany(size, new int[] {current[0]-1, current[1]}, end, currentNumMoves + 1, numMoves, counter);
            this.howMany(size, new int[] {current[0]-1, current[1]+1}, end, currentNumMoves + 1, numMoves, counter);
            this.howMany(size, new int[] {current[0], current[1]+1}, end, currentNumMoves + 1, numMoves, counter);
            this.howMany(size, new int[] {current[0]+1, current[1]+1}, end, currentNumMoves + 1, numMoves, counter);
            this.howMany(size, new int[] {current[0]+1, current[1]}, end, currentNumMoves + 1, numMoves, counter);
            this.howMany(size, new int[] {current[0]+1, current[1]-1}, end, currentNumMoves + 1, numMoves, counter);
            this.howMany(size, new int[] {current[0], current[1] - 1}, end, currentNumMoves + 1, numMoves, counter);
            this.howMany(size, new int[] {current[0]-1, current[1] - 1}, end, currentNumMoves + 1, numMoves, counter);

            // visit all L positions in clock-wise order
            this.howMany(size, new int[] {current[0]-2, current[1] + 1}, end, currentNumMoves + 1, numMoves, counter);
            this.howMany(size, new int[] {current[0]-1, current[1] + 2}, end, currentNumMoves + 1, numMoves, counter);
            this.howMany(size, new int[] {current[0]+1, current[1] + 2}, end, currentNumMoves + 1, numMoves, counter);
            this.howMany(size, new int[] {current[0]+2, current[1] + 1}, end, currentNumMoves + 1, numMoves, counter);
            this.howMany(size, new int[] {current[0]+2, current[1] - 1}, end, currentNumMoves + 1, numMoves, counter);
            this.howMany(size, new int[] {current[0]+2, current[1] - 2}, end, currentNumMoves + 1, numMoves, counter);
            this.howMany(size, new int[] {current[0]-1, current[1] - 2}, end, currentNumMoves + 1, numMoves, counter);
            this.howMany(size, new int[] {current[0]-2, current[1] - 1}, end, currentNumMoves + 1, numMoves, counter);
        }
    }
}

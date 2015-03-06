package edu.princeton.cs.algs4.ch24;
import edu.princeton.cs.algs4.ch13.Stack;
import  edu.princeton.cs.introcs.*;

import java.util.ArrayList;
import java.util.List;

/*************************************************************************
 *  Compilation:  javac TopM.java
 *  Execution:    java TopM M < input.txt
 *  Dependencies: MinPQ.java Transaction.java StdIn.java StdOut.java
 *  Data files:   http://algs4.cs.princeton.edu/24pq/tinyBatch.txt
 * 
 *  Given an integer M from the command line and an input stream where
 *  each line contains a String and a long value, this MinPQ client
 *  prints the M lines whose numbers are the highest.
 * 
 *  % java TopM 5 < tinyBatch.txt 
 *  Thompson    2/27/2000  4747.08
 *  vonNeumann  2/12/1994  4732.35
 *  vonNeumann  1/11/1999  4409.74
 *  Hoare       8/18/1992  4381.21
 *  vonNeumann  3/26/2002  4121.85
 *
 *************************************************************************/

/**
 *  The <tt>TopM</tt> class provides a client that reads a sequence of
 *  transactions from standard input and prints the <em>M</em> largest ones
 *  to standard output. This implementation uses a {@link MinPQ} of size
 *  at most <em>M</em> + 1 to identify the <em>M</em> largest transactions
 *  and a {@link edu.princeton.cs.algs4.ch13.Stack} to output them in the proper order.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/24pq">Section 2.4</a>
 *  of <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class TopM {   

    // This class should not be instantiated.
    private TopM() { }

    /**
     *  Reads a sequence of transactions from standard input; takes a
     *  command-line integer M; prints to standard output the M largest
     *  transactions in descending order.
     */
    public static void main(String[] args) {
        //cmdTest(args);

        int M = 5;
        MinPQ<Transaction> pq = new MinPQ<Transaction>(M+1);

        List<Transaction> list = new ArrayList<Transaction>();

        list.add(new Transaction("Turing      6/17/1990   644.08"));
        list.add(new Transaction("vonNeumann  3/26/2002  4121.85"));
        list.add(new Transaction("Dijkstra    8/22/2007  2678.40"));
        list.add(new Transaction("vonNeumann  1/11/1999  4409.74"));
        list.add(new Transaction("Dijkstra   11/18/1995   837.42"));
        list.add(new Transaction("Hoare       5/10/1993  3229.27"));
        list.add(new Transaction("vonNeumann  2/12/1994  4732.35"));
        list.add(new Transaction("Hoare       8/18/1992  4381.21"));
        list.add(new Transaction("Turing      1/11/2002    66.10"));
        list.add(new Transaction("Thompson    2/27/2000  4747.08"));
        list.add(new Transaction("Turing      2/11/1991  2156.86"));
        list.add(new Transaction("Hoare       8/12/2003  1025.70"));
        list.add(new Transaction("vonNeumann 10/13/1993  2520.97"));
        list.add(new Transaction("Dijkstra    9/10/2000   708.95"));
        list.add(new Transaction("Turing     10/12/1993  3532.36"));
        list.add(new Transaction("Hoare       2/10/2005  4050.20"));

        for(Transaction s : list){
            pq.insert(s);

            // remove minimum if M+1 entries on the PQ
            if (pq.size() > M)
                pq.delMin();
        }

        // print entries on PQ in reverse order
        Stack<Transaction> stack = new Stack<Transaction>();
        for (Transaction transaction : pq)
            stack.push(transaction);
        for (Transaction transaction : stack)
            StdOut.println(transaction);
    }

    public static void cmdTest(String[] args){
        int M = Integer.parseInt(args[0]);
        MinPQ<Transaction> pq = new MinPQ<Transaction>(M+1);

        while (StdIn.hasNextLine()) {
            // Create an entry from the next line and put on the PQ.
            String line = StdIn.readLine();
            Transaction transaction = new Transaction(line);
            pq.insert(transaction);

            // remove minimum if M+1 entries on the PQ
            if (pq.size() > M)
                pq.delMin();
        }   // top M entries are on the PQ

        // print entries on PQ in reverse order
        Stack<Transaction> stack = new Stack<Transaction>();
        for (Transaction transaction : pq)
            stack.push(transaction);
        for (Transaction transaction : stack)
            StdOut.println(transaction);
    }
} 



/*************************************************************************
 *  Copyright 2002-2012, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4-package.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4-package.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4-package.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with algs4-package.jar.  If not, see http://www.gnu.org/licenses.
 *************************************************************************/


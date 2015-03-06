package com.interview.algorithms.list;

import com.interview.datastructures.list.Node;
import com.interview.utils.ConsoleReader;
import com.sun.jmx.remote.internal.ArrayQueue;

import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Find the -M element in a node list
 * 
 * @author zouzhile (zouzhile@gmail.com)
 *
 */
public class C3_3_LastMElementFinder {
    String result = null;

	public int findElement(Node list, int m){
        if(list == null)
            return 0;
        int k = findElement(list.next(), m) + 1;
        if(k == m)
            result = list.getValue();
		return k;
	}

    public String findMElement(Node list, int m){
        Queue<String> queue = new LinkedBlockingQueue<String>();
        while(list != null){
            queue.add(list.getValue());
            if(queue.size() > m) queue.poll();
            list = list.next();
        }
        return queue.poll();
    }
	
	public static void main(String[] args) {
		System.out.println("Search the Node at the '-M' position in the list");
		System.out.println("===============================================================================");
		ConsoleReader reader = new ConsoleReader();
		System.out.print("Please input the node values: ");
		String[] elements = reader.readStringItems();
		System.out.print("Please input the value of M: ");
		int m = reader.readInt();
		//build the node list
		Node head = null, current = null;
		for(int i = 0; i < elements.length; i++){
			Node node = new Node(elements[i], null);
			if(i == 0){
				head = node;
				current = node;
			} else {
				current.setNext(node);
				current = node;
			}
		}
		C3_3_LastMElementFinder finder = new C3_3_LastMElementFinder();
		finder.findElement(head, m);
        String element = finder.findMElement(head, m);
		if (finder.result == null){
			System.out.println("List is empty or its length is smaller than " + m);
		} else {
			System.out.println("The '-M' element is: " + finder.result);
            System.out.println("The '-M' element is: " + element);
		}	    
	}

}

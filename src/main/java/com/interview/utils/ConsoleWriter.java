package com.interview.utils;

import com.interview.basics.model.collection.list.LinkedList;
import com.interview.basics.model.collection.list.Node;

import java.util.Collection;
import java.util.List;

public class ConsoleWriter {

	public static void printIntArray(int[] array){
		for(int i = 0; i < array.length; i++){
			System.out.print(array[i] + ", ");
		}
		System.out.println();
	}

    public static void printIntArray(Integer[] array){
        for(int i = 0; i < array.length; i++){
            System.out.print(array[i] + ", ");
        }
        System.out.println();
    }

    public static void printIntArray(int[][] array) {
        for(int i = 0; i < array.length; i++){
            for(int j = 0; j< array[0].length; j ++)
                System.out.print("\t" + (array[i][j] != Integer.MAX_VALUE? array[i][j] : "~"));
            System.out.println();
        }
    }

    public static void printIntArray(char[][] array) {
        for(int i = 0; i < array.length; i++){
            for(int j = 0; j< array[0].length; j ++)
                System.out.print("\t" + array[i][j]);
            System.out.println();
        }
    }

    public static void print(List<List<Integer>> cols){
        for(List<Integer> item : cols){
            for(Integer number : item){
                System.out.print(number + " ");
            }
            System.out.println();
        }
    }

    public static void printStringList(List<List<String>> cols){
        for(List<String> item : cols){
            for(String number : item){
                System.out.print(number + " ");
            }
            System.out.println();
        }
    }
    
    public static void printCollection(Collection col){
    	for(Object i : col){
			System.out.print(i.toString() + " ");
		}
		System.out.println();
    }

    public static void printLinkedList(LinkedList list){
        Node node = list.getHead();
        while(node != null){
            System.out.print(node.item.toString() + " ");
            node = node.next;
        }
        System.out.println();
    }

    public static void printBooleanArray(boolean[] array){
        for(int i = 0; i < array.length; i++){
            System.out.print(array[i] + " ");
        }
        System.out.println();
    }
    public static void printCharacterArray(Character[] array){
        for(int i = 0; i < array.length; i++){
            System.out.print(array[i]);
        }
        System.out.println();
    }

    public static void printListOfList(List<List<Integer>> cols){
        for(List<Integer> item : cols){
            for(Integer number : item){
                System.out.print(number + " ");
            }
            System.out.println();
        }
    }

    public static void printList(Collection list){
        for(Object obj : list) {
            System.out.println(obj.toString());
        }
    }
    
}

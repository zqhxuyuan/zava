package com.interview.algorithms.list;

import com.interview.basics.model.collection.list.LinkedList;
import com.interview.basics.model.collection.list.Node;

/**
 * Created_By: stefanie
 * Date: 14-7-10
 * Time: 下午10:07
 */
public class C3_11_ListNumberAdd {

    public static LinkedList<Integer> add(LinkedList<Integer> number1, LinkedList<Integer> number2){
        LinkedList<Integer> sum = new LinkedList<>();
        sum.add(1);
        Node<Integer> p = sum.getHead();
        Node<Integer> num1 = number1.getHead();
        Node<Integer> num2 = number2.getHead();
        int carry = 0;
//        while(num1 != null && num2 != null){
//            carry = addNode(num1.item, num2.item, carry, p);
//            num1 = num1.next;
//            num2 = num2.next;
//        }
//        while(num1 != null){
//            carry = addNode(num1.item, 0, carry, p);
//            num1 = num1.next;
//        }
//        while(num2 != null){
//            carry = addNode(0, num2.item, carry, p);
//            num2 = num2.next;
//        }
        while(num1 != null || num2 != null){
            int i = num1 == null? 0 : num1.item;
            int j = num2 == null? 0 : num2.item;
            carry = addNode(i, j, carry, p);
            p = p.next;
            num1 = num1 == null? null : num1.next;
            num2 = num2 == null? null : num2.next;
        }
        if(carry > 0){
            addNode(0, 0, carry, p);
        }
        sum.remove(0);
        return sum;
    }

    private static int addNode(Integer num1, Integer num2, int carry, Node<Integer> current){
        int sumNumber = num1 + num2 + carry;
        Node<Integer> node = new Node<>(sumNumber % 10);
        current.next = node;
        return sumNumber / 10;
    }

    public static LinkedList<Integer> addRecursive(LinkedList<Integer> number1, LinkedList<Integer> number2){
        LinkedList<Integer> sum = new LinkedList<>();
        Node<Integer> head = addList(number1.getHead(), number2.getHead(), 0);
        sum.setHead(head);
        return sum;
    }

    private static Node<Integer> addList(Node<Integer> num1, Node<Integer> num2, int carry){
        if(num1 == null && num2 == null && carry == 0) return null;
        Node<Integer> result = new Node<Integer>(carry);

        int sum = carry;
        if(num1 != null) sum += num1.item;
        if(num2 != null) sum += num2.item;
        result.item = sum % 10;

        if(num1 != null || num2 != null){
            Node<Integer> more = addList(num1 == null? null: num1.next,
                                         num2 == null? null: num2.next,
                                         sum / 10);
            result.next = more;
        }
        return result;
    }
}

package com.interview.algorithms.list;


/**
 * Created_By: stefanie
 * Date: 14-8-5
 * Time: 下午11:02
 *
 * Given a complicate linked list, besides a next pointer, have a sibling pointer which point to any node in the linked list or NULL.
 * Write code to clone the linked list.
 *
 * The trick is like this:
 *  take use of the old pSibling, make it points to the new created cloned node,
 *  while make the new cloned node’s pNext backup the old pSibling.
 *
 */

class ComplicateNode<T>{

    public T item;
    public ComplicateNode<T> next;
    public ComplicateNode<T> sibling;

    public ComplicateNode(T element) {
        item = element;
    }

    @Override
    public int hashCode() {
        return item.hashCode();
    }
}

public class C3_17_ComplicateLinkedListClone {
    public static ComplicateNode clone(ComplicateNode head){
        if(head == null) return null;
        preClone(head);
        inClone(head);
        return postClone(head);
    }

    private static void preClone(ComplicateNode head){
        ComplicateNode p = new ComplicateNode(head.item);
        p.next = head.sibling;
        head.sibling = p;
        if(head.next != null) preClone(head.next);
    }

    private static void inClone(ComplicateNode head){
        ComplicateNode p = head.sibling;
        if(p.next == null) p.sibling = null;
        else p.sibling = p.next.sibling;
        if(head.next != null) inClone(head.next);
    }

    private static ComplicateNode postClone(ComplicateNode head){
        ComplicateNode newHead = head.sibling;

        ComplicateNode sibling = newHead.next;
        if(head.next != null){
            newHead.next = head.next.sibling;
            head.sibling = sibling;
            postClone(head.next);
        } else {
            newHead.next = null;
            head.sibling = sibling;
        }
        return newHead;
    }

}

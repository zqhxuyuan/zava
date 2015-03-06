package com.interview.algorithms.general;

/**
 * Created_By: zouzhile
 * Date: 10/25/14
 * Time: 6:25 PM
 */
public class C1_67_CombinationPrinter {
    public void print(String content) {
        print(content, 0, "");
    }

    public void print(String content, int i, String prefix) {
        System.out.println(prefix + content.charAt(i)); // print current selection

        if(i == content.length() - 1) return;

        print(content, i+1, prefix + content.charAt(i));
        print(content, i+1, prefix);
    }

    public static void main(String[] args) {
        C1_67_CombinationPrinter printer = new C1_67_CombinationPrinter();
        printer.print("abc");
    }
}

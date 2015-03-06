package com.interview.algorithms.general;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: zouzhile
 * Date: 10/25/14
 * Time: 6:30 PM
 */
public class C1_68_PermutationPrinter {
    public void print(String content) {

        for(String permutation: this.permute(content))
            System.out.println(permutation);
    }

    private List<String> permute(String content) {
        List<String> result = new ArrayList<String>();
        if(content.length() == 1) {
            result.add(content);
        } else {
            for(int i = 0; i < content.length(); i ++) {
                for(String permutation : permute(content.substring(0, i) + content.substring(i+1)))
                    result.add(content.charAt(i) + permutation);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        C1_68_PermutationPrinter printer = new C1_68_PermutationPrinter();
        printer.print("abc");
    }
}

package com.interview.flag.g;

/**
 * Created_By: stefanie
 * Date: 14-11-26
 * Time: 下午6:03
 */

import com.interview.utils.ConsoleWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * 数字反转180度，逆向反转，输出长度小于N的所有数字。
 * 如96196，数字反转180度是69169，然后逆转是96196，是他自己。
 */
public class G7_ReverseNumber {
    String[] options = new String[]{"1", "8", "69", "96"};

    List<String> sols;
    public List<String> findAll(int len){
        sols = new ArrayList<>();
        findAll(len, "", "");
        return sols;
    }

    public void findAll(int len, String prefix, String suffix){
        if(len == 0){
            sols.add(prefix + suffix);
        } else if(len == 1){
            sols.add(prefix + "1" + suffix);
            sols.add(prefix + "8" + suffix);
        } else {
            findAll(len - 2, prefix + "1", "1" + suffix);
            findAll(len - 2, prefix + "8", "8" + suffix);
            findAll(len - 2, prefix + "6", "9" + suffix);
            findAll(len - 2, prefix + "9", "6" + suffix);
        }
    }

    public static void main(String[] args){
        G7_ReverseNumber finder = new G7_ReverseNumber();
        List<String> sols = finder.findAll(5);
        ConsoleWriter.printCollection(sols);
    }
}

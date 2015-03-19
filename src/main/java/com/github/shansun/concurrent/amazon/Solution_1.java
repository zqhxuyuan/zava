package com.github.shansun.concurrent.amazon;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Question:
 *
 * We have an array representing customer’s shopping records.
 *
 * For example, it’s an array like this:
 *
 * custA, item1,
 *
 * custB, item1,
 *
 * custA, item2,
 *
 * custB, item3,
 *
 * custC, item1,
 *
 * custC, item3,
 *
 * custD, item2,
 *
 * This array indicates that customer A bought item 1, customer B bought item 1,
 * customer A bought item 2, customer B bought item 3, etc..
 *
 * For a given item X and shopping records array, write code to find out what
 * else (item Y) was bought mostly by the customers who bought item X.
 *
 * For example, in above example, if X is item 1 then Y should be item 3.
 *
 * Rules:
 *
 * 1.One customer can only buy one item once.. 2.The mostly brought item should
 * not be item X.. 3.If no customer brought item X, then return “None”. 4.If all
 * the customers who brought item X only brought item X, then return “None”.
 * 5.The first line of input is the item X. The second line of input is the
 * shopping record array, this shopping record array is split by space.. 6.If
 * there are many other mostly brought items which have equally brought times,
 * then return any one of those items.. Examples:
 *
 * Input1:
 *
 * item1
 *
 * custA item1 custB item1 custA item2 custB item3 custC item1 custC item3 custD
 * item2
 *
 * Output1:
 *
 * item3
 *
 * Input2:
 *
 * item2
 *
 * custA item1 custB item1 custC item1 custA item2 custB item3 custA item3
 *
 * Output2:
 *
 * item1
 *
 * (The output2 can be item3 too)
 *
 * @author lanbo <br>
 * @version 1.0 <br>
 * @date 2012-10-10
 */
public class Solution_1 {
    private String findMostlyBroughtItem(String[] shoppingRecordArray, String itemX) {
        // key=商品ID，value=数量
        Map<String, Integer> itemCountMap = new HashMap<String, Integer>();

        // 需要处理的商品ID集合
        Set<String> affectedItems = new HashSet<String>();

        // 需要关注的买家ID集合
        Set<String> affectedCusts = new HashSet<String>();

        // key=买家ID，value=购买过的商品集合
        Map<String, Set<String>> custItemMap = new HashMap<String, Set<String>>();

        int length = shoppingRecordArray.length, i = 0;
        while (i < length) {
            String cust = shoppingRecordArray[i++];
            String item = shoppingRecordArray[i++];

            if (item.equals(itemX)) {
                affectedCusts.add(cust);
            }

            Set<String> set = custItemMap.get(cust);
            if (set == null) {
                set = new HashSet<String>();
            }
            set.add(item);
            custItemMap.put(cust, set);

            Integer cnt = itemCountMap.get(item);
            itemCountMap.put(item, cnt == null ? 1 : cnt + 1);
        }

        for (String cust : affectedCusts) {
            affectedItems.addAll(custItemMap.get(cust));
        }

        int max = 0;
        String maxItem = null;
        for (String item : affectedItems) {
            int cnt = itemCountMap.get(item);
            if (cnt > max && !item.equals(itemX)) {
                max = cnt;
                maxItem = item;
            }
        }

        return maxItem == null ? "None" : maxItem;
    }

    public static void main(String[] args) {
        Solution_1 solution = new Solution_1();
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            // Initialize the item X
            String itemX = scanner.nextLine();

            // Initialize the shopping record array
            String strLine2 = scanner.nextLine();
            StringTokenizer stringTokenizer = new StringTokenizer(strLine2);
            int arrayLength = stringTokenizer.countTokens();
            String[] shoppingRecordArray = new String[arrayLength];
            for (int j = 0; j < arrayLength; j++) {
                shoppingRecordArray[j] = stringTokenizer.nextToken();
            }

            String mostlyBroughtItem = solution.findMostlyBroughtItem(shoppingRecordArray, itemX);
            System.out.println(mostlyBroughtItem);
        }
    }
}
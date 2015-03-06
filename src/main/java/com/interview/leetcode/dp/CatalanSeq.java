package com.interview.leetcode.dp;

import com.interview.leetcode.utils.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-24
 * Time: 下午12:47
 */
public class CatalanSeq {

    //One-Sequence DP
    public int catalanSeq(int n) {
        if (n == 0) return 0;
        if (n == 1) return 1;
        int[] nums = new int[n + 1];
        nums[0] = 1;
        nums[1] = 1;
        for (int total = 2; total <= n; total++) {
            nums[total] = 0;
            for (int left = 0; left < total; left++) {
                nums[total] += nums[left] * nums[total - 1 - left]; //total - 1 - left is right
            }
        }
        return nums[n];
    }

    /**
     * Given n, generate all structurally unique BST's (binary search trees) that store values 1...n
     */
    static class UniqueBST{

        //Memo DP
        public List<TreeNode> generateTrees(int n) {
            HashMap<String, List<TreeNode>> memo = new HashMap<>();
            return generateTrees(1, n, memo);
        }

        private List<TreeNode> generateTrees(int low, int high, HashMap<String, List<TreeNode>> memo){
            String key = low + "-" + high;
            if(memo.containsKey(key)) return new ArrayList<TreeNode>(memo.get(key));
            List<TreeNode> trees = new ArrayList<TreeNode>();
            if(low > high) {
                trees.add(null);
                return trees;
            }
            for(int value = low; value <= high; value++){      //value as root element
                List<TreeNode> leftTree = generateTrees(low, value - 1, memo);
                List<TreeNode> rightTree = generateTrees(value + 1, high, memo);
                for(TreeNode leftNode : leftTree){
                    for(TreeNode rightNode: rightTree){
                        TreeNode root = new TreeNode(value);
                        root.left = leftNode;
                        root.right = rightNode;
                        trees.add(root);
                    }
                }
            }
            memo.put(key, trees);
            return trees;
        }
    }


    static class Parenthesis {
        List<String> sols;

        public List<String> generateParenthesis(int n) {
            HashMap<String, List<String>> memo = new HashMap<>();
            int left = 0;
            int right = 0;
            generate(left, right, n, "");
            return memo.get("1-" + n);
        }

        public void generate(int left, int right, int total, String prefix) {
            if (prefix.length() == 2 * total) {
                sols.add(prefix);
                return;
            }
            if (left < total) generate(left + 1, right, total, prefix + "(");
            if (right < total && right < left) generate(left, right + 1, total, prefix + ")");
        }
    }
}

package com.interview.algorithms.general;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 14-2-19
 * Time: 上午8:43
 * To change this template use File | Settings | File Templates.
 *
 * Given a capacity value N, and a set of different Item types with values v1, v2, ...,vn
 * 1) Existence Check: check whether N can be filled with a certain combination of items
 * 2) All Combinations: get all the combinations of items that fills N
 * 3) Minimum Combination: get the minimum number of items that fills N
 */
public class C1_32_CapacityAndItems {

    public boolean check(int N, int[] items) {
        return check(N, 0, items);
    }

    public boolean check(int N, int currentSum, int[] items) {
        if(currentSum > N)
            return false;
        if(currentSum == N)
            return true;
        for(int i = 0; i < items.length; i ++) {
            if(this.check(N, currentSum + items[i], items))
                return true;
        }
        return false;
    }

    class Status {
        int sum = 0;
        int[] itemsCount;
        int code = -1;
    }

    /**
     * If N can buy the items with the given items, get the amount of each item
     * @param N
     * @param items
     * @return
     */
    public void getAllCombinations(int N, int[] items) {
        Status status = new Status();
        status.itemsCount = new int[items.length];
        getAllCombinations(N, items, status, 0);
    }

    public void getAllCombinations(int N, int[] items, Status status, int itemType) {

        if(status.sum > N){
            status.code = 1;
            return;
        }

        if(status.sum == N) {
            printCombination(items, status.itemsCount, status.sum);
            status.code = 0;
            return;
        }

        // given current itemsCount[type]
        // the for loop try with more items[type] or the types after the given "type"
        // i.e. look down or right.
        for(int i = itemType; i < items.length; i ++) {
            // put in item[i]
            status.itemsCount[i] += 1;
            status.sum += items[i];
            this.getAllCombinations(N, items, status, i);
            if(status.code >= 0) {
                // when the last itm make the sum equal or bigger than N,
                // move out the last item to try other items
                status.sum -= items[i];
                status.itemsCount[i] -= 1; // take out item[i]
            }
        }

        // try with 1 less items[type] and then look at right side by recursion after line 72
        status.itemsCount[itemType] --;
        status.sum -= items[itemType];
        status.code = -1;
    }

    private void printCombination(int[] items, int[] itemsCount, int sum) {
        for(int i = 0; i < items.length; i ++)
            System.out.print("\t" + items[i] + ":" + itemsCount[i]);
        System.out.print("\tsum=" + sum);
        System.out.println();
    }

    private int[] sortInDescendingOrder(int[] items) {
        // bubble sort, since the amount of items is very trivial
        for(int i = 0; i < items.length - 1; i++)
            for(int j = i + 1; j < items.length; j ++) {
                if(items[j] > items[i]){
                    int tmp = items[i];
                    items[i] = items[j];
                    items[j] = tmp;
                }
            }
        return items;
    }

    /**
     * Sort the items by value in descending order
     * The minimum combination is the first combination that fills N.
     * @param N
     * @param items
     */
    public void getMinimumCombination(int N, int[] items) {
        items = this.sortInDescendingOrder(items);
        Status status = new Status();
        status.itemsCount = new int[items.length];
        getMinimumCombination(N, items, status, 0);
    }


    private void getMinimumCombination(int N, int[] items, Status status, int itemType) {
        if(status.sum > N) {
            status.code = 1;
            return;
        }

        if(status.sum == N){
            status.code = 0;
            printCombination(items, status.itemsCount, status.sum);
            System.exit(0);
        }

        for(int i = itemType; i < items.length; i ++) {
            status.itemsCount[i] += 1;
            status.sum += items[i];
            this.getMinimumCombination(N, items, status, i);
            if(status.code >= 0){
                status.sum -= items[i];
                status.itemsCount[i] --;
            }
        }

        status.itemsCount[itemType] --;
        status.sum -= items[itemType];
        status.code = -1;
    }

    public static void main(String[] args) {
        C1_32_CapacityAndItems checker = new C1_32_CapacityAndItems();
        int N = 42;
        int[] items = new int[]{6, 9, 20};
        String itemString = "";
        for(int item : items)
            itemString += item + " ";
        System.out.println("N = " + N + ", items = " + itemString);
        System.out.println("Combination Exists? : " + checker.check(N, items));
        System.out.println("All Combinations: ");
        checker.getAllCombinations(N, items);
        System.out.println("Minimum Combination");
        checker.getMinimumCombination(N, items);
    }

}

#Problem Lists and HINTs

**Bold Problem** is **HARD and IMPORTANT**

1.  Searching in a special matrix, each row in the matrix is sorted and the first element of each row is larger than
    the last element in previous row. 
    
    *HINT: BinarySearch, convert searching offset into row and col in the matrix*

2.  Given several interval, find how many interval have overlap.

    *HINT: sort the interval by start, and scan*
    
3.  Given N integer, N is even number, write code to check if could find N/2 pair in the array, the sum of each pair
    could be divided by K. Each element can only use once.
    
    *HINT: count mod*
    
4.  Given two int array A and B with same length, each one indicate a number, such A=[1,2,3,4] = 1234, B=[2,4,1,0] = 2410.
    Write code to find the closest number C larger than B, C is re-arranged from A
    In the previous case C = [2,4,1,3].
    
    *HINT: sort A and generate C based on B*
    
5.  Given N points in a canvas, write code to find the K points closest to (0,0).  
    
    *HINT: Max Heap*

6.  Given a int array numbers[], and a int K, find the least subset(smallest subset) which sum > K.
    
    *HINT: Greedy, select bigger number, QuickFind with tracking sum*
    
7.  Find the Lowest Common Ancestor of two nodes in a BST.
    
    *HINT: check the range*

8.  **Find the Lowest Common Ancestor of two nodes in a Generic Tree.**
    Here generic tree is a tree which node may have more than 2 nodes.
    
    *HINT: BSF find the two nodes, and compare the path.* 
    
9.  Given a BST, find K nodes closest to given M.
    
    *HINT: Max Heap + In Order Traverse*

10. **Implement a Iterator for BST, space complexity O(lgN).**
    
    *HINT: the iteratively implementation of in-order traverse.*

11. **Given two string S and T, print out all the string interleaving of S and T.**
    
    *HINT: Recursive like Combination*

12. Given a int array num[], find all offset i which i's left elements <= num[i], and i's right elements >= num[i]
    Assume num[-1] = Integer.MIN_VALUE and num[num.length] = Integer.MAX_VALUE;
    
    *HINT: scan forward and backward.*

13. **Given two array A and B, the length is m and n, m < n.** 
    Insert (n-m) numbers of 0 in A to make get the smallest A * B, return the product value.
    
    Example: A = {1, -1} B = {1,2,3,4}, insert A'={1, 0, 0, -1}  A' * B = -3 return -3
    
    *HINT:DP problem, State Definition is product[i][j] is the min product of A[0]...A[i-1] * B[0]...B[j-1]*
         
    *when i == j; product[i][j] = A[i] * B[j] + product[i-1][j-1];*
         
    *when 0 < i < j <= i + n - m; product[i][j] = min(A[i] * B[j] + product[i-1][j-1], product[i][j-1])*

14. Have a task to paint the house in a street, and the color of house can't be the same as it's neighbors. 
    Each color have different price. Find out the min cost to paint the whole street.
    
    *HINT: Greedy, select two cheapest, and paint one by one*

15. Given a input string, and several character transform rules, print out all the string can be transformed.
    For example: input "face", rules are 'a'->'@','e'->'3','e'->'E', and the output: fac3,facE,f@ce,f@c3,f@cE
    
    *HINT: Combination with Reuse*
    
16. Given a directly graph, find how many ways to travel from node A to node B with N steps. 
    Node can visited multiple times.
    
    *HINT: BSF with N steps without tracking visited*
    
17. **Given a BST, search all nodes between to two given elements.**
    
    *HINT: In order traverse*

18. **Print the path from root to each leaf node non-recursively.**

    *HINT: based on iteratively post order traverse*
    
19. **Given a BST and int K, find two nodes in the BST which sum is K, space complexity O(lgN)**
    
    *HINT: using two stack, like iteratively in order traverse*
    
20. Given a Yang Matrix, find K-th smallest element
    
    Yang Matrix is a 2-dimensional matrix, which row and col are sorted, but the first element in current row 
    may not be larger than last element in previous row, like Question 1.
    
    *HINT: MaxHeap, scan from left-up conner to right-down conner*

21. A integer array [1-N]'s signature can be calculate like following:
    
        'D' means current number is in decreasing order, that num[i] > num[i + 1], 
        'I' means current number is in increasing order, that num[i] < num[i + 1].
        For example: "DDIIDI" is the signature of [3,2,1,4,6,5], [4,2,1,3,6,5], etc
    
    Write code to find the smallest array of a given signature.
    
    *HINT: count the generate*
    
22. Write code to check if a given number is an aggregated number.
    A aggregated number is a number can be partitioned into several part, and part[i] = part[i - 2] + part[i - 1]
    For example: 112358 is aggregated number [1,1,2,3,5,8]; 122436 is aggregated number as well, [12,24,36]
    
    *HINT: try different partition, until find a partition all follow the rule part[i] = part[i - 2] + part[i - 1]*
     
23. **Given a integer array as probability density function of a random number generator.** 
    For example: given int array P, which length is L, return random number r in range [0-L), possibility of i is P[i]/sum(P)
    
    *HINT: convert the int array to range array (0-sum[P]), generate int in that range, and map to [0-L) using BinarySearch* 
    
24. Given a building with N floor, assume you can move 2^k floor every time and can only go up, k is arbitrary integer. 
    Find out the min time you need move to get to the top floor.
    
    *HINT: 2^k is like binary expression of N. So the problem will change to how many 1 in N's binary expression.*
    
25. Implement a Iterator for a Complex Data Structure. At least have hasNext() and next() method.

    *HINT: flattenElement when create Iterator.*


    
   
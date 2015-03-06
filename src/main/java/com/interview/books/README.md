#Question List
**Bold Problem** is **IMPORTANT and HARD/MEDIAN**
    
        Array: Two Pointer, DP, Traversal, Stack/Heap, Permutation/Combination[Back-tracing]
        LinkedList: Two Pointer, DummyHead
        Tree: Traversal(pre, in, post, level), Stack/Queue
        String: DP, Hashing, Stack/Queue/Heap

##Leetcode Blogs
1.  Given a node from a cyclic linked list which has been sorted, write a function to insert a value into the list such 
    that it remains a cyclic sorted list. The given node can be any single node in the list.
    
    *HINT: Careful for edge case: list only have 1 element, value is smaller than head, etc*

2.  Given two axis-aligned rectangles A(p1,p2) and B(q1,q2). Write a function to determine if the two rectangles overlap.

    !(p2.x < q1.x || p1.x > q2.x || p2.y > q1.y || p1.y < q2.y)

3.  **Given a 2D point and a rectangle(p1, p2, p3, p4), determine if the point is inside the rectangle.**
    Note that the rectangle maybe rotated.
    
    http://leetcode.com/2010/04/how-to-determine-if-point-is-inside.html

4.  **Fair Painter**

    You have to paint N boards of length {A0, A1, A2 … AN-1}. There are K painters available (they are in the same speed to paint the house). 
    You have to get this job done as soon as possible under the constraints that any painter will only paint continuous sections of board, 
    say board {2, 3, 4} or only board {1} or nothing but not board {2, 4, 5}.
    
    *HINT: It's a DP, the problem can be transformed to following:*
        
        Given an array of non-negative integers A and a positive integer K, we want to:
        Divide A into K or fewer partitions,
        such that the maximum sum over all the partitions is minimized.

5. **Fair Painter** Could you find a solution without extra Space. Non DP solution
    
    *HINT: BinarySearch in range(max, sum)*
    
6.  **Max Element in the Window**

    Given a long array A[], and a sliding window of size w which is moving from the very left of the array to the very right. 
    You can only see the w numbers in the window. Each time the sliding window moves rightwards by one position. 
    Return an array B[], which B[i] contains the max value in sliding window start from A[i] to A[i+w-1]
    
    *HINT: Heap could achieve O(NlgN), and Double-ended Queue could achieve O(N).
    [When insert pop the smaller element in window from tail, when move forward, pop first elements out of window from head]
    
7.  Studious Student, Facebook Hacker Cup Qualification Round.
    
    You’ve been given a list of words to study and memorize. Being a diligent student of language and the arts, you’ve decided to 
    not study them at all and instead make up pointless games based on them. One game you’ve come up with is to see how you can 
    concatenate the words to generate the lexicographically lowest possible string.
    
    *HINT: directly sort may not work, such as "jibw ji jp bw jibw", created lowest possible string is "bwjibwjibwjijp" not "bwjijibwjibwjp"
    If no word appears to be a prefix of any other words, then the simple sort + concatenate must yield the smallest dictionary order string.*
    
8.  Peg Game: Facebook Hacker Cup Qualification Round: Peg Game [Full Problem Statement](http://leetcode.com/2011/01/peg-game-problem-analysis.html)

    *This problem maybe too difficult for a tech interview*

9.  Double Square: Facebook Hacker Cup Qualification Round  

    A double-square number is an integer X which can be expressed as the sum of two perfect squares. 
    For example, 10 is a double-square because 10 = 3^2 + 1^2. Your task in this problem is, given X, determine the number of 
    ways in which it can be written as the sum of two squares. For example, 10 can only be written as 3^2 + 1^2 
    (we don’t count 1^2 + 3^2 as being different). On the other hand, 25 can be written as 5^2 + 0^2 or as 4^2 + 3^2.
    
    *HINT: searching first element i in [0-sqrt(X)], second element j is X - i^2, if j can be sqrt. have 1 solution.  

10. **Max Count of A [Google Interview Question]**

    Imagine you have a special keyboard with the following keys: A, Ctrl+A, Ctrl+C, Ctrl+V. 
    where CTRL+A, CTRL+C, CTRL+V each acts as one function key for “Select All”, “Copy”, and “Paste” operations respectively.
    If you can only press the keyboard for N times (with the above four keys), please write a program to produce maximum numbers of A. 
    If possible, please also print out the sequence of keys.   
    [LeetCode Discussion](http://leetcode.com/2011/01/ctrla-ctrlc-ctrlv.html)
    
        Note two things:
            1. When you copy something, the content is in clipboard, and you can Ctrl+V to paste the same content
            2. "A, Ctrl+A, Ctrl+C, Ctrl+V" sequence can get only 1 "A", since it will cover the content you "Select All""
    *HINT: It's a DP problem, analysis when continue Paste, when start to do Copy again to achieve O(N) time complexity*
    
11. **Largest BST Subtree** Given a binary tree, find the largest subtree which is a Binary Search Tree (BST),
    where largest means subtree with largest number of nodes in it.    
        
        Note that: subtree means mid-node must include all of its descendants.
    *HINT: Bottom-up approach: based on post order traverse with tracking max in left subtree and min in right subtree
    
12. **Largest BST** Given a binary tree, find the largest Binary Search Tree (BST), where largest means BST with largest number of nodes in it. 
    The largest BST may or may not include all of its descendants.
    
    *HINT: Top-down approach: based on pre-order traverse(check if node.val in range of min and max given by it's parent).
           if yes, build subtree and return itself as child to parent, if no, treat itself as the root.
    
    **The different of 11 and 12, in 11 whether node i in or not depends on it's left&right subtree, so use post-order traverse,
    in 12, it's only depends on itself, so use in-order traverse. Both are tracking min and max bound.**
    
13. Random generation. Given a function which generates a random integer in the range 1 to 7, write a function which generates 
    a random integer in the range 1 to 10 uniformly.
    
        A more generic problem is: Given a function generates a random integer in range 1 to M, write a function to generate random
        integer  1 to N uniformly.
        
    *HINT: use randM generate (randM() - 1) * M + randM(), and normalize it to (1 - ((M*M)/N)*N) and mod N.*
    
        Be careful about the case M^2 < N.
        1. find out p to make M^p > N, then generate p number using randm(), and combine them using 
           (randm() - 1) * M ^(p-1) + (randm() - 1) * M ^(p-2) + … + (randm() - 1) * M + randm() to get number, 
           this number have uniform distribution on (1 ~ M^p),
        2. then define a threshold = (M^p / N) * N, if the number is larger then threshold, re-generate, 
           if not, return (number % N) + 1

14. **KMP Problem** Replace all occurrence of the given pattern to ‘X’.For example, given that the pattern=”abc”, replace “abcdeffdfegabcabc” 
    with “XdeffdfegX”. Note that multiple occurrences of abc’s that are contiguous will be replaced with only one ‘X’.
    
    *HINT: Using KMP to find the first match offset, replace if previous is not X*

15. **Print all edge nodes of a complete binary tree anti-clockwise.**
    That is all the left most nodes starting at root, then the leaves left to right and finally all the rightmost nodes.
    In other words, print the boundary of the tree. Variant: Print the same for a tree that is not complete.
    
    *HINT: Top-down approach by identify boolean flag print?. On the left edge, if node have no left child, need print right; 
    On the right edge, if node have no right child, need print left. And left is pre-order, and right is post-order.
    
16. Design an algorithm and write code to serialize and deserialize a binary tree. Writing the tree to a String is called ‘serialization’ 
    and reading back from the String to reconstruct the exact same binary tree is ‘deserialization’.
    
    *HINT: do level order traverse, save the null child as "#" but no save null's child.* 
    
17. Describe an algorithm to save a Binary Search Tree (BST) to a file in terms of run-time and disk space complexity. 
    You must be able to restore to the exact original BST using the saved format.
    
    *HINT: BST in-order is sorted, so just need to persistent pre-order or post-order, than can restore BST(Using binary search to achieve O(NlgN)).*
    
18. A very interesting Math problem: Given a list of positive integers: t1, t2, …, tn, and ti ? tj for some i, j. Find the smallest integer y >= 0 
    such that each ti + y is divisible by an integer T. T must be the largest of all possible divisors. 
    [Solution at LeetCode](http://leetcode.com/2010/05/problem-b-fair-warning-solution.html)
    
    *HINT: t1 + y = k1 * T and t2 + y = k2 * T, can got T = t1 - t2 and y = t1 - 2*t2*
    
19. Given a string of lowercase characters, reorder them such that the same characters are at least distance d from each other.
    Input: { a, b, b }, distance = 2; Output: { b, a, b }
    
    *HINT: Greedy Strategy: The character that has the most duplicates has the highest priority of being chosen to put in the new list. 
    If that character cannot be chosen (due to the distance constraint), we go for the character that has the next highest priority.*
    
##NineChapter Blogs

1.  Given two int array A[] and B[], each have K elements, select 1 element from A and B and do sum, we could get K * K pair sum. 
    Find the Top K pair sum value.
    
    *HINT: imagine to build a pair sum matrix (Yang Matrix), so same problem as TopK in Yang Matrix (PriorityQueue storing offsets)*
    
2.  As question 1, if give you N arrays, and pick N element from array, and find the Top K sum.

    *HINT: Divide and Conqur using Answer of 1, like merge K sorted list.

3.  Random generation problem: Given a rand(2) generate 1 in possibility p, and 0 in possibility 1-p. 
    Now try to use rand(2) create a new function generate 0 and 1 in the sample possibility. 
    
    **Enhanced Random generation**: create a new function to generate 1 - N with sample possibility.

    *HINT: Binary version of a integer*
    
4.  **Two Pointer to O(N):** Given a article of N words, and M pair of words, find the min distance of the word pair(sequence matters) in the article 
    if they both appears. For example: article is "ABBCABC" and for word pair (A, C) the min distance is 1 ("ABC")
    
    *HINT: for multiple action build index of all appearance of words, and compare the two list.(binary search O(NlgN) and two pointer O(N))
    
5.  When you throw a ball from floor P and above in a N-layer building, it will break, and throw ball under floor P will be OK. 
    Given you K ball, write program to find out the min times of attempt you need to throw to know P. 
    
    *HINT: a DP Problem*
    
6.  Given a range [a,b], for every integer i in the range, if i is dividable by 3 output a 'Fizz', if i is dividable by 5 output a 'Buzz', 
    if i is dividable by 5 and 3, output a 'FizzBuzz'.
    
    If have multiple divider, put divider and words in a HashMap, like {3: “Fizz”, 5:”Buzz”}, any way to optimize the process.
    
    *HINT: placeholder array*
    
7.  Given an N * M matrix as positive integer, start from (1,1) to (n.m), find the max path.
    
    If you could go K times, and each time if the ceil is already used, it's value will be set to 0. find the max value could get after K round.
    [Question Detail](http://www.ninechapter.com/problem/26/)
    
    *HINT: Problem 1 is standard DP problem, and Problem 2 should build a Flow Network and run Max Flow algorithm (not used in interview usually). 

8.  **Given N element array, need query the GCD of arbitrary sub-array. Find a way to pre-process and accelerate the query.** 
    Additional space complexity within O(N)
    
    IF N array change to N * N matrix, and need query GCD of arbitrary sub-matrix.
    
    *HINT: Interval Tree, Interval Tree can used to save the result of sub-interval [i,k], [k+1,j], and GCD(i,j) = GCD(gcd[i,k], gcd[k+1,j])
     So the space is O(N), and query is O(lgN)*
    
9.  **Given a N * M matrix and K point in it. Find a point in matrix make the minimize sum distance to all K points.**
    The distance is Manhattan distance:|x0-x1| + |y0-y1|. 
    
    If the point shouldn't overlap in any of K points.
    
    *HINT: consider x and y separately. If in one dimension, point should be the median. Non-overlap: if exist in K points, put it's surrounding in heap, until poll an un-overlap point.*

10. Given a string only contains 0 and 1, find the longest substring which contains the same number of 0 and 1.

    *HINT: Prefix sum array for all the sub-array problem, change 0 into -1, so find the sub-array which sum is 0. Time: O(N) using HashMap.*
    
11. **Given a array without duplicate integer, create a MaxTree based on the array. The root of MaxTree is the max element in the array, 
    and left subtree build by the left part of the max element in the array, and right subtree is build by the right part.**
     
    *HINT: find the parent of each node, it should be the closest element in left/right which larger than current node. Using Stack to optimize the whole process to O(N).
     Always keep an decrease sequence in the Stack, pop element out if smaller than current, and set left/right during the push and pop.*

12. Given an array contains positive and negative integer, reorder the array to make it in ZigZag pattern(one positive and one negative), put needless number at the end of array.
    Require O(1) space.  For example: [1, 2, 3, -4]->[-4, 1, 2, 3]，[1,-3,2,-4,-5]->[-3,1,-4,2,-5]
    
    If need retain the original sequence in the array, how to achieve. 
    
    *HINT: order-matter-less use swap O(N), and order-matter use rotate: 1 2 3 -4 -> rotate 1 step right from 2 to -4 to get 1 -4 2 3, O(N^2).
     Remember check sequence should start with positive or negative, can use flag(-1,1) to check if number is expected (number * flag > 0).*
     
13. Implement a Queue have 4 interface: offer(), poll(), peek() and min();

    *HINT: Stack is good data structure to keep min/max, 2 stack can implement Queue.*
    
##Book: Silicon Valley Interview

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
    
    *HINT: BST with N steps without tracking visited*
    
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

##Book: Cracking Code Interview

1.  [String] Given a char array, replace ' ' with "%20" in place, assume char[] have enough space.

    *HINT: scan backward with two pointer*
    
2.  [String] Implement a method to perform basic string compression using the counts of repeated characters. For example, the string
    "aabcccccaaa" would become "a2b1c5a3". If the compressed string would not become smaller, just return the original string. 
    
    *HINT: count and use StringBuffer to generate compressed string, check if it smaller when return.*

3.  [String] Assume you have a method isSubstring which checks if one word is a substring of another. Given two strings, s1 and s2, write code
    to check if s2 is a rotation of s1 using only one call of isSubstring(). 
    
    *HINT: concatenate s1 with s1, and check if s2 is substring of it.
    
4.  [List] Implement a function to check if a linked list is a palindrome.

    *HINT: Stack and fast/slow pointer, careful about odd/even elements
    
5.  [Design/Stack] Describe how you could use a single array to implement three Stacks.
    
    *HINT: fix capacity is simple to implement for interview. Flexible capacity: When one stack exceeds its initial capacity, we grow
    the allowable capacity and shift elements as necessary.*
    
6.  [Design/Stack] SetOfStacks should be composed of several stacks and should create a new stack once the previous one exceeds capacity.
    Implements SetOfStacks with pop(), push(), popAt(int index).
    
    *HINT: List<Stack>, push(): create a new Stack when last is full, pop(): delete last Stack when it's empty*
    
7.  [Design/Stack] Implement Hanoi Game
    
    *HINT: Divide and Conquer. simplify the game when only 2 disk, move 1 to tower[1], move 2 to tower[2], then move 1 to tower[2]. 
    So the generic solution is: move n-1 dish to tower[1] using tower[2] as buffer, move n to tower[2], move back n-1 dish to tower[2] using tower[0] as buffer.
    
8.  [Stack]Sort the elements in the stack in ascending order.
    
    *HINT: use an additional stack keep pushing element in decreasing order.*
    
9.  [Tree] Given a binary tree, design an algorithm which create a linked list of all the nodes at each depth.

    *HINT: Level-order traversal, Queue or Level Array.*
    
10. [Tree] Given a binary tree, design an algorithm to find the common lowest ancestor of two given nodes.

    *HINT: based on post-order traversal.*
    
11. [Tree] You have two very large binary trees: T1 with millions of nodes, and T2 with hundreds of nodes. Create an algorithm to decide 
    if T2 is a subtree in T1. T2 is subtree of T1 if there exists of a node n in T1, if you cut off the tree at node n, the two trees
    would be identical.
    
    *HINT: Different approach for T1 T2 balanced or un-balanced. Do some math.*
     
        In small amount of data or balanced case, we could get pre-order and in-order traverse of T1 and T2, and check if the 
        traverse string of T2 is substring of T1. 
        But in this un-balanced case, we could directly do treeMath on every node of T1 if node.val = T2 root.val. 
        Assume the node is selected from 1000 value, so 1000,000 nodes in T1, have 1000 node same as T2's root.
        1000,000(T1) + 1000(same node) * 100(T2) = 1,100,000, it's very near linear time. And no additional space complexity.

12. [Tree] Given a binary tree which each node contains a value, Design an algorithm to print all paths which sum to a given value. The
    path doesn't need to be start from root or leaf.
    
    *HINT: Keep path during traverse, and enumerate all the possible path ended with current node.*
    
13. [Design/BitMap] Implements BitMap, have get(idx), set(idx) and clear(idx) 3 method.
    
    *HINT: BitMap use int[]/long[] buffer to store bits(32bit/64bit).* 
    
        The range of BitMap, the max range of a BitMap: 2^31(1-Integer.MAX_VALUE) * 2^6(long[]) = 2^37 bit
            could mark 2^37 elements = 2^7G elements = 128G elements
            storage: 2^37 bit = 2^34 byte = 2^4 G = 16G
        If use int[] 2^31 * 2^5 = 2^36 bit = 64G elements, storage: 8G

14. **[Bit] Given a positive integer, print the next smallest and previous largest number that have the same number of 1 bits in their binary 
    representation.**
    
    *HINT: use bit operation, count 0 and 1 backwards.*
    
        For next smallest number: flip rightmost non-tailing zero to one, and move rest 1 to rightmost
            scan backwards: count 0, count 1. 
                int p = c0 + c1;
                n |= 1 << p;              //| 00000100000    rightmost zero to one
                n &= ~((1 << p) - 1);     //& 11111100000    clear left of rightmost to zero
                n |= (1 << (c1 - 1)) - 1; //| 00000000011    put rest 1 to the end
            simplify way is: n + (1 << c0) + (1 << (c1 - 1)) - 1;
        For previous largest number: flip rightmost non-tailing one to zero, and put rest 1 to leftmost
           scan backwards, count 1, count 0.
                int p = c0 + c1;
                n &= ((~0) << (p + 1));        //clear from bit p onwards
                int mask = (1 << (c1 + 1)) - 1;//sequence of (c1 + 1) ones
                n |= mask << (c0 - 1);
            simplify way is: n - (1 << c1) - (1 << (c0 - 1)) + 1;
            
15. [Bit] Write a function to determine the number of bits required to convert integer A to integer B.

    *HINT: count how many 1 in (A xor B). Count could use A & (A-1) to flip rightmost 1.*
    
16. [Bit] Write a program to swap odd and even bits in an integer with as few instructions as possibility.

    *HINT: ((n & 0xAAAAAAAA) >> 1) | ((n & 0x55555555) << 1)
    
17. **[Bit] An array A contains all integer from 0 through n expect for one number which is missing. You can only access jth bit of 
    one element in A. Write code to find the missing integer. Can you do it in O(N)?**
    
    *HINT: if n % 2 == 1, count(0s) == count(1s), if n % 2 == 0, count(0s) = count(1s) + 1, we could do same check on each bit.
    
18. [DP] A child is running up a staircase with n steps, and can hop either 1 step, 2 steps or 3 steps at a time. Implement a method
    to count how many possible ways the child can run up the stairs.
    
    *HINT: ways[1] = 1, ways[2] = 2; ways[3] = 4; ways[i] = ways[i - 3] + ways[i - 2] + ways[i - 1]. 
     Be careful about initial way[3] not 3 but 4.*
      
19. [BinarySearch]A magic index in an array[0...n-1] is defined to be an index such that A[i] = i. Given a sorted array of distinct integers. 
    Write a method to find a magic index, if one exist in array. And if the values are no distinct.
    
    *HINT: binary search, find element value = index. When have duplication, need search both left and right, but can use array[mid]
     to adjust the range: left: (low, min(mid - 1, array[mid])) and right: (max(mid + 1, array[mid]), high)*
     
20. [DFS] Implement the "paint fill" function: given a picture (2-dimensional matrix) and a point, and a new color, fill in the surrounding
    area until the color changes from the original color.
    
    *HINT: standardized DFS, careful about range check.*
    
21. [DP] Given an infinite number of quarter(25 cents), dimes(10 cents), nickels(5 cents) and pennies(1 cents). Write code to calculate
    the number of ways of representing n cents.
    
    *HINT: standardized DP, case i % 5 == 0, i % 10 == 0 and i % 25 == 0, not minus >= 0.*
    
        Like decode ways, ways[i] = ways[i - 1] when valid, and for special case i % 5/10/25 == 0, add ways[i-5/10/25]
        Be careful about when is mod and when is minus.
        
22. [DP] You have a stack of n boxes, with widths wi, height hi and depth di. The boxes can't be rotated and can only be stacked on top of 
    one another if each box in the stack is strictly larger than the box above it in width, height and depth. Implement a method to 
    build the tallest stack possible, where the height of a stack is the sum of the heights of each box.
    
    *HINT: height[i]: the max height is box i as the last box in the stack, and height[i] = boxes[i].height for initialize.
     height[i] = boxes[i].height + max(height[j]) for every j < i and canPutOnTop(boxes[j], boxes[i]) 
        
23. **[DP] Given a boolean expression consisting of the symbol 0,1,&,| and ^, and a desired boolean result value result. implement a function
    to count the number of ways of parenthesizing the expression such that it evaluates to result.**
    For example: expression: 1^0|0|1, and desired result is 0. 2 ways: 1^((0|0)|1) and 1^(0|(0|1)).
    
    *HINT: Top-down DP using a memo (HashMap<String, Integer>).*
    
        The total solution of adding parenthesize in a n operator expression if a Catalan Sequence. 
        So put result == true in the memo, and result == false is total - memo.get(). 
        Analysis every operator in the expression base different operator & and | and ^. (different left and right)
             op == &: true & true
                ways(whole, true) = ways(left, true) * ways(right, true).
             op == |: true | true, true | false, false | true, so ^(false | false)
                ways(whole, true) = total(left) + total(left) - (ways(left, false) * ways(right, false))
             op == ^: true ^ false and false ^ true 
                ways(whole, true) = ways(left, true) * ways(right, false) + ways(left, false) * ways(right, true).
        When only one character(start == end) when char == 1, ways = 1 and when char == 0, ways = 0;
        
24. [Math] Write an algorithm which computes the number of trailing zeros in n factorial.

    *HINT: n factorial have trailing zero by 2 * 5, so count how many number dividable by 5 from 1 - N.*
    
25. [Math] Write a method which finds the maximum of two integers without using if-else or any other comparison operator.

    *HINT: Look the sign of a - b by multiplication. Be careful about overflow.* 
    
26. [String] The game of Master Mind

        The computer has 4 slots, and each slot will contain a ball that RED, YELLOW, GREEN, BLUE. The user try to guess the solution. 
        When you guess the correct color for the correct slot, you got a hit. 
        When you guess a color exist but not in the correct slot, you got a pseudo-hit. 
        Note that a slot that is a hit can never count as a pseudo-hit.
        Example: the solution is RGBY, you guess GGRR, you got one hit and one pseudo-hit.
        Write a method that, given a guess and a solution, returns the number of hits and pseudo-hits.
        
    *HINT: Careful about the edge case: calculate HITs before Pseudo HITs, and decrease counter when found a HIT to avoid double count.*

27. **[Array] Given an array of integer, write a method to find indices m and n such that if you sorted element m through n, the entire
    array would be sorted. Minimize n - m.(that is find the smallest such sequence.)**
    
    *HINT: m is the leftmost element that its right have an element < array[m], and n is the rightmost element that its left have 
    an element > array[n].*
    
        The problem can be solve scan 4 times:   Space: O(N)
            1. scan right-left to find min[]
            2. scan left-right to find leftmost element i that min[i] != array[i]
            3. scan left-right to find max[]
            4. scan right-left to find rightmost element j that max[j] != array[j]
            (i, j) is the range
        Or we can reduce the scan to 2 times:   Space: O(1)
            1. scan right-left to find the longest increasing sequence.  
            2. scan left-right to find the longest decreasing sequence.
            after 2 scan, array can be divide into (left in increasing) (mid) (right in decreasing)
            3. find the min and max of mid, min = leftEnd + 1, max = rightBegin - 1; scan mid if have. (if left and right no overlap have mid)
            4. shrink leftEnd to element[i] < min, shrink rightBegin to element[j] > max, find the range of mid.
            return the range of mid. 
            Since right-left is increasing and left-right is decreasing order, 1&2 1 times, 3&4 1 times = 2 times.
      
28. [String] Given any integer, print an English phrase that describes the integer (e.g. "One Thousand, Two Hundred Thirty Four").
    
    *HINT: recursion on thousand/million/billion, different method of number < 1000. Careful about teens and tens.*
    
        digits = {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
        teens = {"Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        tens = {"Ten", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
        bigs = {"", "Thousand", "Million", "Billion"};
        
29. **[DP] Given a dictionary, design an algorithm to find the optimal way of unconcatenating a sequence of words.** 
    In this case, the optimal is defined to be the parsing which minimizes the number of unrecognized sequences of characters.
    For example "jesslookedjustliketimherbrother", parsed as "JESS looked just like TIM her brother", JESS and TIM is 
    unrecognized sequences marked as CAPITAL.
    
    *HINT: TrieTree and Memo DP.*
        
        memo[start]: record the tokenized result of substring(start). 
        during the tokenize, consider three case: 
            1. substring(start, end) is a word, so it can break, and continue to (end, end + 1) and extend (start, end + 1)
                return a better solution with min unrecognized characters.
            2. substring(start, end) is not a word, but it's a suffix of other word, need extend (start, end + 1)
            3. substring(start, end) is not a word and not a suffix, mark current word to UPPERCASE.
        Need extend TrieTree to support partialMatch to check suffix.
    
30. [Math] Write function to add two numbers without using + or any arithmetic operators.

    *HINT: use bit manipulation, calculate sum and carry separately.*
    
31. [Math] Write a method to shuffle a deck of cards[1-52] perfectly. 

    *HINT: generate from right-left, for i-th element, generate random int r [0-i], swap(r, i).*
     
        for i-th element, if could have 
            1/i+1 (i-position swap) + i+1/i+2 (i+1 position swap) + i+2/i+3 ... + n-1/n (n-position) = 1/n
        It's a perfect shuffle.
        
32. [Math] Write a method to randomly generate a set of m integers from an array of size n. Each element  must have equal probability.
            
    *HINT: put array[i] in the set when i < m, for each j >= m, generate r [0,j], if r < m, swap(j,r), else discard it.
    
33. **[Math] Write a method to count the number of 2s that appear in all the numbers between 0 and n (inclusive).  More generic question is
    write function to calculate how many M(1~9) appear in 0 and n (inclusive).**
 
    *HINT: analysis one bit by one bit by calculate: full_count, times, low_number.*
        
        initialize: count = 0; times = 1; low_number = 0; full_count = 0;
        function:   while(n > 0) 
                    int mod = n % 10; get the rightmost bit
                    if mod > M:  count += times + mod * full_count;
                    if mod == M: count += low_number + 1 + M * full_count;
                    if M != 1 and mod == 1: count += mod * full_count;
                    n = n/10; low_number += mod * times; full_count = 10 * full_count + times; times *= 10; 
                    
34. **[String/DP] Given a list of words, write a program to find the longest word made of other words in the list.**

    *HINT: sort the words by its length. check if the word can be break to several parts from the lists.
     Use a memo to avoid duplicate calculation, and be careful about to handle the original word case.*
     
35. **[String/TrieTree] Given a string S and an array of smaller string T, design a method to search s for each small string in T.** 
    For example: S = "abcbdefcd", T = {"ab", "abc", "def", "cd"}.
    
    *HINT: Trie Tree and Suffix Tree.*
    
        SuffixTreeNode is a TrieNode, char value, Map<Character, SuffixTreeNode> children, List<Integer> indexes.
        2 method: 
            insert word: recursively insert word from this node.
                if word == null || word.length == 0 return;
                char first = word.charAt(0);
                SuffixTreeNode child = children.get(first); //create new if doesn't exist and put in children.
                child.indexes.add(index);
                child.insert(word.substring(1), index);
            search word: recursively search word in the subtree of this node.
                if word == null || word.length == 0 return this.indexes;
                char first = word.charAt(0);
                if(children.containsKey(first))
                    return children.get(first).search(word.substring(1));
                else return new ArrayList();
    
36. [Heap] Numbers are randomly generated and passed into a method. Write a program to find and maintain the median value as new value
    are generated.
    
    *HINT: Using Heap, have a median, and keep maxHeap in the left and minHeap in the right, and keep two heap size different <= 1.
     This problem also could be solved using BST, but need rotate BST, much complicated than Heap.*
    
37. **[Matrix] Imagine you have a square matrix, where each cell is either black or white. Design an algorithm to find the maximum subsquare such
    that all four borders are filled with black pixels.**
    
    *HINT: Loop on the square size K (matrix.length ~ 1), for start left-top point row, col, to find if could get a square size K with
     black border. O(N^4). The process could be optimized by make find square into O(1). By pre-processing to calculate the continuous
     black border from right-left and down-up.*
    
38. **[String] Given a list of millions of words, design an algorithm to create the largest possible rectangle of letters such that every row 
    forms a word (reading from left to right) and every column forms a word (reading top to bottom). The words need not be chosen consecutively
    from the list, but all rows must be the same length and all columns must be the same height.**
    
    *HINT: This problem is very complicated, include Trie, Back-tracing, etc.*
    
        Many problems involving a dictionary can be solved by doing some pre-processing.
        1. Group the words by it's length.
        2. Max rectangle should be size as longestWord * longestWord
            for z = maxRectangle to 1 {
                for each pair of numbers(i, j) where i*j = z{
                    attempt to make rectangle, return if successful.
                }
            }
        3. To makeRectangle(i, j), select j words from words length is i, to make each column is also a word length j.
           This operation could be optimize by build a Trie to easily lookup if a substring is a prefix of a word in the dictionary.
           If YES, continue to build, if NO, backtrace to previous word selection.

39. [Math] There are three ants on different vertices of a triangle. What is the probability of collision (between any two or all of them)
    if they start walking on the sides of the triangle? assume each ant randomly pick a direction.
    
        Answer: 
            clockwise: (1/2)^n, counter clockwise: (1/2)^n, so the same direction: (1/2)^n-1
            so collision: 1 - same direction = 1 - (1/2)^n-1
            when n = 3, collision probability: 3/4
            
40. [Math] Given two squares on a two-dimensional plane, find a line that would cut these two squares in half. Assume that the top and 
    bottom are parallel to the x-axis.
    
        Answer:
            the line can partition two squares in half is the line connect the two middles.
                     
41. [Array]A circus is designing a tower routine consisting of people standing atop one another’s shoulders for practical and 
    aesthetic reasons, each person must be both shorter and lighter than the person below him or her. 
    Given the heights and weights of each person in the circus, write a method to compute the largest possible number of people 
    in such a tower. 
    
        EXAMPLE:
              Input (ht, wt): (65, 100) (70, 150) (56, 90) (75, 190) (60, 105) (68, 110)
              Output: The longest tower is length 6 and includes from top to bottom: (56, 90) (65,100) (68,110) (70,150) (75,190) 
   
    *HINT: sort people by ht, and find the longest sub-sequence which person[i].ht < person[j].ht && person[i].wt < person[j].wt, 
     using DP method.* 
     
##Technical Interview Question from Question 300

1.  [Array] Given three sorted arrays (in ascending order), you are required to find a triplet (one element from each array) 
    such that distance is minimum. Please give a solution in O(n) time complexity 
    
        Distance is defined like this : If a[i], b[j] and c[k] are three elements then 
            distance = max(abs(a[i]-b[j]), abs(a[i]-c[k]) ,abs(b[j]-c[k]))
    *HINT: three pointer, and always move the smallest one to next. Tracking the min distance during visiting. O(N).*

2.  [Array/Graph] Given a un-directed graph with N vertex, and M edges, find the connected components on the graph.
 
    *HINT: Balanced Union Find, note that UnionFind only for un-directly graph or directed graph to find WCC.*
    
3.  [Digital Convert] Write a function (with helper functions if needed) called to Excel that takes an excel column value (A,B,C,D
    ,..Z,AA,AB,AC,..,ZZ,AAA..) and returns a corresponding integer value (A=1,B=2,..,AA=26..).
    
    *HINT: A-Z is 26-based digital system, but without 0, so add one after conversion if not the last bit.*
    
4.  [Interval] Given a set of ranges, find the two ranges with the greatest overlap.
    
    *HINT: sort the interval by start, and keep tracking the max end, and max overlap. Since i.start <= j.start when j > i, 
    and if j.end > i.end, and for any k, k > j > i, the max overlap should be j.end - k.start.* 
       
5.  [Math] Given a time, calculate the angle between the hour and minute hands.

    *HINT: pre-calculate degree per minute and per hour. degree_hour = (hour + minute/60F) * DEGREE_PRE_HOUR. Also remember to 
     do mod on hour and minutes, and normalize angle if it < 0 or > 180.*
     
6.  [Math] Write a method to get the greatest common divisor(GCD) of two given integer. And if you can't use division and mod.
    
    *HINT: while(i % j != 0) {int mod = i % j; i = j; j = mod; }, be careful the condition of i > j.
     Without division and mod, use Binary Bit Manipulation.*
     
         Avoid to use division and mode, instead using >> or & bit operation.
             if x, y both are even        f(x,y) = 2 * f(x/2, y/2)
             if x is even, y is not,      f(x,y) = f(x/2, y)
             if y is even, x is not,      f(x,y) = f(x, y/2)
             if x, y both are not even,   f(x,y) = f(x, y - x)

7.  [Combination] There are k exactly same boxes, and we have to put n exactly same items into them. Assume there is no limitation on box, 
    and the only requirement is that each item should be put into one box. Please write out the code to print out all the possible 
    possibilities, print error the input can not get any result. 
                   
    *HINT: standard combination problem, be careful is box could empty or not (add "index == k - 1" condition when print solution).*
       
8.  **[Math] In Lucky 7 sequence, we need remove all the number contains 7 from the integer, such as 7, 17, 71, etc, and define a method 
    to return corresponding number when giving a regular number, and decode the Lucky 7 number to the regular number.**

    *HINT: Consider this problem as convert this integer to base9 digit system, and if digit >= 7 increasing one.*
    
9.  [Math] Write a function to print out all the amicable numbers pair within 10000; amicable numbers pair is the numbers which the 
    sum of its real factor equals to the other. such as 220 and 284;
    
    *HINT: sum[i] to persist the sum of i's real factor, initialize sum[i] = 1, loop from 2 to N/2, calculate the sum.
    scan sum, if(sum[i] < N && sum[i] > i && i == sum[sum[i]]), i and sum[i] is a pair.*
    
10. [Array] N integer from 0 - N-1 form a cycle, start to delete number by visit M step. The process is started at 0.
    Given N and M, please write code to calculate which number will last at the final round.
    
    *HINT: The first round it will delete Mth number from 0. then the second round, it is N-1 number delete Mth start from M%N. 
    So f(N,M) = (f(N-1, M)+M) % N.*
    
11. **[Array/Application] There is N teams in a match. w[N][N] store the competition result between each two team, order[N] store 
    the order of team. At the first round, order[0] vs. order[1], and order[2] vs. order[3], etc. the winner comes to next round. 
    Finally comes the winner. Write code to compute the ranking of the match.**
    
    *HINT: use a rank[] to re-order the team, every time, team[i] compete team[i+1], the winter put in rank[group_start+i] and loser
    put in rank[group_start+group/2+i], every time, partition the group into 2 part, the group len loop from order.length to 1, and
    group_start loop from group_len * i. Remember to assign rank to order for next round competition.*
     
12. **[String/DFS] Having n string length is M + 1, here is a rule to conjoin 2 string together: if the prefix M character equals suffix M character.
    Write code to find the length of the conjoined string, and give a error when it could find a cycle.**
    
    *HINT: create a graph based on prefix-suffix matching, and find the longest path in the graph. Start from any nodes do DFS to
    find the longest path.*
    
13. **[Math/TwoPointer]Having N, find there exist how many continuous int sequence which sum is N.**
    Such as Given 15, 1+2+3+4+5 = 4+5+6 = 7+8 = 15, so output should be 3.
    
    *HINT: iteration over 1~(N+1)/2 using two pointer(start, end), keep tracking sum, when sum == N, found an answer. when sum >= N, 
     shrink start until sum < N.*
     
        Advanced Discussion:
            a. Some integer can't find this kind of combination, please specify the rules of this kind of integer.
            b. In 32-bit integer, which number have most combination.
      
14. [Array] Write code to determine the 5 poker card is a straight or not. King can replaced to any card.

    *HINT: convert card to integer, sort and if missing number < count of King, it's a straight.*
    
15. [Math] Given N sieves, write code to calculate the possibility of each sum of all the sieves number.
    
    *HINT: it's a multinomial distribution problem, and also could be solve using DP.*
    
        p[s][k]: the how many times to get sum = s using k sieves
        initialize: p[i][1] = 1, p[i][i] = 1; p[6*i][i] = 1;
        function: p[s][k] = p[s-6][k-1] + p[s-5][k-1] + .. + p[s-1][k-1]
        result: p[s][k]/6^k
        since p[*][k] only depends on p[*][k-1] so can deduce space into 2*6*K = O(K)
        
16. [Math/Geometry] Given N points, every line go through 2 point, write code to find the line with largest slope.
    
    *HINT: enumerate every 2 points, calculate the slope and keep tracking the largest slope. Be careful when x is same.*
    
17. [Math] Given a integer, write code to check if it's a square of some integer, can't use sqrt()

    *HINT: only check if the number can be divided by prime * prime, (prime: 2,3,5,etc) until 1.*
    
18. [Math/Geometry] Given 4 points, write code to determine whether the 4 points is a rectangle or not.

    *HINT: don't assume the rectangle is align with x-axis. Need check slope, when vertical, the slope product is -1.
     The order of 4 points should be sorted based on x, if x equals sort based on y.*
     
        public static boolean isVertical(Point[] p, int i, int j, int k){    //(i-j) vertical to (i-k)
            return ((p[i].y - p[j].y) * (p[k].y - p[i].y))/((p[i].x - p[j].x) * p[k].x - p[i].x) == -1;
        }
        
19. [Math] Given a int number, write code to judge the number of all its factor is an even number or an odd number.

    *HINT: if the number is square of other number, its factor will be odd number, otherwise be even. Code refer to 17.*
    
20. [Math] Given a int N, write code to find the closest M < N which is power of 2.

    *HINT: only get the highest binary bit.*
    
21. [Math] Write code to get N prime numbers.

    *HINT: keep a prime[], loop on integer, check if it is dividable by any prime[i], if not, it's a new prime.*
    
22. **[Math/DP] Let A be a set of the first N positive integers :A={1,2,3,4.........N}.
    Write code to find such subset pair, (x,y), x and y are the subset of A; 
    Relation 1 pair: x not a subset of y, y is not a subset of x, and x,y have no intersection.
    Relation 2 pair: x not a subset of y, y is not a subset of x, and x,y have intersection.
    Given N, write code to calculate how many Relation 1 pair and Relation 2 pair.**
    
    *HINT:DP, Define the count of relation1 and relation2 when n element based on n-1 element case, try to think how to generate 
     those subset.*
    
        r1s[i] is the count of relation1 using element 1-i, r2s[i] is the count of relation2.
        1. r1s[i] = r1s[i - 1] + 2 * r1s[i - 1] + 2^(i - 1) - 1
            r1s[i-1] is the result set of 1~i-1, no i-th element in both x and y.
            2 * r1s[i - 1]: is created by add i-th element in x or y.
            2^(i - 1) - 1: is created by x = subset(1~i-1) y = i-th element
            combine together get r1s[i]
        2. r2s[i] = r2s[i - 1] + 3 * r2s[i - 1] + 3 * r1s[i - 1];
            r2s[i - 1] is the result set of 1~i-1, no i-th element in both x and y.
            3 * r2s[i - 1] is created by add i-th element in x or y, or both x and y.
            3 * r1s[i - 1] is created by add i-th element in the result of r1 in 1~i-1, each (x, y), 
            since (x,y) is the result of r1 so x and y have no intersection.
                (x+ith, y+ith) the intersection is i-th element
                (x+ith, x+y)   the intersection is x
                (x+y, y+ith)   the intersection is y.
            combine together get r2s[i]
        create a int array to hold r1s and r2s result, and calculate them in a loop
    
23. **[Math/BinarySearch] Given a integer N, find the minimal M to make N * M contains only 0 and 1. such as: N = 2, M = 5, N * M = 10.**

    *HINT: When N = 99, M = 1122334455667789L, can't search M by increasing 1 every step. So the N * M only contains 0 and 1, 
    so binary search N * M is much easier.* 
    
        N*M should be composed only by 1 and 0, so let's make M = M*10 for each round. 
        How to handle 11 or 101 or 111, use mod[] saves different M % N, mod[i] save the 1 sequence which mod % N == i.
        Actually we don't need to apply 11, 101 and 111, 1001, 1101, etc, just add all mod[] to M.
        If (M + add)%N == 0, we found the number: (M+add)/N
        NOTE: need create a new _mod array for current round, and assign _mod to mod every round to 
              avoid mod[i] created in current round be used to add again.
              
24. **[Geometry/DivideConquer] Given N points in a 2-dimensional space, find the min distance between any two points.**

    *HINT: The brute-force solution is O(N^2), to avoid un-useful distance calculation, using p.x to split the left part and 
     right part, the min distance should be within left part or right part, or the square of 2 * min_dis(left, right) 
     center at splitter, optimize to O(NlgN).*
    
        1. Sort points by p.x
        Divide and Conquer
        2. If only two points directly calculate the distance.
        3. find the mid point on x-axis as splitter, the closest point pair should be in left part, or right part, or the points 
        which x with 2 * min-dis(left, right) with center of splitter, both x and y-axis. 
            such as x1 x2  |  x3 x4    the min dis of left is d1 and min dis of right is d2, 
            do extra binary search on the points lay in splitter +/- min(d1, d2).
        Optimization: we also could use Y to filter candidate when do extra binary search, the candidate should no larger than 
        min(d1, d2) in Y with the min Y in all the candidates.
        
25. **[Interval/BinarySearch] Given a list of source interval and a target interval, write code to check if the target interval is 
    covered by source range(could merge), or covered by any one source interval(no merge).** 
     
    *HINT: sort, merge and do binary search check O(lgN). If can't merge the interval, need tracking the max_end in the interval 
     of it's left right subtree using division. In each query, just like search in interval BST, also O(lgN).*
     
        When enable merge, than the source interval should have on overlap, so during the binary search
            if isCover(source[mid], target) return true;
            else if(target.end < source[mid].start) search in (low, mid - 1);
            else if(target.start > source[mid].end) search in (mid + 1, high);
            else return false;
        When can't merge, need pre-process the interval to tracking the maxEnd of subtrees.
            if(isCover(source[mid], target) return true;
            if(maxEnd(mid] < target.end) return false;
            if(hasLeft && maxEnd[left] >= target.end) search in (low, mid - 1)
            else if(target.start >= source[mid].start] && hasRight) search in [mid + 1, high];
            else return false;

26. **[Math/Geometry] Giving a triangle ABC (ABC in wise-clock order), and a point D. Write code to check if D is inside of ABC.**
    
    *HINT: the simplest way to check is based on area, D can divide ABC into ABD, BCD, and CAD three triangle, if D is inside ABC, 
    so check if area(ABC) = area(ABD) + area(ACD) + area(BCD). The area of triangle can be calculate by edges using Heron formula. 
    area(ABC) = Math.sqrt(p * p-a * p-b * p-c), which p = (a+b+c)/2  Heron's formula
    A better way is based on relative position of line and point.*
    
        if D is inside of ABC, it always in the left side of AB, BC, and CA.
        The relative position could be identified by vector product, 
            "if vector product(AB, AD) > 0, D is in the left side of AB."
        so only need to check the vector product of (AB, AD), (BC, BD), (CA, CD) is all >= 0.
        
        Vector product is calculate by following: product(ab, ac) 
            public static double product(Point a, Point b, Point c){
                return (b.x - a.x) * (c.y - a.y) - (c.x - a.x) * (b.y - a.y);
            }

27. [Tree] Given two binary tree T1 and T2, write code to check if T2 is a subtree of T1.
  
    *HINT: if T2 is a subtree of T1, in-order(T2)/pre-order(T2) is a substring of in-order(T1)/pre-order(T1). Be careful need
     to print null-left and null-right if not both left/right is null, to make topology matters.*

28. [Tree] Given a sequence of int, write code to check if this the post-order traverse of a binary search tree.

    *HINT: for BST, the root should partition tree to left and right part, left <= root, and right >= root. Try to partition the 
    array, if it can well partitioned, it is a BST post-order. Or can try to rebuild the BST, since in-order of BST is sorted, so
    have both post-order and in-order, we can rebuild the tree, if tree can be built, it's a post-order of BST.*

29. [Tree] Given a BST, define f = (max + min)/2, write code to find the element > f but closest to f.

    *HINT: get min and max, calculate f. do binary search in tree, if f >= node.val, go to right subtree, if f < node.val, try to
     find in left subtree, if can't find, return node.* 
     
30. [Graph] Print all the cycles in a directed graph.

    *HINT: for directed graph, need keep a global visited, and path<GraphNode> and indexes<GraphNode, Integer> for every round, 
    when found some node already in indexes, print the cycle based on index range.*
    
31. [Graph/Greedy] Print the minimum spanning tree of given weighted graph. Find a MST(min-weight spanning tree). A spanning tree 
    of G is a subgraph T that is connected and acyclic.
           
    *HINT: 2 Algorithm(Greedy) to find MST: Prim and Kruskal. Prim is selecting the min_weight_edge from current tree to an unvisited
    node until every node is visited; Kruska is selecting min_weight_edge not make cyclic (the two end node from same community).
    (Prim is based on traverse, so can be used to un-directed and directed graph, if Kruskal using on directed graph, UnionFind is 
    not correct.)*
    
32. [Graph] Calculate Shortest Path between two given vertexes for weighted graph.

    *HINT: for un-weighted graph, just do BFS. For weighted graph, if the graph edge all positive number, could use Dijkstra
    (IndexedHeap and DP), if the edge will be negative, but on negative cycle, use Bellman-Ford.*

33. [Graph] DAG Graph topological sorting/ordering, usually used in task scheduling.

    *HINT: count the in-degree of vertexes, put 0-in-degree nodes at the front, and start to do BFS on the graph, when found an
    unvisited node, decrease it's in-degree by one, if in-degree == 0, add to sorted list.*
    
34. [Graph] Given a graph, find the minimal color printed on each node, the color of each node can't be the same with its neighbors.
35. [Graph] Tree update and query: see the instruction in the code
36. [Graph] Is a graph can be bipartite?

    *HINT: hold a even layer and odd layer hashset, do BFS, check if current node exist in opposite layer.*

37. [Greedy] Task Selection: There is N task, each one need use a un-shared resource. Given the start time and end time of each task, 
    it will use the resource in duration of [start-time, end-time), write code to select the maximal set of un-conflict tasks.
    
    *HINT: The earlier ended task always in the maximal set of un-conflict tasks. Sort the input data by end-time, and greedily select the 
    earlier ended task.*
    
38. [Greedy] Huffman Encoding: Given N char with specific usage frequency, using Huffman encoding policy to make the encoding 
    string with minimal length.
    
    *HINT: Use binary tree to do the 0-1 coding, create TreeNode for every char, and put in a heap ordered by frequency, every time
    select the two node with least frequency, and create a new TreeNode as their parent which frequency is their frequency sum, and 
    put back to heap, until there is only 1 node in heap. This node should be the root of the binary tree. Then do traversal on the 
    tree to do encoding for all the leaf node, left child is '0' and right child is '1'.*
    
39. [String] Given 1-N number, find all the permutation of the numbers alias with the given rule. 
    Such as: given 1,2,3,4,5, rule is 4 can't be place at the 3rd place, and 3 and 5 can't be together.
    
    *HINT:define rules, and do permutation on the number, if it follow the rule put in the result list, if not throw it.* 
    
40. [String] Given several sets of strings, write code to combine the sets which intersections.
    Such as {aa,bb,cc},{bb,dd},{hh},{uu,jj},{dd,kk}, the result should be {aa,bb,cc,dd,kk},{hh},{uu,jj}
    
    *HINT: model this problem using graph, each set is a vertex, and if two sets have intersection there is a edge between them.
    so the problem is find the connected component in the graph, and return the components. Using UF to solve it.*
    
41. [String] Given a string, write code to find the longest substring which repeated more than once.

    *HINT: use suffix, for a string, get all its suffix in an array, and sort it, and find the common length (scan from 0) in 
    each neighbor suffix, and tracking the longest one. The algorithm is O(N^2lgN).*
    
42. **[String] K－important strings.**

        You are given a set of N strings S0, S1, …, SN-1. These strings consist of only lower case characters a..z and have 
        the same length L. A string H is said to be K-important if there are at least K strings in the given set of N strings 
        appearing at K different positions in H. These K strings need not to be distinct.
        Your task is to find the shortest K-important string. If there are more than one possible solution, your program can 
        output any of them.
    *HINT: this is a DP problem.*
    
43. [Array] Given a sorted array, there is only one value K has multiple occurrence, find the repeating element and its 
    first occurrence.
    
    *HINT: Binary Search extension.*

44. [Array] Implement a cyclic int buffer with an int array.

    *HINT: like implements a Queue with cyclic array.*

45. [Array] Find the uniq amount of absolute values in a given sorted array.
    
    *HINT: two pointer begin and end scan the array.* 

46. [Array] There is an array A[N] of N numbers. You have to compose an array Output[N] such that Output[i] will be
    equal to multiplication of all the elements of A[N] except A[i].
    For example Output[0] will be multiplication of A[1] to A[N-1] and Output[1] will be multiplication of A[0] and 
    from A[2] to A[N-1]. Solve it without division operator and in O(n).
    
    *HINT: use left[i] and right[i] two array to store and multiplication of A[0] to A[i-1] and A[i+1] to A[N], and
    left[i] = left[i-1] * A[i-1] scan forward, right[i] = right[i+1] * A[i+1] scan backward, both can be calculate
    in O(N), so Output[i] = left[i] * right[i], also O(N).*
    
47. **Unknown** Given two sequences of items, find the items whose absolute number increases or decreases the most 
    when comparing one sequence with the other by reading the sequence only once.
    
48. [Array] How to find the median among the given numbers whose values are unknown but falls into a narrow range.
    
    *HINT: since the numbers are in a narrow range, could use BitMap to do counting, the number whose index is 
    (totalSize/2) will be the median.*

49. [Array] Closest Pair. Given an int array a[], find the closest two numbers A and B so that the absolute value
    |A-B| is the smallest. The time complexity should be O(NlogN).
    
    *HINT: sort the array, then scan i and i+1 by tracking the min gap.*
    
50. [Array] Farthest Pair. Given an int array a[], find the farthest two numbers A and B so that the absolute value
    |A-B| is the biggest. The time complexity should be O(N).
    
    *HINT: the pair should be the min and max in the array.*
    
51. [Array] Given a list of number 0,1; find the start of runs (the length contiguous sequence of 1 is larger than
    a given number)
    
    *HINT: scan the numbers by tracking the count of contiguous 1, if count > given number, return i - count as 
    the start, if get 0, set count = 0 and continue.*
    
52. [Array] Define a function that takes an array of integer numbers and returns an array of numbers of the same length.
    Each element of the output array out[i] should be equal to the product of all of the elements of the input
    array except for in[i]. Example: input {1,2,3,4} output {24,12,8,6}

    *HINT: do special check on 0, if zeroCount >= 2, and zeroCount == 1, and zeroCount == 0.*

53. [Stack] Given two int array, one is the push sequence, write function to check if the second one is a pop sequence.

    *HINT: use a Stack to simulate.*

54. [Array] Given two int array, find a switch of the items in the two array to make the SUM of two array closest.

    *HINT: if switch A[i] with B[j], the different is Math.abs(sumA - A[i] + B[j] - (sumB + A[i] - B[j])), so it's 
     Math.abs(sumA - sumB - 2A[i] + 2B[j]). Scan B for each B[j] find the A[i] closest to (sumA - sumB + 2 * B[i])/2
     using binary search, and keep tracking A[i] and B[j] achieve the minimal gap. The time complexity is O(NlgN).*
     
55. [Array] Given two int array, find switches of the items in the two array to make the SUM of two array closest. 
    
    *HINT: this problem equals to find a division of a array to get two balanced sub array.*
    
56. [DP] Given an int array, find a division of the array into two subsets whose sum is closest to each other.

    *HINT: DP problem.*
        
        state: sums[i][j] is the sum of subset of (0~ith element) closest to j.
        initialize: sum[0][*] = 0;
        function: sum[i][j] = 
                     Math.max(sum[i-1][j], sum[i-1][j-array[i]] + array[i]) if j >= array[i]
                     sun[i-1][j] if j > array[i]
        result: sum[n-1][target]
        backtracing: if(( i > 0 && sums[i][k] > sums[i-1][k]) || (i == 0 && k == array[i]))  mark[i] = true and k -= array[i]

57. [Array] Given a array of N number which arrange is 1-M, write code to find the shortest sub array contains all 1-M 
    numbers. Also consider if the array is cycle (connected head and tail).
    
    *HINT: use int[] to mark occurrence location, and shrink begin when found all the array. For cyclic, check begin won't 
    go over the end of the array. Remember to use end with cyclic data to compare length, and decoded end to get number 
    and mark occurrences.*
    
58. **Unknown** Define a function on an array, when you increase 1 on one cell, the neighbor (up, down, left and right) all increase one.
    Given an array, write code to determine whether this array can be generated using the above function.
    
59. Given an array, write code to divide the array into M sub array (find largest M), make sure the sum of all sub array 
    are the same. 
    
    *HINT: assume array has N element, M should 1 <= M <= N, and sum(array) mod M == 0. So loop M based clue 2, if find 
    the M division, if could find a solution return m.* 

60. Given an int array, combine all the integer to a int, such as {23, 125} -> 12523. Write code to get the smallest 
    combined number.
    
    *HINT: sort the array with a customized comparator, which compare two integer by "i1+i2" and "i2+i1"*
    
61. Given an int array, write code to find the numbers, which left number all not larger then it, and right number all 
    not smaller than it.
    
    *HINT: do 1st scan from left to right and save max value in each position in max[], 2nd scan from right to left
    check if it's min in that position and check if it also is max[i].*
    
62. Given an array with integer, write code to put all odd number before even number.
    
    *HINT: if doesn't need maintain original order, do swap using two pointer like QuickSort. If need maintain the 
    order, use additional array to achieve O(N).*
    
63. **Longest Arithmetic Progression** Given an array of integer, write code to find the arithmetic progression(等差数列)
    （length > 3), return the longest arithmetic progression from min to max.
    
    *HINT: sort the array and scan using DP approach.*
    
        count[i][step]: the count of element in the progression end at i-th element with step
        function: for each i and j, j < i, step = array[i] - array[j].
                  count[i][step] = count[j][step] == 0? 2 : count[j][step] + 1;
            tracking the maxLen and lastElement and step of maxLen progression.
        result: re-build the progression based on lastElement, step and count.
        
64. A round road have N station, A0..AN-1, given an array D contains the distance between each neighborhood station, 
    D1 = distance(A0-A1), D2 = distance(A1, A2), D0 = distance(AN-1, A0)
    Write code to most effective to find the shortest distance between two station Ai and Aj. Space O(N) most.
    
    *HINT: use sum[i] to store sum from 0 D0 ~ Di. so distance(i, j) = min(sum[j]-sum[i], sum[N]-sum[j]+sum[i]), 
    time complexity is O(1) and space complexity is O(N).*
    
65. **Increasing Subarray Count** Given an array, write code to find how many increasing sub array could generated 
    from it. Such as: Given {1,2,3}, it could generate {1,2}, {1,3}, {2,3}, {1,2,3}, 4 increasing sub array.
    
    *HINT: calculate using DP solution.*
    
        counts[i] is the subset count get from 0~ith elements
        initial: counts[0] = 0;
        function: counts[i] = sum(counts[j] + 1) for every j < i and array[j] < array[i]
             if j < i and array[j] < array[i], for every subset S of array[j], we could create a subset {S, array[i]}
             as a new subset, and also {array[j], array[i]} as one, so counts[i] += counts[j] + 1;
        result: sum(count[i]).
        
66. **Merge Two Sorted Array in Place with O(N)** Given an array is sorted in two part, such as {4,7,10, 1,5,8}. 
    write code to merge the partial sorted array in place. Time: O(N) Space: O(1)
    
    *HINT: if do insert need move elements, so try to use rotation.* 
    
        assume the array be split into A and B, a as the A start, b as the B start
            1. find the first element A[i] in A larger than B[a], so A[b]..A[i-1] should put before B[a] so no need to move
            2. find the first element B[j] in B larger than A[i], so B[a]..B[j-1] should put before A[i], need move
            3. then put B[a]..B[j-1] before A[i] using rotation:
                3.1 rotate A[i]...B[j-1], so got A[a]...A[i-1]B[j-1],B[j-2]....B[b]A[N]...A[i]B[j]...B[M]
                3.2 rotate B[b]...B[j-1], so got A[a]...A[i-1]B[b],...B[j-2],B[j-1]A[N]...A[i]B[j]...B[M]
                3.3 rotate A[i]...A[N],   so got A[a]...A[i-1]B[b],...B[j-2],B[j-1]A[i]...A[N]B[j]...B[M]
                and A[a] ~ B[j-1] is sorted
            4. update A's start a = i and B's start b = j, and do the process again.
            
67. **Shuffle Without Random Unknow** Write code to random shuffle an array without using random variables.
    
68. [Array] Given an array S[N], find the max d in array could find a combination with the other element in the array, 
    such as a1,a2..am also in S[N] follow d = a1 +..+ am. S = {2,3,7,10}, find the max element is 10 = 3 + 7.
    
    *HINT: break complicated problems into sub-problems: sort the array, then scan from right to left, and find
    the max element could be combine by 0~ith elements by backtracing.*
    
69. Given an array with N integer, write code to find the maximal product of any N-1 elements in the array.

    *HINT: should consider zero count and total product without zeros.* 
    
        Scan array, and count zeros and total product without zeros.
        if zeroCount > 1, product = 0, 
        if zeroCount == 1, 
            if total is negative, product = 0,
            if total is positive, product = total.
        if zeroCount == 0;
            if total is negative, remove the largest negative, product = total/largest_negative.
            if total is positive, remove the smallest positive, product = total/smallest_positive.
            
70. Given an array whose values first decrease and then increase, write an algorithm to determine whether a given value 
    target exists in the array.
    
    *HINT: as BinarySearch.*
    
        find the mid, if target < array[mid] return mid
        if(target < array[mid]){
            if mid is the min, return -1;
            if mid in the increasing part, search in the left part
            if mid in the decreasing part, search in the right part
        } else {
            if target < array[low], search in the left part,
            if no result from left part and target < array[high], search in the right part.
        }
71. Given a stream of integer, design and implement a data structure tracking the rank of integers.
    It have 2 methods: track(int n) is called when generate a new integer n, and rank(int n) return how many integers 
    in the stream is smaller than n.  
           
    *HINT: use BinarySearchTree, and keep size(), in track(int n) add a number in tree, and rank(int n) is calculate
    the rank of n, both element is O(lgN) if tree is balanced.
    
72. **Inplace Reorder** Given a String having first n integers and next n chars. A = i1 i2 i3 … iN c1 c2 c3 … cN.
    Write an in-place algorithm to rearrange the elements of the array ass A = i1 c1 i2 c2 … in cn
    
    *HINT: do the swap from center to endpoint, and pair by pair step in 2.*
    
        int n = chars.length / 2;
        for(int i = n - 1; i > 0; i--) {
            for(int j = i; j < 2 * n - i; j += 2) {
                swap(chars, j, j + 1);
            }
        }
        so for 1,2,3,4,a,b,c,d, n = 4, i in [3,1].
        i = 3, j in [3, 5), so swap 4 and a, get 1,2,3,a,4,b,c,d 
        i = 2, j in [2, 4, 6), so swap 3 and a, 4 and b, get 1,2,a,3,b,4,c,d
        i = 1, j in [1, 3,5,7), so swap 2 and a, 3 and b, 4 and c, get 1,a,2,b,3,c,4,d
    

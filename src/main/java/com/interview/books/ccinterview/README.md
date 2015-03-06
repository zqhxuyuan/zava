#Interview Question in Cracking Code Interview

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
    
    *HINT: BFS, BFS is a better solution for DFS.*
    
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
                ways(whole, true) = total(left) + total(right) - (ways(left, false) * ways(right, false))
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
        2. Max puzzle should be size as longestWord * longestWord
            for rows = maxRectangle to 1 {
                for cols = maxRectangle to rows {
                    attempt to make puzzle, return if successful.
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
            
40. [Math/Geometry] Given two squares on a two-dimensional plane, find a line that would cut these two squares in half. Assume that the top and 
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
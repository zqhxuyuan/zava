##Non Leetcode Interview Question from http://www.fgdsb.com/

1.  [L] Factor Combinations: Print all unique combination of factors (except 1) of a given number. 

    *HINT: do combination of all the elements in [2 ~ N/2] which make M divisible.* 
    
2.  [G] Implement a PeekIterator, with three method: hasNext(), next(), peek(). peek() only get the element not 
    moving cursor forward. 
    
    *HINT: when call peek(), call next() and use a refer to peekedElement. when call next(), if peekedElement is null, 
    return next(), if not null, return peekedElement and set it to null.*

3.  [F] PrettyBSTPrinter, Print a BST such that it looks like a tree (with new lines and indentation, the way 
    we see it in algorithms books). 
    
    
                 4                or      3         
           2           6               1     4   
        1     3     5     7               2     5      
                             8

    *HINT: get max height of the tree, and calculate spacing based on complete tree, for node even null print.*

4.  Meeting Rooms: Given a array of pairs where each pair contains the start and end time of a meeting (as in int),
    Determine if a single person can attend all the meetings
    For example: Input array { pair(1,4), pair(4, 5), pair(3,4), pair(2,3) }, Output: false;
    Follow up: determine the minimum number of meeting rooms needed to hold all the meetings.
    Input array { pair(1, 4), pair(2,3), pair(3,4), pair(4,5) }; Output: 2
    
    *HINT: create a int[] array put start and end(use negative number to mark) to the array, sort it using abs value, 
    check if the array is interleaving for question1. 
    For question2, check how many consecutive start together, tracking the max count.*
    
7.  [G] ZigZag Iterator: Implements a ZigZagIterator, init with two iterators, and visit them alternantly. Omit the empty one. 
    For example: A: 1234, B: abcd, the output is：1a2b3c4d，
    
    *HINT: keep a index of which iterator is visiting, and do cyclic update of the index: index = (index+1)%N;
    when getNext(), update index until found a iterator hasNext() return true within one round, hasNext() just return 
    currentIterator.hasNext().* 
    
8.  [G] Intersection of Two Quadtrees.
    Given a quadtree structure:
        
        class QuadNode {
            int ones = 0;
            QuadNode[] children = new QuadNode[4];
        };
        
    Please build a quadtree to represent a 0-1 matrix, assume the matrix is a square and the dimension is power of 2.
    Given two such quadtrees with same depth, please write a function to calculate how many 1s are overlapped.
    
    *HINT: keep a length, if length == 1, return a QuadNode(value), if length > 1, split the matrix into 4.
    The intersection can be calculate using divide and concur, if either nodes.ones == 0, return 0, if is leaf, return 1, if
    not leaf, recursively call its children.*
    
9.  [F] **Valid Tree**. There are n nodes numbered from 0 to n-1 and a set of edges (undirected). Please determine if it is 
    a valid tree. For example: n = 5, edge set = {{0,1}, {0,2}, {2,3}, {2,4}}, Result: true
    n = 5, edge set = {{0,1}, {1,2}, {0,2}, {2,3}, {2,4}}, Result: false
    
    *HINT: sort the edge set by start, do BFS and check if the edges is connected in layer. another solution is using
    UnionFind, each node can only have one parent defined by one edge. Remember to check all the nodes is visited or 
    all nodes have one parent after scan the edges.*
    
10. [F] Group Contacts. Having a class Contact, define as following
        
        class Contact{
            String name;
            List<String> emails;
        }
    Given a list of Contact, please write code to group the contacts by person. You can determine the two contact if same person
    if they have one or more same email.
    
    *HINT: Find communities in a un-directed group, so use UnionFind.*
    
11. [Airbnb] **Get Similar Words**: Given a word list and a target word, write code to find words in list which edit distance to 
    target word is not larger than K.
      
    *HINT: Trie can used to find words which edit distance is not larger than a given K, using getFuzzyWords in DZ22_Tries, it can 
    find all words in Trie with K missingLetters.* 
    
12. [G] **WiggleSort** sort the array following a1 <= a2 >= a3 <= a4 >=...

    *HINT: simple solution could use swap(i, i+1) when is not follow the rule. Better solution is use a current to tracking the 
    current non-placed value.*
    
13. [G] Timer Callback Register. Given a system function to register a callback method at given time:
        
        register_system_timer_callback(long time, Callback callback)
    When given time is now or past, system will immediate call the callback. And only one callback method can register, the new 
    registration will override the previous one if not called yet.
     
    Use the interface, to implement a Register take multiple system timer callback registration.
    
    *HINT: use a TreeMap to hold all the registered callback use time as key, tracking current registered callback using currentTimer, 
    create a Wrapper of Callback to register next callback when this one is executed.*
    
14. [G] Maximum Length of the Loop: Given two arrays: Indexes 0 1 2 3 4 and Values 3 2 1 4 0. 
    Values are the next index of the jump 0 -> 3 -> 4 -> 0... 1 -> 2 -> 1...
    Write a function to detect if there are loops. If yes, get the max length of the loop possible, otherwise just return zero.
    
    *HINT: the problem is find the longest cycle in directed graph, do DFS and marking visited and tracking the length.*
    
15. [G] Sums of All Subtrees: Given a TreeNode array, each one have a id, a parent_id, and a value.
    Find out the sum value of all subtrees.
    
    *HINT: do DFS(bottom-up) on the (id,parent_id) to calculate the level of each nodes, then calculate subtree sum from bottom-up.*
    
16. Nested Iterator: Program an iterator for a List which may include nodes or List. 
    For example: {0, {1, 2}, 3, {4, {5, 6}}}, Iterator returns 0 1 2 3 4 5 6
    
    *HINT: use Stack like do pre-order-traversal for trees.*
    
17. [G] **Merge Two BST**: You are given two balanced binary search trees. Write a function that merges the two given balanced 
    BSTs into a balanced binary search tree. Your merge function should take O(M+N) time and O(1) space.
    
    *HINT: first flatten BST into linkedlist, then merge the two sorted linkedlist, then build the new BST.*
    
        Flatten: use DummyNode and do in-order traversal and keep tracking last visited.
        Build BST: divide by length, if length == 0 return null, else length == 1, reset right and return the node, 
            else build left using length/2, and build node, build right using length - length/2 - 1;
            
18. [G] Smallest Range: You have k lists of sorted integers. Find the smallest range that includes at least one number from each 
    of the k lists. 
    For example, List 1: [4, 10, 15, 24, 26]; List 2: [0, 9, 12, 20]; List 3: [5, 18, 22, 30]
    The smallest range here would be [20, 24] as it contains 24 from list 1, 20 from list 2, and 22 from list 3.
    
    *HINT: like K-merge, use a PriorityQueue of K list, pop() the min and update the range.*
    
19. [G] Array Combination: Given A,B,C 3 list of Strings, find out all combination built by select one from each, if empty 
    just go over it. Don't need consider duplications.
    
    *HINT: be careful about empty list case, it may break inner loops. so delete empty list first.*
    
20. [G] All Factors of the Product of a List of Distinct Primes: 
    Print all factors of the product of a given list of distinct primes.
    Example: Input: 2 3 7, Output: 1 2 3 6 7 14 21 42
    
    *HINT: same as subset combination, don't forget 1.*
    
21. [G] Count the Number of 1s in 32-bit Integer. 

    *HINT: loop and non_loop version.*
        
        loop version:
            int count = 0;
            while(x != 0) {
                count += x & 1;
                x >>= 1;
            }
            return count;
        un_loop version: do add by 2 bits unit, 4 bits unit, 8 bit unit and 16 bit unit.
            x = ((x & 0xAAAA) >> 1) + (x & 0x5555);
            x = ((x & 0xCCCC) >> 2) + (x & 0x3333);
            x = ((x & 0xF0F0) >> 4) + (x & 0x0F0F);
            x = ((x & 0xFF00) >> 8) + (x & 0x00FF);
            return x;
            
22. [G] Valid Rolling String: Given a String s, check if s contains all presentation of K digits base A numbers, and no invalid number. 
    For example: s = "00110", A=2, K=2 => true (s contains 00，01，10，11)
    
    *HINT: use rolling hash, base = base * A + current, if idx >= K, need do base -= charAt(idx - K) * Math.pow(A, K-1);
    use a boolean[] to mark and count.*
    
23. [G] Given a String only contains A, B and C, and the three characters can be put in 3 consecutive char.
    Find out how many valid string of length N.
    
    *HINT: a DP problem, state can define as two cases.*
    
        same[i] = the count of strings char(i) == char(i-1), diff[i] = the count of strings char(i) != char(i-1).
        initial: same[0] = 3, diff[0] = 0;
        function: same[i] = same[i-1] + diff[i-1], diff[i] = same[i-1] * 2 + diff[i-1].
        result: same[N-1] + diff[N-1];
        since same and diff only related to previous state, can only O(1) space.
        
24. [L] Isomorphic Strings: Given two (dictionary) words as Strings, determine if they are isomorphic. Two words are called isomorphic if 
    the letters in one word can be remapped to get the second word. Remapping a letter means replacing all occurrences of it with another 
    letter while the ordering of the letters remains unchanged. No two letters may map to the same letter, but a letter may map to itself.
    For example: given "foo", "app"; returns true, we can map 'f' -> 'a' and 'o' -> 'p'; given "bar", "foo"; returns false, we can’t map 
    both 'a' and 'r' to 'o'.
    
    *HINT: check length, scan the two words to build the mapping rules, and check if have conflict.*
    
25. [G] **Drawing the Skyline** a building is identified by (left, height, right), given a list of buildings, they may have overlap. Write
    code to draw the skyline of given list of building. A skyline is the max border of all the buildings.
    Detail diagram refer to: http://www.fgdsb.com/2015/01/13/drawing-the-skyline/
    
    *HINT: It's like merge interview in 2D.*
    
        Using sweep line:
            1. sort building by their left coordinate.
            2. scan the building using a sweep line, keep current height, use a PriorityQueue to hold the right of building.
                pop building in heap which end before building[i].left, and also update height.
                if(building[i].height > height) update height and add points in skyline.
                add building.right to the heap.
        Time complexity is: O(NlgN).

26. [G] Minimum Cover Matrix: 给你一个字符矩阵，求出它的最小覆盖子矩阵，即使得这个子矩阵的无限复制扩张之后的矩阵，能包含原来的矩阵。 即二维的最小覆盖
    子串。比如如下矩阵：{ "ABABA","ABABA" }, 其最小覆盖子矩阵为AB，长度为2，故返回2。
    
    *HINT: 一维字符串采用KMP求解prefix, 二维的通过按行按列求最小公倍数组合.*
    
        首先先考虑如何计算一维字符串的最小覆盖子串长度：对于某个字符串s，它的最小覆盖子串指的是长度最小的子串p，p满足通过自身的多次重复得到q，
        且s为q的子串。
        这个可以通过KMP算法的prefix数组(next数组)得出。最小覆盖子串长度 = n - next[n-1].
        对于矩阵，先求出每一行最小覆盖子串的长度，取所有行算出来结果的的最小公倍数，得出最小覆盖矩阵的宽度。
        再求出每一列的最小覆盖子串的长度，再求最小公倍数，就可以获得最小覆盖矩阵的高度了。两个相乘就是面积。
        
27. 判断一个32位integer是否为4的幂次数。
    
    *HINT: 4次幂数字就是偶数位为0的2次幂数字.
        
            if(num <= 0) return false;
            return (num & 0xAAAAAAAA) == 0 && (num & (num - 1)) == 0;

28. Given a rope of length n meters, cut the rope in different parts of integer lengths in a way that maximizes product of lengths of 
    all parts. You must make at least one cut. Assume that the length of rope is more than 2 meters.
    Examples: Input: 2, return 1 because 1x1 = 1; Input: 5, return 6 because 2x3 = 6
    
    *HINT: standard DP problem.*
    
        max[i] is the max product by divide n
        initial: max[0] = 0, max[1] = 1;
        function: max[i] = max(j * (i-j), j * max[i-j]); j = 1, 2, ..., i-1;
        result: max[N]
        这题还有一个O(n)的解法。当n>4的时候，每次cut实际上都是按照每隔3来一次。比如n=5的时候，解就是3x2，n=7的时候，解就是3x4，n=10的时候，解就是3x3x4。
        
29. Valid UTF-8: Write a function to validate whether the input is valid UTF-8. Input will be string or byte array, output should 
    be true or false.
    
    *HINT: bit operation.*
    
        UTF-8是一种变长的编码方式。它可以使用1~4个字节表示一个符号，根据不同的符号而变化字节长度。UTF-8的编码规则很简单，只有二条：
        1）对于单字节的符号，字节的第一位设为0，后面7位为这个符号的unicode码。因此对于英语字母，UTF-8编码和ASCII码是相同的。
        2）对于n字节的符号（n>1），第一个字节的前n位都设为1，第n+1位设为0，后面字节的前两位一律设为10。剩下的没有提及的二进制位，全部为这个符号的unicode码。
        比如：
        0xxxxxxx是一个合法的单字节UTF8编码。
        110xxxxx 10xxxxxx是一个合法的2字节UTF8编码。
        1110xxxx 10xxxxxx 10xxxxxx是一个合法的3字节UTF8编码。
        11110xxx 10xxxxxx 10xxxxxx 10xxxxxx是一个合法的4字节UTF8编码。
        
30. Given a rod of length n inches and an array of prices that contains prices of all pieces of size smaller than n. Determine the 
    maximum value obtainable by cutting up the rod and selling the pieces. For example, if length of the rod is 8 and the values of 
    different pieces are given as following, then the maximum obtainable value is 22 (by cutting in two pieces of lengths 2 and 6)
    Example: Pricing list: {1, 5, 8, 9, 10, 17, 17, 20}; Result = 22 (cut into two pieces of length 2 and 6)
    
    *HINT: same as Boolean Knapsack, solve by DP.*
    
        max[i] is the max prices could get cutting rod length i.
        initial: max[0] = 0
        function: max[i] = max((prices[j] + max[i-j])) for all j smaller than i.
        result: max[N]
        if each price can only use once, need boolean[][] to mark the usage, copy boolean[j] to boolean[i] when find the max.
        
31. **Find Maximum H**: Given a array, find the max h: at least h number not smaller than h.
    Example: {3,2,5}, answer is 2; {4,2,3,5}, answer is 3; {8,6,7,5} answer is 4.
    
    *HINT: do binary search, find the max elements in follow the rule of array[i] >= array.length - i.(use searchHigh()), in each loop
     use array[i] to partition the array find the correct index of array[i]. In searchHigh(), if array[high] >= array.length - high, 
     return high, if not array[high] is the first element break the rule, so the max h is array.length - high.*
     
32. Find the longest increasing(increasing means one step) sequence in an integer matrix in 4 directions (up down left right), 
    return the sequence. For Example:  [1 2 3 4, 8 7 6 5]
    The output should be [1, 2, 3, 4, 5, 6, 7, 8]
    
    *HINT: memo based DP, it's not so easy to come up a loop version, since init value should be the cell which larger than all its 
    surroundings, but it's straight-forward to do it in recursive DP. maxLen[i][i] = max(maxLen[surrounding] + 1) if surrounding is larger
    then matrix[i][j], use memo to avoid re-calculation.*
    
33. [G]Search Longest String in Dictionary: 给一个dictionary, 一个string,找出dict 里能全部用string里的letter 表示的所有最长的词。
    For example: 字典包含如下单词: abcde, abc, abbbc, abbbccca, abbbcccabbcx; 给string = "abc"，最长单词应为"abbbccca"
    
    *HINT: build a trie of all words in dict, and do dfs on the Trie if char in current layer is contains in given string. Another
    solution is sort the words by length, check one by one.*
    
34. [G]给一个dictionary, 再给一个set of coding string （g5, goo3, goog2, go2le………). return all string from dictionary that can be matched 
    with the coding string. 要求尽量减少dictionary look up次数。
    g5 means g*****, 5 arbitrarily letter after g.
    
    *HINT: use Trie, and if occur number do fuzzy matching any char.*
    
35. [G] 一个球从起点开始沿着通道，看能不能滚到终点。不过有限制，每次球中间不能停留，除非到边界或者障碍物。 可以上下左右走，然后让写个function 给定起点，
    终点，和图，判断是不是solvable.
    
        For example (1代表有障碍, 0代表可以通过):
        0(start) 0 0 1
        1        0 1 0
        1        0 0 0(end)  it can't pass
        
        0(start) 0 0 1
        1        0 0 1
        1        0 0 0(end)  it can pass
        
    *HINT: do BFS. instead of use i-1, i+1, j-1 and j+1, it need continue to go if the cell in same direction is 0, and mark all passed
     cell as visited, return true if it reach end.*
     
36. [G] **Maximum Difference of Two Subarrays** Given an array of integers. Find two disjoint contiguous subarrays such that the absolute 
    difference between the sum of two subarray is maximum. Note: The subarrays should not overlap.
    For example: Array: { 2, -1, -2, 1, -4, 2, 8 }, Result subarrays: {-1, -2, 1, -4 }, { 2, 8 }, Maximum difference = 16.
    
    *HINT: Like Best-Time-By-StockIII, need scan forward and backward, 分别存每个index左侧的最大连续和，左侧的最小连续和，右侧的最大连续和，
    右侧的最小连续和。然后枚举任意一个位置, 找到max(abs(right_max[i+1] - left_min[i])和abs(left_max[i-1] - right_min[i])).*
    
37. Fence Painter: Write an algorithm that counts the number of ways you can paint a fence with N posts using K colors such that no 
    more than 2 adjacent fence posts are painted with the same color.
    
    *HINT: Similar as problem 23, define two state: same[i]: the ways to paint i the same color of i-1th. diff[i], the ways to paint i
    different from i-1th. so same[0] = 0, diff[1] = 1; same[i] = diff[i-1], diff[i-1] = same[i-1] * (K-1) + diff[i-1] * (K-1), and 
    total solution is same[i] + diff[i].*
    
38. Sum Weighted Nested List: Given a nested list of positive integers: {{1,1},2,{1,1}} Compute the reverse depth sum of a nested list 
    meaning the reverse depth of each node (ie, 1 for leafs, 2 for parents of leafs, 3 for parents of parents of leafs, etc.) times 
    the value of that node.
    For example: {{1,1},2,{1,1}} => 1*1 + 1*1 + 2*2 + 1*1 + 1*1 = 8. {1,{4,{6}}} => 6*1 + 4*2 + 1*3 = 6+8+3 = 17
    
    *HINT: use a List<List<Integer>> to hold the number in different layer, for {{1,1},2,{1,1}} => {2},{1,1,1,1}, then calculate the
    sum: sum += every number in list(i) * list.length - i.*
    
39. 一个线程奇数一个线程偶数，顺序输出

    *HINT: use two Semaphore oddPrinted and evenPrinted. OddPrinter need evenPrinted.acquire(), and after it print, it release 
    evenPrinted, same as EvenPrinter. init as evenPrinter locked.*
    
    

     
    
    
    
    

                                
        
    
        
        
    
    

    
    




        
                            
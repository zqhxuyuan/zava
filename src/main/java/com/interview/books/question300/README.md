#Technical Interview Question

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
    
        
        
        

    
    
    

    
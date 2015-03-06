#Problem Lists and HINTs

**Bold Problem** is **IMPORTANT and HARD/MEDIAN**

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
    
5.  **Throw Balls** When you throw a ball from floor P and above in a N-layer building, it will break, and throw 
    ball under floor P will be OK. Given you K ball, write program to find out the min times of attempt you need to 
    throw to know P. 
    
    *HINT: a DP Problem*
    
6.  Given a range [a,b], for every integer i in the range, if i is dividable by 3 output a 'Fizz', if i is dividable by 5 output a 'Buzz', 
    if i is dividable by 5 and 3, output a 'FizzBuzz'.
    
    If have multiple divider, put divider and words in a HashMap, like {3: “Fizz”, 5:”Buzz”}, any way to optimize the process.
    
    *HINT: loop and put in placeholder array by specific steps.*
    
7.  Given an N * M matrix as positive integer, start from (1,1) to (n.m), find the max path.
    
    If you could go K times, and each time if the ceil is already used, it's value will be set to 0. find the max value could get after K round.
    [Question Detail](http://www.ninechapter.com/problem/26/)
    
    *HINT: Problem 1 is standard DP problem, and Problem 2 should build a Flow Network and run Max Flow algorithm (not used in interview usually). 

8.  **Given N element array, need query the GCD of arbitrary sub-array. Find a way to pre-process and accelerate the query.** 
    Additional space complexity within O(N)
    
    IF N array change to N * N matrix, and need query GCD of arbitrary sub-matrix.
    
    *HINT: Segment Tree, Segment Tree can used to save the result of sub-interval [i,k], [k+1,j], and GCD(i,j) = GCD(gcd[i,k], gcd[k+1,j])
     So the space is O(N), and query is O(lgN)*
    
9.  **Given a N * M matrix and K point in it. Find a point in matrix make the minimize sum distance to all K points.**
    The distance is Manhattan distance:|x0-x1| + |y0-y1|. 
    
    If the point shouldn't overlap in any of K points.
    
    *HINT: consider x and y separately. If in one dimension, point should be the median. 
    Non-overlap: if exist in K points, put it's surrounding in heap, until poll an un-overlap point.*

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
    


      
#Instruction
    All the interview algorithm question is organized using categories. 
    Each question SHOULD have a implementation class and corresponding test class to verify. 
    
#Useful Interview Question Links:
    http://www.careercup.com/page
    www.codechef.com
    www.topcoder.com
    http://www.leetcode.com
    http://www.hackerrank.com/
    http://codekata.pragprog.com/2007/01/code_kata_backg.html#more
    http://geeksforgeeks.org/forum/forum/interview-questions
    http://www.impactinterview.com/2009/10/140-google-interview-questions/#software_engineer
    Dynamic Programming: http://hawstein.com/posts/dp-novice-to-advanced.html

#Categorized Interview Questions:

##C1: General
    1) Given a function which produces a random integer in the range 1 to 5, write a function which produces a random integer in the range 1 to 7.
       Given a function which produces a random integer in the range 1 to 7, write a function which produces a random integer in the range 1 to 10.
    2) Write a regular expression which matches a email address. (Ramp up Regular Expression)
    3) Implement Union Find, which is used for connectivity detection: http://algs4.cs.princeton.edu/15uf/
    4) Write a function (with helper functions if needed) called to Excel that takes an excel column value (A,B,C,D,..Z,AA,AB,AC,..,ZZ,AAA..) and returns a corresponding integer value (A=1,B=2,..,AA=26..).
    6-N) What method would you use to look up a word in a dictionary?
    7-N) Find or determine non existence of a number in a sorted list of N numbers where the numbers range over M, M>> N and N large enough to span multiple disks. Algorithm to beat O(log n) bonus points for constant time algorithm.
    8-N) Given a file of 4 billion 32-bit integers, how to find one that appears at least twice?
    9) Given a set of coin denominators, find the minimum number of coins to give a certain amount of change. Same Problem with C12_2
    10) Given a set of ranges, find the two ranges with the greatest overlap.
    11) Given a time, calculate the angle between the hour and minute hands
    12) Design a algorithm to point all permutation of a string, assume all the character are unique  same as C11_4
    13) Numbers are randomly generated and stored in an array. How would you keep track of the median.
    14) Write a method to get the greatest common divisor(GCD) of two given integer.
    15) Write a method to transfrom a string to int.
    23) Given an arithmatic expression, evaluate the value of the expression, suppose the expression only contains "+", "-", "*", "/", "(", ")" and numbers are integers.
    23A)Given an arithmatic expression, transform the expression into postfix expression, suppose the expression only contains "+", "-", "*", "/", "(", ")" and numbers are integers.
    24) Verify whether an arithmatic expression is valid, suppose the expression only contains "+", "-", "*", "/", "(", ")" and numbers are integers.
    25) The bet problem
    26) Count the amount of '1's in an integer's binary form
    26A) Given two integer A and B, write code to find how many bit need to change if want to change A into B.
    27) There are k exactly same boxes, and we have to put n exactly same items into them. Assume there is no limitation on box, 
        and the only requirement is that each item should be put into one box. Please write out the code to print out all the possible \
        possibilities, print error the input can not get any result.
    28) Get the amount of ending zeros of N! without calculating N!
    29) Find un-duplicate integer number in 250 million integer.
    30) Remove all the number contains 7 from the integer, and define a method to return corresponding number when giving a regular number
    31) Given a string, rearrange the string to a palindrome and return the palindrome if present or -1
    32) Given a capacity value N, and a set of different Item types with values v1, v2, ...,vn
         1) Existence Check: check whether N can be filled with a certain combination of items
         2) All Combinations: get all the combinations of items that fills N
         3) Minimum Combination: get the minimum number of items that fills N    Same Problem with C12_2
    33) Given a timer time() with nanosecond accuracy and given the interface
        interface RealTimeCounter:
        void increment()
        int getCountInLastSecond()
        int getCountInLastMinute()
        int getCountInLastHour()
        int getCountInLastDay()
        implement the interface. The getCountInLastX functions should return the number of times increment was called in the last X.
    34）Implement BitSet
    35) Write a function to print out all the amicable numbers pair within 10000;
            amicable numbers pair is the numbers which the sum of its real factor equals to the other. such as 220 and 284;
    36) N integer from 0 - N-1 form a cycle, start to delete number by visit M step. The process is started at 0.
        Given N and M, please write code to calculate which number will last at the final round.
    37) Given a N, write function to calculate how many 1 appear in 1-N. [Google]
        Such as 12, it should have 1, 10, 11, 12, and it should return 5.
    37A)Given a N, write a generic function to calculate how many M appear in 1-N. M is [1-9].
    38) There is N teams in a match. w[N][N] store the competition result between each two team. order[N] store the order of team. 
        At the first round, order[0] vs. order[1], and order[2] vs. order[3], etc. the winner comes to next round. Finally comes the winner. 
        Write code to compute the ranking of the match. 
    39) Having n string length is m+1, here is a rule to conjoin 2 string together: if the prefix n character equals suffix n character.
        Write code to find the length of the conjoined string, and give a error when it could find a cycle.
    40) Having N, find there exist how many continuous int sequence which sum is N. 
        Such as Given 15, 1+2+3+4+5 = 4+5+6 = 7+8 = 15, so output should be 3.
    41) There is k parenthesis, write code to calculate how many permutations could have. 
        For 2 parenthesis, there is 2 permutations: ()() and (()).
        This problem is the same as: 
           1. there is N non-duplicate number, how many different sequences when pushing these numbers to a stack.
           2. given N non-duplicate number, how many different binary tree could be built.
           3. given an N edge convex polygon, how many different way to using non-cross diagonal line to cut polygon into triangle.
        It's the Catalan number: h(0)=1,h(1)=1, the recursive definition is：
                    h(n)= h(0)*h(n-1)+h(1)*h(n-2) + ... + h(n-1)h(0) (n>=2)
    42) We call the number which factors only include 2,3,5 as "Ugly Number". Write code to compute 1500 ugly number. [Google]
    43) Write code to determine the 5 poker card is a straight or not.
    44) Given N sieves, write code to calculate the possibility of each sum of all the sieves number.
    45) Write code to calculate the math power function: power(base, exp)
    46) There is a random method rand(), generate 0 in possibility p, and generate 1 in possibility 1-p. [Baidu]
        Write code to use this rand() generate 0 and 1 in the same possibility 0.5.
        Write code to generate 1,2,3 in same possibility 1/3
        Write code to generate 1-N in the same possibility 1/N.
    47) Given N points, every line go through 2 point, write code to find the line with largest slope. 
    48) Given a integer, write code to check if it's a square of some integer, can't use sqrt()
    49) Given an int array, numbers between 0-9, such as [0,1,3,8], write code to find the closest number built by these numbers larger then K.  [Google]
        Such as [0,1] and K = 21, should return 100.
    50) Given 4 points, write code to determine whether the 4 points is a rectangle or not
    51) Given a set of chars, write code to print out all the permutations.
    52) Have M memory, given a set of task, each have need request R[i] memory for handling, and O[i] memory to store the result (O[i] < R[i]). 
        Write code to assign the task as a sequence to make sure all the task can be done, 
        return a empty assignment if whatever sequence can't be fulfill these requirement. [Google]
        There assume the task can only be done in sequence, not parallel.
    53) There is 25 horses, need find the fastest 5 ones, there are 5 racing tracks, so each race can have 5 horse to have a competition. How any round need it.
    54) Given two axis-aligned rectangles A and B. Write a function to determine if the two rectangles overlap. 
        Each rectangle is identified by the left-up corner and right-down corner.
    55) Sudoku Game: Given a 3*3 matrix, and 1-8 numbers in random order, 1 place as space. 
        Write code to find the min exchange of numbers to make the matrix in order
            5 4 1           1 2 3
            3   2   --->    8   4
            7 8 6           7 6 5
    56) Given a int number, write code to judge the number of all its factor is an even number or an odd number
    57-N) Given two number A and B, find how many numbers between A and B follow this rule: 
        assume C = c1c2c3c4(between A and B), when (c1+c2+c3+c4)/4 > 7 count one, otherwise not.
        such as 8675, (8+6+7+5)/4 < 7 not count one, 8695, (8+6+9+7)/4 > 7 count one.
        Write code time complexity is O(logA + logB)  [Google]
    58) Given a int N, write code to find the N which is the closet number is power of 2.
    58A)Given a int N, write code to check if N is the power of 2.
    59) Write code to get N prime numbers
    60) Let A be a set of the first N positive integers :A={1,2,3,4.........N}
        Write code to find such subset pair, (x,y), x and y are the subset of A
        Relation 1 pair: x not a subset of y, y is not a subset of x, and x,y have no intersection.
        Relation 2 pair: x not a subset of y, y is not a subset of x, and x,y have intersection.
        Given N, write code to calculate how many Relation 1 pair and Relation 2 pair.
    61) Given a integer N, find the minimal M to make N * M contains only 0 and 1.
        such as: N = 2, M = 5, N * M = 10.
    62) Given N point in two dimentional space, find the closest two points.
    63) Given a source range, and a list of target range, write code to check if the source range is in target range. 
    64) Given a integer, write code to find a combination of continuous integer which sum is the given integer.
        Such as: 9 -> 4 + 5, 11 -> 5 + 6,  6 -> 1 + 2 + 3,  20 ->  2 + 3 + 4 + 5 + 6
        a. Some integer can't find this kind of combination, please specify the rules of this kind of integer.
        b. In 32-bit integer, which number have most combination.
    65) Giving a triangle ABC (ABC in wise-clock order), and a point D. Write code to check if D is inside of ABC.
    66) A circus is designing a tower routine consisting of people standing atop one another’s shoulders For practical and aesthetic reasons, 
        each person must be both shorter and lighter than the person below him or her Given the heights and weights of each person in the circus, 
        write a method to compute the largest possible number of peo- ple in such a tower
        EXAMPLE:
        Input (ht, wt): (65, 100) (70, 150) (56, 90) (75, 190) (60, 95) (68, 110)
        Output: The longest tower is length 6 and includes from top to bottom: (56, 90) (60,95) (65,100) (68,110) (70,150) (75,190)
    67) Given a string, print all the combinations with the chars in the string.
        Example: for abc, the combinations are a, b, c, ab, ac, bc, abc.
    68) Given a string, print all the permutations of the string.
    69) Implement an algorithm to print all valid combinations of n-pairs of parentheses.
    70) Write a function that adds two numbers. You should not use + or any arithmetic operators.
    71) Say you have an array for which the ith element is the price of a given stock on day i.
        Design an algorithm to find the maximum profit. You may complete as many transactions as you like (ie, buy one and sell one share of the stock multiple times). 
        However, you may not engage in multiple transactions at the same time (ie, you must sell the stock before you buy again).
    72) Determine whether an integer is a palindrome. Do this without extra space. [LeetCode]
    73) Some about permutation:  [LeetCode]
        73A) Given a collection of numbers, return all possible permutations.   For example,
            [1,2,3] have the following permutations:
            [1,2,3], [1,3,2], [2,1,3], [2,3,1], [3,1,2], and [3,2,1].
        73B) Implement next permutation, which rearranges numbers into the lexicographically next greater permutation of numbers.
            If such arrangement is not possible, it must rearrange it as the lowest possible order (ie, sorted in ascending order).
            The replacement must be in-place, do not allocate extra memory.
        73C) The set [1,2,3,…,n] contains a total of n! unique permutations.
            By listing and labeling all of the permutations in order, We get the following sequence (ie, for n = 3):
            "123", "132", "213", "231", "312", "321", the 4th permutation is "312" 
            Given n and k, return the kth permutation sequence. Note: Given n will be between 1 and 9 inclusive.   
        73D) Given a collection of numbers that might contain duplicates, return all possible unique permutations. g
            For example, [1,1,2] have the following unique permutations: [1,1,2], [1,2,1], and [2,1,1].
    74) Given two binary strings, return their sum (also a binary string).
        For example, a = "11" b = "1" Return "100".
    75) The string "PAYPALISHIRING" is written in a zigzag pattern on a given number of rows like this: [LeetCode]
        (you may want to display this pattern in a fixed font for better legibility)
            P   A   H   N
            A P L S I I G
            Y   I   R
        And then read line by line: "PAHNAPLSIIGYIR"
        Write the code that will take a string and make this conversion given a number of rows.
    76) Some about combination: [LeetCode]
        76A) Given a collection of numbers, return all possible combinations
        76B) Given two integers n and k, return all possible combinations of k numbers out of 1 ... n.
             For example,   If n = 4 and k = 2, a solution is:
             [[2,4], [3,4], [2,3], [1,2], [1,3], [1,4]]
        76C) Given a set of candidate numbers (C) and a target number (T), find all unique combinations in C where the candidate numbers sums to T.
             The same repeated number may be chosen from C unlimited number of times.
             All numbers (including target) will be positive integers.  Elements in a combination (a1, a2, … , ak) must be in non-descending order. (ie, a1 ≤ a2 ≤ … ≤ ak).
             The solution set must not contain duplicate combinations.  For example, given candidate set 2,3,6,7 and target 7, 
             A solution set is: [7], [2, 2, 3] 
    77) The gray code is a binary numeral system where two successive values differ in only one bit.   [LeetCode]    
        Given a non-negative integer n representing the total number of bits in the code, print the sequence of gray code. A gray code sequence must begin with 0.        
        For example, given n = 2, return [0,1,3,2]. Its gray code sequence is:  00 - 0  01 - 1  11 - 3  10 - 2 
        As solution, grey code is generated by ((i >> 1) ^ i);
    78) Roma to integer and integer to Roma.
    79) Given n non-negative integers a1, a2, ..., an, where each represents a point at coordinate (i, ai). [LeetCode] 
        N vertical lines are drawn such that the two endpoints of line i is at (i, ai) and (i, 0). 
        Find two lines (not need to be adjacent), which together with x-axis forms a container, such that the container contains the most water.        
    80) Given an array of non-negative integers, you are initially positioned at the first index of the array.   [LeetCode]
        Each element in the array represents your maximum jump length at that position.
        Determine if you are able to reach the last index.
        For example:    A = [2,3,1,1,4], return true. A = [3,2,1,0,4], return false.
    80A)Given an array of non-negative integers, you are initially positioned at the first index of the array.
        Each element in the array represents your maximum jump length at that position.
        Your goal is to reach the last index in the minimum number of jumps.
        For example:  Given array A = [2,3,1,1,4]  The minimum number of jumps to reach the last index is 2. (Jump 1 step from index 0 to 1, then 3 steps to the last index.)
    81) Given a digit string, return all possible letter combinations that the number could represent.   [LeetCode]
        A mapping of digit to letters (just like on the telephone buttons) is given below.
            2: a b c   
            3: d e f   
            4: g h i   
            5: j k l   
            6: m n o   
            7: p q r s 
            8: t u v   
            9: w x y z 
        Input:Digit string "23", Output: ["ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"].
    82) There are N gas stations along a circular route, where the amount of gas at station i is gas[i]. [LeetCode]
        You have a car with an unlimited gas tank and it costs cost[i] of gas to travel from station i to its next station (i+1). 
        You begin the journey with an empty tank at one of the gas stations.
        Return the starting gas station's index if you can travel around the circuit once, otherwise return -1.
        
    
##C2: CS Theory
    1) What is the difference between a mutex and a semaphore? Which one would you use to protect access to an increment operation?
    2) How are cookies passed in the HTTP protocol?
    3) Design the SQL database tables for a car rental database.
    4) What is multithreaded programming? What is a deadlock?
    5) Implement division (without using the divide operator, obviously).
    6) Suppose you have given N companies, and we want to eventually merge them into one big company. How many ways are theres to merge?
    7) How long it would take to sort 1 trillion numbers? Come up with a good estimate.
    8) What's the difference between a hashtable and a hashmap?
    9) Write a C program which measures the the speed of a context switch on a UNIX/Linux system.
    10) Explain how congestion control works in the TCP protocol.
    11) Billions of numbers, how to effectively find the median. [Google]

##C3: List
    1) Merge two sorted lists
    2) Given two linked lists A and B, return a list C containing the intersection elements of A and B. The nodes of C should appear in the order as in B.
    3) Given a single list, find the "Mth to the last" element.
    4) Given a circular linked list, implement an algorithm which returns the node at the beginning of the loop.
    5）Write a routine to reverse every k nodes in a given linked list without using additional memory.
    6) [LeetCode] LRU Cache
    7) We have a linked list as a1,a2,a3...an,b1,b2,b3...bn, we need a rearrange method to rearrange the linked list into
       a1,b1,a2,b2...an,bn
    8) Write code to remove duplicate from an unsorted linked list. How would you solve this problem if a temporary buffer is not allowed? same as C11_9
    9) Implement an algorithm to delete a node in the middle of a singly linked list, given only access to that node.
    10) Write code to partition a linked list around a value x, such that all nodes less then x come to before, all nodes greater than or equal to x.
    11) You have two numbers represented by a linked list, where each node contains a single digit, The digit are stored in reverse order
        617 == 7->1->6
        Write a function to adds the two numbers and returns the sum as a linked list.
    12) Implement a function to check if a linked list is a palindrome.
    13) Write code to check if two linked list have intersection.
    14) Write code to check if a linked list contains cycle.
    15) Write code to reverse a linked list. Try to not using any extra space.
    16) Write code to find the first common node of two linked list
    17) Given a complicate linked list, besides a next pointer, have a sibling pointer which point to any node in the linked list or NULL. 
        Write code to clone the linked list.
    18) Write code to sort a linked list, specify which sort method you choice and why.
    19) Given a sorted cyclic linked list in ascending order, write code to insert a int into the list and keeping it in ascending order.
    20) Given a linked list, remove the nth node from the end of list and return its head.
        For example,
            Given linked list: 1->2->3->4->5, and n = 2.    
            After removing the second node from the end, the linked list becomes 1->2->3->5.
    21) Reverse a linked list from position m to n. Do it in-place and in one-pass.
        For example:    Given 1->2->3->4->5->NULL, m = 2 and n = 4, return 1->4->3->2->5->NULL.
    22) Given a sorted linked list, delete all nodes that have duplicate numbers, leaving only distinct numbers from the original list.
        For example,
        Given 1->2->3->3->4->4->5, return 1->2->5.  Given 1->1->1->2->3, return 2->3.

##C4: Array
    1) Given an image represented by an N\*N matrix, where each pixel in the image is 4 bytes, write a method to rotate the image by 90 degrees. Can you do this in place?
    1A)Given an integer n, generate a square matrix filled with elements from 1 to n2 in spiral order.
       For example, Given n = 3, You should return the following matrix:
        [ 1, 2, 3 ],
        [ 8, 9, 4 ],
        [ 7, 6, 5 ]
    2) Given an array, find the longest continuous increasing subsequence.
    3) You are given with three sorted arrays ( in ascending order), you are required to find a triplet ( one element from each array) such that distance is minimum. 
       Distance is defined like this : If a[i], b[j] and c[k] are three elements then distance=max(abs(a[i]-b[j]),abs(a[i]-c[k]),abs(b[j]-c[k]))
       Please give a solution in O(n) time complexity
    4) Given an array whose elements are sorted, return the index of a the first occurrence of a specific integer. Do this in sub-linear time. 
       I.e. do not just go through each element searching for that element.
    5) Given an array, print the array elements cyclicly from outside to center.
    6) Given an array, use Binary Search to find a given element
    7) Given a sorted array, there is only one value K has multiple occurrence, find the repeating element and its first occurrence.
    8) Find the Kth small element in an array.
    9) Given an array, there is an element whose occurence is greater than the half of the array's length, find this element. [Google, Baidu, Microsoft]
    10) Given an m\*n grid, how many paths are there from the left bottom corner to the up right corner.
    11) Randomly shuffle an array.
    12) Implement a cyclic int buffer with an int array
    13) Suppose you have an NxM matrix of positive and negative integers. Write some code that finds the sub-matrix with the maximum sum of its elements.
    14) Given a set S, find all the maximal subsets whose sum <= k.
    14.1) Given a set S, find all possible subsets
    15) Find the uniq amount of absolute values in a given sorted array
    16) Write code to reverse a array of int
    17) There is an array A[N] of N numbers. You have to compose an array Output[N] such that Output[i] will be equal to multiplication of all the elements of A[N] except A[i].
        For example Output[0] will be multiplication of A[1] to A[N-1] and Output[1] will be multiplication of A[0] and from A[2] to A[N-1]. Solve it without division operator and in O(n).
    18) Given two sequences of items, find the items whose absolute number increases or decreases the most when comparing one sequence with the other by reading the sequence only once.
    19) Merge two sorted arrays
    20) How to find the median among the given numbers whose values are unknown but falls into a narrow range.
    21) Array Element Finder
       A) Given a sorted array, write an algorithm to determine whether a given value target exists in the array
       B) For an array whose values first increase and then decrease,
          write an algorithm to determine whether a given value target exists in the array
       C) There are two arrays A1 and A2 sorted in ascending order. The largest value of A2 is smaller than the smallest value of A1.
          Array A3 is formed by appending A2 after A1. Write an algorithm to check whether a given value target exists in array A3.
    22) Closest Pair. Given an int array a[], find the closest two numbers A and B so that the absolute value |A-B| is the smallest. The time complexity should be O(NlogN).
    23) Farthest Pair. Given an int array a[], find the farthest two numbers A and B so that the absolute value |A-B| is the biggest. The time complexity should be O(N).
    24) Given a list of number 0,1; find the start of runs (the length contiguous sequence of 1 is larger than a given number)
    25) You are given an unsorted array of integers that contain duplicate numbers. Only one number is duplicated odd number of duplications, other numbers are repeated even number of duplications. Find this number.
    26) Define a function that takes an array of integer numbers and returns an array of numbers of the same length. Each element of the output array out[i] should
       be equal to the product of all of the elements of the input array except for in[i]. Example: input {1,2,3,4} output {24,12,8,6}
    27) Given a rotated sorted array, the element might appears in the order 3,4,5,6,7,1,2. How would you find the minimum element
    28) Write a algorithm such that if an element in an element in a M*N matrix is 0, it's entire row and column is set to 0.
    29) A int array contains positive and negative number, find the max sum of all continuous sub arrays
    29.1) A int array contains positive and negative number, find the sub array with the maximum sum number
    30）A int array contains 1-N number, only 1 number appear twice, please find the number.
    31) Given two integer N and M, please find all the combination of numbers within[1,N] which sum is M.
    32) Given a int array, write a code to find a pair integers which sum is a specific number K
    32A)Given a int array, write a code to find three integers which sum is a specific number K.
    32B)Given a int array, write a code to find unique three integers which sum is a specific number K.
    33) Given two int array, one is the push sequence, write function to check if the second one is a pop sequence.
    34) Given two int array, find a switch of the items in the two array to make the SUM of two array closest.
    35) Given two int array, find switches of the items in the two array to make the SUM of two array closest. could use C12_27 or C4_54 to divide the array.
    36) Given matrix, find the sub 2 \* 2 array with max sum.
    37) Given a array of N number which arrange is 1-M, write code to find the shortest sub array contains all 1-M numbers.
        Also consider if the array is cycle (connected head and tail).
    38-N) Define a function on an array, when you increase 1 on one cell, the neighbor (up, down, left and right) all increase one.
        Given an array, write code to determine whether this array can be generated using the above function.
    39) Given an array, write code to divide the array into M sub array (find largest M), make sure the sum of all sub array are the same. 
    40) Given an array, is rotated from a sorted array, such as 5,6,1,2,3,4. Write code to search element in the array.
    41) Given an array contains only 2 number appear only once, the other all appear twice. Write code to find the 2 numbers.
    41A)Given an array of integers, every element appears three times except for one. Find that single one.
    42) Given an int array, combine all the integer to a int, such as {23, 125} -> 12523. Write code to get the smallest combined number. [Baidu]
    43) Given an int array, write code to find the numbers, which left number all not larger then it, and right number all not smaller than it. [Baidu]
    44) Given an array with integer, write code to put all odd number before even number. [Baidu]
    45) Given an array with integer, a "trick pair" is called for any 2 numbers is not follow the arrange rule: larger number should be put 
        at the right side of the small number. 
        Given an array list, write code to find how many trick pair exist.
    46) Given an array of integer, write code to find the arithmetic progression（length > 3), return the arithmetic progression from min to max.
    47) Given an sorted array of integer, write code to find the occurrences of a given number.  O(lgN)  
    48) Given an array, which number can be divied by a or b. write code to generate smallest N number in the array when given a, b and N.  [Google]
        Like the Ugly Number in C1_42.
    49) Given an matrix M * N, write code to clock-wisely print the matrix 
    50) Given an array contains positive and negitive numbers, write code to re-arrange the array to make negitive number placed before positive number
        and the relative position of positive numbers and negitive numbers are not changed.
        The best code should be: time O(N) and space O(1)
    
    51) Given an unsorted array of number, find the max difference between the numbers in sorted order.   time O(N), space O(N)
    52) Given an array A[N], N is unknown. getNum() will return one of the number in the array, and return NULL when the array is empty. 
        Write a get() method to random get a number in the array with 1/N probability.
    53) Given an int array A and a sliding window width w, scan the array with sliding window and keep the max value in the window to array B.
        Write code to find B. B[i] should be the max value among A[i] ~ A[i + w - 1];
    54) Given an int array contains N integer, write code to find M integer in the array, which could make sum of M integer closest to sum of other N-M integer.
        The same question is: 
            Given an int array, write code to divide this array into two part which sum is closest to the others.
            Given an int array, write code to find the sub array which sum is closest to K (which K is the sum/2 in this case).     same as C12_27
    55) Given an int array, write code to remove duplicate numbers. Time O(N), Space O(1)
    56) A round road have N station, A0..AN-1, given an array D contains the distance between each neighborhood station, 
        D1 = distance(A0-A1), D2 = distance(A1, A2), D0 = distance(AN-1, A0)
        Write code to most effective to find the shortest distance between two station Ai and Aj.    Space O(N) most
    57) Given a very long array, write code to find the first number which only appear once. [Google]
    58) Given an array, write code to find how many increasing sub array could generated from it. [MS]
        such as: Given {1,2,3}, it could generate {1,2}, {1,3}, {2,3}, {1,2,3}, 4 increasing sub array.
    59) Given an array is sorted in two part, such as {4,7,10, 1,5,8}. write code to merge the partial sorted array in place.
        Time: O(N) Space: O(1)
    60) Given an array which is partial sorted, write code to find a specified item in it.
    61) Write code to random shuffle an array without using random variables.
    62) Given an array S[N], find the max d in array could find a combination with the other element in the array, 
        such as a1,a2..am also in S[N] follow d = a1 +..+ am.
        S = {2,3,7,10}, find the max element is 10 = 3 + 7
    63）Given an array with N integer, write code to find the maximal product of any N-1 elements in the array.
    64) Imagine you have a square matrix, where each cell (pixel) is either black or white. 
            Design an algorithm to find the maximum subsquare such that all four borders are filled with black pixels.
    65) Given a matrix, number in each row and column in increasing order, write code to find a given integer exist in the matrix or not. [Yang Matrix Searching]
    66) You are given two sorted arrays, A and B, where A has a large enough buffer at the end to hold B. Write method to merge B into A.
    67) Given an array whose values first decrease and then increase, write an algorithm to determine whether a given value target exists in the array
    68) Given an matrix contains 0 and 1, write code to find all the group of 1 which is connected (up and left).
    69) Given a stream of integer, design and implement a data structure tracking the rank of integers.
        It have 2 methods: track(int n) is called when generate a new integer n, and rank(int n) return how many integers in the stream is smaller than n.
    70) Given an array of integers, write a method to find indices m and n such that if you sorted elements m through n, the entire array would be sorted.
        Minimize n - m (that is find the smallest such sequence).
        Example: 1,2,4,7,10,11,7,12,6,7,16,18,19, return (3,9)
    71) Given an array, find the max K element in the array.
    72) Given a M*N matrix A, A[i][j] < A[i][j+1] and A[i][j] < A[i+1][j] for any element A[i][j] in the matrix. Given a
        value N, do binary search in matrix A to check whether N exists in A.
    73）Given an array A of integers, find the maximum of j-i subjected to the constraint of A[i] < A[j].
    74) Given a Data Structure having first n integers and next n chars. A = i1 i2 i3 … iN c1 c2 c3 … cN.
        Write an in-place algorithm to rearrange the elements of the array ass A = i1 c1 i2 c2 … in cn
    75) Given a triangle, find the minimum path sum from top to bottom. Each step you may move to adjacent numbers on the row below. [LeetCode]
        For example, given the following triangle
        [
             [2],
            [3,4],
           [6,5,7],
          [4,1,8,3]
        ]
        The minimum path sum from top to bottom is 11 (i.e., 2 + 3 + 5 + 1 = 11).
    76) Given a 2D board containing 'X' and 'O', capture all regions surrounded by 'X'.  [LeetCode]
        A region is captured by flipping all 'O's into 'X's in that surrounded region.
        For example,
            X X X X
            X O O X
            X X O X
            X O X X
        After running your function, the board should be:
            X X X X
            X X X X
            X X X X
            X O X X
    77) Given n non-negative integers representing an elevation map where the width of each bar is 1, 
        compute how much water it is able to trap after raining. [LeetCode]
        For example, Given [0,1,0,2,1,0,1,3,2,1,2,1], return 6.
    78) Given an unsorted array of integers, find the length of the longest consecutive elements sequence. [LeetCode]
        Your algorithm should run in O(n) complexity.
        For example, Given [100, 4, 200, 1, 3, 2], The longest consecutive elements sequence is [1, 2, 3, 4]. Return its length: 4.
    79) There are two sorted arrays A and B of size m and n respectively. Find the median of the two sorted arrays. [LeetCode]
        The overall run time complexity should be O(log (m+n)).
    80) Given a sorted array of integers, find the starting and ending position of a given target value.   [LeetCode]
        Your algorithm's runtime complexity must be in the order of O(log n).
        If the target is not found in the array, return [-1, -1].
        For example,    Given [5, 7, 7, 8, 8, 10] and target value 8, return [3, 4].
    81) Given an unsorted integer array, find the first missing positive integer.
        For example, Given [1,2,0] return 3, and [3,4,-1,1] return 2.
        Your algorithm should run in O(n) time and uses constant space.
    
##C5: Tree
    1) Create a binary-tree datastructure, having pre-order, in-order, and post-order traverse.
    2) Tree search algorithms. Write BFS and DFS code, explain run time and space requirements. in C5_1
       Modify the code to handle trees with weighted edges and loops with BFS and DFS, make the code print out path to goal state.
    3) How do you put a Binary Search Tree in an array in a efficient manner.
       Hint :: If the node is stored at the ith position and its children are at 2i and 2i+1(I mean level order wise)Its not the most efficient way.
    4) How do you find out the fifth maximum element in an Binary Search Tree in efficient manner. in BinarySearchTree.select()
       Note: You should not use use any extra space. i.e sorting Binary Search Tree and storing the results in an array and listing out the fifth element.
    5) Given a binary tree, programmatically you need to prove it is a binary search tree.
    6) Given two binary trees, write a compare function to check if they are equal or not.
       Being equal means that they have the same value and same structure.
    7) Given a BinarySearchTree and value K, find all value pairs whose sum is K in O(n).
    8) Given the pre-order and in-order traverse list, write code to find the post-order. 
       Note: pre-order and post-order can't determine a binary tree, but pre-order and in-order, post-order and in-order can.
    8A) Rebuilt Binary Tree
        Given the pre-order and in-order traverse list, write code to rebuild the tree.
        Given the post-order and in-order traverse list, write code to rebuild the tree.
    9) Given a list of pairs (a,b) where a is the number of a node and b is its parent, construct a tree and return the root.
    10) Implement a function to check if a tree is balanced. For the purposes of this question,
        a balanced tree is defined to be a tree such that no two leaf nodes differ in distance
        from the root by more than one.
    11) Given a sorted (increasing order) array, write an algorithm to create a balanced binary search tree.
    11A)Given a sorted (increasing order) linkedlist, write an algorithm to create a balanced binary search tree.
    12) Write an algorithm to find the ‘next’ node (i.e., in-order successor) of a given node in
        a binary search tree where each node has a link to its parent. in BinarySearchTree.successor();
    13) Given a binary search tree, design an algorithm which creates a linked list of all the
        nodes at each depth (i.e., if you have a tree with depth D, you’ll have D linked lists)
    14) Design an algorithm and write code to find the first common ancestor of two nodes
        in a binary tree. Avoid storing additional nodes in a data structure. NOTE: This is not
        necessarily a binary search tree
    15) You have two very large binary trees: T1, with millions of nodes, and T2, with hun-
        dreds of nodes. Create an algorithm to decide if T2 is a subtree of T1.
    16) You are given a binary tree in which each node contains a value. Design an algorithm
        to print all paths which sum up to that value. Note that it can be any path in the tree
        - it does not have to start at the root.
    17) Write a method to transfer a BinarySearchTree to a sorted LinkedList without using extra space.
    18) Given a sequence of int, write code to check if this the post-order traverse of a binary search tree.
    19) Find the max distance of two node in a binary tree.
    20) Write code to create a mirroring of a binary tree
    21) Given a BST, define f = (max + min)/2, write code to find the element > f but closest to f.
    22) Given a complete binary tree, write code to add a next point in each node which point to the next node in the same layer, 
        and leave it as NULL when the node is last one in the layer.
    23) Given a binary tree which node is a int (positive and negitive), write code to find a sub-tree which node sum is maximal. [Facebook]
    25) Given a binary tree, print tree in each layer in one line, also could print a specific layer when given layer number. (root layer = 0)
    26) Given a binary tree, check whether it is a mirror of itself (ie, symmetric around its center).  [LeetCode]       
        For example, this binary tree is symmetric:        
            1
           / \
          2   2
         / \ / \
        3  4 4  3
        But the following is not:
            1
           / \
          2   2
           \   \
           3    3
    27)Given n, generate all structurally unique BST's (binary search trees) that store values 1...n.
       For example, Given n = 3, your program should return all 5 unique BST's shown below.
       
          1         3     3      2      1
           \       /     /      / \      \
            3     2     1      1   3      2
           /     /       \                 \
          2     1         2                 3
    28)Two elements of a binary search tree (BST) are swapped by mistake. Recover the tree without changing its structure.

##C6: Graph
    1) What's the maximum number of edges in a Directed Asynclic Graph with N node.
            1+2+3+...+N-1 = N(N-1)/2
    2) Print all the cycles in a directed graph
    3) Print the minimum spanning tree of a graph
       Find a MST(min-weight spanning tree).
            A spanning tree of G is a subgraph T that is connected and acyclic.
            Algorithm(Greedy): Prim and Kruskal
    4) Implement the DFS and BFS traverse algorithms
    5) Calculate Shortest Path between two given vertexes for weighted graph
       Find the shorted path (smallest weight) from one node to other
            Algorithm:
               *Dijkstra: when all the weight is positive number
               A* search: use heuristic to select candidates
               Bellman-Ford: support negative weight and rewrite weight
    6) DAG Graph topological sorting/ordering
    7) Connected Component: Given a directed graph, design an algorithm to find out whether there is a route be-
       tween two nodes.
    8) Is a graph bipartite?
    9) Find a cycle.
    10-N) Eulerian tour. Find a (general) cycle that uses every edge exactly once.
        Is there a (general) cycle that uses each edge exactly once?
        Yes iff connected and all vertices have even degree.
    11-N) Hamiltonian cycle: Find a cycle that visits every vertex exactly once.
    12-N) Are two graphs identical except for vertex names?
    13-N) Lay out a graph in the plane without crossing edges?
    14-N) Find the strongly connected component in directed graph.
       	    Kosaraju-Sharir algorithm
       	    Simple (but mysterious) algorithm for computing strong components.
       	        Phase 1: run DFS on GR to compute reverse postorder.
       	        Phase 2: run DFS on G, considering vertices in order given by first DFS.
    15) Find the shortest path from one node to other.
    16) Write a code to check if there is cycle in a graph
    17-N) Write code to find the bridge node in a connected graph. Bridge node is that the graph will be connected if 
          delete this node from graph.
    18) Given a graph, find the minimal color printed on each node, the color of each node can't be the same with its neighbors.
    19）Tree update and query: see the instruction in the code
    

##C7: Stack AND Queue
    1) How would you design a stack which, in addition to push and pop, also has a function
       min which returns the minimum element? Push, pop and min should all operate in
       O(1) time.
    2) Implement a Queue with Stacks.
    3-N) Implement a Queue with constant stacks, and each list operation could be done in O(1).
    4-N) Describe how you could use a single array to implement three stacks
    5) Imagine a (literal) stack of plates. If the stack gets too high, it might topple. There-
       fore, in real life, we would likely start a new stack when the previous stack exceeds
       some threshold. Implement a data structure SetOfStacks that mimics this. SetOf-
       Stacks should be composed of several stacks, and should create a new stack once
       the previous one exceeds capacity. SetOfStacks.push() and SetOfStacks.pop() should
       behave identically to a single stack (that is, pop() should return the same values as it
       would if there were just a single stack).
       FOLLOW UP
       Implement a function popAt(int index) which performs a pop operation on a specific
       sub-stack.
    6) In the classic problem of the Towers of Hanoi, you have 3 rods and N disks of different
       sizes which can slide onto any tower. The puzzle starts with disks sorted in ascending
       order of size from top to bottom (e.g., each disk sits on top of an even larger one). You
       have the following constraints:
            (A) Only one disk can be moved at a time.
            (B) A disk is slid off the top of one rod onto the next rod.
            (C) A disk can only be placed on top of a larger disk.
       Write a program to move the disks from the first rod to the last using Stacks
    7) Write a program to sort a stack in ascending order. You should not make any assump-
       tions about how the stack is implemented. The following are the only functions that
       should be used to write this program: push | pop | peek | isEmpty
    8) Using recursive method to reverse a Stack.
    9) Write code to implement a Queue, besides inqueue() and dequeue() method, it has a max() return the max element in the list.
    10)Given a string containing just the characters '(' and ')', find the length of the longest valid (well-formed) parentheses substring.
       For "(()", the longest valid parentheses substring is "()", which has length = 2.
       Another example is ")()())", where the longest valid parentheses substring is "()()", which has length = 4.
    
##C8: Sorting
    1) If you have 1 million integers, how would you sort them efficiently?
    2) You are given a small sorted list of numbers, and a very very long sorted list of numbers. so long that it had to be put on a disk in different blocks.
       How would you find those short list numbers in the bigger one?
    3) What sort would you use if you had a large data set on disk and a small amount of ram to work with?
    4) What sort would you use if you required tight max time bounds and wanted highly regular performance.
    5) How to sort integer in O(N)

##C9: Design Patterns and OOP
    1) Design a class library for writing card games.
    2) Write code to implement a 6 faces Rubik's cube game.
    3) Write code to implement a Ant Game: there is N ants on a stick (Mcm) at: n1, n2, n3, n4, n5, etc. ant only can go forward, and when two ants meet each other they will both turn back.
       Given N and M, write code to find the min and max time all these ant leave the stick.
       The min time is: the stick be divided into two equal half, and ant on each half, direction to the end. the time should be the longest between the ant and the end.
       The max time is: all the ant direct to same direction which is the time from the leftest and rightest ant to the other end.
    4) 5 person go fish together, ABCDE, they got lots of fish, and A throw 1 fish away, then divide the total amount of fish into 5 part, 
       and take one part, then B,C,D,E all take fish in the same way. Write code to check the min amount of fish they get.
    5) The game of Master Mind is played as follows:
        The computer has 4 slots, and each slot will contain a ball that RED, YELLOW, GREEN, BLUE. The user try to guess the solution. 
        When you guess the correct color for the correct slot, you got a hit. 
        When you guess a color exist but not in the correct slot, you got a pseudo-hit. Note that a slot that is a hit can never count as a pseudo-hit.
        Example: the solution is RGBY, you guess GGRR, you got one hit and one pseudo-hit.
        Write a method that, given a guess and a solution, returns the number of hits and pseudo-hits.

##C10: System Design
    1) Design and describe a system/application that will most efficiently produce a report of the top 1 million Google search requests. These are the particulars: a) You are given 12 servers to work with. They are all dual-processor machines with 4Gb of RAM, 4x400GB hard drives and networked together.(Basically, nothing more than high-end PC��); b) The log data has already been cleaned for you. It consists of 100 Billion log lines, broken down into 12 320 GB files of 40-byte search terms per line. c) You can use only custom written applications or available free open-source software.
    2) Write a program for displaying the ten most frequent words in a file such that your program should be efficient in all complexity measures.
    3) Create a fast cached storage mechanism that, given a limitation on the amount of cache memory, will ensure that only the least recently used items are discarded when the cache memory is reached when inserting a new item. It supports 2 functions: String get(T t) and void put(String k, T t).
    4) Remove duplicated lines in a very large block of text.
    5) Write a multi-thread program to handle Producer-Consumer Problem.
    6) You have a stream of infinite queries (ie: real time Google search queries that people are entering).     [Google]
       Describe how you would go about finding a good estimate of 1000 samples from this never ending set of data and then write code for it.
    7) Write a blocking list implementation using cyclic array.
    8) We have a Foo class, it have 3 method, first(), second() and third(), the same instance of Foo will be passed to three different threads. 
       Design a mechanism to ensure that first it called before second and second is called before third.
    9) The dinning philosophers problem, a bunch of philosophers are sitting around a circular table with one chopstick between each of them. 
       A philosophers needs both chopsticks to eat, and always picks up the left chopstick before the right one. A deadlock could potentially occur if all the philosophers reached for the left chopstick at the same time. 
       Using threads and locks, implement a simulation of the dining philosopher problem that prevents deadlocks.
    10)You are given a game of Tic Tac Toe. You have to write a function in which you pass the whole game and name of a player. 
       The function will return whether the player has won the game or not. 
       First you to decide which data structure you will use for the game. 
       You need to tell the algorithm first and then need to write the code. Note: Some position may be blank in the game। 
       So your data structure should consider this condition also.

##C11: String
    1) Implement an algorithm to determine if a string has all unique characters. What if you cannot use additional data structure.
    2) Write a function f(a, b) which takes two character string arguments and returns a string containing only the characters found in both strings in the order of a. Write a version which is order N-squared and one which is order N.
    3) Given two strings, write a method to decide if one is a permutation of the other
    4) Write some code to find all permutations of the letters in a particular string.
    5) Given that you have one string of length N and M small strings of length L. How do you efficiently find the occurrence of each small string in the larger one?
    5A)Given a string s and an array of smaller string T, design a method to search s for each small string in T
    6) Assume you have a method isSubstring which checks if one word is a substring of another. Given two strings s1 and s2, write code to check
	   if s2 is a rotation of s1 using only one call to isSubstring (e.g. "waterbottle" is a rotation of "erbottlewat".
    7) Implement a function to reverse a string
    8) Given a string array, find the max common part of all string elements. E.g. the max common string of ["abcde", "abccd", "abc", abcef"] is "abc"
    9) Design an algorithm and write code to remove the duplicate characters in a string without using any additional buffer.
    10) Write a method to replace all spaces in a string with "%20"
    11) Write a method to find the longest common sub sequence (no need to be continuous) of characters of two given string.
    12) Write a method to find the longest common sub string (need to be continuous) of characters of two given string.
    13) Write a method to rotate a given string by K indices.
    14) Write a more effective sort method of strings, need be O(N)
    15) Implement a method to perform basic string compression using the counts of repeated characters.
        For example: aabcccccaaa would become a2b1c5a3
        And if the compressed string is longer then the original one, just return the original.
    16) Write a function effective to sort large set of string
        1. key-indexed counting
        Key-indexed counting is a sub-sort problem, which sort N element have lots of duplication in key,
        which indicate key is much smaller than N.
        Sample application: Sort string by first letter. only have 26 key.
        The solution is
        	use array to counting the number of each key, and calculate the excursion of each key,
        	then directly copy the element to the right location.
        	It's also a stable sort.
        2. LSD radix sort
        LSD radix sort is Least-significant-digit-first string sort, consider characters from right to left.
        Stably sort using dth character as the key (using key-indexed counting).
        Time: 2*W*N (W is a fix length of keys)
        Sample application: Sort one million 32-bit integers.
        	sort the integer by bit, have 0 / 1 two keys, 32*2*2*1M ~= 1M
        3. MSD radix sort
        MSD string (radix) sort is Most-significant-digit-first string sort.
        Partition array into R pieces according to first character (use key-indexed counting).
        Recursively sort all strings that start with each character (key-indexed counts delineate sub arrays to sort).
    17) Write basic functions of String: reverse, suffixes and longest common prefix.
    18) Write code to implement the string match using patten. In patten, could use * as wildcard character, it could match 1-n any character.
    19) Write code to reserve the word sequence. input as I am Stefanie output is Stefanie am I
    20) Substring match: give string A and B, find the first occurence of B in A.
    21) Given a string, find the longest substring which is plalindrome.
    22) Given 1-N number, find all the combination of the numbers alias with the given rule. 
        Such as: given 1,2,3,4,5, rule is 4 can't be place at the 3rd place, and 3 and 5 can't be together
    23) Given a string, find the length of the continuous longest substring without repeating characters. 
        For example, the longest substring without repeating letters for “abcabcbb” is “abc”, which the length is 3. 
        For “bbbbb” the longest substring is “b”, with the length of 1.
    24) Replace all occurrence of the given pattern to ‘X’.
        For example, given that the pattern=”abc”, replace “abcdeffdfegabcabc” with “XdeffdfegX”. 
        Note that multiple occurrences of abc’s that are contiguous will be replaced with only one ‘X’.
    25) Given several sets of strings, write code to combine the sets which interactions.
        Such as {aa,bb,cc},{bb,dd},{hh},{uu,jj},{dd,kk}, the result should be {aa,bb,cc,dd,kk},{hh},{uu,jj}
    26) Given a string, write code to find the longest substring which repeated more than once.
    27) Given a string contains several words separate with more than 1 whitespace, such as abc    efg    hij
        Write code to remove multiple whitespace with 1, and reverse the words, such as cba gfe jih
    28) Given a word, the words contains same character but in different order call "brother word"
        Write code to find all the "brother words" of given word in a dictionary.
    29) K－important strings:
        You are given a set of N strings S0, S1, …, SN-1. These strings consist of only lower case characters a..z and have the same length L.
        A string H is said to be K-important if there are at least K strings in the given set of N strings appearing at K different positions in H. 
        These K strings need not to be distinct.
        Your task is to find the shortest K-important string. If there are more than one possible solution, your program can output any of them.
    30) Given two words of equal length that are in a dictionary, write a method to transform one word into another word by changing only one 
        letter at a time, the new word you get in each step must be in the gdictionary. 
        Such as: DAMP -> LAMP -> LIMP -> LIME -> LIKE
    31) Write a method to sort an array of strings so that all the anagrams are next to each other.
    32) Given a sorted array of strings, which is interspersed with empty strings, write a method to find the location of a given string.
    33) Given two string a and b, write method to check if the chars in b all exist in a. find a linear algorithm.
    34) Given a dictionary, design an algorithm to find the optimal way of unconcatenating a sequence of words. 
        In this case, the optimal is defined to be the parsing which minimizes the number of unrecognized sequences of characters.
        For example "jesslookedjustliketimherbrother", parsed as "JESS looked just like TIM her brother", JESS and TIM is unrecognized sequences marked as CAPITAL.
    35) Given a list of words, write a program to find the longest word made of other words in the list.
    36) A message containing letters from A-Z is being encoded to numbers using the following mapping:
        'A' -> 1    'B' -> 2    ... 'Z' -> 26
        Given an encoded message containing digits, determine the total number of ways to decode it.
        For example,    Given encoded message "12", it could be decoded as "AB" (1 2) or "L" (12). The number of ways decoding "12" is 2.
    37) Palindrome Partition [LeetCode]
    37A) Given a string s, partition s such that every substring of the partition is a palindrome.
         Return all possible palindrome partitioning of s.
         For example, given s = "aab",
         Return [ ["aa","b"], ["a","a","b"] ]
    37B) Given a string s, partition s such that every substring of the partition is a palindrome.
         Return the minimum cuts needed for a palindrome partitioning of s.
         For example, given s = "aab", return 1 since the palindrome partitioning ["aa","b"] could be produced using 1 cut.
         
##C12: Dynamic Programming
    1) Boolean Knapsack, Complete Knapsack
    2) Given a list of N coins, their values (V1, V2, ... , VN), and the total sum S.
        2.1) Find the minimum number of coins the sum of which is S
        2.2) Find the required number of each coin, which is the minimum number of coins required to get the sum S.
    3) [top coder] Given a sequence of N numbers - A[1] , A[2] , ..., A[N] .
        Find the length of the longest non-decreasing sequence.
        For example, "5, 3, 4, 8, 6, 7" yields 4 with the sequence "3, 4, 6, 7"
    4) [top coder] Given an undirected graph G having N (1<N<=1000) vertices and positive weights.
    Find the shortest path from vertex 1 to vertex N, or state that such path doesn't exist.          Same Problem with C6_5
    5) [top coder] ZigZag problem: http://community.topcoder.com/stat?c=problem_statement&pm=1259&rd=4493
    6) [top coder] Bad Neighbors: http://community.topcoder.com/stat?c=problem_statement&pm=2402&rd=5009
    7) [top coder] Flower Garden : http://community.topcoder.com/stat?c=problem_statement&pm=1918&rd=5006
    8) [top coder] Apple Collection: A table composed of N x M cells, each having a certain quantity of apples, is given.
        You start from the upper-left corner. At each step you can go down or right one cell.
        Find the maximum number of apples you can collect.
    9) [top coder] AvoidRoads: http://community.topcoder.com/stat?c=problem_statement&pm=1889&rd=4709
    10) [top coder] ChessMetric: http://community.topcoder.com/stat?c=problem_statement&pm=1592&rd=4482
    11) Given an undirected graph G having positive weights and N vertices.
        You start with having a sum of M money. For passing through a vertex i, you must pay S[i] money.
        If you don't have enough money - you can't pass through that vertex. Find the shortest path from vertex 1 to vertex N,
        respecting the above conditions; or state that such path doesn't exist. If there exist more than one path having the same length,
        then output the cheapest one. Restrictions: 1<N<=100 ; 0<=M<=100 ; for each i, 0<=S[i]<=100.
    12-N) [top coder] Jewelry: http://community.topcoder.com/stat?c=problem_statement&pm=1166&rd=4705
    13-N) [top coder] StripePainter: http://community.topcoder.com/stat?c=problem_statement&pm=1215&rd=4555
    14) [top coder] QuickSums: http://community.topcoder.com/stat?c=problem_statement&pm=2829&rd=5072
    15-N) [top coder] ShortPalindromes: http://community.topcoder.com/stat?c=problem_statement&pm=1861&rd=4630
    16-N) [top coder] StarAdventure: http://community.topcoder.com/stat?c=problem_statement&pm=2940&rd=5854
    17-N) [top coder] MiniPaint: http://community.topcoder.com/stat?c=problem_statement&pm=1996&rd=4710
    18) [Introduction to Algorithm Chp.15] The assembly line problem
    19) Longest Common Subsequence and Longest Common Substring, see C11_11_LongestCommonSubsequence and C11_12_LongestCommonSubstring
    20) find sub array which sum closest to K. see C12_27
    21）N factories in one road, the distance between each of them to the west end of the road is D[N]. 
        Need pick M factories as supplier, to make the sum distance between the other factories to these M factories shortest.
    22) [Introduction to Algorithm Chp.15] The matrix multiply
        There is M matrix, A1 A2 .. AM, write code to find the smallest cost ways to make these M matrix could multiply. 
        (A1 * (A2 * A3)) or ((A1 * A2) * A3) give the same answer, but may cause different of computing effect when A1 is a very small matrix
        And A1*A1 could multiply when dimensionality is the same, so d[N] save the dimensions, Ai's dimension is di-1 and di
    23) Optimized Binary Search Tree. Assume every key in the search tree have specific search probability, and also for the keys range not in the tree.
        keys ki probability pi, and not-existing-key-range di probability qi, write code to create a binary search tree to 
        achieve the smallest the expectation of searching behavior.
    24）Editing Distance: given two string A and B, could do the following actions to A to make it equals B:
        Add a char, Delete a char, Modify a char. 
        Write code to find the minimal actions needed to make A equals B, as the Editing Distance of A and B.
    25) Viterbi Algorithms: the decode of HMM.
    26) Task Dispatch: There is N tasks, each one need time ti to complete and get value as pi, and need complete before di (deadline) to get the value.
        Given N tasks, write code to find a dispatch to achieve maximal total value.
    27) Given an int array with all positive numbers, find the sub arrays whose sum is equals or closest smaller to a given K
    28) K－important strings, see C11_29
    29）Having a space with N * M unit, giving 1*2 blocks, write code to find how many filling solution using blocks to cover the N * M space.
     
        
##13: Greedy Algorithm
    1) Task Selection: There is N task, each one need use a un-shared resource. Given the start time and end time of each task, it will use 
       the resource in duration of [start-time, end-time), write code to select the maximal set of un-conflict tasks.
       Assume the input data about the tasks are sorted by end-time
    2) Huffman Encoding: Given N char with specific usage frequency, using Huffman encoding policy to make the encoding string with minimal length.
    3) Minimal Span Tree: Given a graph, could create a span tree from one node, which contains all the nodes but no cycle. 
       The classical solution is Prim and Kruskal algorithm C6_3_PrimMSTSolver and C6_3_KruskalMSTSolver
        Prim is starting with vertex 0 and greedily grow tree T, select the min weight edge with exactly one endpoint in T, repeat until V - 1 edges.
        Kruskal is starting with sort the edges, add smaller weight edge to tree unless doing so would create a cycle. Time: O(E log E)
    4) Shortest path in Positive-weighted graph: Dijkstra.
        

##C14: Path Finding
    To solve a problem when there is no clear algorithm for computing a valid solution, we turn to path finding. In this chapter we will
    cover two related path-finding approaches, one for game trees and the other for search trees. These approaches rely on a common structure,
    namely a state tree where the root node represents the initial state and edges represent potential moves that transform the state into a new state.

    1) Game Tree
        Two players take alternating turns in making moves that modify the game state from its initial state. There are potentially many states in
        which either player can win the game. There also may be some states that are “draws,” in which case no one wins. A path-finding algorithm
        maximizes the chances that a player will win the game (or force a draw).
    2) Search Tree
        A single agent is given a task to accomplish, starting from an initial board state, with a series of allowed move types. In most cases,
        there is exactly one goal state that is desired. A path-finding algorithm identifies the exact sequence of moves that will transform the
        initial state into the goal state.
        
##C15: Network Flow
    Numerous problems that can be viewed as a network of vertices and edges, with a capacity associated with each edge over which commodities flow.
    1) Assignment
        Given a set of tasks to be carried out by a set of employees, find an assignment that minimizes the overall expense when different employees
        may cost different amounts based upon the task to which they are assigned.
    2) Bipartite Matching
        Given a set of applicants who have been interviewed for a set of job openings, find a matching that maximizes the number of applicants
        selected for jobs for which they are qualified.
    3) Transportation
        Determine the most cost-effective way to ship goods from a set of supplying factories to a set of retail stores selling these goods.
    4) Transshipment
        Determine the most cost-effective way to ship goods from a set of supplying factories to a set of retail stores selling these goods,
        while potentially using a set of warehouses as intermediate stations.
    5) Maximum Flow
        Given a network that shows the potential capacity over which goods can be shipped between two locations, compute the maximum flow supported
        by the network.

##C16: Bit Manipulation
    1) You are given two 32-bit numbers, N and M, and two bit position, i and j. Write a method to insert M into N such that M starts at bit j and ends at bit i.
       You can assume that the bits j through i have enough space to fit all of M.
       Such as N = 1000000000, M = 10011, i = 2, j = 6  output N = 1001001100
    2) Given a real number between 0 and 1, that is passed in using a double, write code to print the binary representation. 
       Assume the binary form have 32 bit, overflow one return "OVERFLOW"
    3) Given an integer, print the next smallest and next largest number that have the same number of 1 bits in their binary representation
    4) Given an integer, write code get the integer which binary format is the reverse of the given integer. 
    
##C17: Backtracking
    1) N-Queen

##C18: Geometry
    1) Given a line AB and a dot C, calculate the distance from C to line AB. Line AB is identified by the two dots A and B.
    2) Given a polygon formed by a list of given points, calculate the area of the polygon.
    3) Line Intersection. Each line is determined by two points.
    4) Given 3 points which are not colinear (all on the same line) those three points uniquely define a circle, find the center of the circle
    5) Given a test point, (testPointX, testPointY), and the vertices of a simple polygon, vertices, determine if the test point is in the interior, in the exterior or on the boundary of the polygon. Return the String "INTERIOR", "EXTERIOR", or "BOUNDARY".






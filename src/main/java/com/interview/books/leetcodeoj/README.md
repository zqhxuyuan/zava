#Question and Answer of LeetCode Online Judge

1.  Two Sum. *HashMap*
    - index start from 1
    - new int[0] and new int[]{.....}
    - map.containsKey(key); 
2.  Add Two Number (List) *Dummy node, Carry*
    - move prev, l1, l2 one step forward
3.  Longest Substring without Duplicate Chars. *Index Array[256], update start with find duplication.*
    - fill indexes with -1, Arrays.fill(indexes, -1);
    - no dup when indexes[s.charAt(i)] < start
    - update maxLen every time if no dup: maxLen = Math.max(maxLen, i - start + 1);
4.  **Median Of Two Sorted Array.**
   *find the k-th elements in A+B. do binary search on shorter array to find the first element not in topK.*
    - do binary search on shorter array
    - K is started from 1, so findKthElement(A, B, median + 1);
    - binary search: int high = A.length; and if(bIdx >= B.length || A[mid] < B[bIdx]) low = mid + 1;
    - check index out of range when get prevA and prevB
5.  **Longest Palindrome Substring** *Scan by center for both even and odd case.*
    - for each position: find the palindrome which center is this char(odd) or this char and it's next char.(even)
    - position loop 0 ~ length - 1;
    - do while loop when charAt(i - len) == charAt(i + len), and calculate len when break using (len - 1);
    - remember to check index out of range before call charAt(i);
6.  ZigZag Conversion *StringBuffer*
    - use n StringBuffer to store data in each row.
    - init StringBuffer array
    - careful about row increase and decrease, and when to set down
7.  Reverse Integer
    - UPPER_BOUND = Integer.MAX_VALUE/10;
    - handle negative case using flag and x = Math.abs(x);
    - clarify return what when the number overflow.
8.  **String to Integer.** *try to parse the str and build an integer*
    - remember to handle negative case: parse sign
    - use method in Character to make clean code: Character.isWhitespace(), Character.isDigit(),Character.getNumericValue();
    - remember to check overflow, num == max && digit >= 8. (INT_MAX (2147483647) or INT_MIN (-2147483648))
    - return MAX_VALUE or MIN_VALUE based on sign: sign == 1? Integer.MAX_VALUE : Integer.MIN_VALUE; 
9.  Palindrome Number. *reverse and check equals.*
    - check overflow when reverse
    - clarify return what for negative: just false;
10. **RegularExpressionMatching.** *Two Sequence DP*
    - '.' can matching any char in matchChar(i, j)
    
        state: match[i][j]: if s.substring(0,i) can matchChar p.substring(0,j);
        init: match[0][0] = true, if(p.charAt(j - 1) == '*') match[0][j] = match[0][j-2]
        function: match[i][j] = true if
              1. match[i-1][j-1] && matchChar(i, j)
              2. if(p.charAt(j - 1) == '*'
                2.1 match[i][j-2]    //""  matching "a*"
                2.2 match[i][j-1]    //"a" matching "a*"
                2.3 match[i-1][j]    //"aa...a" matching "a*"
                    the third case: match[i-1][j] not match[i-1][j-1], need include * in the matched p
        result: match[s.length()][p.length()]
        matchChar(i, j): means (p.charAt(j - 1) == '.' or s.charAt(i - 1) == p.charAt(j - 1)
        
11. Container with Most Water. *Two Pointer: Left and Right*
    - area = Math.min(height[i], height[j]) * (j - i);
    - width = (j-i) not (j-i+1)
12. Integer to Rome. *From High to Low.*
13. Rome to Integer. *The relation with next bit.*
14. Longest Common Prefix. 
    - use strs[0] as pivot and remember to check offset >= strs[i].length() for other str.
15. Three Sum. *Sort and Two Pointer.*
    - i, j, k is the offset not element;
    - inner while
    - de dup using while
16. Three Sum Closest. *Sort and Two Pointer.*
    - tracking closest and return closest + target
    - i, j, k is offset, not elements.
17. Letter Combinations of a Phone Number. *Permutation*
    - use String to hold all the options
    - init global class variable carefully
    - for recursive permutation generation, be carefully about when to return, if(offset >= digits.length())
18. Four Sum. *Sort and Two Pointer*
    - i, j, k is all offset, not element
    - de dup;
19. Remove N-th Node from End of the List. *Two Pointer: Slow and Fast*
    - use slow and fast pointer to scan the list, fast is n + 1 steps forward, 
    - delete the next node of slow
    - be careful about n > len(list). if(fast == null && n > 0) return dummy.next;
    - use sample to verify the condition
20. Valid Parentheses. *Stack and HashMap*
    - return stack.isEmpty() when parse over the string.
21. Merge Two Sorted Lists *DummyNode*
22. Generate Parentheses. *Permutation, tracing retain left and right, right > left when add right.*
23. Merge K Sorted List. *Merge every two list*
    
        Time Complexity: O(NM * lgN)
        assume N list, each have M number, 
        1st round, merge every 2 list: 2 * M,  N/2 operation = N * M
        2nd round, merge every 2 list: 2 * 2M, N/4 operation = N * M
        total lgN round, so time complexity is O(NM * lgN), same as using Min Heap, 
        Space complexity: O(1)
        
24. Swap Nodes in Pair. *DummyHead*
    - be careful on the pointer assignment
25. Reverse Nodes in K Group. *DummyHead, Reverse List with three pointer.*
    - remember to set dummy.next = head, when create a dummyHead
    - count start from 1 and count < k
    - when tail != null do the reverse, and pre = cur, tail = pre.next, count = 1;
26. Remove Duplicates from Sorted Array *Two Pointer*
27. Remove Element *Two Pointer*
28. **Substring Matcher**
    - Naive solution: for every position of str, try to check if it can match pattern. O(N^2).
    - KMP: optimize by minimize the backtracing in str, str only go forward, and pattern backtracing to least using next[].
        - next[] is calculate based on the longest suffix equals to prefix.
        - basic process: match str.charAt(i) and pattern.charAt(j), 
            - if match both move towards, and check if already visit to the end of pattern
            - if not match, if j == 0, just need move i forward, if j != 0, move j to next[j]
        - calNext: init next[0] = next[1] = 0;
            - for other i, init j = next[i-1], and match pattern.charAt(j) and pattern.charAt(i-1)
            - if match, next[i] = j + 1;
            - if not match, if j == 0, next[i] = 0; if j != 0, move j to next[j];
    - callNext() as the same process of strStr(); and next[0] = -1;
29. Divide Two Integer. *Minus*
    - use minus to do division, check how many divisor, given dividend can minus.
    - optimized solution is: try to minus most 2^i divisor (can be calculated by left shift).
    - clarify the edge case handling
    - carefully about negative dividend and divisor
    - use long to do the shift calculation to avoid overflow.
    - while condition a >= b
    - shift start from 0
    - update: a -= b << (shift - 1); and answer += (1 << (shift - 1)); 
30. Substring with Concatenation of All Words
    - expected and found using HashMap.
31. Next Permutation.
    - find the first element not in non_decreasing order backwards, then find the min element in left larger than current.
    - check non_decreasing order: while(offset >= 0 && num[offset] >= num[offset + 1]) offset--;
    - find min element as the replaced num: while(replaceIdx >= 0 && num[replaceIdx] <= num[offset]) replaceIdx--;
32. Longest Valid Parentheses *DP*
    - len[i] is the longest valid parentheses end with (i-1)-th char, so s.charAt(i-1)
    
        State: len[i] the longest valid parentheses end with (i-1)-th char
        Init: len[0] = 0
        Transfer: if s.charAt(i-1) == '('   len[i] = 0
                  if s.charAt(i-1) == ')' && i - len[i-1] - 2 >= 0 && S.charAt(i-len[i-1] - 2) == ‘('
                         len[i] = len[i-1] + 2 + len[i-len[i-1] - 2]
        Answer: max of len[*]
        
33. Search in Rotated Array. *Binary Search*
    - low, mid, high is offset, not elements
    - if(A[low] <= A[mid] && target < A[low])
    - if(A[high] >= A[mid] && target > A[high])
34. Search for A Range. *Binary Search*
    - search first and last, high = mid and low = mid + 1;
    - first: return A[low] == target? low : -1; 
    - last:  return A[high] == target? high : high - 1;
35. Search Insertion Position. *Binary Search*
    - low < high
    - if(A[mid] >= target) high = mid; else low = mid + 1;
    - return (A[low] >= target)? low : low + 1;
36. Valid Sudoku. 
    - check each row, col, and cube
    - be careful about the index, row/col/i/j
37. Sodoku Solver *Backtracing*
    - backtracing: find a placable char ['0' - '9'], if find, call solve() for next position, if can find, return false.
    - char k is between '1' to '9'
    - do loop on each position (i,j) to find a position is '.'
38. Count And Say
    - try to generate the sequence one step by one step
    - n--;
    - char curr = base.charAt(0), not int
39. Combination Sum I and Combination Sum II *Backtracing Combination*
    - find all combination, and tracking the sum of combination.
    - sort num to de-dup
    - when sum + num[offset] == K, also need remove new-added element in cur.
    - when could reuse, do(offset, cur, sum + num[offset]) and remove new-added element then (offset + 1, cur, sum).
    - when not reuse, do (offset + 1, cur, sum + num[offset]) and remove new-added element then (offset + 1, cur, sum)
    - de dup by while(offset + 1 < num.length && num[offset + 1] == num[offset]) offset++;
40. Combination Sum II *same as 39* 
41. First Missing Positive
    - put element in position it should be, 1 to A[0], 2 to A[1], then find the first element doesn't exist.
    - during scan, if swap, need i--;
    - position != i && A[position] != A[i], then swap
42. Trapping Rain Water *Forward/Backward, Max Index*
    - find the max index, and scan from left and right to center, tracking blocks and increasing total
    - when A[i] < A[left] or A[i] < A[right], blocks += A[i];
    - when find another boundry, total += A[left] * (i - left - 1) - blocks; remember to set blocks = 0;
43. **MultiplyString**
    - loop for the lowest digit in num1 and num2, and tracking carry
    - int[] num3 = new int[num1.length() + num2.length()];
    - init carry = 0 in every loop of i
    - after loop of j put carry to num3[i]; // num3[i + j + 1] where j is -1;
    - num3 is stored from lowest to highest digit, so need visit reversely when put in StringBuffer.
    - offset from 0 and offset < num1.length() + num2.length() - 1, omit highest 0
44. **Wildcard Matching** *Backtracing with last position of star*
    - keeping the last position of star and matched position in str for backtracing
    - remember to check p < pattern.length()
    - when pattern.charAt(p) == '*', starIdx = p, matched = s, p++;
    - when not match and not '*', and starIdx != -1, p = starIdx + 1, s = ++matched;
    - remember to go through the end '*' and return p == pattern.length();
45. **Jump Game II** *DP*
    - scan from begin to last, find the min step from the first element to i-th element
    - only scan the point is reachable from the first element
    - if j, k position both can reach i, step[j] < step[k], so scan from left to right, when found 1st valid break
    - use a maxJump to control the beginning of scan, int begin = i - maxStep < 0? 0 : i - maxStep; since element before begin can't directly jump to i.
46. Permutation *Backtracing*
    - try to put every element in List and use a boolean[] to avoid duplication
    - to de dup: check if previous element with same value all used, if not have duplication
47. Permutation II *Backtracing as 46*
48. Rotate Image 
    - use layer to visit matrix layer by layer
    - for(int i = 0; i < last - layer; i++), rotate copy 
        matrix[layer][layer + i] <- matrix[last - i][layer] <- matrix[last][last - i] <- matrix[layer + i][last];
        
        matrix is 
            (layer, layer) .....    (layer, last)
                   ...                  ...
            (last, layer)  .....    (last, last)
            
49. Anagrams *HashMap*
50. Pow(X, N) *double everytime*
    - basic method to do n times multiply x, a better way to do in 2's exponent, every time double the result.
    - consider n to be positive or negative, the base case will be n == 0/1/-1
    - consider n to be even or odd, do p1 = pow(x, n/2) and p2 = pow(x, n - 2*(n/2));
    - if n is even, p2 == 1, n is odd, p2 = -1/1 based on n's flag.
    - return p1 * p1 * p2
51. N-Queens *Backtracing / Permutation*
    - check if current position can place: two queens can't put in diagonal line：(Math.abs(queens[i] - position) == offset - i)
    - fill queens with -1 for initialize
52. **N-Queens II** *Backtracing using loop*
    - scan from offset 0, and queens[offset] = -1;
    - while(offset >= 0 && offset < n) do searching by queens[offset]++;
    - try next position when can't fit: while(queens[offset] < n && !canPlace(offset, queens)) queens[offset]++;
    - if(queens[offset] == n) can't find a solution, offset--; backtrace
    - else if already in last queens, mark the solution, if not find the next queens by offset++, queens[offset] = -1;   
53. Maximum Subarray *DP, Statistical*
    - scan and tracking sum and max, if sum < 0, reset to 0.
    - if max == 0, return the largest elements in A.
54. **Spiral Matrix**
    - visit by layer: top, right, bottom and left.
    - int board = Math.min(rows, cols) + 1;
    - if rows != cols, layer should loop from [0 to board/2 - 1]
    - when layer == board/2 - 1 and Math.min(rows, cols) % 2 == 1, do loop the last two round on bottom and left.
55. Jump Game
    - scan from A.length - 1 to 0, and tracking the lowest place can jump to the end
    - if A[i] + i >= lowest, position i can jump to end, otherwise can't. lowest should update when find a lower position.     
56. Merge Interval *Sort by start*
57. Insert Interval *Iterator*
    - mark overlapped interval to newInterval, and remove overlapped one in intervals.
    - use Iterator to enable remove during scan
    - check index == intervals.size(), in this case intervals.add(newInterval), other case intervals.add(index, newInterval);
58. Length of Last Word. *String parsing*
    - string.length()
    - be careful of index begin and end.
59. Spiral Matrix II. 
    - use layer, loop from [0 ~ (n + 1)/2];
    - when n is odd, omit the bottom and left loop on last round
60. **Permutation Sequence** *Math/Factor*
    - permutation with n digits will have n! elements, based on this rule find K-th element
    - calculate factors from [0, n], factors[0] = 1;
    - for initialize: k-- and k = k % factors[n];
    - for every offset: k -= cur * factors[n - 1] and n--;
61. Rotate List *Fast/Slow Pointer*
    - get length and normalize n = n % length;
    - when(n > 0) n-- and fast = fast.next;
    - then fast and slow go together when fast.next != null;
62. Unique Path I *Matrix DP*
63. Unique Path II *Matrix DP*
64. Minimum Path Sum *Matrix DP*
65. **Valid Number** *String Parsing, Clean Code*
    - clarify what is valid and what is invalid.
        - the whitespace at begin and end is valid, such as "  34   "
        - positive and negative flag is valid, such as "-1" or "+1"
        - number could be double, such as "1.234"
        - number could contains E exponent, such as "1e30" or "1e-30", but can't be "1e3.2"
    - parse the string by rules, and check if can parse to the end and contains a valid number
        - parse begin ' '
        - parse positive or negative flag
        - parse digit numbers  (isNumber = true)
        - parse '.' than parse digit numbers (isNumber = true)
        - parse 'e' (isNumber = false)
        - parse positive or negative flag
        - parse digit numbers  (isNumber = true)
        - parse end ' '
        - check if isNumber == true && offset == n
66. Plus One *carry*
67. Add Binary *Bit Manipulation*
68. **Text Justification** *String, Clean Code*
    - trick is do clear calculation on spaceNeeded, spaceCount, spaceSlot, spaceEach, spaceExtra
        - spaceNeeded = curLen + words[i].length() + (i - begin)
        - spaceCount = L - curLen
        - spaceSlot = i - begin - 1; 
        - spaceEach = spaceCount/spaceSlot, 
        - spaceExtra = spaceCount%spaceSlot
            - (spaceEach + (j - begin < spaceExtra? 1 : 0))
    - also be careful about the edge case of i and j
        - i == words.length: last line
        - j != i - 1: not last word
    - scan word one by one, and tracking begin and curLen of words
    - if last line or space_needed > L, create a string, else curLen += words[i].length();
    - when create line, if spaceSlot == 0 or it's last line, 
        - put 1 space between the words
        - L - buffer.length() space at the end
    - else put (spaceEach + (j - begin < spaceExtra? 1 : 0)) space between each word.
    - set begin = i and curLen = i < words.length? words[i].length() : 0;
69. Sqrt *Math, BinarySearch*
    - do binary search on range[0, x], using long to avoid overflow of mid * mid
    - if can't find a sqrt, check the low * low <= x? return low otherwise return low - 1;
    - be careful of the change of int and long
70. Climbing Stairs *DP*
    - optimize space complexity to constant space by mod. ways[i%3] = ways[(i - 2)%3] + ways[(i - 1)%3];
71. Simplify Path *String, Two Pointer*
    - edge cases: null, "/", "/.",
    - three different cases: ".", "..", "a"
    - string equals uisng .equals() not ==
    - when offset == 0, don't do offset-- when steps[i].equals("..");
    - return "/" when offset == 0
72. Edit Distance *Two Sequence DP*
    
        state: distance[i][j]: the min edit distance of a.substring(0, i) and b.substring(0, j);
        initialize: distance[i][0] = i and distance[0][j] = j
        function: distance[i][j] = distance[i-1][j-1] if a.charAt(i - 1) == b.charAt(j - 1)
                  distance[i][j] = min(distance[i-1][j-1], distance[i-1][j], distance[i][j-1]) + 1, 
                                                      if a.charAt(i - 1) != b.charAt(j - 1)
        result: distance[a.length][b.length];
73. Set Matrix Zeros *Scan and Clean*
    - use the row and col of first zero to store the mark.
    - check if row == -1 after a full scan, directly return
    - check i != row and j != col when do reset in second stage.
74. Search in 2D Matrix *BinarySearch*
    - do binary searching in range [0, rows * cols - 1];
    - while(low <= high) do search
    - convert mid into (row, col) and check the value match. row = mid / cols, and col = min % cols.
75. Sort Color *Three Way Quick Partition*
    - keep two pointer: small(before element are smaller than key), equal(between small and equal are equals to key)
    - scan the array
        - if A[j] == key, swap(A, ++equals, j)
        - if A[j] < key, swap(A, ++small, j) and equal++, then check if(A[equal] > A[j]) swap(A, equal, j);
76. Minimum Window Substring *String Mark*
    - use two int[256] as expected and found to scan T and S
    - once found all the chars in T, shrink begin to get minimum window
    - while(begin < S.length()) when shrink begin
    - update window: if(window == "" || i - begin + 1 < window.length()) window = S.substring(begin, i + 1);
77. Combination *Backtracing*
    - a solution is cur.size() == K
78. Subset *Backtracing*
    - de dup by sort(S) and while(offset < S.length - 1 && S[offset + 1] == S[offset]) offset++;
79. Word Search
    - in dfs, if(word.length() == 1) return true;
    - in dfs, visited[row][col] = true and String suffix = word.substring(1);
    - remember to mark visited[row][col] = false when return false;
80. Remove Duplicated from Sorted Array II *Two Pointer*
    - tracking occurrence, if A[i] == A[i-1] && occurrence == 2, just continue
    - if A[i] != A[i-1] then occurrence = 1; else occurrence++, and copy A[i] to A[offset++];
81. Search in Rotated Sorted Array II *Binary Search*
    - de-dup while(low < high && A[low] == A[high]) high--;
82. Remove Duplicates from Sorted List II *Three Pointer*
    - use three pointer: prev, front and back.
    - while(back != null && back.val == front.val) back = back.next;
    - if(front.next == back) prev.next = front; prev = prev.next;
    - set prev.next = null at the end.
83. Remove Duplicates from Sorted List I *Two Pointer*
    - prev = prev.next;
84. **Largest Rectangle In Histogram** *Stack*
    - while(!stack.isEmpty() && (i == height.length || height[i] < height[stack.peek()])) pop and calculate
    - offset = stack.pop(); and int width = stack.isEmpty()? i : i - stack.peek() - 1; area = width * height[offset]
    - remember to push i in stack
    
        the area of [i,j] is the min(A[i]...A[j]) * (j - i + 1); Optimize get min(A[i]...A[j]) use Stack
        put index in Stack to calculate (j - i + 1), and put element in Stack in increasing sequence
        the increasing sequence make sure: height = height[offset], width = i - stack.peek() - 1
        when found a element not in increasing sequence, pop all the element in stack to keep increasing sequence.
85. **Largest Rectangle** *DP, Stack*
    - use largestRectangleArea() method, loop every row to calculate histogram
    - matrix is char[][], so need check matrix[i][j] == '0'
86. Partition List
    - keep smallHead, largeHead, and small, large
    - large.next = null and small.next = largeHead.next;
87. **Scramble String** *DP*
    - String DP: Three dimensional DP matrix, scramble[len][i][j], and loop len, i, j
    
        state: scramble[len][i][j], whether substring of length len start from i in s1 and j in s2 are scramble.
        initialize: scramble[1][i][j] = true, if s1.charAt(i) == s2.charAt(j)
        function: scramble[len][i][j] = true for any cutting point k from 1 to len - 1 meeting one of the following conditions:
                  1) scramble[k][i][j] and scramble[len-k][i+k][j+k]
                  2) scramble[k][i][j+len-k] and scramble[len-k][i+k][j]
        result: scramble[n][0][0]   
88. Merge Sorted List *Two Pointer, Scan Backward*
    - scan backward
89. Grey Code
    - grey code is i ^ (i >> 1)
90. Subset II *Backtracing, Dedup by sort*
    - de dup by sort(S) and while(offset < S.length - 1 && S[offset + 1] == S[offset]) offset++;
91. Decode Ways *DP*
    - edge case: s.charAt(0) == '0' return 0;
    - invalid case: "0", "01", "30"
    - valid case: 
        - "10/20":  ways[i] = ways[i-2]
        - "27/201": ways[i] = ways[i-1]
        - others:   ways[i] = ways[i-1] + ways[i-2]
        
        state: ways[i]: is the decode ways of s.substring(0, i);
        initialize: ways[0] = 1, ways[1] = 1;
        function: cur = s.charAt(i - 1), pre = s.charAt(i - 2)
                  if(cur == '0') 
                      if(pre == '0' || pre > '2') return 0;
                      else ways[i] = ways[i-2];
                  else num = (pre - '0') * 10 + (cur - '0');
                      if(num < 10 || num > 26) ways[i] = ways[i - 1];
                      else ways[i] = ways[i-1] + ways[i-2];
        result: ways[s.length]
92. Reverse Linked List II *DummyNode*
    - find the prev, and tail based on m and n
    - reverse nodes between prev.next and tail
93. **Restore IP Address** *Backtracing, Permutation*
    - based on permutation
    - offset == chars.length && count == 0, prefix is a valid solution
    - calculate available char, if available < count || available > count * 3, it's not a valid solution
    - permutate on different solution: of 1 ~ 3 chars, and offset + i <= chars.length on loop condition
    - invalid option: option.length() > 1 && option.charAt(0) == '0' and Integer.parseInt(option) > 255
94. **Binary Tree In-order Traversal** *Stack*
    - use Stack to push node.left;
    - the while loop condition: (root != null || !stack.isEmpty())
    - root = stack.pop(), nodes.add(root.val), root = root.right;
95. **Unique Binary Search Trees II** *Permutation*
     - BST: the left subtree is smaller than root, and the right subtree is larger than root
     - use low and high to do permutation
96. Unique Binary Search Trees *Backtracing, 
    - catalan sequence
    - for every left = 0..left - 1; nums[total] += num[left] * num[total - 1 - left];
97. Interleaving String *DP*
    - edge case: if(s1.length() + s2.length() != s3.length()) return false;

        state: interleaving[i][j]: if s3.substring(0, i+j) is interleaving string of s1.substring(0, i) and s2.substring(0, j).
        initialize:   interleaving[i][0] == true when s3.charAt(i - 1) == s1.charAt(i - 1)
                      interleaving[0][j] == true when s3.charAt(j - 1) == s2.charAt(j - 1)
        function:     interleaving[i][j] == true when 
                          s3.charAt(i + j - 1) == s1.charAt(i - 1) && interleaving[i-1][j]
                          s3.charAt(i + j - 1) == s2.charAt(j - 1) && interleaving[i][j-1]
        result:       interleaving[s1.length()][s2.length()]
98. Validate Binary Search Tree *In-order Traverse*
99. Recover Binary Search Tree *In-order Traverse*
    - find the breakpoint during in-order traverse by checking last visited node
    - swap the values of breakpoint node
100. Same Tree *Pre-order Traverse*
     - check root, then check left-subtree and right-subtree
101. Symmetric Tree *Pre-order Traverse*
     - check left child and right child, than check left.left = right.right and left.right = right.left
102. Binary Tree Level Order Traverse *Queue*
     - use Queue and loop on queue.size() for every level
103. Binary Tree ZigZag Level Order Traverse *Queue*
     - use Queue and tracking isEven level.
104. Maximum Depth of Binary Tree *Post-order Traverse*
105. Construct Binary Tree from Preorder and Inorder Traversal *Divide and Conquer*
     - use preorder[offset] to divide inorder into left and right part as left/right subtree.
     - offset should be class attribute to enable offset++.
     - build left subtree before right subtree
106. Construct Binary Tree from Postorder and Inorder Traversal *Divide and Conquer*
     - offset initialize as inorder.length - 1, and offset--
     - during buildTree, build right(position + 1, high) before build left(low, position - 1)
107. Binary Tree Level Order Traversal II *Level-order traversal*
108. Convert Sorted Array to Binary Search Tree *Divide and Conquer*
     - find the mid to create node, node.left = (low, mid - 1) and node.right = (mid + 1, high)
109. **Convert Sorted List to Binary Search Tree** *In-order Traversal, Length*
     - base in-order traversal to build a tree.
     - use current to tracking visited node in list, and length to tracking when to return.
     - left part is length/2, the right part is length - 1 - length/2;
110. Balanced Binary Tree *Post-order Traverse*
     - if balanced return height, if not return -1.
     - check left subtree and right subtree, then check node itself.
111. Minimum Depth of Binary Tree *Level order traverse*
     - could using post-order traverse, but level-order visiting smaller subset of nodes.
     - init depth = 0; and return depth + 1 if node.left == node.right == null
     - remember depth++ in every loop.
112. Path Sum *Pre-order traversal*
     - root-to-leaf path, so need check sum == root.val && root.left == root.right == null.
     - if not get sum, do hasPathSum on root.left and root.right with sum - root.val
113. Path Sum II *Backtracing and Pre-order traversal*
     - backtracing based on pre-order traversal.
     - solution is node.val == sum && node.left == node.right == null
114. Flatten Binary Tree to Linked List *Pre-order Traversal*
     - based on pre-order traversal, and backup left and right child
115. Distinct Subsequences *DP*
     
        state: count[i][j]: is the distinct subsequence count of S.substring(0, i) and T.substring(0, j)
        initialize: count[0][j] = 0 and count[i][0] = 1
        function: if S.charAt(i - 1) != T.charAt(j - 1) count[i][j] = count[i-1][j] 
                  if S.charAt(i - 1) == T.charAt(j - 1) count[i][j] = count[i-1][j] + count[i-1][j-1]
        result: count[S.length()][T.length()]
116. Populating Next Right Pointers in Each Node *Pre-order and Post-order*
     - Pre-order: connect left and right child of it's self, than connect(node.left) and connect(node.right), 
     - Post-order: fill the middle gap.
     
        while(root.right != null && root.next != null) {
            root.right.next = root.next.left; 
            root = root.right
        }
117. Populating Next Right Pointers in Each Node II *Level-order traversal*
     - use Queue do level order traversal, and assign the next pointer.
118. Pascals Triangle  *Top-down generation*
     - keep prev list and generate current list: 0, i-1 + i, size() - 1;
     - assign current to prev, numRows--;
119. Pascals Triangle II  *Top-down generation*
     - copy the last element in new row, and scan backward: row.set(i, row.get(i) + row.get(i-1));
     - remember rowIndex--;
120. Triangle *DP: top-down*
        
        state: path[i] is the path from root to i-th element in current layer
        initialize: path[0] = triangle.get(0).get(0);
        function: loop layer from 1 to triangle.size() - 1, current = triangle.get(i)
              for j from current.size() - 1 to 0:
                  j == current.size() - 1 path[j] = path[j-1] + current.get(i);
                  j == 0                  path[j] = path[0] + current.get(i);
                  other                   path[j] = Math.min(path[j], path[j-1]) + current.get(j);
        result: min value in path[*]
121. Best Time to Buy and Sell Stock  *DP*
     - scan forward, and tracking min price, the max profit is (prices[i] - min)
     - edge case: if(prices.length <= 1) return 0;
122. **Best Time to Buy and Sell Stock II** *Special Point Searching*
     - find the buy point and sell point
        - buy point is i:  prices[i] <= prices[i-1] && prices[i] < prices[i+1];
        - sell point is i: prices[i] >= prices[i-1] && prices[i] > prices[i+1];
        - profit += prices[sell] - prices[buy];
     - treat i == 0 and i == prices.length - 1 separately
        - if(prices[0] < prices[1]) buy = 0;
        - if(buy != -1) profit += prices[prices.length - 1] - prices[buy];
123. Best Time to Buy and Sell Stock III *DP, forward and backward scan*
     - do two DP process
        - scan forward with tracking min, left[i] = maxProfit can get make one transaction in [0, i]
        - scan backward with tracking max, right[i] = maxProfit can get make one transaction in [i, prices.length - 1]
     - result is max(left[i] + right[i])
124. Binary Tree Maximum Path Sum *Post-order Traversal*
     - do pre-order traversal, maxPath = left + right + node.val;
     - return max singlePath: max(max(left, right) + node.val, 0), remember to set singlePath to 0 when it < 0
125. Valid Palindrome *String*
     - use while(front < back && !validChar(s.charAt(front))) front++; to omit the invalid char
     - remember to check after two while: if(front >= back) return true;
126. **Word Ladder II**  *Level-order traversal, backtracing(permutation), graph*
     - Solution based on level-order traversal
        - to find all shortest path solution, should use level-order traversal
        - to build the path, need create a retrieval data structure, Node(String word, List<Node> prev, int depth)
        - use a Map<String, Node> map to mark if a word is visited and tracking it's prev
        - while(!queue.isEmpty() && !found) do traversal
        - if(found) use backtracing(permutation) to find all the path.
     - Important:
        - tracking depth in Node, only if (nextNode.depth == node.depth + 1) nextNode.prev.add(node);
        - set found = true when found a solution, but need finish visiting this layer
        - chars[i] = original; when permutation on char[i] to 'a' to 'z'
        - when permutate to get path, clone path and add prev in until prev is start.
127. Word Ladder *Level-order traversal*
     - do Level-Order traverse on the generation process, each step only change only one char
     - mark visited and check equals(end) when add into queue, not when poll from queue to avoid duplication element in queue.
     - for edge case: if(start.equals(end)) return 1
     - remember set char[i] = original after change char in position i
     - remember to length++ when after visit one layer.
128. Longest Consecutive Sequence *HashMap*
     - use HashMap to hold all the num and mark if the num is visited
     - scan num, grow to smaller and larger to get the longest consecutive sequence.
129. Sum Root to Leaf Numbers *Pre-order Traversal*
     - pre-order traversal: root-to-leaf, so need check node.left == node.right == null
130. **Surrounded Region** *BFS and Queue*
     - based on DFS may get a stackoverflow if the board is too large. better to use BFS
     - scan row 0 and rows - 1, col 0 and cols - 1 to enqueue 'O', then do BFS based on queue.
     - set board[row][col] = 'C' when enqueue, and set 'C' to 'O' and 'O' to 'X' at the end scan.
     - use row * cols + col as position identifier in queue.
131. Palindrome Partition *Backtracing(Permutation)*
     - find all partition solution, using backtracing(permutation)
     - to permutate all the palindrome, i in [offset + 1, s.length()]
        - call isPalindrome(s, offset, i - 1), 
        - create prefix by s.substring(offset, i)
        - dfs call partition(s, i, current)
        - remember to delete prefix in current: current.remove(current.size() - 1);
132. **Palindrome Partition II** *DP: Two DP problem*
     - Two DP process: minCut[i] and isPalindrome[i][j]
     - function of minCut[i]: 
        - for j in [1, i], if(isPalindrome[j][i]) minCut[i] = min(minCut[i], minCut[j-1] + 1)
        - minCut[j-1] not minCut[j]
     - function of isPalindrome[i][j]: loop on len and start
        - state[i][i+len] = (len == 1? true : state[i+1][i+len-1]) && s.charAt(i) == s.charAt(i + len);
     
        DP for minCut:
            state: minCut[i] is the min cut to partition s.substring(i + 1) into palindrome
            initialize: minCut[0] = 0;
            function: if(isPalindrome[0][i]) minCut[i] = 0;
                      for j in [1, i], if(isPalindrome[j][i]) minCut[i] = min(minCut[i], minCut[j-1] + 1)
            result: minCut[s.length() - 1]
        DP for isPalindrome: 
            state: isPalindrome[i][j] == true, s.substring(i, j + 1) is palindrome
            initialize: isPalindrome[i][i] = true
            function: loop on length(1, s.length()), and loop on start(0, i+len<s.length())
                      state[i][i+len] = (len == 1? true : state[i+1][i+len-1]) && s.charAt(i) == s.charAt(i + len);
                      
133. **Clone Graph** *BFS, HashMap*  
     - use BFS to clone the graph, keep HashMap<oldNode, cloneNode> pair
     - when clone a new node, put in nodeMap and also offer in queue
     - make sure the node in stack have a copy in nodeMap, use nodeMap as visited
134. **Gas Station** *Statistical Number*
     - tracking the current retain gas, if current < 0, mark next station as start.
     - also tracking the total gas and cost, if total < 0 after scan, no start point is OK, so return -1.
135. Candy *Forward and Backward Scan*
     - scan forward and backward to adjust candy based on the rule
     - forward, i compare with i - 1, backward, i compare with i + 1
     - during backward scan, condition is (ratings[i] > ratings[i+1] && candy[i] <= candy[i+1])
136. Single Number *Math, XOR*
     - xor ^= number[i] for every number, return xor
     - xor should init as 0 or number[0]
137. **Single Number II** *Math, XOR*
     - use two number once and twice, 
        - when number[i] appear once, store it value in once
        - when number[i] appear twice, store it value in twice, and clear it in once
        - when number[i] appear third time, clear it in twice.
     - both once and twice is init as 0, once = (once ^ num[i]) & ~twice; twice = (twice ^ num[i]) & ~once;
        
        int once = 0; int twice = 0;
        for(int i = 0; i < num.length; i++){
             once = (once ^ num[i]) & ~twice;
             twice = (twice ^ num[i]) & ~once;
        }
        return once;
        
138. **Copy List with Random Pointer** *List Population, HashMap*
     - clone process be divided into three stage:
        - clone RandomListNode and insert after the node;
        - copy random by node.next.random = node.random.next;
        - split the list into old one and clone one; 
     - create a dummy head when split the list.
139. Word Break *DP*
     - optimization: find the max length of word in dict, and adjust j based on maxLength(i - j <= maxLength);
        
        state: canSegment[i] == true when s.substring(0, i) can be segmented.
        initialize: canSegment[0] = true;
        function: canSegment[i] == true when found j (0, i-1) s.substring(j, i) is a word and canSegment[j] == true
        result: canSegment[s.length()]
        
140. **Word Break II** *Memo DP*
     - Memo DP can be used to find all the solutions
     - backtracing to get all break solution, using memo to avoid duplication segmentation
     - generate segments(List<String>) for s, by partition it into word, 
        - if word is the end of s
        - if word is not end of s, get segments of rest, for each rest add word in front of it as a solution
     - put in memo before return.
141. Linked List Cycle *Fast/Slow Pointer*
     - use fast and slow pointer to scan list. fast go 2 steps and slow go 1 step
142. Linked List Cycle II  *Fast/Slow Pointer*
     - use fast and slow pointer, fast go 2 steps, slow go 1 steps
     - if fast != slow, no cycle, return null
     - slow = slow.next and fast = head, both go 1 step until meet, the meet node is the beginning of cycle.
143. Reorder List *Fast/Slow Pointer, Reverse, Interleaving*
     - find the middle element
     - reverse the middle element till tail
     - interleaving the two list: front half and back half
144. Binary Tree Pre-order Traversal *Stack*
     - put right in stack before left
145. **Binary Tree Post-order Traversal** *Stack HashSet*
     - use Stack and HashSet(tracking if child of one node is already put in stack).
     - stack.peek() one node, if(!childrenVisited.contains(node)) 
        - put node.right and node.left in stack, and put node in childrenVisited
        - else visit this node.
146. LRU Cache *HashMap, Double Linked List*
     - hashmap to achieve O(1) search, double-linkedlist to maintain visit sequence.
     - when retrieve node by key, remove the node from list and insert in the front
     - also tracking tail of the list, if insert new key over the whole capacity, remove the tail.
147. **Insertion Sort List** *List, Dummy Node*
     - scan from the list, insert i-th node to the right place among 0 - (i-1)th nodes.
     - use dummy node to avoid head change
     - loop insert node from head.next, and set previous 0-(i-1)th nodes end with null.
148. **Sort List** *Divide and Conquer using length*
     - use length to partition the list
        - find the mid by for(int i = 0; i < length/2; i++)  mid = mid.next;
        - mergesort the first half:  head = mergesort(head, length/2);
        - mergesort the second half: mid = mergesort(mid, length - length/2);
        - merge the two sorted list using dummy node;
     - important: when length == 1, set head.next = null and return head
149. Max Points on a Line *HashMap, Slope*
     - use HashMap to calculate how many point pairs with the same slope
     - be careful of slope is double, same point, p.x == q.x(slope is max positive).
     - call slopeMap.clear(); in the loop of select cur point
150. Evaluate Reverse Polish Notation *Stack*
     - use stack to hold the numbers, when find a operator pop two and eval the value and push back to stack.
     - return stack.pop().
151. Reverse Words in a String *Backward Scan*
     - scan s backward, tracking the end of word and find the begin
     - if s.charAt(i) == ' ', update end to i
     - begin is i == 0 || s.charAt(i - 1) == ' ', buffer.append(s.substring(i, end));
152. Maximum Product Subarray *Forward/Backward, Statistical* 
     - scan backward and forward tracking products. if product == 0, reset to 1.
     - tracking max which is bigger one of backward and forward
153. Find Minimum in Rotated Sorted Array *Binary Search*
     - max element is array[i] > array[i + 1] && array[i - 1] > array[i](default); min element is array[i + 1];
     - do binary search low = 0 and high = array.length - 1, 
        - if(array[mid] > array[mid + 1]) return array[mid + 1];
        - else if(array[mid] > array[high]) breaking point in high part, so low = mid + 1;
        - else breaking point in low part, so high = mid;
     - if no breaking point found, the min element is array[0];
154. Find Minimum in Rotated Sorted Array II *Binary Search*
     - de dup by checking array[low] == array[high], do high--;
155. Min Stack *Stack*
     - use two stacks, if element smaller or equals to current min push into min.
156. Binary Tree Upside Down *Two Pointer*
     - tracking parent and parentRight, as reverse linked list do while to reverse the root.left and right
     - parent and parentRight is init to null
     - while(root != null)
        - save node.left as next
        - assign node.left = parentRight, parentRight = node.right, root.right = parent;
        - update parent = root; root = next;
     - return parent as new root of the upside down tree
157. Read N Characters Given Read4 *Loop Condition Checking*
     - use a char[4] readBuf to read file using read4(); use readSize to tracking how many char read use read4
     - while condition is (offset < n && readSize == 4).
     - read and doing copy using for loop, condition is (i < readSize && offset < n)
158. Read N Characters Given Read4 II - Call multiple times *Class attribute as global dataholder*
     - need hold the un-read char in readBuf for next time call.
     - save previous read status using private class attribute char[] readBuf, int bufIdx, int bufSize
        - when there is remain data in readBuf, copy it before call read4 again.
        - while(bufIdx < bufSize && offset < n) buf[offset++] = readBuf[bufIdx++];
        - offset still smaller than n, call read4 and copy data when (bufIdx < bufSize && offset < n)
        - if(bufSize < 4) break the while loop since there is no more data.
159. **Longest Substring with At Most Two Distinct Characters**  *Two Pointer, begin and next*
     - use two pointer begin and next, begin is the begin of the substring, (next + 1) is the option of next begin
     - so char between (next + 1) and i-th should be the same, so the two distinct char is s[i-1] and s[next]
     - loop on every char
        - if s[i] == s[i-1] just continue
        - else if(next == -1) next = i-1;
        - else if(s.charAt(i) == s.charAt(next)) next = i-1;
        - else if(s.charAt(i) != s.charAt(next)) more than two char, max = Math.max(max, i-begin), begin = next+1, next = i-1;
     - so the condition is:
        - if(s[i] == s[i-1]) continue;
        - if(next != -1 && s.charAt(i) != s.charAt(next)) max = Math.max(max, i-begin), begin = next+1;
        - next = i - 1;
     - at the end, need check max = Math.max(max, s.length() - begin);
160. Intersection of Two Linked Lists *Length*
     - use Length, if lenA > lenB, only move A, when lenA == lenB, move A and B together
161. One Edit Distance
     - make sure s is the longer one
     - edge case: if s.length() - t.length() > 1, return false
     - go through t, offset = 0 and shift = m - n; 
        - while(offset < n && s.charAt(offset) == t.charAt(offset)) offset++;
        - if offset == n, go util the end, check if m - n == 1;
        - if m == n, so both t and s need go one step forward, if m - n == 1, only s go one step forward
            - so if(shift == 0) offset++;
            - while(offset < n && s.charAt(offset + shift) == t.charAt(offset)) offset++;
     - return offset == n; scan till the end
162. Find Peak Element *Index Out of Range*
     - the condition of peak element is: num[i] > num[i-1] && num[i] > num[i+1]
     - be careful about the index out of range
163. Missing Ranges *Begin tracking*
     - create a util method: getRange(start, end) to handle "2" and "4->49" different range representation.
     - use a begin, init as start, to scan the vals, 
        - if begin < vals[i], create a range(begin, vals[i] - 1);
        - whatever, set begin = vals[i] + 1;
     - remember to set the last range after scan: if(begin <= end) create a range(begin, end);
164. **Maximum Gap** *Bucket Sort*
     - Bucket placement: range = max - min, bucketSize = (max - min)/n-1, bucketIdx = (num[i] - min)/bucketSize;
     - max gap = bucket[i].max - bucket[j].min, i and j are continuous non-empty bucket

        Suppose there are N elements and they range from MIN and MAX.
        Then the maximum gap will be no smaller than diff = ceiling[(MAX - MIN) / (N - 1)]
        Then we could create buckets to contains the element in range diff.
        Let the length of a bucket to be diff, then we will have at most ((MAX - MIN)/diff) + 1 of bucket
        
        For any element num[i] in the array, we can easily find out which bucket it belongs by calculating 
        bucketIdx = (num[i] - MIN)/diff and therefore maintain the maximum and minimum elements in each bucket.
        
        Since the maximum difference between elements in the same buckets will be at most len - 1, so the final 
        answer will not be taken from two elements in the same buckets.
        
        For each non-empty buckets p, find the next non-empty buckets q, then q.min - p.max could be the 
        potential answer to the question. Return the maximum of all those values.
        
165. Compare Version Number *Compare two list of number"
     - split version into numbers sequence, compare till end if equals, 
     - split(String regex), so "\\." instead of ".";
     - one sequence remain, return 0 if the remain sequence is all '0';
166. Fraction to Recurring Decimal *HashMap*
	 - use HashMap to store index of division of numerator in StringBuffer, if find existed numerator, make recurring
     - numerator and denominator can be negative, need tracking flag and change using Math.abs()
     - numerator and denominator can be out of range when do abs(), so need use Long
167. Two Sum II - Input array is sorted *Two pointer: low and high*
     - low = 0, high = num.length - 1, get sum = num[low] + num[high]
        - if sum == target, return (low+1, high+1)
        - if sum < target, low++
        - if sum > target, high--
168. Excel Sheet Column Title *BASE26, BASE exchange*
     - convert to base26, special case for 1-26
     - since 'A' -> 1, 'B' -> 2, so do n-- to leftshift one every time.
        
        public String convertToTitle(int n) {
            StringBuffer buffer = new StringBuffer();
            while(n > 0){
                n--;
                char ch = (char)('A' + (n % 26));
                buffer.insert(0, ch);
                n = n / 26;
            }
            return buffer.toString();
        }
        
169. Majority Element *Counteract*
     - keep a element and count
        - if num[i] == element, count++
        - if num[i] != element and count > 0; count--;
        - if num[i] != element and count == 0; element = num[i], count = 1
170. Two Sum III - Data structure design
     - discuss about the time complexity needed for add() and find()
        - Hash table solution: add O(1), find O(N), space O(N)
        - Hash table solution to store pair sum: add O(N), find O(1), space O(N^2)
        - Sorted array or list solution: add O(N), find O(N), space O(N)
        - Array solution: add O(1), find(NlgN), space O(N). sort array when call find()
     - be careful about two same number sum to target, need tracking count of number using HashMap
171. Excel Sheet Column Number *BASE26, BASE exchange*
     - change base26 string to base10 number, rightshift one since 'A' -> 1, and 'B' -> 2.
172. Factorial Trailing Zeroes *Power of 5*
     - trailing zero can be generated by 2 and 5, so calculate how many number contains 5, 25, 125, power of 5, etc
     - how many number contains 5, is n/5, so count+= n/5, n/25, n/125, until n/power of 5 < 0
         
         public int trailingZeroes(int n) {
             int count = 0;
             if(n < 0) return -1;
             for(int i = 5; n / i > 0; i *= 5){
                 count += n / i;
             }
             return count;
         }
         
173. BSTIterator *Stack*
     - like use Stack do in-order traverse, have a pushLeft(node), push node in stack, and assign node to node.left
     - when init, pushLeft(root), when next(), pop() one from stack and pushLeft(node.right);
174. **DungeonGame**  *DP from right-down corner to left-up corner.* 
     - reversed DP calculation process: from right-down corner to left-up corner.
     - life[i][j] = Math.max(1, Math.min(life[i + 1][j], life[i][j+1]) - dungeon[i][j])
        - life[i][j] = Math.min(life[i + 1][j], life[i][j+1]) - dungeon[i][j])
        - if life[i][j] > 1, reset life[i][j] = 1, this will reduce the case positive after negative case.
179. Largest Number
     - change num into String and create a comparator of String s1 and s2, return 1 when s1+s2 > s2+s1 (parse to long value)
     - sort the strs using comparator and create largest number by scan backwards.
     - be careful of "all zero" cases.
186. Reverse Words in a String II
     - reverse the entire string, then reverse by word.
     - handle edge cases: if(s == null || s.length <= 1) return;
     - handle the last word
     - when found a ' ', next word start from i+1
187. Repeated DNA Sequence
     - use Integer to present char sequence, 00 for A, 01 for C, 10 for G, 11 for T, need 20 bit. (Integer)
     - scan string, generate new key by ((prev << 2) & 0x000FFFFF) + map.get(current);
     - count using HashMap
     

     
        

    
    
    

        



        
    
        
    
        
    
    
    
    


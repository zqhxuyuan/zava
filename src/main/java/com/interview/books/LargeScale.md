#Question List

   General Approach:

    Hash table: Key in memory, searching in O(1)
        1. find the most frequent IP visit some website.
        2. find un-duplicate phone number in 1 billion records
    Heap: insert and popMin/popMax in O(lgN), getMin/getMax in O(1).
        1. get topK integer in massive integers.
        2. get median of massive integers.
        3. tracking median in a stream of integer.
    SelectionRank: find the TopK or K-th element in massive data.
    BitMap: mark integer/long occurrences, used in searching, find duplication, delete duplication.
        1. find un-duplicate phone number in 1 billion records
        2. find un-duplicate integer in 2500 billion integers
    Partition: partition data based on some rules, and use multiple machine to process or process multi-times.
    Inverted Index: create word-docs list, attr-entity index, find co-occurrence searching
        1. keyword searching
        2. search keyword suggestion
    OuterSort: use disk to sort massive data. K-merge sort
    Trie: compress suffix of words for better searching performance. O(L), L is the average word length.
        1. search a word in dictionary or not, memory limit can't load all word one by one in memory. 
        2. top 10 hot query keywords
    MapReduce: distributed computer framework, Map(Input -> <K1, V1>), Reduce(<K1, [V1, V2..]> -> <K2, V1>)
        massive log analytics, data mining and machine learning algorithm on massive data.
        
1.  Design a method to find the frequency of occurrences of any given word in a book. *[HashMap / Trie]*

        Answer based on one-time query or multi-time operation.
        one-time query: scan and count: O(N)
        multi-time query: create an index of words frequency, using HashMap or TrieTree. Time: O(1), 
            HashMap:  Space: O(M) M is the num of word
            TrieTree: Space: O(26^len) len is the largest length of word
            
    If the book is 1G, and we need find out top 100 frequent words. Memory Limit is 1MB. *[HashMap, Partition]*
    
        Answer: 1. count the word occurrences. 2. use a 100 word min heap.
            1. count: to partition the words into different machine by on hash(word) % n (n is machine number).
               each machine, have a hash table <word, count>
               assume have M un-duplicated words in 1G file, the average length of word is L.
               total size of M * L byte (string) and M * 4 byte (count) = (L+4)M byte.
            2. use 100 word min heap, scan M un-duplicated words, and keep the top 100 in heap.

2.  You have a large text file contains words. Given any two words, find the shortest distance between them in file. How to optimize it
    for multi-times operations. *[InvertedIndex]*
        
        Answer:
            1. for once operation, just scan and keep lastPosWord1 and lastPosWord2, and tracking the min distance.
            2. for multi-operation, we could create a hash table with each word and the locations where it occurs.
               when the query, we just need to find the minimum different between a value in listA (n) and listB(m).
               Merge ListA and ListB into one ListC but tracking the element from which list (n + m).
               Scan the ListC one by one, tracking the min distance between to nodes not from same list. O(n + m).
        
3.  Describe an algorithm to find the smallest one million numbers in one billion numbers. Assume that the computer memory can hold all
    one billion numbers. *[Heap / SelectionRank]*
        
        Answer: generically have 3 methods: sort, min heap and selection rank (quick select).  N = 1 billion, M = 1 million
            sort: Time: O(NlgN) Space: based on different sort algorithm O(1) or O(N)
            min heap: Time: O(NlgM), Space: O(M)
            selection rank: Time: O(N) for average case. Space: O(1)
                N + N/2 + N/4 + N/8 + .... + 1 ~= 2N => O(N)
                selection rank will need to modify the original array to do the partition. 
            selection rank with median of median as pivot, could guarantee to do it in linear time.

4.  Given you 1 billion phone number, find out the un-duplicated phone number. *[BitMap]*

        Answer: BitMap, Bi-BitMap, Range calculation. 
            Data Range: phone number 10 digit number, 1,000,000,000 - 9,000,000,000. 10^10 data range ~= 2^34 phone numbers.
                        10^10 is bigger than integer range, so need to use long to present a phone number.
                            int range:   -2^31 ~ 2^31-1
                            long range:  -2^63 ~ 2^63-1
            BitMap: to find out the un-duplicated phone number, could use 2 BitMap.
                make i-th bit in Map1 when meet a phone number(i) first time, make i-th bit in Map2 when meet more than once.
                the un-duplicated phone number is i Map1.get(i) == true && Map2.get(i) == false;
            BitMap present range:
                int[]  as store: 2^31(0-Integer.MAX_VALUE) * 2^5 = 2^36 bit; storage 2^33 byte = 8G
                long[] as store: 2^31(0-Integer.MAX_VALUE) * 2^6 = 2^37 bit; storage 2^34 byte = 16G
                so to mark phone number int[] BitMap is enough.
            To store 10^10 phone number ~= 2^34 phone numbers, need 2^34 bit ~= 2^31 byte = 2G, total 2*2G = 4G memory.
            
            If we can have 4G memory, we could partition the 1 billion phone number into different partition based on their first
            digit number, so only need 4G/10 = 0.4G ~ 512M memory. If still can't put in memory could partition using first-two
            digit numbers, memory need will be 4G/100 = 0.04G = 51M memory.
 
5.  Find the median of integers placed in N machines. *[Partition, SelectionRank]*
            
        Answer: Selection Rank: find the K-th number (K = total/2). 
            1. Find a machine as coordinator, send command to other N-1 machines, firstly find total integer number.
            2. The coordinator, 
                2.1 random pick one of its number as pivot(if itself doesn't have, send command to other machine to random
            pick one), 
                2.2 send command to other N-1 machine to partition numbers based on pivot, return how many number smaller or equal
            to pivot, 
                2.3 assume returned value sum is M. if M == K, pivot is median; if M < K, let machine remove all the number smaller 
                or equals pivot and K = K - M; if M > K, let machine remove all the number larger than pivot.
                2.4 repeat 2.1 ~ 2.3 until K == M
            The time complexity: assume every machine have (total/N) numbers, i-th round, compare O(total/(N*2^i)), the coordinator 
            need sum the result O(N), total O(lg(total/N) round. Total: O(total/N + N * log(total/N)), since N << log(total/N), so 
            Time complexity is O(total/N), so it's a linear based on number stored on each machine.

6.  Given an input file with 4 billion non-negative integers, provide an algorithm to generate an integer which is not contains in
    the file. Assume you have 1G of memory available. *[BitMap]* 
            
        Answer: integer range 2^32 or 4 billion distinct integer, use BitMap to mark the integer in files, need 0.5G
            
    If we only have 10MB memory? *[Partition]*
        
        Answer: partition integer into 50 partition (500M/10M), every partition could contains 2^26 ~ 2^27 integer, could partition
        based on it's value range.
        If have multiple machine, could pass in parallel, if not need pass 50 times to get all integer marked.
    
    Given a file of 4 billion 32-bit integers, how to find one that appears at least twice?
            
        Answer: use 2 BitMap, scan to number i, 
            if(map1.get(i) == false) map1.set(i);
            else map2.set(i);
        The number appear twice should be map1.get(i) == true && map2.get(i) == true
        BitMap for 4 billion 32bit-integers need 2^32 bit = 2^29 byte = 0.5G
        So only need 1G to hold 2 BitMap.
            
7.  You have an array with all the numbers from 1 to N, where N is at most 32,000. The array may have duplicate entries and do not
    know what N is. With only 4K memory available, how would you print all duplicate element in the array. *[BitMap]*
    
        Answer: 4KM = 2^(2+10+3) = 2^15 bit using BitMap. 32000 ~= 32 * 2^10 = 2^15 number. so could use a BitMap in memory.
    
8.  You have 10 billion of URLs, how to detected the duplicate URLs.[HashMap/Partition]

        Answer: 10^10 URL, each URL have 100 char in average, so 10^12 chars. each char have 4 byte, ~= 4000GB. (10^9 ~= 1G)
            1. Disk storage: partition into 4000 files, hashing by hash(URL)%4000;
            2. Partition Multiple machines: 4000 files could partition to N machine. 
               How to handle machine failure, need a global coordinator, and if task failed or timeout, re-assign to other machine.[Map/Reduce]   

9.  Find or determine non existence of a number in a sorted list of N numbers where the numbers range over M, M >> N and N large enough
    to span multiple disks. Algorithm to beat O(log n) bonus points for constant time algorithm.   
            
        Answer:
            1. If it's once operation, just do binary search on the N numbers, O(lgN). 
               Since the N numbers is sorted, so could find a partition maybe contains this number based on (start, end) pair of 
               each partition, than do binary search on the partition.
            2. If it's multiple operation, could do pre-process of N numbers. 
               Since M << N, so create a BitMap to mark the occurrences of numbers, a BitMap contains all integer need 0.5G.
               M << N, so BitMap should be able to put on one machine.
               After the pre-process, query will be done in O(1)   

10. Design and describe a system/application that will most efficiently produce a report of the top 1 million Google search requests. 
    These are the particulars: 
    a) You are given 12 servers to work with. They are all dual-processor machines with 4Gb of RAM, 4x400GB hard drives and networked 
    together.(Basically, nothing more than high-end PC); 
    b) The log data has already been cleaned for you. It consists of 100 billion log lines, broken down into 12*320 GB files of 40-byte 
    search terms per line. 
    c) You can use only custom written applications or available free open-source software.  [Map/Reduce, Heap]
    
        Answer: the process: count search request, find top 1 million, and request: 1M search request * 40-byte = 40M byte
            Counting: 12 * 320G data, using Hadoop Map/Reduce
                Map:    <LineOffset, LineContent> -> <40-byte search terms, 1>
                Reduce: <40-byte search terms, [1,1,1,etc,]> -> <40-byte search terms, count> 
                Optimization: Combiner to reduce the traffic
                Output File: <40-byte search term, count>
            Top 1M: A min heap hold the 1 million search term
                Each entry: 40-byte search term + 8-byte count = 48-byte, total 48M byte.
                Use one machine to scan the output file, 
                    if heap.size() < 1M, put in heap.
                    else if(heap.peek().count < current.count){
                        heap.poll();
                        heap.add(current);
                    }
                Optimize: we could select 1M in each machine, and one machine to do merge to find the top 1M in whole dataset.
                In this case, the total time: 320G(each machine) + 12 * 48M records(last merge).
        If give you one 4T file of 1 billion logs, you can first partition the file into different partitions, maybe based on 
        hash(search terms) % 12 then we could reduce the merge or multi-machine data merge, since one record will be only on 
        one machine. So each machine will have 320 data. we can also doing counting. 

11. Write a program for displaying the ten most frequent words in a file such that your program should be efficient in all 
    complexity measures. [Trie]
        
        Answer: Need find Top 10, so need counting and top 10.
            Storage: Use Trie to save words in memory, as a 26 child tree[use a hashmap to only keep the child if the character exist.]
                     Each TrieNode contains a char and a count.
            Time Complexity: for each word search or insert, need O(lgL) L is the average length of word.
            Space Complexity: the trie is O(26^L).
        Assume the file contains N words, counting need O(NlgL)  lgL can be consider as constant time. top10 should use a 10 element
        min heap, do traverse on the Trie to scan all the words, and keep tracking the most frequent 10.
             
12. Create a fast cached storage mechanism that, given a limitation on the amount of cache memory, will ensure that only the least 
    recently used items are discarded when the cache memory is reached when inserting a new item. [Cache]
    It supports 2 functions: String T get(K k) and void put(K k, T t).
    
        Answer: It's a LRU cache, should use double-linked-list(head/tail) to save the visiting sequence, hash table to save
        <key, value> pair, value is the ListNode. 
            When get(K): first found ListNode Node in hashmap using K, and put this Node at the first in the list, return it's value.
            When put(K, T): check if the K is already existing, if yes, update the value, and put in the first; if no, check if cache
                is full, if full, delete the last one; insert a new ListNode at the first, and put the <k, node> pair in hashmap.
                
13. Remove duplicated lines in a very large block of text. [Hash, Partition]
    
        Answer: 
            1. Ask clarify question: how big the block of text? how long one line is? How many machine we have, and the 
               memory limitation on each machine?
               Assume the text is 4TB, each line is a URL(at least 256 characters, and average case 100 character, 400 byte.)
               Totally 100 billion line.
               We have machine with 8G memory, add you can get as many as you want.
            2. De-Dup, we need partition the text to multiple machine.
                each machine is 8G. Assume the average duplication rate is 1 : 100
                so total 40G data after de-dup. so we need 50 machine to do the task in parallel.
            3. Process:
                first partition line based on hash(line) % N to partition: N is the machine number. N = 50
                each machine process one partition:
                    hold a hashset in memory, if !set.contains(line) set.add(line)
                    after one round, print out all the line in hashset.
            4. Optimized
                If line is very very line, we could do MD5 to normalize line into 128 byte or 64 byte.
 
14. You have a stream of infinite queries (ie: real time Google search queries that people are entering).
    Describe how you would go about finding a good estimate of 1000 samples from this never ending set of data and then write code 
    for it. [Sampling]
    
        Answer: Abstract this problem as "do random sampling of a stream of data.".
        Get a 1000 String array queries[], and keep a index of each query get from stream.
        When the index < 1000, just put in queries[index]
        When the index >= 1000, get a random number r which range in [0, index].
            if r < 1000, put current query in queries[r]
            if r >= 1000, just throw current query.
        It will get a perfect sample on the stream of data.
        
        When query count is very very large, so we could use long to tracking the index. long is in range (-2^63 ~ 2^63 - 1), we 
        only use 0 and positive number, its 2^63 ~= 10^18 = 1 million trillion of query.

15. If you have 1 million integers, how would you sort them efficiently?
        
        Answer: 1. Ask clarify question, do I know the the range of integers? requirements of time and space complexity.
            1M integer = 10^6 ~= 2^20 integer, total 4M byte
            If less memory used: use Heap sort, it can achieve O(NlgN) without extra space.
            If less time: use BitMap do counting, radix sort or bucket sort.
                BitMap: 2^32 integer * 4 byte = 2^34 byte = 16G total memory
                Radix sort: by index [0-9] 10 long number, to count and do re-placement from lower to higher index.
                    LSD radix sort is Least-significant-digit-first, every time need scan all the number once.
                    MSD radix sort is Most-significant-digit-first, every time only sort next bit within its partition
                Bucket sort: put number in different bucket(0 ~ 2^10, 2^10 + 1 ~ 2^20, 2^20+1 ~ 2^31 - 1, also for negative number)
                    then sort number in the bucket using QuickSort or other sort algorithm.
                
16. You are given a small sorted list of numbers, and a very very long sorted list of numbers. so long that it had to be 
    put on a disk in different blocks.
    How would you find those short list numbers in the bigger one?
    
        Answer:
            since the long list is also sorted, it be partition into M files and each file have N/M number, totally N number  
            so for each number in small list, 
                1. find which partition the number should be based on the first/last number in different partition of the large
                file. we could also use binary search. this searching is in O(lgM)
                2. when we found a partition, we could do binary search in the partition, the searching is in O(lgN/M).
                so whole process to find one number is O(lgN/M + lgM), lgM << lgN/M, so it basically is O(lgN/M).
            
17. What sort would you use if you had a large data set on disk and a small amount of ram to work with?

        Answer: K-MergeSort, partition the large data set into K files, sort each file using MergeSort or QuickSort.
        Merge the K sorted file using K-MergeSort.
        Optimization: if data set is very very large, we could do it in several layer, 
            Layer1: partition to K, each have N/K number
            Layer2: partition each partition into K, each have N/K^2 number
            Layer3: ....
        After the under layer is sorted, merge it using file-based merge.
        In the file-based merge, the process can be optimized using buffer, hold certain data in memory buffer, when buffer
        is full, flush to file once.
        
18. What sort would you use if you required tight max time bounds and wanted highly regular performance.

        Sort based on counting or radix sort can achieve linear time complexity, but it also have limitations:
            1. counting sort, need load all counting data in memory, could use HashMap or BitMap.
            2. radix sort, need to know the range of each bit, usually used to sort String or Integer.
        If time is very tight, could partition to multiple machine sort in parallel and merge back together.
          
19. Given a very large file, a sens in each line, may have same sens in different line. 
    1. Find all the sens only appear once. 
    2. Find the similar sens. Two sens are similar if they only have one words not the same, and the other words
       are also in the same order, for example: "I love you" and "I love" or "I love him", "I love you too".
    Design a effective algorithm to implements the above function. 
    
        Assume all the sens(N) can be placed in one machine:
         We could create bucket based on hash(sens), The hash could use MD5 or SHA, then in each bucket compare 
         the sens pair in case hash collision. so we could get non-duplicated sens.
         To get similar sens, we could delete every word in sens to create a new sens, and check if the new sens
         is existed, assume M words in sens in average, we could do searching in O(M) for each sens, so total 
         O(N*M). 
        For large scale, we could use distributed hashing like Memcached or Redis, put hashing in K nodes using 
         consistent hashing, the placement is mapping hash(sens) into 2^32-1 range, and each node maintain a sub
         range, such as 1~2^30(A), 2^30+1 ~ 2^31(B), 2^31+1 ~ 2^32-1(C), hash(sens)%2^30 could get the identity
         of the node.
         consistent hashing is better for add or remove node from hashing server, not need re-hashing, just move
         data to one of the neighbors, and extend the range of that neighbors.

20. Given a very large file, every line is a positive integer, all Y integer, and the range is [1, X], there may
    have duplicated numbers. How to find the first missing positive. 
    Assume the cases: if Y and X can be fit in one machine or can't.
    
        1. Assume X and Y can fit in one machine:
            Create a BitMap, if number i appear, set i-th bit as 1, after scan, find the first missing positive
            is the first bit is 0 by scan from bit[1].
            BitMap for all positive integer is 256M, and the scan can also be done 32bit as step using the inner 
            integer for store bit == -1, so all bit is 1, no need to check one bit by one bit.
        2. Assume X and Y can't fit in one machine:
            We could divide range [1-X] into sub-ranges, based on machine's memory, assume each machine only 
            have 128M, and the original range [1-X] is [1-Integer.MAX_VALUE], 128M could hold BitMap for 2^30 number, 
            all positive integer is 2^31, so need have two machine, A and B, partition the data based on the rules:
            A hold the integer <= 2^30, and B hold the number > 2^30. 
            Do the same process in one machine, scan A before scan B to get the first missing positive.
        3. If memory is very limited, could also use interval, if a1, a2, a3, is continuous, could merge it in interval, 
            and when add a new integer, merge the interval if they become continuous. Then find the missing missing 
            positive is find the first missing value in a sorted interval.
        4. Also could do sort first, and find the first missing one. For sort, if memory is very limited, and file is 
            very larger, we could do sort based files, such as K-merge sort. But sort is not a very good solution for 
            this problem since the sorting need O(YlgY), and Y is very large.
            
21. A and B two files, each have 5 Billion URL, each have 64B. Find the URL both in A and B using 4G memory. If you 
    have several files.
    
        Do some calculation first, 4G = 2^32B = 2^35bit ~= 34Billion bit.
        Use BloomFilter, use K hash function to hash URL to a int range of 0~34Billion, for each URL, calculate K hash
        for it, and make K bits to 1. Check existing is the same, check if all the K bit is 1.
        
        For BloomFilter, E is the error rate, the total bit space M >= 1.44*nlg(1/E), and K = (ln2)*(m/n). 
        E = 0.01, N = 5Billion, need use 47Billion bit, so maybe E = 0.04, M = 33.4Billion bit, and K = 5(4.713)
         
        If you have several files(K), can't use BitMap, need do counter int[], and count[hash] for each hash == K.
                                                 


        
            
            
    
        
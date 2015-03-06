#OO Design Problems

1.  Design a Card Game: Design the data structures for a generic deck of cards. Explain how you would sub-class it to implement 
    particular card games: *package cardgame*
    
        Basic Objects: Suit, Card, Deck, Hand, Game, GameAutomator. 
        BlackJackGame: BlackJackCard, BlackJackHand, BlackJackGameAutomator
    
2.  Imagine you have a call center with three levels of employees: Respondent, Manager and Director.
    An incoming telephone call must be allocated to a fresher who is free can’t handle the call, he or she must escalate 
    the call to technical lead not free or not able to handle it, then the call should be escalated to PM. 
    Design classes and data structures for this problem. *package callcenter* 
    
        Basic Objects: Rank, Call, Caller, Employee(Respondent, Manager, Director), CallHandler.
         
3.  Design a musical juke box using object oriented principles. *package jukebox*

        Basic Objects: Song, CD, Playlist, AudioPlayer, Display, JukeBox, User. 

4.  Design a parking lot using OOD. *package parkinglot*

        Basic Objects: VehicleSize, Vehicle(Motorcycle, Car, Bus), ParkingShot, Level, ParkingLot, ParkingSimulator
         
5.  Design the data structures for an online book reader system. *package onlinereader*

        Basic Objects: Book, Library, User, UserManager, Display, OnlineReaderSystem. 
        
6.  Explain how you would design a chat server In particular, provide details about the various backend components, 
    classes, and methods.  What would be the hardest problems to solve? *package onlinechatting*
    
        Basic Objects: User UserStatus/UserStatusType, UserManager, Message, Conversation(GroupChat, PrivateChat), 
        AddRequest/RequestStatus, System
    
7.  Explain the data structures and algorithms that you would use to design an in-memory file system Illustrate 
    with an example in code where possible. *package filesystem*
    
        Basic Objects: Entry(File, Directory), FileSystem

8.  Othello is played as follows:
 
        Each Othello piece is white on one side and black on the other When a piece is surrounded by its opponents on 
        both the left and right sides, or both the top and bottom, it is said to be captured and its color is flipped.
        On your turn, you must capture at least one of your opponent’s pieces The game ends when either user has no more valid moves, 
        and the win is assigned to the person with the most pieces.
        
    Implement the object oriented design for Othello. *package othellogame*
    
        Basic Objects: Color, Piece, Board, Location, Direction, Player, Automator, Game.
        
9.  Design and implement a hash table which uses chaining (linked list) to handle collisions. *package hashtable*

        Basic Objects: Cell<Key/Value>, Hash(LinkedList<Cell<K, V>>[])
        
10. Design a blocking list: Producer put message in the list when list is not full, if full, block producer. 
    Consumer get message from the list when list is not empty, if empty, block consumer.
    
    *HINT: use synchronize and wait/notify, or Lock/Condition, await/signal.*
    
11. Implement CircularArray, having a rotate(int offset) which rotate array to left given offset, for example, [1,2,3,4].rotate(1) = 
    [2,3,4,1], also need have get/set and could visit using for(Item item : array) (which need return a Iterator when call iterator())

    *HINT: rotate using 3-way reverse.*
    
12. Implement BitMap

13. Implement Hanoi Game.

14. Implement LRU Cache.
    
    *HINT: double linked list to maintain the visit sequence, and HashMap to enable O(1) searching.*
    
    Implement LFU Cache.
    
    *HINT: double linked list put in a HashMap frequencies <frequency, list head>, and HashMap to enable O(1) searching.
    every time visit a Node, increase it frequency, and put in another frequencies entry, keep tracking lowest frequency,
    when insert a key,value pair doesn't exist, if the cache is full, delete one from lowest frequency entry.* 
    
15. Design a data structure to achieve operation insert, delete, search and random access all in O(1).

    *HINT: Array can insert, random access in O(1), could use to store element. HashMap can insert, delete, search in O(1), 
    could use as index <value, offset>. Logic for delete: get the offset of the value, and put the last element to that offset.
    remember to update the index hashmap.*
    
16. Design a data structure that can implements the following 2 search method in O(lgN)

        1. public V get(K key), return the value which key is key
        2. public List<V> getRange(K key1, K key2) return all the values which key is between key1 and key2
        
    *HINT: BST have good performance to do range search.*
   
17. Implements Multi-Thread of Producer Consumer Problem. 

18. Implements DiningPhilosopher Problem.

        The dinning philosophers problem, a bunch of philosophers are sitting around a circular table with one chopstick 
        between each of them. 
        A philosophers needs both chopsticks to eat, and always picks up the left chopstick before the right one. 
        A deadlock could potentially occur if all the philosophers reached for the left chopstick at the same time. 
        Using threads and locks, implement a simulation of the dining philosopher problem that prevents deadlocks.
    
19. Given a timer time() with nanosecond accuracy and given the interface
            
        interface RealTimeCounter:
            void increment()
            int getCountInLastSecond()
            int getCountInLastMinute()
            int getCountInLastHour()
            int getCountInLastDay()
    
    Implement the interface. The getCountInLastX functions should return the number of times increment was called in the last X.
    
    *HINT: use cyclic buffer persistent the count in every second, and implements get(int distanceFromEnd), 
    so countInLastSecond = buffer.get(0) - buffer.get(1), countInLastMinute = buffer.get(0) - buffer.get(60).
    This data structure could use to do RateLimit for certain period of time.*
    
20. We have a Foo class, it have 3 method, first(), second() and third(), the same instance of Foo will be passed to three 
    different threads. 
    Design a mechanism to ensure that first it called before second and second is called before third.
    
    *HINT: use Semaphore sem = new Semaphore(1), sem.acquire() and sem.release().

21. Give Object oriented design for the snake game (that was in old nokia phones) . 
    Only class and diagram was needed, no code/implementation. 
    it should have extensibility to accomodate different types of fruits, 
    (eg one gives + 5 len + 10 pts) it should be scalable to diff platforms.
        
        Answer: 
        Basic Objects:
            Cell(row, col)
            Snake(Queue<Cell>, currentHead, initLength): addCell(), removeTail(), length(), 
            Board(rows, cols, int[][] store): validCell(), getCellData(), updateCellData(), 
            Display: paint(Board), showGameOver()
            Game(currentLevel, board, snake, display, gameover, direction): play(), nextCell()           

22. Implements Trie with following function: 1. add a string, 2. search a string, 3. search a prefix. 4. get all 
    strings with a given prefix.
    And also give time & space complexity of the code.
    
    *HINT: could use Tree with 26 children, keep children in Array(TrieNode[26]) or HashMap(<Character, TrieNode>).
    The time complexity of add, searchWord, searchPrefix are all in O(L), L is the length of the word. Get all words
    with given prefix, is O(26^(maxLen - Len)). The space complexity: O(26^AverageLen).*
    
    Also implements getFuzzyWords(word, missingLetters) to return all words based on given words and allow missing n letters.
    
23. Write code to implement a 6 faces Rubik's cube game.

24. Design a Stock Trading System, include high level definition, data structure and algorithms.

#System Design

1.  How would you design the data structure for large social network like Facebook or Linkedin? Describe how you would design an 
    algorithm to show the connection, or path, between two people (e.g. Me -> Bob -> Susan -> Jason -> You.)
            
        Answer
            1. Basic simple case: construct a graph by treating each person as a node and letting an edge between two nodes 
                indicate that the two users are friends. Each user have a list of friends reference. If we want to find the shortest 
                connection between two person, just do BFS from one user to the target user. 
            2. Handle millions of users: 
                Data Storage: users data may placed in hundreds of machine, for each user, generate a unique id, and
                put it on a particular machine based on hashing algorithms (consistent hashing), and could allocate to the machine
                when we need retrieve user's info by its id.
                Find path algorithm: also do BSF, but for every user, fetch friends info maybe from other machine.
            3. Optimize:
                1. Reduce Machine Jump: instead of randomly jumping among machines for each friends, we could use batch jumps.
                2. Smart Division of People and Machines: people live in the same country be more likely to be friends, so could
                   put them on one machine, it also could reduce the number of jumps.
                3. Make a hashset of visited nodes instead of marking on node.
                4. Real-world problem:
                    1. Severs fails: put users data in multiple server with replication. a leader of all the replication is in charge 
                       of data modification, and replicate data into other replications.[Hadoop]
                    2. Caching?
                    3. Do you search until the end of the graph? How do you decide when to give up?
                    4. In real life, some people have more friends of friends than others, and are therefore more likely to make a 
                       path between you and someone else. Could use user out-degree as heuristic method to do heuristic searching.
                       
2.  If you were designing a web crawler, how would you avoid getting into infinite loops?
    
        Answer: infinite loop occurs when the linkage is cyclic, so determine a page is visited or not before crawl.
        How to identify if the page is visited, based on URL or content? URL and content can't determine this problem perfectly.
        We could create a priority-based crawling system, a page is deemed to be sufficiently similar to other page we de-prioritize 
        crawling its children, but still add it back with a low priority.
        In this case, the crawler will never complete, if you definitely need stop crawler, you can make a threshold of priority.
                    
3. How to design a cache system of web searching system? 
            
        Answer:
        1. First at all, we need make some assumptions:
            a. Other than calling out to processSearch as necessary, all query processing happens on the initial machine that was called.
            b. The number of queries we wish to cache is large. (millions).
            c. Calling between machines is relatively quick.
            d. The result for a given query is an ordered list of URLs, each of which has an associated 50 character title and 200 description.
            e. The most popular queries are extremely popular, such that they would always appear in the cache.
        2. Understand primary functions:
            a. Efficient lookup give a key.
            b. Expiration of old data so that it can be replaced with new data. We need handle updating or clearing the cache when the query
               result updated. Because some query may very very hot, and it always in cache. 
        3. Design:
            a. Single Machine Case:
                a double linked list to store data(easy purging of old data, and move "fresh" items to the front.)
                a hash table allows efficient lookup of data
                Cache expire could base on time, and Cache replace policy would be LRU(Least Recent Used) or LFU(Least Frequent Used). 
                LFU is more complexity than LRU, need a counter of usage, and sort linked list based on frequency.
            b. Multiple Machine Case: 
                a. Each machine has it's own cache, and no share. It's not good since query is round-robin dispatch to different machine.
                b. Each machine has a copy of the cache, if the total size of cache is M, current only can cache M/machine_count data.
                c. Each machine store a segment of the cache, and have a partition rule, such as hash(query)%machine_count or consistent hashing
                   When a machine get a query, it can allocate where to find the cache of that query.
            c. Update result when query result change: page content changes or query result list changes.
                define a "automatic timeout" on the cache, and make a key-mutex lock when update data to avoid too much query calling backend 
                service concurrently.
            d. Further Optimize:
                a. for very very popular query, could hold result in every machine to avoid jump to other machine.
                b. could re-architect the load balance policy to route the query also based on cache policy to avoid machine jump.
                c. "automatic timeout" threshold could defined based on the type of query or content update frequency.
           
4.  Design question based on storing images.Stress on performance and scale.

5.  A design problem of the sorts : You need to present a ppt to say N users who are viewing it live in their 
    browsers. What you have is a web page where the ppt is opened and has say two buttons : next and previous. 
    You need to design basically what will happen / how will pressing of the buttons reflect a change across 
    all the users.( He wanted something as to how the DNS on processing the next request would change the URL 
    and convey it to all connected users)
    
6.  If the production application hang, how do you find out what caused the problem?

        1. Log
        2. Server CPU, memory, I/O blocking
        3. If the application is depends on some other service, such as database, check depended services.
        4. Monitoring
        
        Specific for application, thread dump should be considered at first, kill -3 $pid.
        
        Further suggestion is: the key is to prepare, not react on such accidents. If there is a number you 
        want to know when it hangs, you should build it before hand.
        I would set up metrics to cover the frequent API calls for both volume and latency. I would have the 
        metrics logged to a separate server and displayed on a timeline chart, and alerts to warn me if the 
        volume/latency is over certain threshold compared to history. I would even set up circuit breaker if 
        self recovery is possible. It should be pretty easy to narrow down which call is causing trouble. 
        there are open source tools on all these.
        
        
        



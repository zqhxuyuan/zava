#Common Question about Computer Science

1. What's the difference between a thread and a process?

        1. A process can be thought of a instance of a program in execution. A process is an independent entity to which system 
        resource  (CPU time and memory) are allocated. Each process is executed in a separate address space, and one process can't 
        access the variables and data structures of another process. If a process wishes to access another process's resources, 
        inter-process communications have to be used, including pipes, files, sockets, and other forms.
        2. A thread exists within a process and shares the process's resources (including its heap space). Multiple threads within 
        the same process will share the same heap space. Each thread still has its own registers and it's own stack. A thread is a 
        particular execution path of a process. When one thread modifies a process resource, the change is immediately visible to 
        sibling thread.

2. What is the difference between a mutex and a semaphore? Which one would you use to protect access to an increment operation?

        a semaphore is a data type for controlling access by multiple processes to a common resource in a parallel programming 
        environment. Semaphore which allow an arbitrary resource count are called counting semaphores. While semaphores which 
        are restricted to the value 0 to 1 (locked / unlocked) are called binary semaphore. (the same functionality that mutex have)

3.  How are cookies passed in the HTTP protocol?

4.  What is multi-threaded programming? What is a deadlock?

        1. Multi-threaded programming is to folk multiple thread in one process, and when running thread is waiting, other thread 
        could take over and running based on thread schedule policy. In the case, non CPU density work, multi-threaded programming 
        could running more parallel, so faster.
        2. A thread exists within a process and shares the process's resources (including its heap space). Multiple threads within 
        the same process will share the same heap space. To solve the conflict in multi-threaded programming, need make threads visit
        shared resource in sequence.
        3. Deadlock happens when two or more thread have cyclic waiting on the resource hold by other thread. Deadlock happen have 4 
        condition: Mutual exclusion, Hold and Wait, No preemption, Circular Wait. Most of the case, we try to break Circular Wait.
        
5.  How long it would take to sort 1 trillion numbers? Come up with a good estimate.

6.  What's the difference between HashMap, Hashtable and ConcurrentHashMap?
        
        In Java, have 3 implementation of Hash Table: HashMap, Hashtable and ConcurrentHashMap. 
        1. Hashtable and ConcurrentHashMap is thread-safe, and HashMap is not. Hashtable use synchronized to achieve thread-safe, and
           ConcurrentHashMap use Lock, and ConcurrentHashMap divide several Segment to store key/value to avoid lock the entire Map.
           In default, ConcurrentHashMap allow 16 thread to visit the map in parallel.
           The speed: HashMap > ConcurrentHashMap > Hashtable
        2. HashMap allow key and value to be null, Hashtable can't.
         
7.  What is **Context Switch**.
         
        1. A context switch is the time spent switching between two processes. (bringing a waiting process into execution and sending 
        an executing process into waiting/terminated state). This happens when multi-tasking. The OS must bring the state information 
        of waiting processes into memory and save the state information of the current running process.
        2. Context switch between processes and threads: The main distinction between a thread switch and a process switch is that 
        during a thread switch, the virtual memory space remains the same, while it does not during a process switch. Both types 
        involve handing control over to the operating system kernel to perform the context switch. The process of switching in and 
        out of the OS kernel along with the cost of switching out the registers is the largest fixed cost of performing a context 
        switch. A more fuzzy cost is that a context switch messes with the processors caching mechanisms. Basically, when you context 
        switch, all of the memory addresses that the processor "remembers" in it's cache effectively become useless. The one big 
        distinction here is that when you change virtual memory spaces, the processor's Translation Lookaside Buffer (TLB) or 
        equivalent gets flushed making memory accesses much more expensive for a while. This does not happen during a thread switch.

10. Explain how congestion control works in the TCP protocol.

11. Billions of numbers, how to effectively find the median.
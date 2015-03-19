Distributed File Cache
===

DataNode:实际的存储节点，一个DataNode由多个partition组成，个数取决于机器物理条件
CacheNode:hash环上的虚拟节点，每个CacheNode由n个partition组成　(n=副本书)

ps:CacheNode的个数取决于副本数，DataNode个数以及每个DataNode上的partition个数

多副本：
存储对象和DataNode的partition由相同的hash算法，会落到同一个CacheNode上面，这样




平衡性：
2d:为CacheNode添加虚拟节点
http://www.ibm.com/developerworks/cn/java/j-javaactors/index.html
http://www.ibm.com/developerworks/library/j-javaactors/

消息 是在 actor 之间发送的消息。
ActorManager 是一个 actor 管理器。它负责向 actor 分配线程（进而分配处理器）来处理消息。
Actor 是一个执行单元，一次处理一条消息。


下图显示了 actor 之间的关系。每个 actor 可向其他 actor 发送消息。
这些消息保存在一个消息队列（也称为邮箱；从概念上讲，每个 actor 有一个队列，
当 ActorManager 看到某个线程可用于处理消息时，就会从队列中删除该消息，并将它传送给在线程下运行的 actor，以便处理该消息。

![overview](actor-overview.gif "actor-overview")

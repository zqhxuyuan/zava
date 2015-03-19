https://github.com/techrobby/SimplePubSub
====

 SimplePubsub is pure java implementation of publisher subscriber mechanism without any other library dependency.
 The project can be used in any java project including ANDROID.

 IMPLEMENTATION :

 Pubsub.java is a threadsafe singleton implementation of publisher subscriber system.
 It uses a threadpool to deliver the events to the subscribers.
 If you want ordering of the events simply configure the threadpool to be single threaded.

 Currently NUMBER_OF_THREADS = 1, but you can change it to any number depending on your needs.

 USAGE :

 PUBLISHER : Publisher should have an instance to Pubsub object and simply calls the publish function on a particular topic.
 Example : pubsub.publish("topic_food",obj), where obj could be any object used to pass to the subscriber.

 SUBSCRIBER : Subscriber should implement the listener Pubsub.listener interface and should subscribe to a particular topic
 using pubsub.addListener(topic,listener) function.
 All the events related to that topic will now be received in the onEventReceived() function along with the object passed.

 A sample testcase and usage is also included in the project. PubsubTest

 this simple project include 2 files: Pubsub.java and PubsubTest.java

#### How PubSub Implement
1. 消息&主题:一个主题可以有多个消息,可以往一个主题多次发送消息.
   订阅者订阅某个主题, 发布者往某个主题发布消息, 所有这个主题的订阅者都可以收到消息.
2. 使用BlockingQueue阻塞队列用来存取无序的Operation. Operation包括主题名称type和消息内容object
   阻塞队列存取的对象和订阅者没有关系. 之和发送者要往哪个主题(type),发送哪些消息(object)有关.

3. 监听器是订阅者, 订阅者订阅了某个主题,则在收到这个主题的消息时,要做出自己的响应.
   PubsubTest中定义了三个监听器/订阅者,分别在收到指定的主题时,打印相关的消息.
4. 监听器还要有一个初始化订阅的过程. 否则发送者无法知道有哪些订阅者订阅了指定的主题.
   订阅的动作是调用Pubsub.addListener(TOPIC, this)将自己(监听器)加入到Pubsub的listeners中
5. Pubsub的listeners保存了topic和这个topic的所有订阅者集合.
   监听器在初始化注册的时候,传递了topic和监听器对象. 这样发送者往某个topic发送消息时,
   就能从listeners中根据topic,取出所有注册了这个topic的监听器集合.
   然后回调监听器的监听方法,触发监听器自己的订阅内容方法.
6. Pubsub的消息发送采用异步阻塞方式, 在发送数据时,首先将主题topic和消息内容object封装成Operation
   直接放入阻塞队列中. 而不是同步的方式. 因为一个主题可能有很多订阅者,而消息的发送可能会大批量到达.
7. 真正消息的发送过程要从阻塞队列中依次弹出Operation,根据topic,获取listeners中所有注册该topic的监听器,然后进行回调.
   Pubsub也是一个线程,因为要确保阻塞队列中有数据时,Pubsub就要取出队列里的元素进行处理.

#### Design Patterns
1. Subscribe使用注册监听器:addListener(topic,this)
            和事件回调:Listener.onEventReceived
2. Publish采用异步阻塞处理消息:BlockingQueue
3. Pubsub如何保存主题和订阅者集合:listeners


https://github.com/Syynth/JMessenger
====

同步方式的消息发送和订阅实现

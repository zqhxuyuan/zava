# Actor模型（异步处理框架）

Actor模型是参照[developerWorks](http://www.ibm.com/developerworks/cn/java/j-javaactors/index.html)上一篇文章修改实现。

每秒约可接受40万条消息。

具体用法如下：

Step1. 定义消息处理Actor

	static class DemoActor extends AbstractActor {

		@Override
		public String getName() {
			return "demo-actor";
		}

		@Override
		public String getCategory() {
			return "default";
		}

		@Override
		public boolean process(Message message) {
			System.out.println("DemoActor received " + message);
			return true;
		}
	}

Step2. 设置并初始化Actor管理类

	ActorManager  actorManager = ActorManagerBuilder.newBuilder() //
					.withLogger(LoggerFactory.getLogger(IpmLogConstants.IpmMonitor)) //
					.withThreadCount(10) //
					.withRejectedMessageHandler(RejectedMessageHandlers.discardPolicy()) //
					.build();

	actorManager.initialize();

Step3. 注册Actor到管理类

	actorManager.createAndStartActor(DemoActor.class);

Step4. 发送消息到指定Actor

	actorManager.send(new SimpleMessage(System.currentTimeMillis()), null, "demo-actor");

类设计图：

![Actor类图](http://ww2.sinaimg.cn/large/600db342gw1dvxnfo8thmj.jpg)

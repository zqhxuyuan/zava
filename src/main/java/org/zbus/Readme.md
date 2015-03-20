# ZBUS--轻量级消息队列、服务总线

##**ZBUS** 特性


* **消息队列 -- 生产者消费者模式、发布订阅**
* **服务总线 -- 适配改造已有业务系统，使之具备跨平台与语言, RPC**
* **RPC -- 分布式远程方法调用，Java方法透明代理**
* **跨平台、多语言**
* **轻量级, 无依赖单个jar包**
* **高可用、高并发**

##**ZBUS** SDK 
* [Java SDK](http://git.oschina.net/rushmore/zbus "zbus-java") 
* [C/C++ SDK](http://git.oschina.net/rushmore/zbus-c "zbus-c") 
* [Python SDK](http://git.oschina.net/rushmore/zbus-python "zbus-python") 
* [C# SDK](http://git.oschina.net/rushmore/zbus-csharp "zbus-csharp") 
* [Node.JS SDK](http://git.oschina.net/rushmore/zbus-nodejs "zbus-nodejs") 

##**ZBUS** 桥接

* [微软MSMQ](http://git.oschina.net/rushmore/zbus-msmq "zbus-msmq") 
* [金证KCXP](http://git.oschina.net/rushmore/zbus-kcxp "zbus-kcxp") 

## ZBUS 启动与监控 

1. zbus-dist选择zbus.sh或者zbus.bat直接执行
2. 通过源码ZbusServer.java个性化控制启动

![简单监控](http://git.oschina.net/uploads/images/2015/0212/103207_b5d2e1d3_7458.png)

总线默认占用**15555**端口， [http://localhost:15555](http://localhost:15555 "默认监控地址") 可以直接进入监控，注意zbus因为原生兼容HTTP协议所以监控与消息队列使用同一个端口

**高可用模式启动总线**
分别启动ZbusServer与TrackServer，无顺序之分，默认ZbusServer占用15555端口，TrackServer占用16666端口。


## ZBUS 设计概要图（挫了点待完善）

![设计概要图](http://git.oschina.net/uploads/images/2015/0212/094231_fa14e07f_7458.png)


## ZBUS 示例

### Java Maven 依赖（单个jar包）

	<dependency>
		<groupId>org.zbus</groupId>
		<artifactId>zbus</artifactId>
		<version>5.1.0-SNAPSHOT</version>
	</dependency>

### 生产者


		//1）创建Broker代表
		SingleBrokerConfig config = new SingleBrokerConfig();
		config.setBrokerAddress("127.0.0.1:15555");
		Broker broker = new SingleBroker(config);
		
		//2) 创建生产者
		Producer producer = new Producer(broker, "MyMQ");
		Message msg = new Message();
		msg.setBody("hello world");
		
		producer.send(msg, new ResultCallback() {
			@Override
			public void onCompleted(Message result) { 
				System.out.println(result);
			}
		}); 


### 消费者

		//1）创建Broker代表
		SingleBrokerConfig brokerConfig = new SingleBrokerConfig();
		brokerConfig.setBrokerAddress("127.0.0.1:15555");
		Broker broker = new SingleBroker(brokerConfig);
		
		MqConfig config = new MqConfig(); 
		config.setBroker(broker);
		config.setMq("MyMQ");
		
		//2) 创建消费者
		Consumer c = new Consumer(config);
		while(true){
			Message msg = c.recv(10000);
			if(msg == null) continue;
			
			System.out.println(msg);
		}

 
### RPC动态代理【各类复杂类型】

参考源码test目下的rpc部分

		SingleBrokerConfig config = new SingleBrokerConfig();
		config.setBrokerAddress("127.0.0.1:15555");
		Broker broker = new SingleBroker(config);

		RpcConfig rpcConfig = new RpcConfig();
		rpcConfig.setBroker(broker);
		rpcConfig.setMq("MyRpc"); 
		
		//动态代理处Interface通过zbus调用的动态实现类
		Interface hello = RpcProxy.getService (Interface.class, rpcConfig);

		Object[] res = hello.objectArray();
		for (Object obj : res) {
			System.out.println(obj);
		}

		Object[] array = new Object[] { getUser("rushmore"), "hong", true, 1,
				String.class };
		
		
		int saved = hello.saveObjectArray(array);
		System.out.println(saved);
		 
		Class<?> ret = hello.classTest(String.class);
		System.out.println(ret);




 
 
### Spring集成--服务端(RPC示例)

**无任何代码侵入使得你已有的业务接口接入到zbus，获得跨平台和多语言支持**

	<!-- 暴露的的接口实现示例 -->
	<bean id="interface" class="org.zbus.rpc.biz.InterfaceImpl"></bean>
	
	<bean id="serviceHandler" class="org.zbus.client.rpc.RpcServiceHandler">
		<constructor-arg>
			<list>
				<!-- 放入你需要暴露的接口 ,其他配置基本不变-->
				<ref bean="interface"/>
			</list>
		</constructor-arg>
	</bean>
	
	<!-- 切换至高可用模式，只需要把broker的实现改为HaBroker配置 -->
	<bean id="broker" class="org.zbus.client.broker.SingleBroker">
		<constructor-arg>
			<bean class="org.zbus.client.broker.SingleBrokerConfig">
				<property name="brokerAddress" value="127.0.0.1:15555" />
			</bean>
		</constructor-arg>
	</bean>
	
	<!-- 默认调用了start方法，由Spring容器直接带起来注册到zbus总线上 -->
	<bean id="zbusService" class="org.zbus.client.Service" init-method="start">
		<constructor-arg>  
			<bean class="org.zbus.client.ServiceConfig">
				<property name="broker" ref="broker"/>
				<property name="mq" value="MyRpc"/>
				<property name="threadCount" value="2"/>
				<property name="serviceHandler" ref="serviceHandler"/>
			</bean>
		</constructor-arg>
	</bean>
	


### Spring集成--客户端

	<!-- 切换至高可用模式，只需要把broker的实现改为HaBroker配置 -->
	<bean id="broker" class="org.zbus.client.broker.SingleBroker">
		<constructor-arg>
			<bean class="org.zbus.client.broker.SingleBrokerConfig">
				<property name="brokerAddress" value="127.0.0.1:15555" />
			</bean>
		</constructor-arg>
	</bean>
	

	<!-- 动态代理由RpcProxy的getService生成，需要知道对应的MQ配置信息（第二个参数） -->
	<bean id="interface" class="org.zbus.client.rpc.RpcProxy" factory-method="getService">
		<constructor-arg type="java.lang.Class" value="org.zbus.rpc.biz.Interface"/> 
		<constructor-arg>
			<bean class="org.zbus.client.rpc.RpcConfig">
				<property name="broker" ref="broker"/> 
				<property name="mq" value="MyRpc"/>
			</bean>
		</constructor-arg>
	</bean>
 

**Spring完成zbus代理透明化，zbus设施从你的应用逻辑中彻底消失**

	public static void main(String[] args) { 
		ApplicationContext context = new ClassPathXmlApplicationContext("ZbusSpringClient.xml");
		
		Interface intf = (Interface) context.getBean("interface");
		
		System.out.println(intf.listMap());
	}
	





# ZBUS消息协议 

## 协议概览 
ZBUS协议继承于HTTP协议格式，主体采用HTTP头部协议扩展完成,HTTP协议由HTTP头部和HTTP包体组成，
ZBUS协议在HTTP头部KV值做了扩展，支持浏览器方式直接访问，但是ZBUS链接机制采用保持长连接方式。
原则上, 编写客户端SDK只需要遵循下面ZBUS协议扩展即可。

ZBUS扩展头部主要是完成

*   消息命令
*   ZBUS消息队列寻址
*	异步消息匹配
*	安全控制

扩展头部Key-Value解释

###1. 消息命令
命令标识，决定Broker（ZbusServer|TrackServer)的处理 

cmd: produce | consume | request | heartbeat | admin(默认值)

###2. 消息队列寻址

mq: 消息目标队列

mq-reply: 消息回复队列

###3. 异步消息匹配
msgid: 消息唯一UUID

msgid-raw: 原始消息唯一UUID, 消息消费路由后ID发生变化，该字段保留最原始的消息ID

###4. 安全控制
token: 访问控制码，不填默认空

###5. 其他可扩展
broker: 消息经过Broker的地址

topic: 消息主题，发布订阅时使用

ack: 是否需要对当前消息ACK，不填默认true

encoding: 消息体的编码格式

sub_cmd: 管理命令的二级命令


###6 HTTP头部第一行，ZBUS协议保持一致理解

请求：GET|POST URI

应答：200 OK 

URI做扩展Key-Value的字符串理解


## 协议细节 
 
### 生产者Produce

请求格式

*	[必填]cmd: produce 
*	[必填]mq: 目标队列 
*	[可选*]msgid: 消息UUID， 需要ACK时[必填]
*	[可选]mq-reply: 回复队列，默认为请求UUID。 需要应答的时候由mq_reply + msgid路由返回
*	[可选]topic: 发布订阅时发布消息的主题
*	[可选]token: 访问控制码
*	[可选]HTTP消息体 承载业务数据

应答格式（在启用ack的时候才有应答）

*	[可选]msgid: 消息UUID=请求消息UUID，客户端匹配使用


### 消费者Consume



请求格式

*	[必填]cmd: consume 
*	[必填]mq: 目标队列 
*	[必填]msgid: 消息UUID 
*	[可选]topic: 发布订阅时订阅感兴趣消息的主题
*	[可选]token: 访问控制码 

应答格式

*	[必填]msgid: 消息UUID，为了匹配消费请求
*	[必填]broker: 消息路由经历的Broker地址
*	[可选]mq-reply: 回复队列, 需要反馈结果的Consumer利用mq-reply指定目标消息队列
*	[可选]msgid-raw: 原始消息UUID，需要反馈结果的Consumer利用msgid-raw指定回复消息ID
*	[可选]HTTP消息体 承载业务数据



### 服务请求Request


请求格式

*	[必填]cmd: request 
*	[必填]mq: 目标队列 
*	[必填]msgid: 消息UUID
*	[必填]mq-reply: 回复队列，不制定的情况下默认为当前发送者的UUID 
*	[可选]token: 访问控制码
*	[可选]HTTP消息体 承载业务数据

应答格式（在启用ack的时候才有应答）

*	[必填]msgid: 消息UUID=请求消息UUID，客户端匹配使用
*	[可选]HTTP消息体 承载业务数据


### 监控管理


请求格式

*	[可选]cmd: admin，不填写默认为admin 
*	[可选]sub_cmd: create_mq
*	[可选*]msgid: 消息UUID, 客户端需要消息匹配时需指定
*	[可选]token: 访问控制码
*	[可选]HTTP消息体 承载业务数据

应答格式

*	[可选*]msgid: 消息UUID=请求消息UUID，客户端匹配使用
*	[可选]HTTP消息体 承载业务数据


### URI格式


URI = /   
 
监控首页 = /?cmd=admin&&method=index


URI = /MyMQ 

第一个?之前理解为消息队列 mq=MyMQ


*	/MyMQ?cmd=produce&&msgid=aed14-2343-1dea0-32&&body=xxxyyyzzz
*	/MyMQ?cmd=consume&&msgid=aed14-2343-1dea0-32
*	/MyMQ?cmd=request&&msgid=aed14-2343-1dea0-32

第一个?之后理解为Key-Value, URI的KV优先级低于头部扩展


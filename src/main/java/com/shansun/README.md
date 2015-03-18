# 基础模块

目前包含的功能有：

+ 工具类
+ Actor模型（异步处理框架）
+ 统一缓存（未开始）
+ 命令模板
+ 事件通知（同步 & 异步）
+ 统计计数
+ 开关模块

上层工程可以直接使用其中的功能，完成相应的工作。


## 工具类

### MoreToStringBuilder

扩展了common-lang里的ToStringBuilder。

ToStringBuilder支持在序列化对象时，忽略某些属性：

	ReflectionToStringBuilder.setExcludeFieldNames
	ReflectionToStringBuilder.toStringExclude

使用MoreToStringBuilder将支持在序列化时，仅包含指定的某些属性：

	MoreToStringBuilder.setIncludeFieldNames
	MoreToStringBuilder.toStringInclude

## Actor模型（异步处理框架）

Actor模型是参照[developerWorks](http://www.ibm.com/developerworks/cn/java/j-javaactors/index.html)上一篇文章修改实现。

每秒约可接受40万条消息。

具体用法如下：

	// Step1. 定义消息处理Actor
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

	// Step2. 设置并初始化Actor管理类
	ActorManager  actorManager = ActorManagerBuilder.newBuilder() //
					.withLogger(LoggerFactory.getLogger(IpmLogConstants.IpmMonitor)) //
					.withThreadCount(10) //
					.withRejectedMessageHandler(RejectedMessageHandlers.discardPolicy()) //
					.build();

	actorManager.initialize();

	// Step3. 注册Actor到管理类
	actorManager.createAndStartActor(DemoActor.class);

	// Step4. 发送消息到指定Actor
	actorManager.send(new SimpleMessage(System.currentTimeMillis()), null, "demo-actor");

## 命令模板

易用的命令模板，简单用法如下：
	
	public class CommandTest extends TestCase {

		class SimpleCommand extends AbstractCommand {
	
			@Override
			public boolean canExecute(Context context) throws CommandException {
				return context != null; // 检查入参，绝对是否接受参数
			}
	
			@Override
			public void doExecute(Context context) throws CommandException {
				System.out.println(context);
				context.addProperty("return", "success");
			}
	
			@Override
			public void redo() throws CommandException {
				throw new UnsupportedOperationException();
			}
	
			@Override
			public void undo() throws CommandException {
				throw new UnsupportedOperationException();
			}
		}
	
		class SimpleContext extends Context {
		}
	
		@Test
		public void test() throws CommandException {
			CommandTest test = new CommandTest();
	
			Context ctx = test.new SimpleContext();
			test.new SimpleCommand().execute(ctx);
	
			System.out.println(ctx.getProperty("return"));
		}
	}

## 事件通知
	
目前仅实现同步事件通知，异步事件可以考虑使用Actor方式

用法如下：
	
	（待补充）

## 统计计数

用法示例：

	CountStatistic stat = Statistics.getCountStat("Your-Stats-Name");
	stat.incr();
	long cnt = stat.getCount();

## 开关模块

	（未实现）

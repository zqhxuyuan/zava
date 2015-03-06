package com.interview.design.pattern.behavioral;


import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 下午3:16
 *
 * Command pattern is a data driven design pattern and falls under behavioral pattern category.
 * A request is wrapped under a object as command and passed to invoker object.
 * Invoker object looks for the appropriate object which can handle this command and pass the command to
 * the corresponding object and that object executes the command.
 *
 * 命令对象本身的设计可以是heavy or light的
 * light：命令本身只是指令，需要一个dispatcher将其分发到特定的handler去处理
 * heavy：命令本身就包括处理逻辑，只需要一个Broker将其执行以下
 *
 * 命令模式的目的就是达到命令的发出者和执行者之间解耦，实现请求和执行分开，
 *
 * 在下面的情况下应当考虑使用命令模式：
 * 1、使用命令模式作为"CallBack"在面向对象系统中的替代。"CallBack"讲的便是先将一个函数登记上，然后在以后调用此函数。
 * 2、需要在不同的时间指定请求、将请求排队。一个命令对象和原先的请求发出者可以有不同的生命期。换言之，原先的请求发出者可能已经不在了，而命令对象本身仍然是活动的。这时命令的接收者可以是在本地，也可以在网络的另外一个地址。命令对象可以在串形化之后传送到另外一台机器上去。
 * 3、系统需要支持命令的撤消(undo)。命令对象可以把状态存储起来，等到客户端需要撤销命令所产生的效果时，可以调用undo()方法，把命令所产生的效果撤销掉。命令对象还可以提供redo()方法，以供客户端在需要时，再重新实施命令效果。
 * 4、如果一个系统要将系统中所有的数据更新到日志里，以便在系统崩溃时，可以根据日志里读回所有的数据更新命令，重新调用Execute()方法一条一条执行这些命令，从而恢复系统在崩溃前所做的数据更新。
 * 5、一个系统需要支持交易(Transaction)。一个交易结构封装了一组数据更新命令。使用命令模式来实现交易结构可以使系统增加新的交易类型。
 *
 * 命令允许请求的一方和接收请求的一方能够独立演化，从而且有以下的优点：
 *  1. 命令模式使新的命令很容易地被加入到系统里。
 *  2. 允许接收请求的一方决定是否要否决（Veto）请求。
 *  3. 能较容易地设计-个命令队列。
 *  4. 可以容易地实现对请求的Undo和Redo。
 *  5. 在需要的情况下，可以较容易地将命令记入日志。
 *  6. 命令模式把请求一个操作的对象与知道怎么执行一个操作的对象分割开。
 *  7. 命令类与其他任何别的类一样，可以修改和推广。
 *  8. 你可以把命令对象聚合在一起，合成为合成命令。比如宏命令便是合成命令的例子。合成命令是合成模式的应用。
 *  9. 由于加进新的具体命令类不影响其他的类，因此增加新的具体命令类很容易。
 * 命令模式的缺点如下：
 *  使用命令模式会导致某些系统有过多的具体命令类。某些系统可能需要几十个，几百个甚至几千个具体命令类，这会使命令模式在这样的系统里变得不实际。
 *
 */
public class CommandPattern {

    static class LightCommandPattern{
        static interface Command {
            public void exe();
        }

        static class SimpleCommand implements Command {

            private Receiver receiver;

            public SimpleCommand(Receiver receiver) {
                this.receiver = receiver;
            }

            @Override
            public void exe() {
                receiver.action();
            }
        }

        static class Receiver {
            public void action(){
                System.out.println("command received!");
            }
        }

        static class Invoker {

            private Command command;

            public Invoker(Command command) {
                this.command = command;
            }

            public void action(){
                command.exe();
            }
        }

        public static void main(String[] args) {
            Receiver receiver = new Receiver();
            Command cmd = new SimpleCommand(receiver);
            Invoker invoker = new Invoker(cmd);
            invoker.action();
        }
    }

    static class HeavyCommandPattern{
        static interface Order {
            void execute();
        }

        static class Stock {

            private String name = "ABC";
            private int quantity = 10;

            Stock(String name, int quantity) {
                this.name = name;
                this.quantity = quantity;
            }

            public void buy(){
                System.out.println("Stock [ Name: "+name+", Quantity: " + quantity +" ] bought");
            }
            public void sell(){
                System.out.println("Stock [ Name: "+name+", Quantity: " + quantity +" ] sold");
            }
        }

        static class BuyStock implements Order {
            private Stock abcStock;

            public BuyStock(Stock abcStock){
                this.abcStock = abcStock;
            }

            public void execute() {
                abcStock.buy();
            }
        }

        static class SellStock implements Order {
            private Stock abcStock;

            public SellStock(Stock abcStock){
                this.abcStock = abcStock;
            }

            public void execute() {
                abcStock.sell();
            }
        }

        static class Broker {
            private List<Order> orderList = new ArrayList<Order>();

            public void takeOrder(Order order) {
                orderList.add(order);
            }

            public void placeOrders() {
                for (Order order : orderList) {
                    order.execute();
                }
                orderList.clear();
            }
        }

        public static void main(String[] args) {
            Stock abcStock = new Stock("Apple", 100);

            BuyStock buyStockOrder = new BuyStock(abcStock);
            SellStock sellStockOrder = new SellStock(abcStock);

            Broker broker = new Broker();
            broker.takeOrder(buyStockOrder);
            broker.takeOrder(sellStockOrder);

            broker.placeOrders();
        }
    }
}

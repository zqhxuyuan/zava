package com.interview.design.pattern.behavioral;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 下午5:14
 *
 * In Visitor pattern, we use a visitor class which changes the executing algorithm of an element class.
 * By this way, execution algorithm of element can varies as visitor varies.
 * As per the pattern, element object has to accept the visitor object so that visitor object handles
 * the operation on the element object.
 *
 * 访问者模式把数据结构和作用于结构上的操作解耦合，使得操作集合可相对自由地演化。
 * 访问者模式适用于数据结构相对稳定算法又易变化的系统。因为访问者模式使得算法操作增加变得容易。
 * 若系统数据结构对象易于变化，经常有新的数据对象增加进来，则不适合使用访问者模式。
 * 访问者模式的优点是增加操作很容易，因为增加操作意味着增加新的访问者。
 * 访问者模式将有关行为集中到一个访问者对象中，其改变不影响系统数据结构。其缺点就是增加新的数据结构很困难
 * 简单来说，访问者模式就是一种分离对象数据结构与行为的方法，通过这种分离，可达到为一个被访问者动态添加新的操作而无需做其它的修改的效果。
 *
 * 该模式适用场景：
 *  如果我们想为一个现有的类增加新功能，不得不考虑几个事情：
 *  1、新功能会不会与现有功能出现兼容性问题？
 *  2、以后会不会再需要添加？
 *  3、如果类不允许修改代码怎么办？
 *  面对这些问题，最好的解决方法就是使用访问者模式，访问者模式适用于数据结构相对稳定的系统，把数据结构和算法解耦，
 */
public class VisitorPattern {
    static interface ComputerPartVisitor{
        public void visit(Computer computer);
        public void visit(Mouse mouse);
        public void visit(Keyboard keyboard);
        public void visit(Monitor monitor);
    }
    static interface ComputerPart {
        public void accept(ComputerPartVisitor computerPartVisitor);
    }

    static class Keyboard  implements ComputerPart {

        @Override
        public void accept(ComputerPartVisitor computerPartVisitor) {
            computerPartVisitor.visit(this);
        }
    }

    static class Monitor  implements ComputerPart {

        @Override
        public void accept(ComputerPartVisitor computerPartVisitor) {
            computerPartVisitor.visit(this);
        }
    }

    static class Mouse  implements ComputerPart {

        @Override
        public void accept(ComputerPartVisitor computerPartVisitor) {
            computerPartVisitor.visit(this);
        }
    }

    static class Computer implements ComputerPart {
        ComputerPart[] parts;

        public Computer(){
            parts = new ComputerPart[] {new Mouse(), new Keyboard(), new Monitor()};
        }


        @Override
        public void accept(ComputerPartVisitor computerPartVisitor) {
            for (int i = 0; i < parts.length; i++) {
                parts[i].accept(computerPartVisitor);
            }
            computerPartVisitor.visit(this);
        }
    }

    static class ComputerPartDisplayVisitor implements ComputerPartVisitor {

        @Override
        public void visit(Computer computer) {
            System.out.println("Displaying Computer.");
        }

        @Override
        public void visit(Mouse mouse) {
            System.out.println("Displaying Mouse.");
        }

        @Override
        public void visit(Keyboard keyboard) {
            System.out.println("Displaying Keyboard.");
        }

        @Override
        public void visit(Monitor monitor) {
            System.out.println("Displaying Monitor.");
        }
    }

    static class ComputerPartClickVisitor implements ComputerPartVisitor {

        @Override
        public void visit(Computer computer) {
            System.out.println("Clicking Computer.");
        }

        @Override
        public void visit(Mouse mouse) {
            System.out.println("Clicking Mouse.");
        }

        @Override
        public void visit(Keyboard keyboard) {
            System.out.println("Clicking Keyboard.");
        }

        @Override
        public void visit(Monitor monitor) {
            System.out.println("Clicking Monitor.");
        }
    }

    public static void main(String[] args) {

        ComputerPart computer = new Computer();
        computer.accept(new ComputerPartDisplayVisitor());
        computer.accept(new ComputerPartClickVisitor());
    }

}

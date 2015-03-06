package com.interview.design.pattern.structural;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 上午11:51
 * Facade pattern hides the complexities of the system and provides an interface to the client using
 * which the client can access the system.
 * This pattern involves a single class which provides simplified methods which are required by client and
 * delegates calls to existing system classes methods.
 *
 * 外观模式是为了解决类与类之家的依赖关系的，像spring一样，可以将类和类之间的关系配置到配置文件中，而外观模式就是将他们的关系放在一个Facade类中，
 * 降低了类类之间的耦合度，该模式中没有涉及到接口
 *
 */
public class FacadePattern {
    static class CPU {

        public void startup(){
            System.out.println("cpu startup!");
        }

        public void shutdown(){
            System.out.println("cpu shutdown!");
        }
    }

    static class Memory {

        public void startup(){
            System.out.println("memory startup!");
        }

        public void shutdown(){
            System.out.println("memory shutdown!");
        }
    }

    static class Disk {

        public void startup(){
            System.out.println("disk startup!");
        }

        public void shutdown(){
            System.out.println("disk shutdown!");
        }
    }

    static class Computer {
        private CPU cpu;
        private Memory memory;
        private Disk disk;

        public Computer(){
            cpu = new CPU();
            memory = new Memory();
            disk = new Disk();
        }

        public void startup(){
            System.out.println("start the computer!");
            cpu.startup();
            memory.startup();
            disk.startup();
            System.out.println("start computer finished!");
        }

        public void shutdown(){
            System.out.println("begin to close the computer!");
            cpu.shutdown();
            memory.shutdown();
            disk.shutdown();
            System.out.println("computer closed!");
        }
    }

    public static void main(String[] args) {
        Computer computer = new Computer();
        computer.startup();
        computer.shutdown();
    }
}

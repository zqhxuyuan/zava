package com.interview.design.pattern.structural;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 下午12:05
 *
 * Bridge is used where we need to decouple an abstraction from its implementation so that the two can vary independently.
 * This pattern involves an interface which acts as a bridge which makes the functionality of concrete classes independent
 * from interface implementer classes. Both types of classes can be altered structurally without affecting each other.
 *
 * 桥接模式就是把事物和其具体实现分开，使他们可以各自独立的变化。
 * 桥接的用意是：将抽象化与实现化解耦，使得二者可以独立变化，像我们常用的JDBC桥DriverManager一样，
 * JDBC进行连接数据库的时候，在各个数据库之间进行切换，基本不需要动太多的代码，甚至丝毫不用动，原因就是JDBC提供统一接口，
 * 每个数据库提供各自的实现，用一个叫做数据库驱动的程序来桥接就行了。
 */
public class BridgePattern {

    static interface DrawAPI {
        public void drawCircle(int radius, int x, int y);
    }

    static class RedCircle implements DrawAPI {
        @Override
        public void drawCircle(int radius, int x, int y) {
            System.out.println("Drawing Circle[ color: red, radius: "
                    + radius +", x: " +x+", "+ y +"]");
        }
    }

    static class GreenCircle implements DrawAPI {
        @Override
        public void drawCircle(int radius, int x, int y) {
            System.out.println("Drawing Circle[ color: green, radius: "
                    + radius +", x: " +x+", "+ y +"]");
        }
    }

    /**
     * shape is a bridge to decouple an abstraction DrawAPI from its implementation RedCircle and GreenCircle
     */
    static abstract class Shape {
        protected DrawAPI drawAPI;
        protected Shape(DrawAPI drawAPI){
            this.drawAPI = drawAPI;
        }
        public abstract void draw();
    }

    static class Circle extends Shape {
        private int x, y, radius;

        public Circle(int x, int y, int radius, DrawAPI drawAPI) {
            super(drawAPI);
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        public void draw() {
            drawAPI.drawCircle(radius,x,y);
        }
    }

    public static void main(String[] args) {
        Shape redCircle = new Circle(100,100, 10, new RedCircle());
        Shape greenCircle = new Circle(100,100, 10, new GreenCircle());

        redCircle.draw();
        greenCircle.draw();
    }
}

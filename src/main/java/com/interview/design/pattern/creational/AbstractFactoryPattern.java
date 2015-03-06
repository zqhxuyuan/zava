package com.interview.design.pattern.creational;

/**
 * Created_By: stefanie
 * Date: 14-12-2
 * Time: 下午9:05
 *
 * Abstract Factory patterns works around a super-factory which creates other factories.
 * This factory is also called as Factory of factories.
 * In Abstract Factory pattern an interface is responsible for creating a factory of related objects,
 * without explicitly specifying their classes. Each generated factory can give the objects as per the Factory pattern.
 *
 * The difference of AbstractFactoryPattern and FactoryPattern is:
 * FactoryPattern: each Factory create one type of Object
 * AbstractPattern: each Factory create one series of Object, and have a AbstractFactory get all type of factory together.
 *      usually, need parse in a type flag to identify which type of Object/Factory needed.
 */
public class AbstractFactoryPattern {

    public static interface Shape {
        void draw();
    }

    public static class Rectangle implements Shape {

        @Override
        public void draw() {
            System.out.println("Inside Rectangle::draw() method.");
        }
    }

    public static class Square implements Shape {

        @Override
        public void draw() {
            System.out.println("Inside Square::draw() method.");
        }
    }

    public static interface Color {
        void fill();
    }

    public static class Red implements Color {

        @Override
        public void fill() {
            System.out.println("Inside Red::fill() method.");
        }
    }

    public static class Green implements Color {

        @Override
        public void fill() {
            System.out.println("Inside Green::fill() method.");
        }
    }

    public static abstract class AbstractFactory {
        abstract Color getColor(String color);
        abstract Shape getShape(String shape) ;
    }

    public static class FactoryProducer {
        public static AbstractFactory getFactory(String choice){
            if(choice.equalsIgnoreCase("SHAPE")){
                return new ShapeFactory();
            } else if(choice.equalsIgnoreCase("COLOR")){
                return new ColorFactory();
            }
            return null;
        }
    }

    public static class ShapeFactory extends AbstractFactory {

        @Override
        public Shape getShape(String shapeType){
            if(shapeType == null){
                return null;
            } else if(shapeType.equalsIgnoreCase("RECTANGLE")){
                return new Rectangle();
            } else if(shapeType.equalsIgnoreCase("SQUARE")){
                return new Square();
            }
            return null;
        }

        @Override
        Color getColor(String color) {
            return null;
        }
    }

    public static class ColorFactory extends AbstractFactory {

        @Override
        public Shape getShape(String shapeType){
            return null;
        }

        @Override
        Color getColor(String color) {
            if(color == null){
                return null;
            }
            if(color.equalsIgnoreCase("RED")){
                return new Red();
            } else if(color.equalsIgnoreCase("GREEN")){
                return new Green();
            }
            return null;
        }
    }

}

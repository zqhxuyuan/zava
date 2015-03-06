package com.interview.basics.java;


import java.lang.reflect.*;

/**
 * Created_By: stefanie
 * Date: 14-12-7
 * Time: 下午4:45
 */

class Rectangle {
    private double x;
    private double y;

    public Rectangle(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double area(){
        return x * y;
    }

    private void doubleSize(){
        x *= 2;
        y *= 2;
    }
}

class Person {
    private String name;
    private String id;
    private String[] repos;

    public Person(String name, String id, String[] repos) {
        this.name = name;
        this.id = id;
        this.repos = repos;
    }
}

/**
 * Java dump method. It takes any object as a parameter and uses the Java reflection API print out every field name and value.
 */
class JavaDumpper{

    public static String dump(Object o, int callCount) {
        StringBuffer tabs = new StringBuffer();
        for (int k = 0; k < callCount; k++) {
            tabs.append("\t");
        }
        StringBuffer buffer = new StringBuffer();
        Class oClass = o.getClass();
        if (oClass.isArray()) {
            buffer.append("\n");
            buffer.append(tabs.toString());
            buffer.append("[");
            for (int i = 0; i < Array.getLength(o); i++) {
                if (i > 0)
                    buffer.append(",");
                Object value = Array.get(o, i);
                dumpObject(value, buffer, callCount);
            }
            buffer.append(tabs.toString());
            buffer.append("]\n");
        } else {
            buffer.append("\n");
            buffer.append(tabs.toString());
            buffer.append("{\n");
            while (oClass != null) {
                Field[] fields = oClass.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    buffer.append(tabs.toString());
                    fields[i].setAccessible(true);
                    buffer.append(fields[i].getName());
                    buffer.append("=");
                    try {
                        Object value = fields[i].get(o);
                        if (value != null) {
                            dumpObject(value, buffer, callCount);
                        }
                    } catch (IllegalAccessException e) {
                        buffer.append(e.getMessage());
                    }
                    buffer.append("\n");
                }
                oClass = oClass.getSuperclass();
            }
            buffer.append(tabs.toString());
            buffer.append("}\n");
        }
        return buffer.toString();
    }

    public static void dumpObject(Object value, StringBuffer buffer, int callCount){
        if (value.getClass().isPrimitive() ||
                value.getClass() == java.lang.Long.class ||
                value.getClass() == java.lang.String.class ||
                value.getClass() == java.lang.Integer.class ||
                value.getClass() == java.lang.Boolean.class
                ) {
            buffer.append(value);
        } else {
            buffer.append(dump(value, callCount + 1));
        }
    }
}

public class ReflectionExample {

    public static void main(String[] args){
        /**
         * The code is using Java Reflection to create a Rectangle instance and call it area method, equivalent as
         *      Rectangle rectangle = new Rectangle(4.2, 3.9);
         *      Double area = rectangle.area();
         */
        Object[] doubleArgs = new Object[]{4.2, 3.9};
        try {
            Class rectangleDefinition = Class.forName("com.interview.basics.java.Rectangle");
            Class[] doubleArgsClass = new Class[]{double.class, double.class};
            Constructor doubleArgsConstructor = rectangleDefinition.getConstructor(doubleArgsClass);
            Rectangle rectangle = (Rectangle) doubleArgsConstructor.newInstance(doubleArgs);

            Method areaMethod = rectangleDefinition.getDeclaredMethod("area");
            Double area = (Double) areaMethod.invoke(rectangle);
            System.out.println(area);

            //will get a runtime exception of java.lang.IllegalAccessException
            //only when try to invoke private method or visit a private member.
            Method doubleSizeMethod = rectangleDefinition.getDeclaredMethod("doubleSize");
            doubleSizeMethod.invoke(rectangle);

            Field xfield = rectangleDefinition.getDeclaredField("x");
            System.out.println(xfield.get(rectangle));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        Person person = new Person("summer", "summerzhao", "interview,algorithm,tools".split(","));
        System.out.println(JavaDumpper.dump(person, 0));

    }
}

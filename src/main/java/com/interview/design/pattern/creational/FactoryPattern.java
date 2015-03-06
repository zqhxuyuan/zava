package com.interview.design.pattern.creational;

/**
 * Created_By: stefanie
 * Date: 14-12-2
 * Time: 下午8:43
 */
public class FactoryPattern {

    static interface Sender{
        public void send();
    }

    static class EMailSender implements Sender {

        @Override
        public void send() {
            System.out.println("send by email");
        }
    }

    static class SMSSender implements Sender {

        @Override
        public void send() {
            System.out.println("send by SMS");
        }
    }

    /**
     * Simple Factory Method
     * USING static method
     */
    static class SenderFactory{

        public static Sender produceMail(){
            return new EMailSender();
        }

        public static Sender produceSms(){
            return new SMSSender();
        }
    }

    /**
     *   Factory Pattern
     *      define a interface Factory, and implements Factory for each Object
     *      Open Close Principle: open for extension, close for modification
     */
    public interface Provider{
        public Sender produce();
    }

    public class EMailSenderProvider implements Provider{

        @Override
        public Sender produce() {
            return new EMailSender();
        }
    }

    public class SMSSenderProvider implements Provider{

        @Override
        public Sender produce() {
            return new SMSSender();
        }
    }


}

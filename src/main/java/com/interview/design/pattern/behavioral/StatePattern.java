package com.interview.design.pattern.behavioral;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 下午4:57
 *
 * In State pattern a class behavior changes based on its state.
 * In State pattern, we create objects which represent various states and
 * a context object whose behavior varies as its state object changes.
 *
 * 当对象的状态改变时，同时改变其行为，很好理解！
 * 就拿QQ来说，有几种状态，在线、隐身、忙碌等，每个状态对应不同的操作，而且你的好友也能看到你的状态，
 * 所以，状态模式就两点：1、可以通过改变状态来获得不同的行为。2、你的好友能同时看到你的变化。
 */
public class StatePattern {
    static interface Membership {
        public double getDiscount(double price);
    }

    static class SilverMembership implements Membership {
        @Override
        public double getDiscount(double price) {
            System.out.println("Get a 10% discount");
            return price * 0.9;
        }
    }

    static class GoldenMembership implements Membership {
        public final static double THRESHOLD = 1000;
        @Override
        public double getDiscount(double price) {
            System.out.println("Get a 20% discount");
            return price * 0.8;
        }
    }

    /**
     * the behavior of Context will change when state changes.
     */
    static class Member{
        private Membership membership;
        private double total;

        public Member(){
            membership = new SilverMembership();
        }

        public double buy(double price){
            double cost = membership.getDiscount(price);
            total += cost;
            System.out.println("Need to pay: " + cost);
            checkAccountUpgrade();
            return cost;
        }

        public void checkAccountUpgrade(){
            if(membership instanceof SilverMembership && total > GoldenMembership.THRESHOLD) {
                membership = new GoldenMembership();
                System.out.println("Upgrade to Golden Membership");
            }
        }
    }

    public static void main(String[] args) {
        Member member = new Member();
        member.buy(100);
        member.buy(500);
        member.buy(700);
        member.buy(500);
    }

}

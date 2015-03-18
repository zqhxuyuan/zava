package com.github.rfqu.so;

import java.util.Random;

/**
 * To illustrate discussion at 
 * http://stackoverflow.com/questions/13080042/the-right-way-to-make-java-function-objects-to-encapsulate-generic-functions
 * Author: Alexei Kaigorodov
 */
public class SoFun {
    static interface Unary<E,R> {
        R ap(E a) ;
    }
    static interface Binary<E,R> {
        R ap(E a, E b) ;
    }
    static interface Group<E> {
        E plus(E op1,E op2);
    }
    static interface Applier {
        <E> E ap (Group<E> gp, E x, E y);
    }
    static class PlusApplier implements Applier  {
        @Override
        public <E> E ap(Group<E> gp, E x, E y) {
            return gp.plus(x, y);
        }
    }
    static class Rational {
        int x, y; // x/y

        public Rational(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj==null) return false;
            if (!(obj instanceof Rational)) return false;
            Rational other=(Rational)obj;
            return x*other.y==y*other.x;
        }
    }
    
    static class Integers implements Group<Integer> {
        @Override
        public Integer plus(Integer op1, Integer op2) {
            return op1+op2;
        }
    }    

    static class Rationals implements Group<Rational> {
        @Override
        public Rational plus(Rational op1, Rational op2) {
            return new Rational(op1.x*op2.y+op1.y*op2.x, op1.y*op2.y);
        }
    }    
    static class F implements Unary<Integer, Rational> {
        public Rational ap (Integer x) { return new Rational(x,2); }
    }

    static class IntPlus implements Binary<Integer, Integer> {
        public Integer ap(Integer a, Integer b) { return a+b; }
    }
    static class IntEq implements Binary<Integer, Boolean> {
        public Boolean ap(Integer a, Integer b) { return a.equals(b); }
    }
    static class RatEq implements Binary<Rational, Boolean> {
        public Boolean ap(Rational a, Rational b) { return a.equals(b); }
    }
    
    static <E,R> boolean checkCommutesWith(
              Unary<E,R> f
            , Applier g
            , Binary<R,Boolean> eq
            , Group<E> domain, Group<R> range
            , E x, E y)
    {
        E g_ap1 = g.ap(domain, x, y);
        R f_ap_x = f.ap(x), f_ap_y = f.ap(y);
        R g_ap2 = g.ap(range, f_ap_x, f_ap_y);
        return eq.ap( f.ap(g_ap1), g_ap2);
    }

    public static void main(String[] args) {
        Random rand=new Random();
        boolean res = checkCommutesWith(new F(), new PlusApplier(), new RatEq(),
                new Integers(), new Rationals(), rand.nextInt(), rand.nextInt());
        System.out.println("res="+res);
    }
}

package com.github.rfqu.so;
import java.util.Random;

/**
 * To illustrate discussion at 
 * http://stackoverflow.com/questions/13080042/the-right-way-to-make-java-function-objects-to-encapsulate-generic-functions
 * Author: Alexei Kaigorodov
 * 
 * All the functionality is in value types (Int and Rational)
 * No separate Group or Ring classes used.
 * "Double dispatch" programming pattern is used.
 */
public class SoFun2 {
    static interface Unary<E,R> {
        R ap(E a) ;
    }
    static interface Binary<E,R> {
        R ap(E a, E b) ;
    }

    static interface Applier {
        Int ap (Int x, Int y);
        Rational ap (Rational x, Rational y);
    }

    static class PlusApplier implements Applier  {
        @Override
        public Int ap(Int x, Int y) {
            return x.plus(y);
        }

        @Override
        public Rational ap(Rational x, Rational y) {
            return x.plus(y);
        }
    }
    
    static class TimesApplier implements Applier  {
        @Override
        public Int ap(Int x, Int y) {
            return x.times(y);
        }

        @Override
        public Rational ap(Rational x, Rational y) {
            return x.times(y);
        }
    }

    static interface Applicable<E> {
        E apply(Applier applier, E y);
    }

    static class Int implements Applicable<Int> {
        int x; 

        public Int(int x) {
            this.x = x;
        }

        @Override
        public Int apply (Applier applier, Int other) {
            return applier.ap(this, other);
        }

        public Int plus(Int other) {
            return new Int(x+other.x);
        }

        public Int times(Int other) {
            return new Int(x*other.x);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Int)) return false;
            Int other=(Int)obj;
            return x==other.x;
        }
    }
    
    static class Rational  implements Applicable<Rational> {
        int x, y; // x/y

        public Rational(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public Rational apply(Applier applier, Rational other) {
            return applier.ap(this, other);
        }

        public Rational plus(Rational op2) {
            return new Rational(x*op2.y+y*op2.x, y*op2.y);
        }

        public Rational times(Rational op2) {
            return new Rational(x*op2.x, y*op2.y);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Rational)) return false;
            Rational other=(Rational)obj;
            return x*other.y==y*other.x;
        }
    }
    
    static class F implements Unary<Int, Rational> {
        public Rational ap (Int x) { return new Rational(x.x,2); }
    }

    static <E extends Applicable<E>, R extends Applicable<R>>
    boolean checkCommutesWith(
              Unary<E,R> f
            , Applier g
            , E x, E y)
    {
        E g_ap1 = x.apply(g, y);
        R f_ap_x = f.ap(x), f_ap_y = f.ap(y);
        R g_ap2 = f_ap_x.apply(g, f_ap_y);
        return f.ap(g_ap1).equals(g_ap2);
    }

    public static void main(String[] args) {
        Random rand=new Random();
        boolean res = checkCommutesWith(new F(), new PlusApplier(),
                new Int(rand.nextInt()), new Int(rand.nextInt()));
        System.out.println("res="+res);
        res = checkCommutesWith(new F(), new TimesApplier(),
                new Int(rand.nextInt()), new Int(rand.nextInt()));
        System.out.println("res="+res);
    }
}

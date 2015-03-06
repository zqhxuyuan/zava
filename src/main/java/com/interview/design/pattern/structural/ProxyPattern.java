package com.interview.design.pattern.structural;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 上午11:13
 *
 * In Proxy pattern, a class represents functionality of another class.
 * In Proxy pattern, we create object having original object to interface its functionality to outer world.
 *
 * 代理模式的应用场景：
 * 如果已有的方法在使用的时候需要对原有的方法进行改进，此时有两种办法：
 * 1、修改原有的方法来适应。这样违反了“对扩展开放，对修改关闭”的原则。
 * 2、就是采用一个代理类调用原有的方法，且对产生的结果进行控制。这种方法就是代理模式。
 * 使用代理模式，可以将功能划分的更加清晰，有助于后期维护！
 */
public class ProxyPattern {
    static class Pat{
        public static int CAT = 1;
        public static int DOG = 2;
        private int type;
        private String name;

        Pat(int type, String name) {
            this.type = type;
            this.name = name;
        }
    }

    static interface PatMarket{
        public List<Pat> availablePats();
    }

    static class RealPatMarket implements PatMarket {
        private List<Pat> pats = new ArrayList<Pat>();

        public List<Pat> availablePats(){
            return pats;
        }
    }

    static class DogProxy implements PatMarket {
        PatMarket market;
        public DogProxy(PatMarket market){
            this.market = market;
        }
        public List<Pat> availablePats(){
            List<Pat> dogs = new ArrayList<>();
            for(Pat pat : market.availablePats()){
                if(pat.type == Pat.DOG) dogs.add(pat);
            }
            return dogs;
        }
    }

    static class CatProxy implements PatMarket {
        PatMarket market;
        public CatProxy(PatMarket market){
            this.market = market;
        }
        public List<Pat> availablePats(){
            List<Pat> cats = new ArrayList<>();
            for(Pat pat : market.availablePats()){
                if(pat.type == Pat.CAT) cats.add(pat);
            }
            return cats;
        }
    }
}

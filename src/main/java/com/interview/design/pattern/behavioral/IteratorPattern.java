package com.interview.design.pattern.behavioral;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 下午3:01
 * Iterator pattern is very commonly used design pattern in Java and .Net programming environment.
 * This pattern is used to get a way to access the elements of a collection object in sequential manner
 * without any need to know its underlying representation.
 *
 * 迭代器模式就是顺序访问聚集中的对象，一般来说，集合中非常常见，如果对集合类比较熟悉的话，理解本模式会十分轻松。
 * 这句话包含两层意思：一是需要遍历的对象，即聚集对象，二是迭代器对象，用于对聚集对象进行遍历访问。
 */
public class IteratorPattern {
    static interface Iterator {
        public boolean hasNext();
        public Object next();
    }

    static interface Container {
        public Iterator getIterator();
    }

    static class NameRepository implements Container {
        public String names[] = {"Robert" , "John" ,"Julie" , "Lora"};

        @Override
        public Iterator getIterator() {
            return new NameIterator();
        }

        private class NameIterator implements Iterator {

            int index;

            @Override
            public boolean hasNext() {
                if(index < names.length){
                    return true;
                }
                return false;
            }

            @Override
            public Object next() {
                if(this.hasNext()){
                    return names[index++];
                }
                return null;
            }
        }
    }

    public static void main(String[] args) {
        NameRepository namesRepository = new NameRepository();

        for(Iterator iter = namesRepository.getIterator(); iter.hasNext();){
            String name = (String)iter.next();
            System.out.println("Name : " + name);
        }
    }
}

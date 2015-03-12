package hdgl.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

/**
 * java.lang.Iterable的辅助类
 *
 * @author elm
 *
 */
public final class IterableHelper {

    static abstract class SimpleCollection<E> implements Iterable<E> {

        public abstract int size();

        public abstract boolean contains(Object obj);

    }

    /**
     * <p>
     * 映射操作接口
     * </p>
     * <p>
     * 该函数需要将一个TIn类型的数据转化为TOut类型
     * </p>
     *
     * @author elm
     *
     * @param <TIn>
     *            输入数据类型
     * @param <TOut>
     *            输出数据类型
     */
    public static interface Map<TIn, TOut> {
        public TOut select(final TIn element);
    }

    /**
     * <p>
     * 聚集操作接口
     * </p>
     * <p>
     * 该函数计算两个T类型的聚集结果
     * </p>
     *
     * @author elm
     *
     * @param <T>
     *            聚集操作的类型
     */
    public static interface Aggregate<T> {
        public T aggregate(final T a1, final T a2);
    }

    /**
     * 辅助类是静态类，不可以继承或实例化
     */
    private IterableHelper() {

    }

    /**
     * 计算集合中元素的数量
     *
     * @param iterable
     *            集合
     * @return 集合中元素的数量
     */
    public static <T> int count(Iterable<? extends T> iterable) {
        if (iterable instanceof Collection) {
            return ((Collection<? extends T>) iterable).size();
        } else if (iterable instanceof SimpleCollection) {
            return ((SimpleCollection<? extends T>) iterable).size();
        } else {
            int i = 0;
            for (@SuppressWarnings("unused")
            T t : iterable) {
                ++i;
            }
            return i;
        }
    }

    /**
     * 测试该序列是否包含某个元素
     *
     * @param iterable
     *            输入序列
     * @param element
     *            测试的元素
     * @return 输入序列中数否有此元素
     */
    public static <T> boolean contains(Iterable<? extends T> iterable,
            Object element) {
        if (iterable instanceof Collection) {
            return ((Collection<? extends T>) iterable).contains(element);
        } else if (iterable instanceof SimpleCollection) {
            return ((SimpleCollection<? extends T>) iterable).contains(element);
        } else {
            for (T t : iterable) {
                if (element.equals(t)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * <p>
     * 映射操作
     * </p>
     * <p>
     * 该操作将序列中所有的元素传递给Selector回调函数， 并且返回经过Selector映射过的元素序列
     * </p>
     * <p>
     * 映射操作是懒惰求值的，即只有当输出的迭代器进行到了某一个元素时， 才会使用该元素调用Selector
     * </p>
     *
     * @param iterable
     *            输入的迭代器
     * @return 经过映射的迭代器
     */
    public static <TIn, TOut> Iterable<TOut> select(
            final Iterable<? extends TIn> iterable,
            final Map<TIn, TOut> selector) {
        return new Iterable<TOut>() {
            @Override
            public Iterator<TOut> iterator() {
                final Iterator<? extends TIn> inIterator = iterable.iterator();
                return new Iterator<TOut>() {
                    @Override
                    public boolean hasNext() {
                        return inIterator.hasNext();
                    }

                    @Override
                    public TOut next() {
                        return selector.select(inIterator.next());
                    }

                    @Override
                    public void remove() {
                        inIterator.remove();
                    }
                };
            }
        };
    }

    /**
     * <p>
     * 聚集操作
     * </p>
     * <p>
     * 该操作将序列中所有的元素转化为聚集类型，然后依次计算聚集函数， 返回最终的聚集值
     * </p>
     *
     * @param iterable
     *            原始序列
     * @param initial
     *            初始聚集值
     * @param mapper
     *            将序列元素转化为聚集值的方法
     * @param aggregator
     *            聚合方法
     * @return 最终聚集的结果
     */
    public static <TIn, TOut> TOut aggregate(Iterable<? extends TIn> iterable,
            TOut initial, Map<TIn, TOut> mapper, Aggregate<TOut> aggregator) {
        TOut v = initial;
        for (TIn elem : iterable) {
            v = aggregator.aggregate(v, mapper.select(elem));
        }
        return v;
    }

    /**
     * <p>
     * 选择操作
     * </p>
     * <p>
     * 返回一个新序列，包含输入序列中predicate为true的元素
     * </p>
     *
     * @param in
     *            输入序列
     * @param predicate
     *            测试输入序列的函数
     * @return 新序列，其中每个元素的predicate都为true
     */
    public static <T> Iterable<T> where(final Iterable<? extends T> in,
            final Map<T, Boolean> predicate) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    T current = null;
                    Iterator<? extends T> iter = findNext(in.iterator());

                    Iterator<? extends T> findNext(Iterator<? extends T> pos) {
                        while (pos.hasNext()) {
                            current = pos.next();
                            if (predicate.select(current)) {
                                return pos;
                            }
                        }
                        current = null;
                        return pos;
                    }

                    public boolean hasNext() {
                        return current != null;
                    }

                    public T next() {
                        T ret = current;
                        findNext(iter);
                        return ret;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    /**
     * 返回序列中所有的非空(null)项
     * @param <T> 序列的元素类型
     * @param input 输入的序列
     * @return 输出序列，其中不含有null项
     */
    public static <T> Iterable<T> notNull(final Iterable<? extends T> input) {
        return where(input, new Map<T, Boolean>() {
            @Override
            public Boolean select(T element) {
                return element != null;
            }
        });
    }

    /**
     * 按照类型筛选序列
     * @param <T> 序列的元素类型
     * @param input 输入序列
     * @param type 要筛选的类型
     * @return input的子序列，其中元素都是type类型的
     */
    public static <T> Iterable<T> ofType(final Iterable<? extends T> input, final Class<? extends T> type) {
        return where(input, new Map<T, Boolean>() {
            @Override
            public Boolean select(T element) {
                return type.isInstance(element);
            }
        });
    }

    public static <T> Iterable<T> concat(final Iterable<? extends T> in) {
    	return concat(in);
    }
    
    public static <T> Iterable<T> concat(final Iterable<? extends T> in1,final Iterable<? extends T> in2) {
    	return concat(in1,in2);
    }
    
    /**
     * 将若干个序列连接起来
     *
     * @param in
     *            输入序列
     * @return 一个新序列，首先会遍历in[0]中的元素，然后遍历in[1]中的元素，以此类推
     */
    @SuppressWarnings("unchecked")
    public static <T> Iterable<T> concat(final Iterable<? extends T>... in) {
        return new SimpleCollection<T>() {
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    int index = 0;
                    Iterator<? extends T>[] iters = getIterators();
                    Iterator<? extends T> current = getCurrent();

                    Iterator<T>[] getIterators() {
                        Vector<Iterator<? extends T>> iters = new Vector<Iterator<? extends T>>();
                        for (Iterable<? extends T> i : in) {
                            Iterator<? extends T> iter = i.iterator();
                            if (iter.hasNext()) {
                                iters.add(iter);
                            }
                        }
                        return iters.toArray(new Iterator[0]);
                    }

                    Iterator<? extends T> getCurrent() {
                        if (index >= iters.length) {
                            return null;
                        } else {
                            return iters[index];
                        }
                    }

                    public boolean hasNext() {
                        return index < iters.length - 1
                                || (current != null && current.hasNext());
                    }

                    public T next() {
                        if (current != null && current.hasNext()) {
                            return current.next();
                        } else {
                            if (index < iters.length - 1) {
                                index++;
                                current = iters[index];
                                return current.next();
                            } else {
                                return null;
                            }
                        }
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            @Override
            public int size() {
                int count = 0;
                for (Iterable<? extends T> i : in) {
                    count += count(i);
                }
                return count;
            }

            @Override
            public boolean contains(Object obj) {
                for (Iterable<? extends T> i : in) {
                    if (IterableHelper.contains(i, obj)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * 返回序列中第一个元素
     *
     * @param iter
     *            输入序列
     * @return 输入的第一个元素
     */
    public static <T> T first(Iterable<? extends T> iter) {
        return iter.iterator().next();
    }

    /**
     * 返回序列中最后一个元素
     *
     * @param iter
     *            输入序列
     * @return 输入的最后一个元素
     */
    public static <T> T last(Iterable<? extends T> iter) {
        Iterator<? extends T> itor = iter.iterator();
        T last = null;
        while (itor.hasNext()) {
            last = itor.next();
        }
        return last;
    }

    /**
     * 从当前序列中去除另一个序列的元素
     *
     * @param iter
     *            当前序列
     * @param exception
     *            被去除的集合
     * @return 当前序列的一个子序列，其中不包含exception的任意元素
     */
    public static <T> Iterable<T> except(final Iterable<? extends T> iter,
            final Collection<T> exception) {

        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {

                    Iterator<? extends T> itor = iter.iterator();
                    T elem = moveNext(itor);

                    T moveNext(Iterator<? extends T> itor) {
                        while (itor.hasNext()) {
                            T ptr = itor.next();
                            if (exception.contains(ptr)) {
                                continue;
                            } else {
                                return ptr;
                            }
                        }
                        return null;
                    }

                    @Override
                    public boolean hasNext() {
                        return elem != null;
                    }

                    @Override
                    public T next() {
                        T ret = elem;
                        elem = moveNext(itor);
                        return ret;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    /**
     * 从序列中随机取出count个元素
     *
     * @param iter
     *            输入序列
     * @param count
     *            取出的数量
     * @return 一个序列，如果输入序列的元素个数大于count，则此序列有count个元素，否则此序列与输入序列相等
     */
    public static <T> Iterable<? extends T> randomTake(
            final Iterable<? extends T> iter, final long count) {
        final long inputCount = count(iter);
        if (inputCount <= count) {
            return iter;
        } else {
            return new Iterable<T>() {
                @Override
                public Iterator<T> iterator() {
                    return new Iterator<T>() {
                        Iterator<? extends T> itor = iter.iterator();
                        long total = inputCount;
                        long taked = count;
                        T element = move(itor);

                        T move(Iterator<? extends T> itor) {
                            while (itor.hasNext()) {
                                T ptr = itor.next();
                                if (Math.random() < taked / (double) total) {
                                    taked--;
                                    total--;
                                    return ptr;
                                } else {
                                    total--;
                                }
                            }
                            return null;
                        }

                        @Override
                        public boolean hasNext() {
                            return element != null;
                        }

                        @Override
                        public T next() {
                            T ret = element;
                            element = move(itor);
                            return ret;
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }

            };
        }
    }

    @SuppressWarnings("unchecked")
	public static <TK,TV> java.util.Map<TK, TV> makeMap(Object... args){
    	if(args.length%2!=0){
    		throw new IllegalArgumentException();
    	}
    	java.util.Map<TK, TV> map = new HashMap<TK,TV>();
    	for(int i=0;i<args.length/2;i++){
    		map.put((TK)args[2*i], (TV)args[2*i+1]);
    	}
    	return map;
    }
    
    @SuppressWarnings("unchecked")
	public static <TV> java.util.Set<TV> makeSet(Object... args){
    	java.util.Set<TV> map = new HashSet<TV>();
    	for(Object o:args){
    		map.add((TV) o);
    	}
    	return map;
    }
}

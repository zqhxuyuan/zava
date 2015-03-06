package com.interview.books.svinterview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created_By: stefanie
 * Date: 14-12-14
 * Time: 下午5:07
 */
public class SV25_ComplexIterator<T> implements Iterator<T> {

    static interface Data<T>{
        public boolean isCollection();
        public Collection<Data<T>> getCollection();
        public T getElement();
    }

    private int _curIdx;
    private ArrayList<T> flatColl;

    public SV25_ComplexIterator(Collection<Data<T>> c){
        _curIdx = 0;
        flatColl = new ArrayList<>();
        flatElements(c);
    }

    private void flatElements(Collection<Data<T>> c){
        for(Data<T> item : c){
            if(item.isCollection()){
                flatElements(item.getCollection());
            } else {
                flatColl.add(item.getElement());
            }
        }
    }

    @Override
    public boolean hasNext() {
        return null != flatColl && _curIdx < flatColl.size();
    }

    @Override
    public T next() {
        if(null == flatColl || _curIdx >= flatColl.size())
            throw new NoSuchElementException();
        return flatColl.get(_curIdx++);
    }

    @Override
    public void remove() {
        throw new NoSuchMethodError();
    }

}

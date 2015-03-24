package com.github.stakafum.mapreduce;

/**
 * 根据key进行分组,一个key会有多个values. 多个value使用GroupedValues封装
 * @param <K>
 * @param <V>
 */
public class GroupedKeyValue<K, V> {
    K key;
    GroupedValues<V> gValues;


    GroupedKeyValue(K key){
        this.key = key;
        this.gValues = new GroupedValues<V>();
    }

    GroupedKeyValue(K key, GroupedValues<V> values){
        this.key = key;
        this.gValues = values;
    }


    void setKey(K key){
        this.key = key;
    }

    void addValue(V value){
        this.gValues.add(value);
    }

    void setValues(GroupedValues<V> gValues){
        this.gValues = gValues;
    }

    K getKey(){
        return this.key;
    }

    V getValue(){
        V value = null;
        if(this.gValues.hasValue())
            value = this.gValues.get();
        else{
            System.err.println("gValues has no values.");
            System.exit(0);
        }
        return value;
    }

    GroupedValues<V> getValues(){
        return this.gValues;
    }

}

package com.interview.basics.model.graph.generic;

import java.util.UUID;

/**
 * Created_By: zouzhile
 * Date: 10/16/13
 * Time: 10:24 AM
 */


public class Vertex<T> implements Comparable<Vertex> {

    VertexColor color;
    int distance = -1;
    T value = null;
    String id;
    Vertex parent;

    public Vertex(T value) {
        this(VertexColor.WHITE, value);
    }

    public Vertex(VertexColor color, T value) {
        this.id = UUID.randomUUID().toString();
        this.color = color;
        this.value = value;
    }

    public void setParent(Vertex parent) {
        this.parent = parent;
    }

    public Vertex getParent(){
        return this.parent;
    }

    public VertexColor getColor() {
        return color;
    }

    public void setColor(VertexColor color) {
        this.color = color;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public T getValue() {
        return value;
    }

    public String getInternalId() {
        return this.id;
    }

    @Override
    public int compareTo(Vertex o) {
        return this.id.compareTo(o.getInternalId());
    }

    @Override
    public boolean equals(Object o) {
        if(! (o instanceof Vertex))
             return false;
        return this.value  == ((Vertex)o).getValue();
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
}

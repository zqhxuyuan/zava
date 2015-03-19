package com.github.martinprillard.shavadoop.util;

import java.io.Serializable;

/**
 * 
 * @author martin prillard
 * 
 */
public class Pair implements Serializable {

    private static final long serialVersionUID = 1L;
    private String val1;
    private String val2;

    public Pair(String _val1, String _val2) {
        this.val1 = _val1;
        this.val2 = _val2;
    }

    public String getVal1() {
        return val1;
    }

    public String getVal2() {
        return val2;
    }

    @Override
    public String toString() {
        return "<" + val1 + ", " + val2 + ">";
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object arg0) {
        if (this == arg0) {
            return true;
        }
        if (arg0 == null) {
            return false;
        }
        if (getClass() != arg0.getClass()) {
            return false;
        }

        Pair other = (Pair) arg0;
        if (this.val1.equalsIgnoreCase(other.val1) && this.val2.equalsIgnoreCase(other.val2)) {
            return true;
        } else {
            return false;
        }
    }

}

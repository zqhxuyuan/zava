package io.qdb.kvstore.test;

/**
 * What we store for tests.
 */
public class ModelObject {

    public int version;
    public String name;

    public ModelObject() { }

    public ModelObject(String name) {
        this.name = name;
    }

    public ModelObject(String name, int version) {
        this(name);
        this.version = version;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ModelObject && name.equals(((ModelObject)o).name);
    }

    @Override
    public String toString() {
        return name + " v" + version;
    }
}

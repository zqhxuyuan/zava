package com.interview.flag.g;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 15-1-26
 * Time: 下午7:25
 */
public class G34_QuadtreeNode<T extends G34_QuadtreeNode.Rectangle> {
    static class Rectangle {
        int x;
        int y;
        int width;
        int height;

        Rectangle(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void print(){
            System.out.printf("(%d, %d) to (%d, %d) \n", x, y, x + width, y + height);
        }
    }

    private int MAX_OBJECTS = 10;
    private int MAX_LEVELS = 5;


    private int level;
    private List<T> objects;
    private Rectangle bounds;
    private G34_QuadtreeNode[] nodes;
    private boolean splited = false;


    public G34_QuadtreeNode(int level, Rectangle bounds) {
        this.level = level;
        this.objects = new ArrayList();
        this.bounds = bounds;
        this.nodes = new G34_QuadtreeNode[4];
    }

    /**
     * clears the quadtree by recursively clearing all objects from all nodes.
     */
    public void clear() {
        splited = false;
        objects.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }


    /**
     * Splits the node into 4 subnodes
     *
     *      1 | 0
     *      -----
     *      2 | 3
     */
    private void split() {
        this.splited = true;
        int subWidth = (int)(bounds.width / 2);
        int subHeight = (int)(bounds.height / 2);
        int x = bounds.x;
        int y = bounds.y;

        nodes[0] = new G34_QuadtreeNode(level+1, new Rectangle(x + subWidth, y, subWidth, subHeight));
        nodes[1] = new G34_QuadtreeNode(level+1, new Rectangle(x, y, subWidth, subHeight));
        nodes[2] = new G34_QuadtreeNode(level+1, new Rectangle(x, y + subHeight, subWidth, subHeight));
        nodes[3] = new G34_QuadtreeNode(level+1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
    }


    /**
     * Determine which node the object belongs to. -1 means
     * object cannot completely fit within a child node and is part
     * of the parent node
     */
    private int getIndex(T obj) {
        int index = -1;
        double verticalMidpoint = bounds.x + (bounds.width / 2);
        double horizontalMidpoint = bounds.y + (bounds.height / 2);

        // Object can completely fit within the top quadrants
        boolean topQuadrant = (obj.y <= horizontalMidpoint && obj.y + obj.height <= horizontalMidpoint);
        // Object can completely fit within the bottom quadrants
        boolean bottomQuadrant = (obj.y >= horizontalMidpoint);

        // Object can completely fit within the left quadrants
        if (obj.x <= verticalMidpoint && obj.x + obj.width <= verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        }
        // Object can completely fit within the right quadrants
        else if (obj.x >= verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }
        return index;
    }


    /**
     * Insert the object into the quadtree. If the node
     * exceeds the capacity, it will split and add all
     * objects to their corresponding nodes.
     */
    public void insert(T obj) {
        if (splited) {
            int index = getIndex(obj);

            if (index != -1) {
                nodes[index].insert(obj);
                return;
            }
        }

        objects.add(obj);

        if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }

            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(objects.get(i));
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                } else {
                    i++;
                }
            }
            if(objects.size() > MAX_OBJECTS) MAX_OBJECTS = Integer.MAX_VALUE;  //only split() and put in once.
        }
    }


    /**
     * Return all objects that could collide with the given object
     */
    public List retrieve(List<T> collisions, T obj) {
        if(splited){
            int index = getIndex(obj);
            if(index != -1) nodes[index].retrieve(collisions, obj);
            else {
                for(int i = 0; i < 4; i++) nodes[i].retrieve(collisions, obj);
            }
        }
        //add the object can't fit in the two subnodes.
        collisions.addAll(objects);

        return collisions;
    }


}

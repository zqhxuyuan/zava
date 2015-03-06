package com.interview.flag.g;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 15-1-26
 * Time: 下午7:51
 */
public class G34_CollisionDetection_Quadtree {
    G34_QuadtreeNode<G34_QuadtreeNode.Rectangle> tree;

    public G34_CollisionDetection_Quadtree(G34_QuadtreeNode.Rectangle bound){
       tree = new G34_QuadtreeNode(0, bound);
    }

    public void add(G34_QuadtreeNode.Rectangle rectangle){
        tree.insert(rectangle);
    }

    public List<G34_QuadtreeNode.Rectangle> collision(G34_QuadtreeNode.Rectangle rectangle){
        List candidates = new ArrayList();
        tree.retrieve(candidates, rectangle);

        Iterator<G34_QuadtreeNode.Rectangle> iterator = candidates.iterator();
        while(iterator.hasNext()){
            G34_QuadtreeNode.Rectangle item = iterator.next();
            if(!hasIntersection(rectangle, item)) iterator.remove();
        }
        return candidates;
    }

    private boolean hasIntersection(G34_QuadtreeNode.Rectangle r1, G34_QuadtreeNode.Rectangle r2){
        if(r1.y >= r2.y + r2.height || r2.y >= r1.y + r1.height || r1.x >= r2.x + r2.width || r2.x >= r1.x + r1.width) return false;
        return true;
    }

    public static void main(String[] args){
        G34_CollisionDetection_Quadtree detector = new G34_CollisionDetection_Quadtree(new G34_QuadtreeNode.Rectangle(0,0,10,10));
        detector.add(new G34_QuadtreeNode.Rectangle(0, 2, 3, 2));
        detector.add(new G34_QuadtreeNode.Rectangle(1, 6, 4, 3));
        detector.add(new G34_QuadtreeNode.Rectangle(7, 3, 2, 1));
        detector.add(new G34_QuadtreeNode.Rectangle(4, 5, 2, 4));
        detector.add(new G34_QuadtreeNode.Rectangle(6, 8, 1, 1));
        detector.add(new G34_QuadtreeNode.Rectangle(7, 7, 1, 2));
        detector.add(new G34_QuadtreeNode.Rectangle(2, 2, 2, 1));
        detector.add(new G34_QuadtreeNode.Rectangle(3, 8, 2, 1));
        detector.add(new G34_QuadtreeNode.Rectangle(0, 4, 4, 5));
        detector.add(new G34_QuadtreeNode.Rectangle(8, 1, 1, 3));
        detector.add(new G34_QuadtreeNode.Rectangle(7, 2, 1, 1));

        List<G34_QuadtreeNode.Rectangle> collision = detector.collision(new G34_QuadtreeNode.Rectangle(5,5,1,1));
        System.out.println("Collision for (5,5,6,6)");
        for(G34_QuadtreeNode.Rectangle rectangle : collision)  rectangle.print();
        //(4, 5) to (6, 9)

        collision = detector.collision(new G34_QuadtreeNode.Rectangle(3,3,4,4));
        System.out.println("Collision for (3,3,7,7)");
        for(G34_QuadtreeNode.Rectangle rectangle : collision) rectangle.print();
        //(0, 4) to (4, 9); (4, 5) to (6, 9); (1, 6) to (5, 9)

        collision = detector.collision(new G34_QuadtreeNode.Rectangle(3,3,6,5));
        System.out.println("Collision for (3,3,9,8)");
        for(G34_QuadtreeNode.Rectangle rectangle : collision) rectangle.print();
        //(0, 4) to (4, 9); (4, 5) to (6, 9); (1, 6) to (5, 9); (7, 7) to (8, 9); (8, 1) to (9, 4); (7, 3) to (9, 4)
    }
}

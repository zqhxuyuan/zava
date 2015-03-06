package com.interview.leetcode.utils;

/**
 * Created_By: stefanie
 * Date: 14-12-10
 * Time: 下午10:51
 */
public class Vector2D{
    double x;
    double y;

    public Vector2D(){

    }

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "Vector2D(" + x + ", " + y + ")";
    }

    // Compute magnitude of vector ....

    public double length() {
        return Math.sqrt ( x * x + y * y );
    }

    // Sum of two vectors ....

    public Vector2D add( Vector2D v1 ) {
        Vector2D v2 = new Vector2D( this.x + v1.x, this.y + v1.y );
        return v2;
    }

    // Subtract vector v1 from v .....

    public Vector2D sub( Vector2D v1 ) {
        Vector2D v2 = new Vector2D( this.x - v1.x, this.y - v1.y );
        return v2;
    }

    // Scale vector by a constant ...

    public Vector2D scale( double scaleFactor ) {
        Vector2D v2 = new Vector2D( this.x*scaleFactor, this.y*scaleFactor );
        return v2;
    }

    // Normalize a vectors length....

    public Vector2D normalize() {
        Vector2D v2 = new Vector2D();

        double length = Math.sqrt( this.x*this.x + this.y*this.y );
        if (length != 0) {
            v2.x = this.x/length;
            v2.y = this.y/length;
        }

        return v2;
    }

    // Dot product of two vectors .....

    public double dotProduct ( Vector2D v1 ) {
        return this.x*v1.x + this.y*v1.y;
    }

    // Exercise methods in Vector2D class

    public static void main ( String args[] ) {
        Vector2D vA = new Vector2D( 1.0, 2.0);
        Vector2D vB = new Vector2D( 2.0, 1.0);

        System.out.println( "Vector vA =" + vA.toString() );
        System.out.println( "Vector vB =" + vB.toString() );

        System.out.println( "Vector vA-vB =" + vA.sub(vB).toString() );
        System.out.println( "Vector vB-vA =" + vB.sub(vA).toString() );

        System.out.println( "vA.normalize() =" + vA.normalize().toString() );
        System.out.println( "vB.normalize() =" + vB.normalize().toString() );

        System.out.println( "Dot product vA.vB =" + vA.dotProduct(vB) );
        System.out.println( "Dot product vB.vA =" + vB.dotProduct(vA) );
    }
}

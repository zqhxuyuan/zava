package com.interview.basics.java;

public class InstanceInitializer {

	private int a = 10; // instance variable initializer
	//private int b = c; // <- This causes compiling error
	private int b = initB(); // b is set to 0, since c is not initialized yet.
    private int c;
    {
        c = 20; // this code block is called instance initializer
    }

    private int d = 2 * a;
    private int e = initE();
    private int h = initH();
    private int f;
    private int g;

    public InstanceInitializer(){
        f = a * 100;
        g = c * 100;
    }
	
	private int initB(){
		return 100 * c;
	}

    private int initE(){
        return 100 * a;
    }

    private int initH() { return 100 * c; }
	
	public int getA() {
		return a;
	}

	public int getB(){
		return this.b;
	}
	
	public int getC(){
		return this.c;
	}

    public int getD() {
        return d;
    }

    public int getE() {
        return e;
    }

    public int getF() {
        return f;
    }

    public int getG() {
        return g;
    }

    public int getH() {
        return h;
    }

    public static void main(String[] args) {
		InstanceInitializer init = new InstanceInitializer();
		System.out.println("A is initialized as: " + init.getA());
		System.out.println("B is initialized as: " + init.getB());
		System.out.println("C is initialized as: " + init.getC());
		System.out.println("D is initialized as: " + init.getD());
		System.out.println("E is initialized as: " + init.getE());
		System.out.println("H is initialized as: " + init.getH());
		System.out.println("F is initialized as: " + init.getF());
		System.out.println("G is initialized as: " + init.getG());
	}

}

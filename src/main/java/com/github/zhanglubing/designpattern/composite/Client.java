/*
 * The MIT License
 *
 * Copyright (c) 2012, Lubing Zhang.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.zhanglubing.designpattern.composite;

/**
 * Composite.
 * 
 * @author Lubing Zhang
 * 
 */
public class Client {

	public static void main(String[] args) {

		Composite root = new Composite("Root");

		Component a = new Leaf("A");

		Composite b = new Composite("B");
		Component b1 = new Leaf("B-1");
		Component b2 = new Leaf("B-2");
		Component b3 = new Leaf("B-3");
		b.addChild(b1);
		b.addChild(b2);
		b.addChild(b3);

		Component c = new Leaf("C");

		root.addChild(a);
		root.addChild(b);
		root.addChild(c);

		root.operation();

	}

}

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

package com.github.zhanglubing.designpattern.memento;

/**
 * Memento.
 * 
 * @author Lubing Zhang
 * 
 */
public class Client {

	public static void main(String[] args) {

		Caretaker caretaker = new Caretaker();

		Originator originator = new Originator("Init State");
		System.out.println(originator.getState());
		caretaker.setMemento("001", originator.createMemento());

		originator.setState("State A");
		System.out.println(originator.getState());
		caretaker.setMemento("002", originator.createMemento());

		originator.setState("State B");
		System.out.println(originator.getState());
		caretaker.setMemento("003", originator.createMemento());

		originator.restoreMemento(caretaker.getMemento("001"));
		System.out.println(originator.getState());
		
		originator.restoreMemento(caretaker.getMemento("002"));
		System.out.println(originator.getState());
		
		originator.restoreMemento(caretaker.getMemento("003"));
		System.out.println(originator.getState());

	}

}

package org.zbus.client.service;

import java.net.URL;
import java.net.URLClassLoader;

public class ContainerClassLoader extends ClassLoader {
	private ChildClassLoader childClassLoader;

	public ContainerClassLoader(URL[] urls) {
		super(Thread.currentThread().getContextClassLoader());
		childClassLoader = new ChildClassLoader(urls, new DetectClass(
				this.getParent()));
	}

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		try {
			if(name.startsWith("org.zbus")){ //ZBUS shared
				return super.loadClass(name, resolve);
			}
			return childClassLoader.findClass(name);
		} catch (ClassNotFoundException e) {
			return super.loadClass(name, resolve);
		}
	}

	private class ChildClassLoader extends URLClassLoader {
		private DetectClass realParent;

		public ChildClassLoader(URL[] urls, DetectClass realParent) {
			super(urls, null);
			this.realParent = realParent;
		}

		@Override
		public Class<?> findClass(String name) throws ClassNotFoundException {
			try {
				Class<?> loaded = super.findLoadedClass(name);
				if (loaded != null)
					return loaded;
				return super.findClass(name);
			} catch (ClassNotFoundException e) {
				return realParent.loadClass(name);
			}
		}
	}

	private class DetectClass extends ClassLoader {
		public DetectClass(ClassLoader parent) {
			super(parent);
		}

		@Override
		public Class<?> findClass(String name) throws ClassNotFoundException {
			return super.findClass(name);
		}
	} 

}

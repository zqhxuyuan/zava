package org.zbus.client.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClassScanner implements Scanner { 
	private final List<String> classpath = new ArrayList<String>();
	private final List<String> jarpath = new ArrayList<String>();
	
	public ClassScanner() { 
	}
	
	public void addClasspath(List<String> classpath) { 
		for (String cp : classpath) {
			this.addClasspath(cp);
		}
	}
	
	public void addClasspath(String classpath) {
		if (this.classpath.contains(classpath)) return ;
		this.classpath.add(classpath);
	}
	
	public void addJarpath(List<String> jarpath) { 
		for (String jp : jarpath) {
			this.addJarpath(jp);
		}
	}

	public void addJarpath(String jarpath) {
		if (this.jarpath.contains(jarpath)) return ;
		this.jarpath.add(jarpath);
	}

	public void scanClass(Listener listener) {
		for (String cp : classpath) {
			if (isBlank(cp)) continue;
			scanClassFromClassPath(cp, listener);
		} 
		
		for (String jp : jarpath) {
			List<File> jars = getJars(jp);
			for (File jar : jars) { 
				scanClassFromJar(jar, listener);
			} 
		} 
	}
	
	@Override
	public void scanJar(Listener listener) { 
		for (String jp : jarpath) {
			List<File> jars = getJars(jp);
			for (File jar : jars) { 
				ScanInfo info = new ScanInfo(); 
				info.jarpath = jar.getAbsolutePath();
				listener.onScanned(info);
			} 
		} 
	}
	
	public static void scanClassFromClassPath(final String classpath, Listener listener) {
		File dir = new File(classpath);
		List<File> files = getFilesFromDir(dir);
		for (File f : files) {
			String ext = getExt(f);
			if (!"class".equals(ext)) continue;
			String ap = f.getAbsolutePath().replace("\\", "/");
			if (ap.startsWith("/")) ap = new String(ap.substring(1));

			String cp = classpath.startsWith("/") ? new String(classpath.substring(1)) : classpath;
			String n = new String(ap.replace(cp, ""));
			String className = new String(n.replace("/", ".").replace(".class", ""));
			if (className.startsWith("."))
				className = new String(className.substring(1));
			
			ScanInfo info = new ScanInfo();
			info.className = className;
			info.classpath = classpath;
			listener.onScanned(info);
		}
	}
	
	public static void scanClassFromJar(final File jar, Listener listener) {
		if (jar == null)
			return;

		ZipInputStream zin = null;
		ZipEntry entry = null;
		try {
			zin = new ZipInputStream(new FileInputStream(jar));
			while ((entry = zin.getNextEntry()) != null) {
				String entryName = entry.getName().replace("\\", "/").replace('/', '.');
				if (!entryName.endsWith(".class"))
					continue;

				final String className = entryName.replace(".class", "");
				
				ScanInfo info = new ScanInfo();
				info.className = className;
				info.jarpath = jar.getAbsolutePath();
				listener.onScanned(info);

				zin.closeEntry();
			}
			zin.close();
		} catch (Throwable e) {
		}
	}
	
	public static List<File> getFilesFromDir(final File file) {
		List<File> files = new ArrayList<File>();
		if (!file.exists())
			return files;
		if (file.isFile()) {
			files.add(file);
		} else {
			File[] ff = file.listFiles();
			if (ff == null)
				return files;
			for (File f : ff) {
				files.addAll(getFilesFromDir(f));
			}
		}
		return files;
	}
	
	public static List<File> getJars(String jarpath) {
		List<File> jars = new ArrayList<File>(); 
		try {
			File jarDir = new File(jarpath);
			if (jarDir.isDirectory() && jarDir.exists()) {
				for (File jar : jarDir.listFiles()) {
					jars.add(jar);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return jars;
	}
 

	public static boolean isBlank(String str) {
		return str == null ? true : str.trim().length() == 0;
	}
	
	public static String getExt(File f) {
		return new String(f.getName().substring(f.getName().lastIndexOf(".") + 1));
	}
	
	public static String getExt(String name) {
		return new String(name.substring(name.lastIndexOf(".") + 1));
	}
}

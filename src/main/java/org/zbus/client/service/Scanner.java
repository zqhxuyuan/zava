package org.zbus.client.service;


public interface Scanner{
	public void addClasspath(String classpath);
	
	public void addJarpath(String jarpath);
	
	public void scanClass(Listener listener);
	
	public void scanJar(Listener listener);
	
	public static class ScanInfo {
		public String classpath;
		public String jarpath; 
		public String className;
		@Override
		public String toString() {
			return "ScanInfo [classpath=" + classpath + ", jarpath=" + jarpath
					+ ", className=" + className + "]";
		}
		
	}
	
	public static interface Listener {
		void onScanned(ScanInfo info); 
	}
}

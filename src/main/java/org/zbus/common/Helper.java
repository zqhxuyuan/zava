package org.zbus.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.channels.SocketChannel;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {   
	
	public static String getLocalIp() {
		try {
			Pattern pattern = Pattern.compile("(192|172|10)\\.[0-9]+\\.[0-9]+\\.[0-9]+");
			Enumeration<NetworkInterface> interfaces = NetworkInterface
					.getNetworkInterfaces();

			while (interfaces.hasMoreElements()) {
				NetworkInterface ni = interfaces.nextElement();
				Enumeration<InetAddress> en = ni.getInetAddresses();
				while (en.hasMoreElements()) {
					InetAddress addr = en.nextElement();
					String ip = addr.getHostAddress();
					Matcher matcher = pattern.matcher(ip);
					if (matcher.matches()) {
						return ip;
					}
				}
			} 
			return "0.0.0.0";
		} catch (Throwable e) {
			e.printStackTrace(); 
			return "0.0.0.0";
		}
	}
 
	public static InputStream loadFile(String resource, Class<?> clazz) {
		ClassLoader classLoader = null;
		try {
			Method method = Thread.class.getMethod("getContextClassLoader");
			classLoader = (ClassLoader) method.invoke(Thread.currentThread());
		} catch (Exception e) {
			System.out.println("loadConfigFile error: ");
			e.printStackTrace();
		}
		if (classLoader == null) {
			classLoader = clazz.getClassLoader();
		}
		try {
			if (classLoader != null) {
				URL url = classLoader.getResource(resource);
				if (url == null) {
					System.out.println("Can not find resource:" + resource);
					return null;
				}
				if (url.toString().startsWith("jar:file:")) { 
					return clazz.getResourceAsStream(resource.startsWith("/") ? resource : "/" + resource);
				} else { 
					return new FileInputStream(new File(url.toURI()));
				}
			}
		} catch (Exception e) {
			System.out.println("loadConfigFile error: ");
			e.printStackTrace();
		}
		return null;
	}  
	
	public static InputStream loadFile(String resource) {
		ClassLoader classLoader = null;
		try {
			Method method = Thread.class.getMethod("getContextClassLoader");
			classLoader = (ClassLoader) method.invoke(Thread.currentThread());
		} catch (Exception e) {
			System.out.println("loadConfigFile error: ");
			e.printStackTrace();
		}
		if (classLoader == null) {
			classLoader = Helper.class.getClassLoader();
		}
		try {
			if (classLoader != null) {
				URL url = classLoader.getResource(resource);
				if (url == null) {
					System.out.println("Can not find resource:" + resource);
					return null;
				}
				if (url.toString().startsWith("jar:file:")) { 
					return Helper.class.getResourceAsStream(resource.startsWith("/") ? resource : "/" + resource);
				} else { 
					return new FileInputStream(new File(url.toURI()));
				}
			}
		} catch (Exception e) {
			System.out.println("loadConfigFile error: ");
			e.printStackTrace();
		}
		return null;
	}  
	
	
	public static String loadFileContent(String resource) { 
		InputStream in = Helper.class.getClassLoader().
				getResourceAsStream(resource);
		if(in == null) return "";
		
		 Writer writer = new StringWriter(); 
         char[] buffer = new char[1024];
         try {
        	 BufferedReader reader = new BufferedReader(
                     new InputStreamReader(in, "UTF-8"));
             int n;
             while ((n = reader.read(buffer)) != -1) {
                 writer.write(buffer, 0, n);
             }
         } catch (UnsupportedEncodingException e) { 
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		} finally {
             try {
				in.close();
			} catch (IOException e) { 
				e.printStackTrace();
			}
         }
         return writer.toString();
	}  
	
	public static String option(Properties props, String opt, String defaultValue){
		String value = props.getProperty(opt, defaultValue);
		return value == null? null : value.trim();
	}
	
	public static int option(Properties props, String opt, int defaultValue){
		String value = option(props, opt, null);
		if(value == null) return defaultValue;
		return Integer.valueOf(value);
	}
	
	public static String option(String[] args, String opt, String defaultValue){
		for(int i=0; i<args.length;i++){
			if(args[i].equals(opt)){
				if(i<args.length-1) return args[i+1];
			} 
		}
		return defaultValue;
	}
	
	public static int option(String[] args, String opt, int defaultValue){
		String value = option(args, opt, null);
		if(value == null) return defaultValue;
		return Integer.valueOf(value);
	}
	
	public static boolean option(String[] args, String opt, boolean defaultValue){
		String value = option(args, opt, null);
		if(value == null) return defaultValue;
		return Boolean.valueOf(value);
	}
	
	
	public static String remoteAddress(SocketChannel channel){
		SocketAddress addr = channel.socket().getRemoteSocketAddress();
		String res = String.format("%s", addr);
		return addr==null? res: res.substring(1);
	}
	
	public static String localAddress(SocketChannel channel){
		SocketAddress addr = channel.socket().getLocalSocketAddress();
		String res = String.format("%s", addr);
		return addr==null? res: res.substring(1);
	}
	
	public static void main(String[] args){ 
		String ip = getLocalIp();
		System.out.println(ip);
	}
}

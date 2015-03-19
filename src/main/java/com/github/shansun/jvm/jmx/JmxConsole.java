package com.github.shansun.jvm.jmx;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * @author lanbo <br>
 * @version 1.0 <br>
 * @date 2012-11-3
 */
public class JmxConsole {
	static List<String> ipList = new ArrayList<String>();
	static String port = "2008";
	static String objectName = "com.taobao.wlb:type=wlb,id=multiWithholdSupport";
	static String methodName = "resetThreadPoolSize";
	static String[] arguments = new String[] {"10", "52", "200", "false"};

	/**
	 * @param args
	 * @throws IOException 
	 * @throws MBeanException 
	 * @throws ReflectionException 
	 * @throws MalformedURLException 
	 * @throws IntrospectionException 
	 * @throws InstanceNotFoundException 
	 * @throws MalformedObjectNameException 
	 */
	public static void main(String[] args) throws MalformedObjectNameException, InstanceNotFoundException, IntrospectionException, MalformedURLException, ReflectionException, MBeanException, IOException {
		ipList.add("10.232.21.134");
		
		for(String ip : ipList) {
			System.err.println(invokeJmx(ip, port, null, objectName, methodName, arguments));
		}
	}

	private static String invokeJmx(String ip, String port, String newPort, String objectName, String methodName, String[] arguments) throws MalformedURLException, IOException,
			MalformedObjectNameException, InstanceNotFoundException, IntrospectionException, ReflectionException, MBeanException {

		try {
			if ("other".equals(port)) {
				port = newPort;
			}

			JMXServiceURL address = new JMXServiceURL("service:jmx:rmi://" + ip + "/jndi/rmi://" + ip + ":" + port + "/defaultConnector");
			JMXConnector connector = JMXConnectorFactory.connect(address);
			MBeanServerConnection mbs = connector.getMBeanServerConnection();

			ObjectName mxbeanName = new ObjectName(objectName); // com.taobao.wlb:id=testJmxMBean,type=mywlb

			MBeanInfo mbeanInfo = mbs.getMBeanInfo(mxbeanName);
			MBeanOperationInfo[] opeInfos = mbeanInfo.getOperations();
			for (MBeanOperationInfo o : opeInfos) {
				if (o.getName().equals(methodName)) {
					int length = o.getSignature().length;
					Object[] params = new Object[length];
					String[] typeNames = new String[length];
					for (int i = 0; i < length; i++) {
						if ("boolean".equals(o.getSignature()[i].getType())) {
							params[i] = Boolean.valueOf(arguments[i]);
						} else if ("int".equals(o.getSignature()[i].getType())) {
							params[i] = Integer.valueOf(arguments[i]);
						} else if ("java.lang.Integer".equals(o.getSignature()[i].getType())) {
							params[i] = Integer.valueOf(arguments[i]);
						} else if ("long".equals(o.getSignature()[i].getType())) {
							params[i] = Long.valueOf(arguments[i]);
						} else if ("java.lang.Long".equals(o.getSignature()[i].getType())) {
							params[i] = Long.valueOf(arguments[i]);
						} else if ("double".equals(o.getSignature()[i].getType())) {
							params[i] = Double.valueOf(arguments[i]);
						} else if ("java.lang.Double".equals(o.getSignature()[i].getType())) {
							params[i] = Double.valueOf(arguments[i]);
						} else {
							params[i] = arguments[i];
						}
						typeNames[i] = o.getSignature()[i].getType();
					}

					Object result = mbs.invoke(mxbeanName, methodName, params, typeNames);
					if (result == null)
						return null;
					return result.toString();
				}
			}
			
			return "Jmx对象中没有该方法";
		} catch (Exception e) {
			e.printStackTrace();
			
			return "执行JMX异常：" + e.getMessage();
		}
	}

}

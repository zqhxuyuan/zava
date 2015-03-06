package org.zbus.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.zbus.rpc.biz.Interface;

public class ZbusSpringClient {

	public static void main(String[] args) { 
		ApplicationContext context = new ClassPathXmlApplicationContext("ZbusSpringClient.xml");
		
		Interface intf = (Interface) context.getBean("interface");
		
		System.out.println(intf.listMap());
	}

}

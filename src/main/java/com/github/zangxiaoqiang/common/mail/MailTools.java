package com.github.zangxiaoqiang.common.mail;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailTools {
	protected static final Logger LOG = LoggerFactory.getLogger(MailTools.class);

	public static String getLocalIP() {
		Enumeration<NetworkInterface> netInterfaces = null;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			LOG.warn("Can not get the local ip!");
			e.printStackTrace();
			return "Can not get server ip!";
		}
		Enumeration<InetAddress> ips = null;
		InetAddress ip = null;
		String localip = null;
		String netip = null;
		boolean found = false;
		while (netInterfaces.hasMoreElements() && !found) {
			NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
			ips = ni.getInetAddresses();
			while (ips.hasMoreElements()) {
				ip = ips.nextElement();
				if (!ip.isSiteLocalAddress() 
						&& !ip.isLoopbackAddress()
						&& ip.getHostAddress().indexOf(":") == -1) {
					netip = ip.getHostAddress();
					found = true;
					break;
				} else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
						&& ip.getHostAddress().indexOf(":") == -1) {
					localip = ip.getHostAddress();
				}
			}
		}
		if (netip != null && !"".equals(netip)) {
			return netip;
		} else {
			return localip;
		}
	}
}

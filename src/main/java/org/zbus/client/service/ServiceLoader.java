package org.zbus.client.service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.zbus.client.Broker;
import org.zbus.client.broker.SingleBroker;
import org.zbus.client.broker.SingleBrokerConfig;
import org.zbus.client.service.Scanner.Listener;
import org.zbus.client.service.Scanner.ScanInfo;
import org.zbus.common.Helper;
import org.zbus.common.logging.Logger;
import org.zbus.common.logging.LoggerFactory;

public class ServiceLoader {
	private static final Logger log = LoggerFactory
			.getLogger(ServiceLoader.class);
	private Broker broker;
	private ExecutorService executor = new ThreadPoolExecutor(4, 16, 120,
			TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

	public ServiceLoader(Broker broker) {
		this.broker = broker;
	}

	public void loadServicesFromBasePath(String basePath) {
		this.loadService(basePath);
		List<File> dirs = getDirs(basePath);
		for (File f : dirs) {
			this.loadService(f.getAbsolutePath());
		}
	}

	public void loadService(final ServiceProvider sp) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				ServiceConfig config = sp.getConfig();
				if (config.getBroker() == null) {
					config.setBroker(broker);
				}
				Service service = new Service(config);
				service.start();
			}
		});
	}

	public void loadService(String servicePath) {
		Scanner scanner = new ClassScanner();
		scanner.addJarpath(servicePath);
		log.info("load service from: " + servicePath);
		final List<URL> urls = new ArrayList<URL>();
		try {
			urls.add(new URL("file:" + servicePath));
		} catch (MalformedURLException e) {
			log.error(e.getMessage(), e);
		}
		scanner.scanJar(new Listener() {
			@Override
			public void onScanned(ScanInfo info) {
				try {
					log.info(info.toString());
					URL url = new URL("file:" + info.jarpath);
					urls.add(url);
				} catch (MalformedURLException e) {
					log.error(e.getMessage(), e);
				}
			}
		});

		final ContainerClassLoader classLoader = new ContainerClassLoader(
				urls.toArray(new URL[0]));
		scanner.scanClass(new Listener() {
			@Override
			public void onScanned(ScanInfo info) {
				try {
					Class<?> clazz = classLoader.loadClass(info.className);
					boolean isServiceProvider = false;
					for (Class<?> inf : clazz.getInterfaces()) {
						if (inf == ServiceProvider.class) {
							isServiceProvider = true;
							break;
						}
					}
					if (!isServiceProvider)
						return;
					log.info("service class found: " + clazz);
					final ServiceProvider sp = (ServiceProvider) clazz
							.newInstance();
					loadService(sp);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		});

	}

	public static List<File> getDirs(String basePath) {
		List<File> dirs = new ArrayList<File>();
		try {
			File base = new File(basePath);
			if (base.isDirectory() && base.exists()) {
				for (File dir : base.listFiles()) {
					if (dir.isDirectory() && dir.exists()) {
						dirs.add(dir);
					}
				}
			}
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
		return dirs;
	}

	public static void load(String serviceBase, String brokerAddress)
			throws Exception {
		
		if (serviceBase == null) {
			log.warn("mssing serviceBase directory");
			return;
		}
		File file = new File(serviceBase);
		if (file.exists()) {
			if (file.isDirectory()) {
				log.info(">>>ServiceBase: " + file.getAbsolutePath());
				SingleBrokerConfig config = new SingleBrokerConfig();
				config.setBrokerAddress(brokerAddress);
				Broker broker = new SingleBroker(config);

				ServiceLoader serviceLoader = new ServiceLoader(broker);
				serviceLoader.loadServicesFromBasePath(serviceBase);
			}
		} else {
			log.warn("!!!ServiceBase not exist: " + file.getAbsolutePath());
		}
	}

	public static void main(String[] args) throws Exception {
		String serviceBase = Helper.option(args, "-serviceBase", null);
		String brokerAddress = Helper
				.option(args, "-broker", "127.0.0.1:15555"); 
		load(serviceBase, brokerAddress);
	}
}
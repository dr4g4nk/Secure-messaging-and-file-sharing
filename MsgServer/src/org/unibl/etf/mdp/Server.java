package org.unibl.etf.mdp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import javax.net.ssl.SSLServerSocketFactory;

import org.unibl.etf.mdp.util.Logger;

public class Server {

	static ConcurrentHashMap<String, InetAddress> usersAddress = new ConcurrentHashMap<String, InetAddress>();

	public static void main(String[] args) {

		try (InputStream in = new FileInputStream(new File("resources" + File.separator + "server.config"))) {
			Properties prop = new Properties();
			prop.load(in);
			int port = Integer.valueOf(prop.getProperty("PORT"));

			String pathToJKS = prop.getProperty("PATH_TO_JKS");
			String passJKS = prop.getProperty("JKS_PASSWORD");

			System.setProperty("javax.net.ssl.keyStore", pathToJKS);
			System.setProperty("javax.net.ssl.keyStorePassword", passJKS);

			Thread monitor = new Thread() {
				public void run() {

					int port = Integer.valueOf(prop.getProperty("MONITORING_SERVER_PORT"));
					ServerSocket monitoringServer;
					try {
						monitoringServer = new ServerSocket(port);

						while (true) {

							new MonitorThread(monitoringServer.accept()).start();
						}

					} catch (IOException e) {
						Logger.log(Level.INFO, e.toString(), e);
					}
				};
			};
			monitor.setDaemon(true);
			monitor.start();

			SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			ServerSocket ss = factory.createServerSocket(port);

			System.out.println("Server pokrenut...");
			while (true) {
				try {
					new ServerThread(ss.accept()).start();
				} catch (IOException e) {
					Logger.log(Level.INFO, e.toString(), e);
				}
			}
		} catch (IOException e) {
			Logger.log(Level.INFO, e.toString(), e);
		}
	}
}

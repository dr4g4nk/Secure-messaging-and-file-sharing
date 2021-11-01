package org.unibl.etf.mdp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Level;

import org.unibl.etf.mdp.util.Logger;

public class MonitorThread extends Thread {

	private Socket socket;
	private int monitoringPort;
	private boolean end = false;

	public MonitorThread(Socket socket) {
		super();
		this.socket = socket;
		try (InputStream in = new FileInputStream(new File("resources" + File.separator + "server.config"))) {

			Properties prop = new Properties();
			prop.load(in);
			monitoringPort = Integer.valueOf(prop.getProperty("USER_PORT"));

		} catch (FileNotFoundException e) {
			Logger.log(Level.INFO, e.toString(), e);
		} catch (IOException e) {
			Logger.log(Level.INFO, e.toString(), e);
		}

	}

	public void run() {
		try (PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

			String username = in.readLine();

			InetAddress address = Server.usersAddress.get(username);
			if (address != null && monitoringPort != 0) {
				try (Socket s = new Socket(address, monitoringPort);
						PrintWriter outS = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);
						BufferedReader inS = new BufferedReader(new InputStreamReader(s.getInputStream()))) {

					new Thread() {
						public void run() {
							try {
								while (!"STOP".equals(in.readLine()));
								end = true;
							} catch (IOException e) {
								Logger.log(Level.INFO, e.toString(), e);
							}

						};
					}.start();
					while (!end) {

						String line = "";
						while (!"END".equals(line = inS.readLine())) {
							out.println(line);
						}
						out.println("END");

					}

					outS.println("STOP");
					
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						Logger.log(Level.INFO, e.toString(), e);
					}

				} catch (IOException e) {
					Logger.log(Level.INFO, e.toString(), e);
				}
			}

		} catch (IOException e) {
			Logger.log(Level.INFO, e.toString(), e);
		}
		
		try {
			socket.close();
		} catch (IOException e) {
			Logger.log(Level.INFO, e.toString(), e);
		}
	}

}

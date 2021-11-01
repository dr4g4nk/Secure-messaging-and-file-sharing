package application.controllers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;

import org.unibl.etf.model.util.Logger;
import org.unibl.etf.model.util.Util;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class UserScreenController {

	@FXML
	private ImageView imageView;

	private static String username;
	private boolean end = false;

	@FXML
	private void initialize() {

		Thread monitor = new Thread() {
			@Override
			public void run() {

				String address = Util.getProperty("MSG_SERVER");
				int port = Integer.valueOf(Util.getProperty("MONITOR_PORT"));
				try (Socket socket = new Socket(address, port);
						PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
						BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

					out.println(username);

					Gson gson = new Gson();
					while (!end) {

						String line = "";
						String content = "";

						while (!"END".equals(line = in.readLine())) {
							content += line;
						}

						if (!end) {
							ByteArrayInputStream bais = new ByteArrayInputStream(gson.fromJson(content, byte[].class));

							imageView.setImage(new Image(bais));
							bais.close();
						}

					}
					out.println("STOP");

					try {
						sleep(500);
					} catch (InterruptedException e) {
						Logger.log(Level.INFO, e.toString(), e);
					}

				} catch (UnknownHostException e) {
					Logger.log(Level.INFO, e.toString(), e);
				} catch (IOException e) {
					Logger.log(Level.INFO, e.toString(), e);
				}

			}
		};
		monitor.setDaemon(true);
		monitor.start();

		new Thread() {
			public void run() {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						imageView.getScene().getWindow().setOnCloseRequest(e -> {
							end = true;
							((Stage) imageView.getScene().getWindow()).close();
						});

					}
				});
			};
		}.start();

	}

	public static void setUsername(String str) {
		username = str;
	}
}

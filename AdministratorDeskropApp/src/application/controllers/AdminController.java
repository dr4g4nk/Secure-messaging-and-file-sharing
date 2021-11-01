package application.controllers;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.rpc.ServiceException;

import org.unibl.etf.AccountManager;
import org.unibl.etf.AccountManagerServiceLocator;
import org.unibl.etf.model.User;
import org.unibl.etf.model.Users;
import org.unibl.etf.model.util.Loader;
import org.unibl.etf.model.util.Logger;
import org.unibl.etf.model.util.Util;

import com.google.gson.Gson;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class AdminController {

	@FXML
	private CheckBox adminCh;

	@FXML
	private TextField usernameText;

	@FXML
	private PasswordField passwordText;

	@FXML
	private ComboBox<String> allUsers;

	@FXML
	private TextArea InformationsText;

	@FXML
	private ListView<String> activeUsers;

	@FXML
	private ComboBox<String> blockedUsers;

	private AccountManager service;
	private MessageDigest func;
	private WebTarget target;
	private Gson gson;
	private static String username;

	@FXML
	private void initialize() {
		target = Util.getTarget();
		gson = new Gson();
		AccountManagerServiceLocator loc = new AccountManagerServiceLocator();

		try {
			service = loc.getAccountManager();
			func = MessageDigest.getInstance("SHA256");

		} catch (NoSuchAlgorithmException e) {
			Logger.log(Level.INFO, e.toString(), e);
		} catch (ServiceException e) {
			Logger.log(Level.INFO, e.toString(), e);
		}

		Thread refreshAllUsers = new Thread() {
			public void run() {
				while (true) {
					refreshAllUsers();
					try {
						sleep(10000);
					} catch (InterruptedException e) {
						Logger.log(Level.INFO, e.toString(), e);
					}
				}
			}
		};

		Thread refreshAcitveUsers = new Thread() {
			public void run() {
				while (true) {
					refreshActiveUsers();
					try {
						sleep(2000);
					} catch (InterruptedException e) {
						Logger.log(Level.INFO, e.toString(), e);
					}
				}
			}
		};
		
		Thread refreshBlockedUsers = new Thread() {
			public void run() {
				while (true) {
					refreshBlockedUsers();
					try {
						sleep(2000);
					} catch (InterruptedException e) {
						Logger.log(Level.INFO, e.toString(), e);
					}
				}
			}
		};

		Platform.runLater(new Runnable() {

			public void run() {
				allUsers.getScene().getWindow().setOnCloseRequest(e -> {
					logout();
				});
			}
		});

		refreshAllUsers.setDaemon(true);
		refreshAcitveUsers.setDaemon(true);
		refreshBlockedUsers.setDaemon(true);
		refreshAllUsers.start();
		refreshAcitveUsers.start();
		refreshBlockedUsers.start();

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				InformationsText.setOnKeyPressed(e -> {
					if (e.getCode().equals(KeyCode.ENTER))
						sendAction();
				});
			}
		});
	}

	@FXML
	void activateUserAction() {
		String username = blockedUsers.getSelectionModel().getSelectedItem();
		if(username!=null) {
			try {
				service.unblockUser(username);
				Loader.showMessage("Nalog aktiviran.");
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@FXML

	private void logout() {
		Invocation.Builder inv = target.path("logout").request(MediaType.APPLICATION_JSON);
		Response response = inv.post(Entity.entity(new User(username), MediaType.APPLICATION_JSON));
		boolean bool = gson.fromJson(response.readEntity(String.class), Boolean.class);
		if (bool) {
			Loader.showMessage("Dovidjenja.");
			((Stage) activeUsers.getScene().getWindow()).close();
		} else {
			Loader.showMessage("Greška.");
		}
	}

	private void refreshAllUsers() {
		Invocation.Builder inv = target.path("users").path("all").request(MediaType.APPLICATION_JSON);

		Response response = inv.get();
		ObservableList<String> list = FXCollections
				.observableArrayList(gson.fromJson(response.readEntity(String.class), Users.class).getUsers());

		list.remove("Svi");
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				allUsers.setItems(list.sorted());
			}
		});
	}

	private void refreshActiveUsers() {
		Invocation.Builder inv = target.path("users").path("active").request(MediaType.APPLICATION_JSON);

		Response response = inv.get();
		ObservableList<String> list = FXCollections
				.observableArrayList(gson.fromJson(response.readEntity(String.class), Users.class).getUsers());

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				activeUsers.setItems(list);
			}
		});
	}
	
	private void refreshBlockedUsers() {
		Invocation.Builder inv = target.path("users").path("/blocked").request(MediaType.APPLICATION_JSON);

		Response response = inv.get();
		ObservableList<String> list = FXCollections
				.observableArrayList(gson.fromJson(response.readEntity(String.class), Users.class).getUsers());

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				blockedUsers.setItems(list);
			}
		});
	}

	@FXML
	void addUserAction() {

		String username = usernameText.getText();
		String password = new String(Base64.getEncoder().encode(func.digest(passwordText.getText().getBytes())));
		boolean isAdmin = adminCh.isSelected();
		try {
			if (service.createUser(username, password, isAdmin)) {
				Loader.showMessage("Uspješno ste dodali novi nalog.");
				usernameText.setText("");
				passwordText.setText("");
			}
			else
				Loader.showMessage("Nalog nije napravljen.");
		} catch (RemoteException e) {
			Loader.showMessage("Nalog nije napravljen.");
			e.printStackTrace();
		}

	}

	@FXML
	void blockUserAction() {

		String username = allUsers.getSelectionModel().getSelectedItem();
		try {
			if (username != null) {
				if (service.blockUser(username))
					Loader.showMessage("Nalog je blokiran.");
				else
					Loader.showMessage("Nalog nije blokiran.");
			}
		} catch (RemoteException e) {
			Loader.showMessage("Nalog nije blokiran.");
			e.printStackTrace();
		}

	}

	@FXML
	void showStatisticAction() {

		String username = activeUsers.getSelectionModel().getSelectedItem();
		if (username != null && !username.isEmpty()) {
			StatisticController.setUsername(username);
			Loader.openWindow(
					"bin" + File.separator + "application" + File.separator + "view" + File.separator
							+ "Statistic.fxml",
					"Statistika", 500, 545, true, true, new File("images" + File.separator + "Chat.png"));
		}
	}

	@FXML
	void sendAction() {
		new Thread() {
			@Override
			public void run() {

				int port = Integer.valueOf(Util.getProperty("MULTICAST_PORT"));
				String address = Util.getProperty("MULTICAST_ADDRESS");
				String msg = InformationsText.getText();
				if (msg.length() <= 1024) {
					byte[] bytes = msg.getBytes();
					try (DatagramSocket socket = new DatagramSocket()) {

						DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(address),
								port);

						socket.send(packet);
						InformationsText.setText("");

					} catch (UnknownHostException e) {
						Logger.log(Level.INFO, e.toString(), e);
					} catch (SocketException e) {
						Logger.log(Level.INFO, e.toString(), e);
					} catch (IOException e) {
						Logger.log(Level.INFO, e.toString(), e);
					}
				} else {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							Loader.showMessage("Maksimalna duzina poruke je 1024 karaktera.");
						}
					});
				}

			}
		}.start();
	}

	@FXML
	void sendOnEnterAction(KeyEvent event) {

		if (event.getCode().equals(KeyCode.ENTER))
			sendAction();

	}

	@FXML
	void showMonitorAction() {
		String username = activeUsers.getSelectionModel().getSelectedItem();
		if (username != null && !username.isEmpty()) {
			UserScreenController.setUsername(username);
			Loader.openWindow(
					"bin" + File.separator + "application" + File.separator + "view" + File.separator
							+ "UserScreen.fxml",
					"Ekran korisnika " + username, 890, 620, true, true,
					new File("images" + File.separator + "Chat.png"));
		} else
			Loader.showMessage("Korisnik nije selektovan.");
	}

	public static void setUsername(String str) {
		username = str;
	}
}

package application.controllers;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Base64;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.imgscalr.Scalr;
import org.unibl.etf.model.User;
import org.unibl.etf.model.Users;
import org.unibl.etf.model.util.Loader;
import org.unibl.etf.model.util.Logger;
import org.unibl.etf.model.util.Util;
import org.unibl.etf.rmi.RMIInterface;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.google.gson.Gson;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ChatController {

	@FXML
	private TextArea msgText;

	@FXML
	private ListView<String> allUsers;

	@FXML
	private ComboBox<String> usersCB;

	@FXML
	private Label fileLabel;

	@FXML
	private ListView<String> files;

	@FXML
	private TextArea informationsText;

	@FXML
	private TextArea inputText;

	@FXML
	private TextField filterTextField;

	private WebTarget target;

	private static String username;

	private Gson gson;
	private Boolean end = false;

	private SSLSocketFactory sf;
	private int rmiPort;
	private RMIInterface stub;
	private File file;
	private long maxSize = 524288000L;
	private ObservableList<String> list;
	private boolean start = false;
	private String msg;
	private int width, height;

	private static Object sync = new Object();

	@FXML
	private void initialize() {

		new Thread() {
			@Override
			public void run() {
				gson = new Gson();
				sf = (SSLSocketFactory) SSLSocketFactory.getDefault();

				int mPort = Integer.valueOf(Util.getProperty("MULTICAST_PORT"));
				String mAddress = Util.getProperty("MULTICAST_ADDRESS");
				Thread informations = new Thread() {
					public void run() {

						try (MulticastSocket mSocket = new MulticastSocket(mPort)) {
							mSocket.joinGroup(InetAddress.getByName(mAddress));
							while (true) {
								byte[] bytes = new byte[1024];
								DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
								mSocket.receive(packet);

								Platform.runLater(new Runnable() {

									@Override
									public void run() {

										String message;
										message = new String(packet.getData());
										informationsText.setText(message);

									}
								});

								File userDir = new File(System.getProperty("user.dir"), username);
								File informationsDir;
								File userFile;
								if (!userDir.exists()) {
									userDir.mkdir();
									informationsDir = new File(userDir, "informations");
									informationsDir.mkdir();
								} else {
									informationsDir = new File(userDir, "informations");
									if (!informationsDir.exists())
										informationsDir.mkdir();
								}

								userFile = new File(informationsDir, "" + System.currentTimeMillis());

								int count = informationsDir.listFiles().length % 4;

								if (count == 0)
									serializeWithJava(informationsText.getText(), userFile);
								else if (count == 1)
									serializeWithGson(informationsText.getText(), userFile);
								else if (count == 2)
									serializeWithXML(informationsText.getText(), userFile);
								else
									serializeWithKryo(informationsText.getText(), userFile);
							}
						} catch (IOException e) {
							Logger.log(Level.INFO, e.toString(), e);
						}
					}
				};
				informations.setDaemon(true);
				informations.start();
				sf = (SSLSocketFactory) SSLSocketFactory.getDefault();

				Thread refreshTextArea = new Thread() {

					public void run() {

						int port = Integer.valueOf(Util.getProperty("MSG_PORT"));
						String address = Util.getProperty("MSG_SERVER");

						try (SSLSocket socket = (SSLSocket) sf.createSocket(address, port);
								PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),
										true);
								BufferedReader in = new BufferedReader(
										new InputStreamReader(socket.getInputStream()))) {

							String response;

							out.println("HELLO");
							response = in.readLine();

							if ("HELLO".equals(response)) {
								out.println("USER " + username);
								if ("OK".equals(in.readLine())) {
									while (!end) {
										try {

											out.println("READ");

											if ("OK".equals(in.readLine())) {

												out.println("FROM " + allUsers.getSelectionModel().getSelectedItem());
												msg = "";
												if ("OK".equals(in.readLine())) {

													String line = "";
													while (!"END".equals(line = in.readLine()) && line != null) {
														msg += new String(Base64.getDecoder().decode(line));
													}
												}
											
												Platform.runLater(new Runnable() {

													@Override
													public void run() {
														msgText.setText(msg);
														msgText.setScrollTop(Double.MAX_VALUE);
													}
												});
											}
										} catch (IOException e) {
											Logger.log(Level.INFO, e.toString(), e);
										}
										try {
											sleep(350);
										} catch (InterruptedException e) {
											Logger.log(Level.INFO, e.toString(), e);
										}
									}
									out.println("QUIT");
									in.close();
									out.close();
									socket.close();
								}
							}
						} catch (UnknownHostException e) {
							Logger.log(Level.INFO, e.toString(), e);
						} catch (IOException e) {
							Logger.log(Level.INFO, e.toString(), e);
						}
					}
				};
			
//				String name = "FileService";
//				rmiPort = Integer.valueOf(Util.getProperty("RMI_PORT"));
//				Thread refreshFiles = null;
//				try {
//					Registry registry = LocateRegistry.getRegistry(Util.getProperty("RMI_SERVER"), rmiPort);
//					stub = (RMIInterface) registry.lookup(name);
//					refreshFiles = new Thread() {
//						public void run() {
//				
//							while (!end) {
//				
//								try {
//									refreshFiles();
//									sleep(3000);
//								} catch (InterruptedException e) {
//									Logger.log(Level.INFO, e.toString(), e);
//								}
//							}
//						}
//					};
//				} catch (RemoteException e) {
//					Logger.log(Level.INFO, e.toString(), e);
//				} catch (NotBoundException e) {
//					Logger.log(Level.INFO, e.toString(), e);
//				}

				Thread refreshUsers = new Thread() {

					public void run() {

						while (!end) {
							try {
								refreshUsers();
								sleep(5000);
							} catch (InterruptedException e) {
								Logger.log(Level.INFO, e.toString(), e);
							}
						}
					}
				};
				
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						allUsers.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
						allUsers.getSelectionModel().selectFirst();
						allUsers.getScene().getWindow().setOnCloseRequest(e -> {
							logout();
						});
					}
				});
				refreshTextArea.start();
//				refreshFiles.setDaemon(true);
//				refreshFiles.start();
				refreshUsers.setDaemon(true);
				refreshUsers.start();
				startMessageThread();
				startMonitoringThread();
			}
		}.start();

	}

	private boolean stop;

	private void startMonitoringThread() {
		Thread monitor = new Thread() {

			public void run() {

				width = Integer.valueOf(Util.getProperty("WIDTH"));
				height = Integer.valueOf(Util.getProperty("HEIGHT"));
				int port = Integer.valueOf(Util.getProperty("PORT"));
				ServerSocket ss;
				try {
					ss = new ServerSocket(port);
					Thread thread = new Thread() {
						@Override
						public void run() {
							boolean flag = true;
							while (flag) {
								if (end) {
									try {
										ss.setSoTimeout(1000);
										flag = false;
									} catch (SocketException e) {
										Logger.log(Level.INFO, e.toString(), e);
									}
								}
							}
						}
					};
					thread.setDaemon(true);
					thread.start();

					while (true) {
						stop = false;

						Socket s = ss.accept();

						try (PrintWriter monitorOut = new PrintWriter(new OutputStreamWriter(s.getOutputStream()),
								true);
								BufferedReader monitorIn = new BufferedReader(
										new InputStreamReader(s.getInputStream()))) {

							Robot robot;
							try {
								robot = new Robot();
								Gson gson = new Gson();

								new Thread() {
									public void run() {
										try {
											while (!"STOP".equals(monitorIn.readLine()));
											stop = true;
										} catch (IOException e) {
											e.printStackTrace();
										}

									};
								}.start();
								while (!stop) {

									BufferedImage imageBuff = robot.createScreenCapture(
											new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

									ByteArrayOutputStream baos = new ByteArrayOutputStream();

									BufferedImage img = Scalr.resize(imageBuff, width, height);
									ImageIO.write(img, "png", baos);

									monitorOut.println(gson.toJson(baos.toByteArray()));

									monitorOut.println("END");

									baos.close();
								}
								

							} catch (AWTException e) {
								Logger.log(Level.INFO, e.toString(), e);
							}
						} catch (IOException e) {
							Logger.log(Level.INFO, e.toString(), e);
						}
						s.close();
					}
				} catch (IOException e) {
					Logger.log(Level.INFO, e.toString(), e);
				}
			};
		};
		monitor.setDaemon(true);
		monitor.start();
	}

	private SSLSocket socket;
	private PrintWriter out;
	private BufferedReader in;

	private void startMessageThread() {

		new Thread() {
			@Override
			public void run() {
				int port = Integer.valueOf(Util.getProperty("MSG_PORT"));
				String address = Util.getProperty("MSG_SERVER");

				try {
					socket = (SSLSocket) sf.createSocket(address, port);
					out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

					out.println("HELLO");
					String response = in.readLine();

					if ("HELLO".equals(response)) {
						out.println("USER " + username);

						response = in.readLine();

						if ("OK".equals(response)) {
							start = true;
						}
					}

				} catch (IOException e) {
					Logger.log(Level.INFO, e.toString(), e);
				}
			}
		}.start();
	}

	private void refreshFiles() {
		try {

			ObservableList<String> list = FXCollections.observableArrayList(stub.getAllFiles(username));

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					files.setItems(list.sorted());
				}
			});

		} catch (RemoteException e) {
			Logger.log(Level.INFO, e.toString(), e);
		}
	}

	private void refreshUsers() {
		target = Util.getTarget();
		Invocation.Builder inv = target.path("users").path("all").request(MediaType.APPLICATION_JSON);

		Response response = inv.get();
		list = FXCollections
				.observableArrayList(gson.fromJson(response.readEntity(String.class), Users.class).getUsers());

		SortedList<String> users = list.sorted();

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				allUsers.setItems(FXCollections.observableArrayList(users));
				usersCB.setItems(users);
			}
		});
	}

	@FXML
	void changePasswordAction() {

		Loader.openWindow(
				"bin" + File.separator + "application" + File.separator + "view" + File.separator + "NewPassword.fxml",
				"Promjena lozinke", 413, 232, true, false, new File("images" + File.separator + "Chat.png"));

	}

	@FXML
	void chooseFileAction() {

		FileChooser chooser = new FileChooser();
		file = chooser.showOpenDialog(null);
		if (file != null)
			fileLabel.setText(file.getName());

	}

	@FXML
	void downloadAction() {
//
//		String fileName = files.getSelectionModel().getSelectedItem();
//		if (fileName != null) {
//
//			FileChooser chooser = new FileChooser();
//			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All files", "*.*");
//			chooser.getExtensionFilters().add(extFilter);
//			chooser.setInitialDirectory(new File(System.getProperty("user.home")));
//			chooser.setInitialFileName(fileName);
//			File file = chooser.showSaveDialog(null);
//			if (file != null) {
//				new Thread() {
//					public void run() {
//						try {
//							byte[] bytes = stub.getFile(username, fileName);
//							try (FileOutputStream out = new FileOutputStream(file)) {
//								out.write(bytes);
//							} catch (FileNotFoundException e) {
//								Logger.log(Level.INFO, e.toString(), e);
//							} catch (IOException e) {
//								Logger.log(Level.INFO, e.toString(), e);
//							}
//						} catch (RemoteException e) {
//							Logger.log(Level.INFO, e.toString(), e);
//						}
//					};
//				}.start();
//			}
//		}

	}

	private String filter = "";

	@FXML
	void filterAction() {

		filter = filterTextField.getText();
		synchronized (sync) {
			allUsers.setItems(FXCollections.observableArrayList(list.filtered(e -> e.startsWith(filter))));
		}
	}

	@FXML
	void logoutAction() {
		logout();
	}

	private void logout() {
		String str = username;
		if (!str.isEmpty()) {
			target = Util.getTarget();
			Invocation.Builder inv = target.path("logout").request(MediaType.APPLICATION_JSON);
			Response response = inv.post(Entity.json(new User(str)));

			Gson gson = new Gson();
			boolean bool = gson.fromJson(response.readEntity(String.class), Boolean.class);
			if (bool) {
				Loader.showMessage("Dovidjenja");
				((Stage) allUsers.getScene().getWindow()).close();
				end = true;

				try {
					out.println("QUIT");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						Logger.log(Level.INFO, e.toString(), e);
					}
					in.close();
					out.close();
					socket.close();
				} catch (IOException e) {
					Logger.log(Level.INFO, e.toString(), e);
				}

			} else
				Loader.showMessage("Greska");
			username = "";
		}
	}

	@FXML
	void sendFileAction() {
//
//		String toUser = usersCB.getSelectionModel().getSelectedItem();
//		if (toUser != null && !toUser.isEmpty()) {
//			if (file != null) {
//
//				new Thread() {
//					public void run() {
//
//						try {
//							long size = Files.size(Paths.get(file.getAbsolutePath()));
//
//							if (size <= maxSize) {
//								byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
//
//								if (stub.sendFile(toUser, file.getName(), Base64.getEncoder().encode(bytes)))
//									Platform.runLater(new Runnable() {
//
//										@Override
//										public void run() {
//											Loader.showMessage("Uspješno slanje.");
//										}
//									});
//							} else {
//
//								Platform.runLater(new Runnable() {
//
//									@Override
//									public void run() {
//										Loader.showMessage("Datoteka je prevelika. Maksimalna velicina 500 MB");
//									}
//								});
//							}
//						} catch (IOException e) {
//							Platform.runLater(new Runnable() {
//
//								@Override
//								public void run() {
//									Loader.showMessage("Greška. Datoteka nije poslana.");
//								}
//
//							});
//							Logger.log(Level.INFO, e.toString(), e);
//						}
//					};
//				}.start();
//
//			} else
//				Loader.showMessage("Niste unijeli datoteku.");
//		} else
//			Loader.showMessage("Niste selektovali korisnika pome saljete.");

	}

	@FXML
	void sendMsgAction() {

		if (start) {
			try {
				out.println("SEND");
				String line = allUsers.getSelectionModel().getSelectedItem();
				out.println("TO " + line);

				if ("MESSAGE".equals(in.readLine())) {

					out.println(new String(Base64.getEncoder().encodeToString(inputText.getText().getBytes())));
					out.println("END");

					inputText.setText("");
				}

			} catch (IOException e) {
				Logger.log(Level.INFO, e.toString(), e);
			}
		}
	}

	@FXML
	void sendOnEnterKey(KeyEvent event) {

		if (event.getCode().equals(KeyCode.ENTER)) {
			sendMsgAction();
		}
	}

	@FXML
	void showStatisticAction() {

		StatisticController.setUsername(username);
		Loader.openWindow(
				"bin" + File.separator + "application" + File.separator + "view" + File.separator + "Statistic.fxml",
				"Statistika", 500, 545, true, true, new File("images" + File.separator + "Chat.png"));

	}

	public static String getUsername() {
		return username;
	}

	public static void setUsername(String str) {
		username = str;
	}

	private void serializeWithJava(String message, File userFile) {

		new Thread() {
			@Override
			public void run() {

				try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(userFile))) {

					out.writeObject(message);

				} catch (FileNotFoundException e) {
					Logger.log(Level.INFO, e.toString(), e);
				} catch (IOException e) {
					Logger.log(Level.INFO, e.toString(), e);
				}
			}
		}.start();

	}

	private void serializeWithGson(String message, File userFile) {

		new Thread() {
			@Override
			public void run() {

				Gson gson = new Gson();
				try (PrintStream out = new PrintStream(new FileOutputStream(userFile))) {

					out.println(gson.toJson(message));

				} catch (FileNotFoundException e) {
					Logger.log(Level.INFO, e.toString(), e);
				}
			}
		}.start();
	}

	private void serializeWithXML(String message, File userFile) {

		new Thread() {
			@Override
			public void run() {

				try (XMLEncoder encoder = new XMLEncoder(new FileOutputStream(userFile))) {

					encoder.writeObject(message);

				} catch (FileNotFoundException e) {
					Logger.log(Level.INFO, e.toString(), e);
				}
			}
		}.start();
	}

	private void serializeWithKryo(String message, File userFile) {

		Kryo kryo = new Kryo();
		kryo.register(String.class);

		try (Output out = new Output(new FileOutputStream(userFile))) {

			kryo.writeClassAndObject(out, message);

		} catch (FileNotFoundException e) {
			Logger.log(Level.INFO, e.toString(), e);
		}

	}
}

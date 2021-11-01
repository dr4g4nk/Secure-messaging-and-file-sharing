package org.unibl.etf.mdp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Level;

import org.unibl.etf.mdp.util.Logger;


public class ServerThread extends Thread {

	private Socket socket;
	private String username;
	private String toUser;
	private String fromUser;
	private SimpleDateFormat formater;
	private File users;

	private static Object obj = new Object();

	public ServerThread(Socket s) {
		this.socket = s;
		formater = new SimpleDateFormat("HH:mm dd.MM.yyyy");
		users = new File("users");
		if(!users.exists())
			users.mkdir();
	}

	@Override
	public void run() {

		try (PrintWriter socketOut = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
				BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

			String response;
			response = socketIn.readLine();

			if ("HELLO".equals(response)) {

				socketOut.println("HELLO");
				username = socketIn.readLine();

				if (username.startsWith("USER ")) {
					username = username.replace("USER ", "");
					socketOut.println("OK");
					
					Server.usersAddress.put(username, socket.getInetAddress());
					System.out.println(username+" "+socket.getInetAddress());
					String option;
					while (!"QUIT".equals(option = socketIn.readLine())) {
						
						if ("SEND".equals(option)) {
							
							toUser = socketIn.readLine();

							if (toUser.startsWith("TO ")) {
								toUser = toUser.replace("TO ", "");

								File userDir, toUserDir;
								File msgToUser = null, msgUser = null;

								if (!"Svi".equals(toUser)) {

									boolean userExist, toUserExist;

									userExist = Arrays.stream(users.listFiles())
											.anyMatch(e -> username.equals(e.getName()) && e.isDirectory());
									toUserExist = Arrays.stream(users.listFiles())
											.anyMatch(e -> toUser.equals(e.getName()) && e.isDirectory());

									if (userExist && toUserExist) {
										toUserDir = Arrays.stream(users.listFiles())
												.filter(e -> toUser.equals(e.getName()) && e.isDirectory()).findFirst()
												.get();

										userDir = Arrays.stream(users.listFiles())
												.filter(e -> username.equals(e.getName()) && e.isDirectory())
												.findFirst().get();

										msgToUser = new File(toUserDir, username);
										msgUser = new File(userDir, toUser);

									} else {
										if (!toUserExist && !userExist) {

											msgToUser = createDirAndFile("users" + File.separator + toUser, username);
											msgUser = createDirAndFile("users" + File.separator + username, toUser);

										} else if (!toUserExist) {

											msgToUser = createDirAndFile("users" + File.separator + toUser, username);

										} else {

											msgUser = createDirAndFile("users" + File.separator + username, toUser);

										}
									}

									boolean existUserFile = msgUser.exists();
									boolean existToUserFile = msgToUser.exists();

									if (!existUserFile)
										msgUser.createNewFile();

									if (!existToUserFile)
										msgToUser.createNewFile();

									try (PrintWriter writerToUser = new PrintWriter(
											new FileWriter(msgToUser, true));
											PrintWriter writerUser = new PrintWriter(
													new FileWriter(msgUser, true))) {

										socketOut.println("MESSAGE");
										String date = formater.format(new Date());
										writerToUser.println(username + ": " + date + "\n");
										writerUser.println("Ja: " + date + "\n");

										synchronized (obj) {
											String line;
											while (!"END".equals(line = socketIn.readLine())) {
												String msg = new String(Base64.getDecoder().decode(line)) + "\n";
												writerToUser.println(msg);
												writerUser.println(msg);
											}

											writerToUser.println();
											writerUser.println();

										}

									} catch (IOException e) {
										Logger.log(Level.INFO, e.toString(), e);
									}
								} else {
									File allUsers = new File(users, "all");
									boolean exist = allUsers.exists();
									if (!exist)
										allUsers.createNewFile();
									try (PrintWriter writer = new PrintWriter(new FileWriter(allUsers, exist))) {

										socketOut.println("MESSAGE");
										String date = formater.format(new Date());
										writer.println(username + ": " + date + "\n");
										String line;

										synchronized (obj) {

											while (!"END".equals(line = socketIn.readLine())) {
												String msg = new String(Base64.getDecoder().decode(line)) + "\n";
												writer.println(msg);
												
											}

											writer.println();
										}
									}
								}

							} else
								socketOut.println("ERROR");

						} else if ("READ".equals(option)) {

							socketOut.println("OK");
							
							fromUser = socketIn.readLine();

							if (fromUser.startsWith("FROM ")) {

								fromUser = fromUser.replace("FROM ", "");

								if (!"Svi".equals(fromUser)) {

									if (Arrays.stream(users.listFiles())
											.anyMatch(e -> username.equals(e.getName()) && e.isDirectory())) {
										
										File userFile = Arrays.stream(users.listFiles())
												.filter(e -> username.equals(e.getName()) && e.isDirectory())
												.findFirst().get();

										if (Arrays.stream(userFile.listFiles()).count() > 0) {
											if (Arrays.stream(userFile.listFiles())
													.anyMatch(e -> fromUser.equals(e.getName()) && e.isFile())) {

												socketOut.println("OK");
												
												File fromUserFile = Arrays.stream(userFile.listFiles()).filter(
														file -> fromUser.equals(file.getName()) && file.isFile())
														.findFirst().get();

												try (BufferedReader reader = new BufferedReader(
														new FileReader(fromUserFile))) {

													String line = "";
													while ((line = reader.readLine()) != null)
														if (!line.isEmpty()) {
															line += "\n";
															socketOut.println(Base64.getEncoder()
																	.encodeToString(line.getBytes()));
														}
													socketOut.println("END");

												} catch (FileNotFoundException e) {
													Logger.log(Level.INFO, e.toString(), e);
												} catch (IOException e) {
													Logger.log(Level.INFO, e.toString(), e);
												}
											} else {
												File fromUserDir = new File(users, fromUser);
												fromUserDir.mkdir();
												socketOut.println("END");
											}
										} else socketOut.println("ERROR");
									} else {
										File userDir = new File(users, username);
										userDir.mkdir();
										socketOut.println("ERROR");
									}
								} else {
									File allUsersFile = new File(users, "all");
									socketOut.println("OK");
									if (!allUsersFile.exists())
										socketOut.println("END");
									else {
										try (BufferedReader reader = new BufferedReader(new FileReader(allUsersFile))) {

											String line = "";
											while ((line = reader.readLine()) != null) {
												if (!line.isEmpty()) {
													line += "\n";
													socketOut.println(
															Base64.getEncoder().encodeToString(line.getBytes()));
												}
											}
											
											socketOut.println("END");
										} catch (FileNotFoundException e) {
											Logger.log(Level.INFO, e.toString(), e);
										} catch (IOException e) {
											Logger.log(Level.INFO, e.toString(), e);
										}
									}
								}
							} else
								socketOut.println("ERROR");
						} else
							socketOut.println("ERROR");
					}
				}else
					socketOut.println("ERROR");
			}
			
		} catch (IOException e) {
			Logger.log(Level.INFO, e.toString(), e);
		}
		Server.usersAddress.remove(username);
		
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private File createDirAndFile(String dir, String file) {
		File directory = new File(dir);
		directory.mkdir();
		File msg = new File(dir, file);
		return msg;
	}

}

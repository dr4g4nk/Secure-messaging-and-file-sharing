package application;
	

import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.security.Security;
import java.util.logging.Level;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.unibl.etf.model.util.Logger;
import org.unibl.etf.model.util.Util;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		
		try {
			
			Parent root = (Parent)FXMLLoader.load(getClass().getResource("view"+File.separator+"Login.fxml"));
			Scene scene = new Scene(root,370,167);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.getIcons().add(new Image(new FileInputStream(new File("images"+File.separator+"Login.png"))));
			primaryStage.setResizable(false);
			primaryStage.setTitle("Login");
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setOnCloseRequest(e -> primaryStage.close());
		} catch(Exception e) {
			Logger.log(Level.INFO, e.toString(), e);
		}
	}
	
	
	
	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		
		System.setProperty("javax.net.ssl.trustStore", Util.getProperty("KEYSTORE"));
		System.setProperty("javax.net.ssl.trustStorePassword", Util.getProperty("KEYSTORE_PASS"));
		
		System.setProperty("java.security.policy", Util.getProperty("CLIEN_POLICY"));
		
		launch(args);
	}
}

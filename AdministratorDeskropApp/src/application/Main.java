package application;
	
import java.io.File;
import java.io.FileInputStream;
import java.security.Security;
import java.util.logging.Level;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.unibl.etf.model.util.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = (Parent)FXMLLoader.load(getClass().getResource("view"+File.separator+"Login.fxml"));
			Scene scene = new Scene(root,370,167);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image(new FileInputStream(new File("images"+File.separator+"Login.png"))));
			primaryStage.setResizable(false);
			primaryStage.setTitle("Prijava");
			primaryStage.show();
		} catch(Exception e) {
			Logger.log(Level.INFO, e.toString(), e);
		}
	}
	
	public static void main(String[] args) {
		
		Security.addProvider(new BouncyCastleProvider());
		
		launch(args);
	}
}

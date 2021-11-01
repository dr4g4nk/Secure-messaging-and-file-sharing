package org.unibl.etf.model.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;

import application.controllers.MsgWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Loader {

	
	public static void showMessage(String msg){
        MsgWindowController.setMessage(msg);
        openWindow("bin"+File.separator+"application"+File.separator+"view"+File.separator+"MsgWindow.fxml", "Poruka", 535, 100, true, true, new File("images"+File.separator+"Message.png"));
    }
	public static void openWindow(String path, String title, double width, double height, boolean disableResizable, boolean msgWindow, File image) {
        try {
        	Parent root = FXMLLoader.load(new File(path).toURI().toURL());
            Stage stage = new Stage();
            stage.setScene(new Scene(root, width, height));
            stage.setTitle(title);
            stage.setMinHeight(height);
            stage.setMinWidth(width);
            stage.getIcons().add(new Image(new FileInputStream(image)));
            if(disableResizable) {
                stage.setResizable(false);
            }
            if(msgWindow) {
            	stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            }
            else
                stage.show();
        } catch (IOException e){
        	Logger.log(Level.INFO, e.toString(), e);
        }
    }
}

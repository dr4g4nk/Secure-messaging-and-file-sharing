package application.controllers;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.unibl.etf.model.User;
import org.unibl.etf.model.util.Loader;
import org.unibl.etf.model.util.Logger;
import org.unibl.etf.model.util.Util;

import com.google.gson.Gson;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class LoginController {
	
	@FXML
    private TextField usernameText;

    @FXML
    private PasswordField passText;

    private MessageDigest func;
    private WebTarget target;
    private String username;
    private String password;
    
    
    
    @FXML
    private void initialize() {
    	try {
			func = MessageDigest.getInstance("SHA256");
			target = Util.getTarget();		
		} catch (NoSuchAlgorithmException e) {
			Logger.log(Level.INFO, e.toString(), e);
		}
    	
    	new Thread() {
    		public void run() {
    			
    			passText.setOnKeyPressed(e ->{
    				if(e.getCode().equals(KeyCode.ENTER))
    					login();
    			});
    			
    		};
    	}.start();
    }
    
    @FXML
    void cancleAction() {
    	((Stage)passText.getScene().getWindow()).close();
    }

    @FXML
    void loginAction() {

    	login();
    }
    
    private void login() {

    	username = usernameText.getText();
    	password = new String(Base64.getEncoder().encode(func.digest(passText.getText().getBytes())));
    	
    	Invocation.Builder inv = target.path("login").path("admin").request(MediaType.APPLICATION_JSON);
    	Response response = inv.post(Entity.json(new User(username,password)));
    	
    	Gson gson = new Gson();
    	boolean bool = gson.fromJson(response.readEntity(String.class), Boolean.class);
    	if(bool) {
    		AdminController.setUsername(username);
    		cancleAction();
    		Loader.openWindow("bin"+File.separator+"application"+File.separator+"view"+File.separator+"Admin.fxml", username, 629, 626, true, true, new File("images"+File.separator+"Chat.png"));
    		
    	} else Loader.showMessage("Korisnicko ime i lozinka nisu validni ili je vas nalog brokiran.");
    }
}

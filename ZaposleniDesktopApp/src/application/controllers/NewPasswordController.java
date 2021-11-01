package application.controllers;

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
import javafx.stage.Stage;

public class NewPasswordController {

	  @FXML
	    private TextField oldPasswordText;

	    @FXML
	    private PasswordField newPasswordText;

	    @FXML
	    private PasswordField newPassRepText;

	    private String username;
	    private String oldPassword;
	    private String newPassword;
	    private String newPassRep;
	    private MessageDigest func;
	    private WebTarget target;
	    private Gson gson;
	    
	    @FXML
	    private void initialize() {
	    	username = ChatController.getUsername();
	    	target = Util.getTarget();
	    	gson = new Gson();
	    	try {
				func = MessageDigest.getInstance("SHA256");
			} catch (NoSuchAlgorithmException e) {
				Logger.log(Level.INFO, e.toString(), e);
			}
	    }
	    
	    
	    @FXML
	    void closeAction() {
	    	((Stage) oldPasswordText.getScene().getWindow()).close();
	    }

	    @FXML
	    void saveAction() {

	    	oldPassword = new String(Base64.getEncoder().encode(func.digest(oldPasswordText.getText().getBytes())));
	    	newPassword = new String(Base64.getEncoder().encode(func.digest(newPasswordText.getText().getBytes())));
	    	newPassRep = new String(Base64.getEncoder().encode(func.digest(newPassRepText.getText().getBytes())));
	    	
	    	if(newPassword.equals(newPassRep)) {
	    		Invocation.Builder inv = target.path("password").request(MediaType.APPLICATION_JSON);
	    		Response response = inv.post(Entity.entity(gson.toJson(new User(username, oldPassword, newPassword)), MediaType.APPLICATION_JSON));
	    		
	    		Boolean bool = gson.fromJson(response.readEntity(String.class), Boolean.class);
	    		if(bool){
	    			Loader.showMessage("Lozinka je uspješno promijenjena.");
	    			closeAction();
	    		} else
	    			Loader.showMessage("Lozinka nije promijenjena.");
	    		
	    	} else 
	    		Loader.showMessage("Lozinke nisu iste.");
	    }
	    
	    
}

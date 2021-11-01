package org.unibl.etf.model.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

public class Util {
	
	public static WebTarget getTarget() {
		WebTarget target = null;
		try(InputStream in = new FileInputStream("resources"+File.separator+"client.conf")) {
    		Properties prop = new Properties();
    		prop.load(in);
			ClientConfig conf = new ClientConfig();
			Client client = ClientBuilder.newClient(conf);
			String uriTemplate = prop.getProperty("REST_SERVICE");
			target = client.target(UriBuilder.fromUri(uriTemplate).build());
		} catch (IOException e) {
			Logger.log(Level.INFO, e.toString(), e);
		}
		return target;
	}
	
	public static String getProperty(String name) {
		String str = "";
		try(InputStream in = new FileInputStream("resources"+File.separator+"client.conf")) {
    		Properties prop = new Properties();
    		prop.load(in);
    		
			str = prop.getProperty(name);
			
		} catch (FileNotFoundException e) {
			Logger.log(Level.INFO, e.toString(), e);
		} catch (IOException e) {
			Logger.log(Level.INFO, e.toString(), e);
		}
		
		return str;
	}
}

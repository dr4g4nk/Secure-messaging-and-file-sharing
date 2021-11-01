package org.unibl.etf.rmi;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

public class Logger {

	private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("logger");
	private static FileHandler fileHandler;
	static {
		
		try {
	        fileHandler = new FileHandler(".log", true);
	       	fileHandler.setFormatter(new SimpleFormatter());
	       	logger.addHandler(fileHandler);
	       	//logger.setUseParentHandlers(false);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized static void log(Level level, String msg, Throwable thrown){
		logger.log(level, msg, thrown);
	}
}

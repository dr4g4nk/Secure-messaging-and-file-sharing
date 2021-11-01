package org.unibl.etf.model;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisWrapper {

	private static String PATH = ".." + File.separator + "redis.properties";
	private Properties conf;
	private JedisPool pool;
	private Jedis jedis;

	
	public JedisWrapper() {
		try {
			conf = new Properties();
			conf.load(getClass().getClassLoader().getResourceAsStream(PATH));
			String address = conf.getProperty("AUTH_SERVER");
			int port = Integer.valueOf(conf.getProperty("AUTH_PORT"));
			pool = new JedisPool(address, port);
			jedis = pool.getResource();
			
		} catch (IOException e) {
			Logger.log(Level.INFO, e.toString(), e);
		}
	}

	public Jedis getJedis() {

		return jedis;
	}

	public void close() {
		jedis.close();
		pool.close();
	}
}

package org.unibl.etf;

import java.net.InetAddress;

import org.unibl.etf.model.JedisWrapper;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;

public class AccountManager {

	public boolean createUser(String username, String password, boolean admin) {

		if(!username.isEmpty() && !password.isEmpty()) {
			
			JedisWrapper wrapper = new JedisWrapper();
			Jedis jedis = wrapper.getJedis();
			if(jedis.hget(username, "password") == null) {
				jedis.hset(username, "password", password);
				jedis.hset(username, "active", "false");
				jedis.hset(username, "session", "0");
				jedis.hset(username, "blocked", "false");
				jedis.hset("users", username, "");
				if(admin)
					jedis.hset(username, "administrator", "true");
				
				wrapper.close();
				return true;
			}
		}
		return false;
	}
	
	public boolean deleteUser(String username) {
		if(!username.isEmpty()) {
			
			JedisWrapper wrapper = new JedisWrapper();
			Jedis jedis = wrapper.getJedis();
			
			if(jedis.exists(username)) {
				jedis.del(username);
				jedis.hdel("users", username);
				wrapper.close();
				return true;
			}
		}
		
		return false;
	}

	public boolean blockUser(String username) {

		if(!username.isEmpty()) {
		
			JedisWrapper wrapper = new JedisWrapper();
			Jedis jedis = wrapper.getJedis();
			
			if(jedis.exists(username) && "false".equals(jedis.hget(username, "blocked"))) {
				jedis.hset(username, "blocked", "true");
				jedis.hset("blocked_users", username, "");
				wrapper.close();
				return true;
			}
		}
		
		return false;
		
	}
	
	public boolean unblockUser(String username) {

		if(!username.isEmpty()) {
		
			JedisWrapper wrapper = new JedisWrapper();
			Jedis jedis = wrapper.getJedis();
			
			if(jedis.exists(username) && "true".equals(jedis.hget(username, "blocked"))) {
				jedis.hset(username, "blocked", "false");
				jedis.hdel("blocked_users", username);
				wrapper.close();
				return true;
			}
		}
		
		return false;
	}
	
	public String getAddress(String username) {
		
		if(!username.isEmpty()) {
			
			JedisWrapper wrapper = new JedisWrapper();
			Jedis jedis = wrapper.getJedis();
			String address = jedis.hget("active_users", username);
			if(address != null && !address.isEmpty()) {
				return address;
			}
		}
		return null;
	}
}

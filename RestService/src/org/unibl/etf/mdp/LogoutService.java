package org.unibl.etf.mdp;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.unibl.etf.model.JedisWrapper;
import org.unibl.etf.model.User;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;

@Path("/logout")
public class LogoutService {

	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(String user) {

		Gson gson = new Gson();
		Boolean bool = logout(gson.fromJson(user, User.class));
		if (bool) {
			return Response.status(200).entity(gson.toJson(bool)).build();
		}
		return Response.status(400).entity(gson.toJson(bool)).build();
	}
	
	private boolean logout(User user) {
		
		JedisWrapper wrapper = new JedisWrapper();
		Jedis jedis = wrapper.getJedis();
		String username = user.getUsername();	
		
		if("true".equals(jedis.hget(username, "active"))) {
			Long session = Long.valueOf(jedis.hget(username, "session"));
			jedis.hset(username, "logoutTime"+session, ""+System.currentTimeMillis());
			jedis.hset(username, "active", "false");
			jedis.hdel("active_users", username);
			
			wrapper.close();
			return true;
		}
		
		wrapper.close();
		return false;
	}
}

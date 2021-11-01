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

@Path("/password")
public class PasswordManager {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response changePassword(String str) {

		Gson gson = new Gson();
		Boolean bool = changePassword(gson.fromJson(str, User.class));
		if (bool)
			return Response.status(200).entity(bool).build();
		return Response.status(400).entity(bool).build();
	}

	private Boolean changePassword(User user) {

		JedisWrapper wrapper = new JedisWrapper();
		Jedis jedis = wrapper.getJedis();

		String username = user.getUsername();
		String password = user.getPassword();
		
		if ("true".equals(jedis.hget(username, "active")) && password.equals(jedis.hget(username, "password"))) {
			if (!user.getNewPassword().isEmpty()) {
				jedis.hset(username, "password", user.getNewPassword());
				wrapper.close();
				return true;
			}
		}

		wrapper.close();
		return false;
	}
}

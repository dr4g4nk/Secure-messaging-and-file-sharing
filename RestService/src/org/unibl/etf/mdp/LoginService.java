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

@Path("/login")
public class LoginService {

	private boolean isAdmin = false;
	private boolean admin = false;
	private Gson gson = new Gson();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(String user) {
		
		boolean bool = login(gson.fromJson(user, User.class));
		if (bool) {
			return Response.status(200).entity(gson.toJson(bool)).build();
		}
		return Response.status(400).entity(gson.toJson(bool)).build();
	}

	@Path("/admin")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response loginAsAdminstrator(String user) {
		admin = true;
		boolean bool = login(gson.fromJson(user, User.class));
		if (bool && isAdmin) {
			return Response.status(200).entity(gson.toJson(isAdmin)).build();
		}
		return Response.status(400).entity(gson.toJson(isAdmin)).build();
	}

	private boolean login(User user) {

		String username = user.getUsername();
		String password = user.getPassword();

		JedisWrapper wrapper = new JedisWrapper();
		Jedis jedis = wrapper.getJedis();

		if (password.equals(jedis.hget(username, "password")) && "false".equals(jedis.hget(username, "active"))) {

			password = "";
			if ("false".equals(jedis.hget(username, "blocked"))) {

				jedis.hset(username, "active", "true");
				Long session = Long.valueOf(jedis.hget(username, "session"));
				session++;
				jedis.hset(username, "session", session.toString());
				jedis.hset(username, "loginTime" + session, "" + System.currentTimeMillis());

				if(!admin) {
					jedis.hset("active_users", username, gson.toJson(user.getAddress()));
				}
				if ("true".equals(jedis.hget(username, "administrator"))) {
					isAdmin = true;
				}
				wrapper.close();
				return true;
			}
		}

		wrapper.close();
		return false;
	}
}

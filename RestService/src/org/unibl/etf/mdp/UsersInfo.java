package org.unibl.etf.mdp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.unibl.etf.model.JedisWrapper;
import org.unibl.etf.model.Users;

import com.google.gson.Gson;

import redis.clients.jedis.Jedis;

@Path("/users")
public class UsersInfo {

	@Path("/all")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllUsers() {
		Gson gson = new Gson();
		return Response.status(200).entity(gson.toJson(getAll())).build();
	}
	
	private Users getAll() {
		JedisWrapper wrapper = new JedisWrapper();
		Jedis jedis = wrapper.getJedis();
		Users users =  new Users(jedis.hkeys("users"));
		wrapper.close();
		return users;
	}
	
	@Path("/active")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActiveUsers() {
		Gson gson = new Gson();
		return Response.status(200).entity(gson.toJson(getActive())).build();
	}
	
	private Users getActive(){
		JedisWrapper wrapper = new JedisWrapper();
		Jedis jedis = wrapper.getJedis();
		Users users = new Users(jedis.hkeys("active_users"));
		wrapper.close();
		return users;
	}
	
	@Path("/blocked")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBlockedUsers() {
		Gson gson = new Gson();
		return Response.status(200).entity(gson.toJson(getBlocked())).build();
	}
	
	private Users getBlocked(){
		JedisWrapper wrapper = new JedisWrapper();
		Jedis jedis = wrapper.getJedis();
		Users users = new Users(jedis.hkeys("blocked_users"));
		wrapper.close();
		return users;
	}
	
}

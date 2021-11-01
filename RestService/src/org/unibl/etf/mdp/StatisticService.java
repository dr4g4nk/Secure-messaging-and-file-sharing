package org.unibl.etf.mdp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.unibl.etf.model.JedisWrapper;
import org.unibl.etf.model.Statistic;
import com.google.gson.Gson;

import redis.clients.jedis.Jedis;

@Path("/statistic/{username}")
public class StatisticService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStatistic(@PathParam("username")String username) {

		Gson gson = new Gson();
		JedisWrapper wrapper = new JedisWrapper();
		Jedis jedis = wrapper.getJedis();
		Statistic s = null;
		if (jedis.hget(username, "session") != null) {
			s = statistic(username);
			return Response.status(200).entity(gson.toJson(s)).build();
		}
		return Response.status(400).entity(gson.toJson(s)).build();
	}

	private Statistic statistic(String username) {

		return new Statistic(username);

	}

}

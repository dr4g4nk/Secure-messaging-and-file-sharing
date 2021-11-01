package org.unibl.etf.model;

import java.util.HashMap;
import redis.clients.jedis.Jedis;

public class Statistic {

	private HashMap<String, Long> loginTimes;
	private HashMap<String, Long> logoutTimes;

	public Statistic(String username) {

		loginTimes = new HashMap<String, Long>();
		logoutTimes = new HashMap<String, Long>();

		JedisWrapper wrapper = new JedisWrapper();
		Jedis jedis = wrapper.getJedis();

		Long session = Long.valueOf(jedis.hget(username, "session"));

		String loginTime, logoutTime;
		for (long i = 1; i < session; ++i) {

			loginTime = jedis.hget(username, "loginTime" + i);
			logoutTime = jedis.hget(username, "logoutTime" + i);
			if (loginTime != null && logoutTime != null) {
				loginTimes.put("loginTime" + i, Long.valueOf(loginTime));
				logoutTimes.put("logoutTime" + i, Long.valueOf(logoutTime));
			}
		}

		wrapper.close();

	}

	public HashMap<String, Long> getLoginTimes() {
		return loginTimes;
	}

	public HashMap<String, Long> getLogoutTimes() {
		return logoutTimes;
	}
}

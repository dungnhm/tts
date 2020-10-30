/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.session.redis;

import redis.clients.jedis.Jedis;

/**
 *
 * @author Admin
 */
public interface SessionStore {

	public static Jedis jedis = new Jedis("localhost");
}

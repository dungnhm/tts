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
public interface  SessionStore {

//    public static Map<String, Session> SessionMap = new HashMap<String, Session>();
    public static Jedis jedis = new Jedis("localhost");

//    public static void setSession(Session session) {
//        SessionMap.put(session.id(), session);
//        //SessionMap.put("email", session.get("email"));
//    }
//
//    public static Session getSession(String SessionId) {
//        return SessionMap.get(SessionId);
//    }
}

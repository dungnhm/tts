/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.server.handler;

import java.util.Date;
import java.util.List;

import com.app.models.ClipServices;
import com.app.pojo.Users;
import com.app.session.redis.SessionStore;
import com.app.util.AppParams;
import com.google.gson.Gson;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.Cookie;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.Session;
import redis.clients.jedis.params.SetParams;

public class LoginHandler implements Handler<RoutingContext>, SessionStore {

	static ClipServices clipServices;

	@SuppressWarnings("unchecked")
	@Override
	public void handle(RoutingContext routingContext) {

		routingContext.vertx().executeBlocking(future -> {
			try {
				Gson gson = new Gson();
				Session session = routingContext.session();
				JsonObject jsonRequest = routingContext.getBodyAsJson();
				String email = jsonRequest.getString("email");
				// String password = Md5Code.md5(jsonRequest.getString("password"));
				String password = jsonRequest.getString("password");

				JsonObject data = new JsonObject();
				data.put("status", "login failed");
				routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.UNAUTHORIZED.code());
				routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.UNAUTHORIZED.reasonPhrase());

				List<Users> list = clipServices.findAllByProperty("from Users where email = '" + email + "'", null, 0,
						Users.class, 0); // Users là class pojo chứ ko phải là table trong database

				if (list.size() > 0) {
					Users userResult = list.get(0);
					if (userResult.getPassword().equals(password)) {
						if (session != null) {
							System.out.println("Connection to server sucessfully");
							// Check server redis có chạy không
							System.out.println("Server is running: " + jedis.ping());
							SetParams ttl = new SetParams();
							ttl.ex(30 * 60);
							jedis.set(session.id(), gson.toJson(list.get(0)), ttl);
							System.out.println("store session timeout " + session.timeout());
							Cookie cookie = Cookie.cookie("sessionId", session.id());
							routingContext.addCookie(cookie);
						} else {
							System.out.println("session is null");
						}
						data.put("name", list.get(0).getName());
						data.put("status", "login successed");
						Date date = new Date();

						// update last log in
						List<Users> listUsers = clipServices
								.findAllByProperty("FROM Users WHERE email = '" + email + "'", null, 0, Users.class, 0);
						Users loggedInUser = listUsers.get(0);
						loggedInUser.setLastLogin(date);
						clipServices.saveOrUpdate(loggedInUser, Users.class, 0);
						routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
						routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
					}
				}
				routingContext.put(AppParams.RESPONSE_DATA, data);
				future.complete();
			} catch (Exception e) {
				routingContext.fail(e);
			}
		}, asyncResult -> {
			if (asyncResult.succeeded()) {
				routingContext.next();
			} else {
				routingContext.fail(asyncResult.cause());
			}
		});
	}

	public static void setClipServices(ClipServices clipServices) {
		LoginHandler.clipServices = clipServices;
	}

}

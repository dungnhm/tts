/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.dashchat.server.handler;

import java.util.Map;
import java.util.logging.Logger;

import com.app.dashchat.services.UserService;
import com.app.dashchat.session.redis.SessionStore;
import com.app.dashchat.util.AppParams;
import com.google.gson.Gson;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.Cookie;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.Session;
import redis.clients.jedis.params.SetParams;

public class LoginHandler implements Handler<RoutingContext>, SessionStore {

	@Override
	public void handle(RoutingContext routingContext) {

		routingContext.vertx().executeBlocking(future -> {
			try {
				JsonObject jsonRequest = routingContext.getBodyAsJson();
				Session session = routingContext.session();

				String email = jsonRequest.getString("email");
				String password = jsonRequest.getString("password");
				// String password = Md5Code.md5(jsonRequest.getString("password"));
				Gson gson = new Gson();
				JsonObject data = new JsonObject();

				Map user = UserService.getUserByEmail(email);
				
				if (!user.isEmpty()) {
					if (user.get("password").equals(password)) {
						if (session != null) {
							LOGGER.info("Connection to server sucessfully");
							// Check server redis có chạy không
							LOGGER.info("Server is running: " + jedis.ping());
							// Set timout cho session
							SetParams ttl = new SetParams();
							ttl.ex(30 * 60);

							// Lưu data của user vào session
							jedis.set(session.id(), gson.toJson(user), ttl);

							// Lưu sessionId vào cookie
							Cookie cookie = Cookie.cookie("sessionId", session.id());
							routingContext.addCookie(cookie);
						} else {
							LOGGER.info("session is null");
						}

						// update last log in
//						userResult.setLastLogin(date);
//						clipServices.saveOrUpdate(userResult, Users.class, 0);
						routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
						routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
						routingContext.put(AppParams.RESPONSE_DATA, user);
					}
				}else {
					routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.UNAUTHORIZED.code());
					routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.UNAUTHORIZED.reasonPhrase());
					routingContext.put(AppParams.RESPONSE_DATA, "{}");
				}
				
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

	private static final Logger LOGGER = Logger.getLogger(LoginHandler.class.getName());

}
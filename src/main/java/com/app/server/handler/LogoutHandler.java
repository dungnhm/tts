/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.server.handler;

import com.app.models.ClipServices;
import com.app.pojo.Users;
import com.app.session.redis.SessionStore;
import com.app.util.AppParams;
import com.google.gson.Gson;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.Session;

public class LogoutHandler implements Handler<RoutingContext>, SessionStore {

	static ClipServices clipServices;

	@Override
	public void handle(RoutingContext routingContext) {

		routingContext.vertx().executeBlocking(future -> {
			try {
				Gson gson = new Gson();
				Session session = routingContext.session();
				HttpServerRequest httpServerRequest = routingContext.request();
				System.out.println("session id from LoginHandler: " + session.id());
				JsonObject jsonRequest = routingContext.getBodyAsJson();
				String sessionId = jsonRequest.getString("sessionId");
				JsonObject data = new JsonObject();
				data.put("message", "log out failed");

				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);
				String email = loggedInUser.getEmail();

				routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.UNAUTHORIZED.code());
				routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.UNAUTHORIZED.reasonPhrase());
				if (!jedis.get(sessionId).isEmpty()) {
					//Comment
					System.out.println("ss info " + jedis.get(sessionId));
					jedis.del(sessionId);
					System.out.println("ss info " + jedis.get(sessionId));
					data.put("message", "log out successed");
					routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
					routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
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
		LogoutHandler.clipServices = clipServices;
	}
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.dashchat.server.handler;

import java.util.Map;
import java.util.logging.Logger;

import com.app.dashchat.models.ClipServices;
import com.app.dashchat.services.UserService;
import com.app.dashchat.session.redis.SessionStore;
import com.app.dashchat.util.AppParams;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.RoutingContext;

public class GetUserHandler implements Handler<RoutingContext>, SessionStore {

	static ClipServices clipServices;

	public static void setClipServices(ClipServices clipServices) {
		GetUserHandler.clipServices = clipServices;
	}

	@Override
	public void handle(RoutingContext routingContext) {

		routingContext.vertx().executeBlocking(future -> {
			try {
				HttpServerRequest httpServerRequest = routingContext.request();
				String email = httpServerRequest.getParam("email");
				LOGGER.info("---email = "+ email);
				JsonObject data = new JsonObject();

//				String sessionId = cookie.getValue();
//				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);
				Map user = UserService.getUserByEmail(email);
				
//				if (!user.isEmpty()) {
//					data = new JsonObject(user.toString());
//				}
				
				routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
				routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
				routingContext.put(AppParams.RESPONSE_DATA, user);
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

	private static final Logger LOGGER = Logger.getLogger(GetUserHandler.class.getName());
	
}

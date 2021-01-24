/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.dashchat.server.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.app.dashchat.pojo.Users;
import com.app.dashchat.services.MessageService;
import com.app.dashchat.session.redis.SessionStore;
import com.app.dashchat.util.AppParams;
import com.google.gson.Gson;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.Cookie;
import io.vertx.rxjava.ext.web.RoutingContext;

public class AddChatHandler implements Handler<RoutingContext>, SessionStore {

	@Override
	public void handle(RoutingContext routingContext) {

		routingContext.vertx().executeBlocking(future -> {
			try {
				HttpServerRequest httpServerRequest = routingContext.request();
				JsonObject jsonRequest = routingContext.getBodyAsJson();
				Map response = new LinkedHashMap();
				
				Cookie cookie = routingContext.getCookie("sessionId");
				Users loggedInUser = new Gson().fromJson(jedis.get(cookie.getValue()), Users.class);
				String sender = loggedInUser.getId();
				String receiver = jsonRequest.getString("receiver");
				String content = jsonRequest.getString("content");
				String type = httpServerRequest.getParam("type");
				
				response = MessageService.insertMessage(sender, receiver, type, content, "", "created", "draft");	
				
				routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
				routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
				routingContext.put(AppParams.RESPONSE_DATA, response);
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

	private static final Logger LOGGER = Logger.getLogger(AddChatHandler.class.getName());
	
}

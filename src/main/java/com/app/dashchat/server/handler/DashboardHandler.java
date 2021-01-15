/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.dashchat.server.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.app.dashchat.pojo.Users;
import com.app.dashchat.services.MessageService;
import com.app.dashchat.services.UserService;
import com.app.dashchat.session.redis.SessionStore;
import com.app.dashchat.util.AppParams;
import com.google.gson.Gson;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.Cookie;
import io.vertx.rxjava.ext.web.RoutingContext;

public class DashboardHandler implements Handler<RoutingContext>, SessionStore {

	@Override
	public void handle(RoutingContext routingContext) {

		routingContext.vertx().executeBlocking(future -> {
			try {
				HttpServerRequest httpServerRequest = routingContext.request();
				Cookie cookie = routingContext.getCookie("sessionId");
				String receiverId = httpServerRequest.getParam("receiver");

				Gson gson = new Gson();
				String sessionId = cookie.getValue();
				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);

				String senderId = loggedInUser.getId();

				Map response = new LinkedHashMap();

				// list contact
				Map userInfo = UserService.getUserById(senderId);
//				Map receiverInfo = UserService.getUserById(receiverId);
				List<Map> chatHistory = new ArrayList<Map>();
				if (receiverId.isBlank() || loggedInUser.getContact().contains(receiverId)) {
					receiverId = receiverId.isBlank() ? senderId : receiverId;
					LOGGER.info("---sender = " + senderId + " ---receiver = " + receiverId);
					chatHistory = MessageService.getChatHistory(senderId, receiverId);
//					LOGGER.info("contactList.get(0) = " + contactList);
				}
				response.put("userInfo", userInfo);
				response.put("contactList", loggedInUser.getContact());
				response.put("chatHistory", chatHistory);
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

	private static final Logger LOGGER = Logger.getLogger(DashboardHandler.class.getName());

}

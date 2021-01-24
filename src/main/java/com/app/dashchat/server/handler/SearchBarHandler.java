/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.dashchat.server.handler;

import java.util.HashMap;
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

public class SearchBarHandler implements Handler<RoutingContext>, SessionStore {

	@Override
	public void handle(RoutingContext routingContext) {

		routingContext.vertx().executeBlocking(future -> {
			try {
				HttpServerRequest httpServerRequest = routingContext.request();
				Cookie cookie = routingContext.getCookie("sessionId");
				String receiver = httpServerRequest.getParam("receiver");
				String searchText = httpServerRequest.getParam("searchText");

				String sessionId = cookie.getValue();
				Users loggedInUser = new Gson().fromJson(jedis.get(sessionId), Users.class);
				List<Map> searchChat = MessageService.searchChat(loggedInUser.getId(), receiver, searchText);
				Map searchUser = UserService.getUserByUsername(searchText);
				
				Map response = new HashMap();
				response.put("searchChat", searchChat);
				response.put("searchUser", searchUser);
				
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

	private static final Logger LOGGER = Logger.getLogger(SearchBarHandler.class.getName());
	
}

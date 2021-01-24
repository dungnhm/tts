/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.dashchat.server.handler;

import java.util.Map;
import java.util.logging.Logger;

import com.app.dashchat.pojo.Users;
import com.app.dashchat.services.GroupService;
import com.app.dashchat.session.redis.SessionStore;
import com.app.dashchat.util.AppParams;
import com.google.gson.Gson;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.Cookie;
import io.vertx.rxjava.ext.web.RoutingContext;

public class CreateGroupHandler implements Handler<RoutingContext>, SessionStore {

	@Override
	public void handle(RoutingContext routingContext) {

		routingContext.vertx().executeBlocking(future -> {
			try {
				JsonObject jsonRequest = routingContext.getBodyAsJson();

				Cookie cookie = routingContext.getCookie("sessionId");
				Gson gson = new Gson();
				String sessionId = cookie.getValue();
				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);
				String userId = loggedInUser.getId();
				String name = jsonRequest.getString("name");
				String type = jsonRequest.getString("type");
				String member = jsonRequest.getString("member");
				LOGGER.info("---"+name+ userId+ type+ member);
				
				Map response = GroupService.insertGroup(name, userId, type, member);

				routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.CREATED.code());
				routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.CREATED.reasonPhrase());
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

	private static final Logger LOGGER = Logger.getLogger(CreateGroupHandler.class.getName());

}
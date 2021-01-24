/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.dashchat.server.handler;

import java.util.Map;
import java.util.logging.Logger;

import com.app.dashchat.pojo.Users;
import com.app.dashchat.services.UserService;
import com.app.dashchat.session.redis.SessionStore;
import com.app.dashchat.util.AppParams;
import com.google.gson.Gson;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.Cookie;
import io.vertx.rxjava.ext.web.RoutingContext;

public class ChangePasswordHandler implements Handler<RoutingContext>, SessionStore {

	@SuppressWarnings("unchecked")
	@Override
	public void handle(RoutingContext routingContext) {

		routingContext.vertx().executeBlocking(future -> {
			try {
				Gson gson = new Gson();
				JsonObject jsonRequest = routingContext.getBodyAsJson();
				String currentPassword = jsonRequest.getString("currentPassword");
				String newPassword = jsonRequest.getString("newPassword");
				String confirmPassword = jsonRequest.getString("confirmNewPassword");
				Cookie cookie = routingContext.getCookie("sessionId");
				String sessionId = cookie.getValue();
				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);

				if (currentPassword.equals(loggedInUser.getPassword()) && newPassword.equals(confirmPassword) && !newPassword.equals(currentPassword)) {
					Map userUpdate = UserService.updateUser(loggedInUser.getEmail(), newPassword,
							loggedInUser.getFirstName(), loggedInUser.getLastName(), loggedInUser.getAddress(),
							loggedInUser.getPhone(), loggedInUser.getContact());

					routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
					routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
					routingContext.put(AppParams.RESPONSE_DATA, userUpdate);
				} else {
					routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.BAD_REQUEST.code());
					routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.BAD_REQUEST.reasonPhrase());
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

	private static final Logger LOGGER = Logger.getLogger(ChangePasswordHandler.class.getName());

}

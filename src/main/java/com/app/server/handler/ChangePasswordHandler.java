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
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.RoutingContext;

public class ChangePasswordHandler implements Handler<RoutingContext>, SessionStore {

	private static ClipServices clipServices;

	@Override
	public void handle(RoutingContext routingContext) {

		routingContext.vertx().executeBlocking(future -> {
			try {
				HttpServerRequest httpServerRequest = routingContext.request();
				JsonObject jsonResponse = new JsonObject();
				Gson gson = new Gson();
				// lay tham so username, password tu path
				JsonObject jsonRequest = routingContext.getBodyAsJson();
				String sessionId = jsonRequest.getString("sessionId");
				String oldPassword = jsonRequest.getString("oldPassword");
				String newPassword = jsonRequest.getString("newPassword");
				String confirmPassword = jsonRequest.getString("confirmPassword");
				JsonObject data = new JsonObject();
				// data.put("sessionId", sessionId);
				data.put("message", "change password failed");
				routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.UNAUTHORIZED.code());
				routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.UNAUTHORIZED.reasonPhrase());
				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);
				String email = loggedInUser.getEmail();
				String password = loggedInUser.getPassword();
				List<Users> list = clipServices
						.findAllByProperty("from Users where email = '" + email + "'", null, 0, Users.class, 0);
				if (list.size() > 0) {
					if (newPassword.equals(confirmPassword)) {
						Users resultUser = list.get(0);
						if (!oldPassword.equals(newPassword) && newPassword.equals(confirmPassword)) {
							Date date = new Date();
							Users newUser = new Users(resultUser.getId(), resultUser.getName(), email, newPassword,
									resultUser.getCreatedAt(), date, resultUser.getLastLogin(), resultUser.getState(),
									resultUser.getCountryCode());
							clipServices.update(newUser, newUser.getId(), Users.class, 0);
							data.put("message", "change password successed");
							routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
							routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
						}
					} else {
						data.put("message", "change password failed");
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
		ChangePasswordHandler.clipServices = clipServices;
	}

}

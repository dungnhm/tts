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
import com.app.dashchat.services.UserService;
import com.app.dashchat.session.redis.SessionStore;
import com.app.dashchat.util.AppParams;
import com.google.gson.Gson;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.Cookie;
import io.vertx.rxjava.ext.web.RoutingContext;
import redis.clients.jedis.params.SetParams;

public class AddContactUserHandler implements Handler<RoutingContext>, SessionStore {

	@Override
	public void handle(RoutingContext routingContext) {

		routingContext.vertx().executeBlocking(future -> {
			try {
				JsonObject jsonRequest = routingContext.getBodyAsJson();

				Cookie cookie = routingContext.getCookie("sessionId");
				Gson gson = new Gson();
				String sessionId = cookie.getValue();
				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);
				String email = loggedInUser.getEmail();

				String emailContact = jsonRequest.getString("email");
				Map contactUser = UserService.getUserByEmail(emailContact);
				
				String password = !jsonRequest.getString("password").isBlank() ? jsonRequest.getString("password")
						: loggedInUser.getPassword();
				String firstName = !jsonRequest.getString("first_name").isBlank() ? jsonRequest.getString("first_name")
						: loggedInUser.getFirstName();
				String lastName = !jsonRequest.getString("last_name").isBlank() ? jsonRequest.getString("last_name")
						: loggedInUser.getLastName();
				String address = !jsonRequest.getString("address").isBlank() ? jsonRequest.getString("address")
						: loggedInUser.getAddress();
				String phone = !jsonRequest.getString("phone").isBlank() ? jsonRequest.getString("phone")
						: loggedInUser.getPhone();
				String contact = !jsonRequest.getString("contact").isBlank() ? jsonRequest.getString("contact")
						: loggedInUser.getContact() + "," + contactUser.get("id");

				LOGGER.info("---contact = "+contact);
				Map response = new LinkedHashMap();

				if (!contactUser.isEmpty()) {
					response = UserService.updateUser(email, password, firstName, lastName, address, phone, contact);
					jedis.set(sessionId, gson.toJson(response), new SetParams().ex(30 * 60));
					LOGGER.info("---user from jedis = "+gson.fromJson(jedis.get(sessionId), Users.class));
				} else {
					response.put("message", "user not found");
				}

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

	private static final Logger LOGGER = Logger.getLogger(AddContactUserHandler.class.getName());

}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.server.handler;

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
import io.vertx.rxjava.ext.web.Session;
import redis.clients.jedis.params.SetParams;

public class LoginHandler implements Handler<RoutingContext>, SessionStore {

	static ClipServices clipServices;

	@Override
	public void handle(RoutingContext routingContext) {

		routingContext.vertx().executeBlocking(future -> {
			try {
				Gson gson = new Gson();
				Session session = routingContext.session();
				HttpServerRequest httpServerRequest = routingContext.request();

				System.out.println("session id from LoginHandler: " + session.id());
				JsonObject jsonResponse = new JsonObject();
				// lay tham so username, password tu path
				JsonObject jsonRequest = routingContext.getBodyAsJson();
				// String username = jsonRequest.getString("name");
				String email = jsonRequest.getString("email");
				// String password = Md5Code.md5(jsonRequest.getString("password"));
				String password = jsonRequest.getString("password");
				// String data = "login failed";
				JsonObject data = new JsonObject();
				data.put("status", "login failed");
				routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.UNAUTHORIZED.code());
				routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.UNAUTHORIZED.reasonPhrase());

				List<Users> list = clipServices
						.findAllByProperty("from Users where email = '" + email + "'", null, 0, Users.class, 0);
				// Users là class chứ ko phải là table trong database
				System.out.println("users size: " + list.size());
				if (list.size() > 0) {
					Users userResult = list.get(0);
					if (userResult.getPassword().equals(password)) {
						if (session != null) {
							// session.put("email", email);// session dang co id va email
							System.out.println("Connection to server sucessfully");
							// check whether server is running or not
							System.out.println("Server is running: " + jedis.ping());
							
							SetParams sp = new SetParams();
							sp.ex(30 * 60);//
							
							jedis.set(session.id(), gson.toJson(list.get(0)),sp);
							System.out.println("store session timeout " + session.timeout());
							session.timeout();
						} else {
							System.out.println("session is null");
						}
						data.put("name", list.get(0).getName());
						data.put("status", "login successed");
						data.put("sessionId", session.id());
						routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
						routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
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
		LoginHandler.clipServices = clipServices;
	}

}

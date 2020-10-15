package com.app.server.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.app.models.ClipServices;
import com.app.pojo.Users;
import com.app.session.redis.SessionStore;
import com.app.util.AppParams;
import com.google.gson.Gson;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.Session;

public class DetailShipmentsHandler implements Handler<RoutingContext>, SessionStore {
	static ClipServices clipServices;

	@Override
	public void handle(RoutingContext routingContext) {
		// TODO Auto-generated method stub
		routingContext.vertx().executeBlocking(future -> {
			try {
				Gson gson = new Gson();
				Session session = routingContext.session();
				HttpServerRequest httpServerRequest = routingContext.request();
				HttpServerResponse httpServerReponse = routingContext.response();
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String sessionId = httpServerRequest.getParam("sessionId");
				String trackingCode = httpServerRequest.getParam("trackingCode");
				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);
				String email = loggedInUser.getEmail();
				JsonObject data = new JsonObject();

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
		DetailShipmentsHandler.clipServices = clipServices;
	}

}

package com.app.dashchat.server.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.app.dashchat.models.ClipServices;
import com.app.dashchat.pojo.Users;
import com.app.dashchat.session.redis.SessionStore;
import com.app.dashchat.util.AppParams;
import com.google.gson.Gson;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.RoutingContext;

public class DetailShipmentsHandler implements Handler<RoutingContext>, SessionStore {
	static ClipServices clipServices;

	@Override
	public void handle(RoutingContext routingContext) {
		routingContext.vertx().executeBlocking(future -> {
			try {
				Gson gson = new Gson();
				HttpServerRequest httpServerRequest = routingContext.request();
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String sessionId = httpServerRequest.getParam("sessionId");
				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);
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

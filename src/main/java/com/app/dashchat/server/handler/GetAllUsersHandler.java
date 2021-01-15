package com.app.dashchat.server.handler;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.app.dashchat.services.UserService;
import com.app.dashchat.session.redis.SessionStore;
import com.app.dashchat.util.AppParams;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.RoutingContext;

public class GetAllUsersHandler implements Handler<RoutingContext>, SessionStore {
	@Override
	public void handle(RoutingContext routingContext) {

		routingContext.vertx().executeBlocking(future -> {
			try {
				HttpServerRequest httpServerRequest = routingContext.request();

				String data = "{}";

				List<Map> listUsers = UserService.getAllUsers("active");
				
				JsonArray listUsersJson = new JsonArray(listUsers);
				data = listUsersJson.toString();
				LOGGER.info("Users result: " + listUsersJson);
				
				routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
				routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
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

	private static final Logger LOGGER = Logger.getLogger(GetAllUsersHandler.class.getName());

}

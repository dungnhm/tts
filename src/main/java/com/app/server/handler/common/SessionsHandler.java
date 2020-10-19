package com.app.server.handler.common;

import static com.app.session.redis.SessionStore.jedis;

import com.app.pojo.Users;
import com.app.util.AppParams;
import com.app.util.ContextUtil;
import com.app.util.LoggerInterface;
import com.google.gson.Gson;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.Cookie;
import io.vertx.rxjava.ext.web.RoutingContext;

/**
 * Created by HungDX on 23-Apr-16.
 */
public class SessionsHandler implements Handler<RoutingContext>, LoggerInterface {

	@Override
	public void handle(RoutingContext routingContext) {

		routingContext.vertx().executeBlocking(future -> {
			try {
				HttpServerRequest httpServerRequest = routingContext.request();
				HttpServerResponse httpServerResponse = routingContext.response();

				routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.UNAUTHORIZED.code());
				routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.UNAUTHORIZED.reasonPhrase());

				// get uri
				String uri = httpServerRequest.uri();
				// if uri can log in
				if (!uri.equals("/webhook/api/login") && !uri.equals("/webhook/api/register")) {

					Cookie c = routingContext.getCookie("sessionId");
					String currentSessionId = c.getValue();
					Gson gson = new Gson();

					// Session session = routingContext.session();
					if (currentSessionId == null) {
						routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.UNAUTHORIZED.code());
						routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.UNAUTHORIZED.reasonPhrase());
						System.out.println("55");
						future.complete();
					} else if (jedis.get(currentSessionId) != null) {
						System.out.println("57");
						// if redis check duoc user co session
						Users loggedInUser = gson.fromJson(jedis.get(currentSessionId), Users.class);
						System.out.println(loggedInUser.getName() + " " + loggedInUser.getId());
						future.complete();
					} else {
						System.out.println("64");
						// if redis khong check duoc user
						routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.UNAUTHORIZED.code());
						routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.UNAUTHORIZED.reasonPhrase());
						int responseCode = ContextUtil.getInt(routingContext, AppParams.RESPONSE_CODE,
								HttpResponseStatus.UNAUTHORIZED.code());
						String responseDesc = ContextUtil.getString(routingContext, AppParams.RESPONSE_MSG,
								HttpResponseStatus.UNAUTHORIZED.reasonPhrase());
						httpServerResponse.setStatusCode(responseCode);
						httpServerResponse.setStatusMessage(responseDesc);
						String responseBody = ContextUtil.getString(routingContext, AppParams.RESPONSE_DATA, "{}");
						httpServerResponse.end(responseBody);
					}
				} else {
					future.complete();
				}
				// get uri
				// uri can login
				// check redis co user
				// if uri khong can login => end
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

}

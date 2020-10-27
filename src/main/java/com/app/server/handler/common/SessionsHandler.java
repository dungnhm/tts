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

				// get uri
				String uri = httpServerRequest.uri();
				// Nếu URI cần yêu cầu đăng nhập
				if (!uri.equals("/webhook/api/login") && !uri.equals("/webhook/api/register")) {
					// Lấy sessionId từ header sessionId, đã set trong cookie từ LoginHandler
					Cookie c = routingContext.getCookie("sessionId");
					String sessionId = c.getValue();
					if (sessionId == null) {
						System.out.println("SessionHandler: sessionId == null");
						routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.UNAUTHORIZED.code());
						routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.UNAUTHORIZED.reasonPhrase());
						future.complete();
					} else if (jedis.get(sessionId) != null) {
						System.out.println("SessionHandler: jedis.get(sessionId) != null");
						Gson gson = new Gson();
						Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);
						System.out.println(loggedInUser.getName() + " " + loggedInUser.getId());
						// reset time out cho session
						int ttl = 1800;
						jedis.expire(sessionId, ttl);
						future.complete();
					} else {
						System.out.println("SessionHandler: jedis.get(sessionId) == null");
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

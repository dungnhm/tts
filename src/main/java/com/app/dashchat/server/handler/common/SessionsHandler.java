package com.app.dashchat.server.handler.common;

import static com.app.dashchat.session.redis.SessionStore.jedis;

import com.app.dashchat.pojo.Users;
import com.app.dashchat.util.AppParams;
import com.app.dashchat.util.ContextUtil;
import com.app.dashchat.util.LoggerInterface;
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
				if (!uri.equals("/dashchat/api/login") && !uri.equals("/dashchat/api/register")) {
					// Lấy sessionId từ header sessionId, đã set trong cookie từ LoginHandler
					Cookie c = routingContext.getCookie("sessionId");
					String sessionId = c.getValue();
					if (sessionId == null) {
						routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.UNAUTHORIZED.code());
						routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.UNAUTHORIZED.reasonPhrase());
						future.complete();
					} else if (jedis.get(sessionId) != null) {
						Gson gson = new Gson();
						Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);
						// reset time out cho session
						int ttl = 1800;
						jedis.expire(sessionId, ttl);
						
						future.complete();
					} else {
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

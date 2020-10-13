package com.app.server.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import com.app.models.ClipServices;
import com.app.pojo.Shipments;
import com.app.pojo.Users;
import com.app.session.redis.SessionStore;
import com.app.util.AppParams;
import com.google.gson.Gson;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.Session;

public class ShowShipmentsHandler implements Handler<RoutingContext>, SessionStore {
	static ClipServices clipServices;

	@Override
	public void handle(RoutingContext routingContext) {
		// TODO Auto-generated method stub
		routingContext.vertx().executeBlocking(future -> {
			try {
				Gson gson = new Gson();
				Session session = routingContext.session();
				HttpServerRequest httpServerRequest = routingContext.request();
				JsonObject jsonRequest = routingContext.getBodyAsJson();
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ");
				// Date date = new Date();
				String dateFrom = jsonRequest.getString("dateFrom");
				String dateTo = jsonRequest.getString("dateTo");
				String trackingCode = jsonRequest.getString("trackingCode");
				String sessionId = jsonRequest.getString("sessionId");
				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);
				String email = loggedInUser.getEmail();
				System.out.println("email request: " + email);
				HttpServerResponse httpServerReponse = routingContext.response();
				// LIST ALL BY EMAIL
				List<Shipments> list = clipServices.findAllByProperty(
						"from Shipments WHERE created_by = '" + email + "'", null, 0, Shipments.class, 0);
				// LIST THEO DATE BY EMAIL
				List<Shipments> dates = clipServices.findAllByProperty(
						"FROM Shipments WHERE (created_by = '" + email + "') AND (created_at BETWEEN '" + dateFrom
								+ "' AND '" + dateTo + "')",
						null, 0, Shipments.class, 0);
				// LIST THEO CODE BY EMAIL
				List<Shipments> search = clipServices.findAllByProperty(
						"FROM Shipments WHERE (created_by = '" + email + "') AND (tracking_code LIKE '%" + trackingCode
								+ "%') AND (created_at BETWEEN '" + dateFrom + "' AND '" + dateTo + "')",
						null, 0, Shipments.class, 0);

				JsonObject jsonResponse = new JsonObject();
				JsonObject data = new JsonObject();

				routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.BAD_REQUEST.code());
				routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.BAD_REQUEST.reasonPhrase());
				if (list.size() > 0) {
					System.out.println(98);
					if (dates.size() > 0) {
//            Select to_address,shipping_status,trackingCode,payment_at
						if (!dateFrom.isEmpty() && !dateTo.isEmpty()) {
							if (!trackingCode.equals("")) {
								data.put("message", "list size: " + search.size());
								data.put("list", search);
								System.out.println(dates.size());
								routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
								routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
							} else {
								data.put("message", "list size: " + dates.size());
								data.put("list", dates);
								System.out.println(dates);
								routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
								routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
							}
						} else {
							data.put("message: ", "list size: " + list.size());
							data.put("list", list);
							routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
							routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
						}
					} else {
						data.put("message", "Empty");
						routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
						routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
					}
				} else {
					data.put("message ", "Empty");
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
		ShowShipmentsHandler.clipServices = clipServices;
	}

}
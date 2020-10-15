package com.app.server.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//				JsonObject jsonRequest = routingContext.getBodyAsJson();
//				String dateFrom = jsonRequest.getString("dateFrom");
//				String dateTo = jsonRequest.getString("dateTo");
//				String trackingCode = jsonRequest.getString("trackingCode");
//				String sessionId = jsonRequest.getString("sessionId");
				String sessionId = httpServerRequest.getParam("sessionId");
				String dateFrom = httpServerRequest.getParam("dateFrom");
				String dateTo = httpServerRequest.getParam("dateTo");
				String trackingCode = httpServerRequest.getParam("trackingCode");
				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);
				String email = loggedInUser.getEmail();
				System.out.println("email request: " + email);
				HttpServerResponse httpServerReponse = routingContext.response();
				List<Shipments> list = clipServices.findAllByProperty(
						"from Shipments WHERE created_by = '" + email + "'", null, 0, Shipments.class, 0);
				List<Shipments> dates = clipServices.findAllByProperty("FROM Shipments WHERE (created_by = '" + email
						+ "') AND (created_at BETWEEN '" + dateFrom + "' AND '" + dateTo + "')", null, 0,
						Shipments.class, 0);
				JsonObject data = new JsonObject();
				routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.BAD_REQUEST.code());
				routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.BAD_REQUEST.reasonPhrase());
				if (list.size() > 0) {
					if (dateFrom == null && dateTo == null && trackingCode == null) {
						dateFrom = dateFormat.format(dateFormat.parse("2000-01-01"));
						dateTo = dateFormat.format(new Date());
						data.put("message", "list shipments");
						data.put("list", list);
						routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
						routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
					} else if (dates.size() > 0) {
						if (dateFrom == null || dateTo == null) {
							data.put("message", "list shipments");
							data.put("list", list);
							routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
							routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
						} else {
							if (trackingCode == null) {
								data.put("message", "list shipments with dates");
								data.put("list", dates);
								routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
								routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
							} else {
								List<Shipments> search = clipServices.findAllByProperty(
										"FROM Shipments WHERE (created_by = '" + email + "') AND (tracking_code LIKE '%"
												+ trackingCode + "%') AND (created_at BETWEEN '" + dateFrom + "' AND '"
												+ dateTo + "')",
										null, 0, Shipments.class, 0);
								data.put("message", "list shipments with trackingCode and dates");
								data.put("list", search);
								routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
								routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
							}
						}
					} else {
						dateFrom = dateFormat.format(dateFormat.parse("2000-01-01"));
						dateTo = dateFormat.format(new Date());
						List<Shipments> search = clipServices.findAllByProperty("FROM Shipments WHERE (created_by = '"
								+ email + "') AND (tracking_code LIKE '%" + trackingCode
								+ "%') AND (created_at BETWEEN '" + dateFrom + "' AND '" + dateTo + "')", null, 0,
								Shipments.class, 0);
						System.out.println("102");
						data.put("message", "list shipments with trackingCode");
						data.put("list", search);
						routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
						routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
					}
				} else {
					data.put("message", "empty");
					data.put("list", "empty");
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
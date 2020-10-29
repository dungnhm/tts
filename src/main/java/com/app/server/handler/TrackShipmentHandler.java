/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.server.handler;

import java.util.List;

import com.app.models.ClipServices;
import com.app.pojo.Shipments;
import com.app.session.redis.SessionStore;
import com.app.util.AppParams;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.RoutingContext;

public class TrackShipmentHandler implements Handler<RoutingContext>, SessionStore {

	static ClipServices clipServices;

	@Override
	public void handle(RoutingContext routingContext) {

		routingContext.vertx().executeBlocking(future -> {
			try {
				HttpServerRequest httpServerRequest = routingContext.request();
//				Cookie cookie = routingContext.getCookie("sessionId");
				String trackingCode = httpServerRequest.getParam("trackingCode");
				System.out.println(trackingCode);
//				Gson gson = new Gson();
				JsonObject data = new JsonObject();

//				String sessionId = cookie.getValue();
//				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);
				List<Shipments> list = getShipmentByTrackingCode(trackingCode);
				if (list.size() > 0) {
					data.put("message", "shipment is found");
					data.put("shipment", list);
				} else {
					data.put("message", "shipment is not found");
					data.put("shipment", "");
				}
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

	@SuppressWarnings("unchecked")
	public static List<Shipments> getShipmentByTrackingCode(String trackingCode) {
		List<Shipments> list = null;
		try {
			list = clipServices.findAllByProperty(
					"FROM Shipments WHERE tracking_code = '" + trackingCode + "' ORDER BY created_at DESC", null, 0,
					Shipments.class, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void setClipServices(ClipServices clipServices) {
		TrackShipmentHandler.clipServices = clipServices;
	}

}

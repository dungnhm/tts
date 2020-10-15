package com.app.server.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.json.JSONArray;

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

public class ShipmentHandler implements Handler<RoutingContext>, SessionStore {
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
				String dateFrom = httpServerRequest.getParam("dateFrom");
				String dateTo = httpServerRequest.getParam("dateTo");
				String trackingCode = httpServerRequest.getParam("trackingCode");
				String sessionId = httpServerRequest.getParam("sessionId");
				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);
				String email = loggedInUser.getEmail();
				System.out.println("email request: " + email);
				HttpServerResponse httpServerReponse = routingContext.response();
				//Json Array
				JSONArray allDataArray = new JSONArray();
				// LIST ALL BY EMAIL
				List<Shipments> list = clipServices.findAllByProperty(
						"SELECT createdAt,toAddress,shippingStatus,trackingCode,currency from Shipments WHERE created_by = '" + email + "'", null, 0, Shipments.class, 0);
				// LIST THEO DATE BY EMAIL
				List<Shipments> dates = clipServices.findAllByProperty(
						" SELECT createdAt,toAddress,shippingStatus,trackingCode,currency FROM Shipments WHERE (created_by = '" + email + "') AND (created_at BETWEEN '" + dateFrom
								+ "' AND '" + dateTo + "')",
						null, 0, Shipments.class, 0);
				
				// LIST THEO CODE BY EMAIL
				List<Shipments> search = clipServices.findAllByProperty(
						"SELECT createdAt,toAddress,shippingStatus,trackingCode,currency FROM Shipments WHERE (created_by = '" + email + "') AND (tracking_code LIKE '%" + trackingCode
								+ "%') AND (created_at BETWEEN '" + dateFrom + "' AND '" + dateTo + "')",
						null, 0, Shipments.class, 0);
				//List THEO CODE 
				List<Shipments> code = clipServices.findAllByProperty(
						"SELECT createdAt,toAddress,shippingStatus,trackingCode,currency FROM Shipments WHERE (created_by = '" + email + "') AND (tracking_code LIKE '%" + trackingCode
								+ "%')",
						null, 0, Shipments.class, 0);
				JsonObject jsonResponse = new JsonObject();
				JsonObject data = new JsonObject();

				routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.BAD_REQUEST.code());
				routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.BAD_REQUEST.reasonPhrase());
				
				if (list.size() > 0) {
//            SELECT to_address,shipping_status,tracking_code,currency
					if (dates.size() > 0) {
						if (dateFrom!=null && dateTo!=null) {
							
								
							if (!trackingCode.equals("")) {
								data.put("message", "list size: " + search.size());
								data.put("list", search);
								System.out.println(dates.size());
								routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
								routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
							} else {
								data.put("message", "list size: " + dates.size());
								data.put("list dates", dates);
								
								
								
							
								routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
								routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
							}
						} else {
							System.out.println(91);
							data.put("message", "list size: " + search.size());
							data.put("List", search);
							routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
							routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
						}
					} else {
						if (trackingCode!=null) {
							data.put("message", "list size: " + code.size());
							data.put("List", code);
							routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
							routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
						}else {
							data.put("message: ", "emty");
							data.put("list", "emty");
							routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
							routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
							
							
						}
						
					}
				} else {
					System.out.println(105);
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
		ShipmentHandler.clipServices = clipServices;
	}

}
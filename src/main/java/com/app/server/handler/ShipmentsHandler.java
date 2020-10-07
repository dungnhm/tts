package com.app.server.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.app.models.ClipServices;
import com.app.pojo.Shipments;
import com.app.util.AppParams;
import com.google.api.client.http.HttpRequest;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.netty.buffer.ByteBufInputStream;

public class ShipmentsHandler implements Handler<RoutingContext> {
	static ClipServices clipServices;

	@Override
	public void handle(RoutingContext routingContext) {
		// TODO Auto-generated method stub
		routingContext.vertx().executeBlocking(future -> {
			try {
				HttpServerRequest httpServerRequest = routingContext.request();
				JsonObject jsonRequest = routingContext.getBodyAsJson();
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ");
				// Date date = new Date();
				String date1 = jsonRequest.getString("date1");
				String date2 = jsonRequest.getString("date2");
				String tracking_code = jsonRequest.getString("tracking_code");

				HttpServerResponse httpServerReponse = routingContext.response();
				// LIST ALL
				List<Shipments> list = (List<Shipments>) clipServices.findAllByProperty("from Shipments", null, 0,
						Shipments.class, 0);
//				 List<Users> resultUser = new ArrayList<Users>();

				// LIST THEO DATE
				List<Shipments> dates = (List<Shipments>) clipServices.findAllByProperty(
						" FROM Shipments WHERE created_at BETWEEN '" + date1 + "' AND '" + date2 + "'", null, 0,
						Shipments.class, 0);
//				Select to_address,shipping_status,tracking_code,payment_at

				// LIST THEO CODE
				List<Shipments> search = (List<Shipments>) clipServices.findAllByProperty(
						"FROM Shipments WHERE tracking_code LIKE '%" + tracking_code + "%' AND (created_at BETWEEN '" + date1 + "' AND '" + date2 + "')", null, 0, Shipments.class, 0);

				JsonObject jsonResponse = new JsonObject();

				JsonObject data = new JsonObject();
				
				routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.UNAUTHORIZED.code());
				routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.UNAUTHORIZED.reasonPhrase());
				data.put("", "SHIPMENT");
				data.put("no", date1.compareTo(date2));
				if (list.size() > 0) {
//	                	date1 = dateFormat.parse(date1);
//	                	date2 = dateFormat.parse(date2);
//	                	Date d1 = dateFormat.parse(date1);
//                		Date d2 = dateFormat.parse(date2);
					if (dates.size() > 0) {

						if (!date1.isEmpty() || !date2.isEmpty()) {
	
								if (!tracking_code.isEmpty()) {

									System.out.printf("date = ", dateFormat.format(list.get(0).getCreatedAt()));
									data.put("list: ", search.size());
									data.put("list Shipments Search: ", search);
									routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
									routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());

								} else {
									System.out.printf("date = ", dateFormat.format(list.get(0).getCreatedAt()));
									// Date1 is before Date2
									data.put("list: ", dates.size());
									data.put("list Shipments Date: ", dates);

									routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
									routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
								}

							
						} else {
							data.put("list: ", list.size());
							data.put("list Shipments: ", list);
							routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
							routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
							


						}
					}else {
						data.put("List Shipments", "Ko co SHipment Trong Day Search");
						routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
						routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
					}

				} else {
					data.put("List ", "Rong");

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
		ShipmentsHandler.clipServices = clipServices;
	}

}

package com.app.server.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.app.models.ClipServices;
import com.app.pojo.Transfer;

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

public class BillingHandler implements Handler<RoutingContext> {
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
//				int status = Integer.parseInt(jsonRequest.getString("status"));
				String status = jsonRequest.getString("status");
				
				HttpServerResponse httpServerReponse = routingContext.response();
				// LIST ALL
				List<Transfer> list = (List<Transfer>) clipServices.findAllByProperty("from Transfer", null, 0,
						Transfer.class, 0);
//				 List<Users> resultUser = new ArrayList<Users>();

				// LIST THEO DATE
				List<Transfer> dates = (List<Transfer>) clipServices.findAllByProperty(
						"FROM Transfer WHERE created_at BETWEEN '" + date1 + "' AND '" + date2 + "'", null, 0,
						Transfer.class, 0);
//				Select to_address,shipping_status,tracking_code,payment_at

				// LIST THEO CODE
				List<Transfer> search = (List<Transfer>) clipServices.findAllByProperty(
						"FROM Transfer WHERE version ='" + status + "'", null, 0, Transfer.class, 0);

				JsonObject jsonResponse = new JsonObject();

				JsonObject data = new JsonObject();
				
				routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.UNAUTHORIZED.code());
				routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.UNAUTHORIZED.reasonPhrase());

				if (list.size() > 0) {
//	                	date1 = dateFormat.parse(date1);
//	                	date2 = dateFormat.parse(date2);
//	                	Date d1 = dateFormat.parse(date1);
//                		Date d2 = dateFormat.parse(date2);
					if (dates.size() > 0) {

						if (date1.isEmpty() || date2.isEmpty()) {
							data.put("list Transfer: ", list);
							routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
							routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
						} else {

							
								if (!status.isEmpty()) {

									System.out.printf("date = ", dateFormat.format(list.get(0).getCreatedAt()));
									data.put("list Transfer Search: ", search);
									routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
									routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());

								} else {
									System.out.printf("date = ", dateFormat.format(list.get(0).getCreatedAt()));
									// Date1 is before Date2
									data.put("list Transfer Date: ", dates);

									routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
									routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
								}


						}
					}else {
						data.put("List Transfer", "Ko co SHipment Trong Day Search");
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
		BillingHandler.clipServices = clipServices;
	}

}

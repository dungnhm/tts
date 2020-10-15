package com.app.server.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.app.models.ClipServices;
import com.app.pojo.Transfer;
import com.app.pojo.Users;
import com.app.pojo.Wallets;
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

public class BillingHandler implements Handler<RoutingContext>, SessionStore {
	static ClipServices clipServices;

	@Override
	public void handle(RoutingContext routingContext) {
		routingContext.vertx().executeBlocking(future -> {
			try {
				Session session = routingContext.session();
				HttpServerRequest httpServerRequest = routingContext.request();
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//				JsonObject jsonRequest = routingContext.getBodyAsJson();
//				String dateFrom = jsonRequest.getString("dateFrom");
//				String dateTo = jsonRequest.getString("dateTo");
//				String status = jsonRequest.getString("status");
//				String sessionId = jsonRequest.getString("sessionId");
				String sessionId = httpServerRequest.getParam("sessionId");
				String dateFrom = httpServerRequest.getParam("dateFrom");
				String dateTo = httpServerRequest.getParam("dateTo");
				String status = httpServerRequest.getParam("status");
				Gson gson = new Gson();
				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);
				String email = loggedInUser.getEmail();
				HttpServerResponse httpServerReponse = routingContext.response();
				// List Users(Lay Id cua Users)
				List<Users> listUsers = clipServices.findAllByProperty("FROM Users WHERE email = '" + email + "'", null,
						0, Users.class, 0);
				String userId = loggedInUser.getId();
				System.out.println(userId);

				// LIST ALL WALLET
				List<Wallets> listWallets = clipServices
						.findAllByProperty("from Wallets Where user_id ='" + userId + "'", null, 0, Wallets.class, 0);
				String walletId = listWallets.get(0).getId();
				System.out.println(walletId);
				// LIST ALL
				List<Transfer> list = clipServices.findAllByProperty("Select createdAt,amount,details,financialStatus from Transfer Where (from_wallet_id ='" + walletId
						+ "') OR (to_wallet_id ='" + walletId + "')", null, 0, Transfer.class, 0);
				// LIST THEO DATE
				List<Transfer> dates = clipServices.findAllByProperty(
						"Select createdAt,amount,details,financialStatus FROM Transfer WHERE ((from_wallet_id ='" + walletId + "') OR (to_wallet_id ='" + walletId
								+ "')) AND (created_at BETWEEN '" + dateFrom + "' AND '" + dateTo + "')",
						null, 0, Transfer.class, 0);
				// LIST THEO CODE

				JsonObject data = new JsonObject();

				routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.BAD_REQUEST.code());
				routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.BAD_REQUEST.reasonPhrase());

				if (list.size() > 0) {
					data.put("available", listWallets.get(0).getBalance());
					if (dateFrom == null && dateTo == null && status == null) {
						dateFrom = dateFormat.format(dateFormat.parse("2000-01-01"));
						dateTo = dateFormat.format(new Date());
						data.put("message", "list tranfer");
						data.put("list size: ", list.size());
						data.put("list", list);
						routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
						routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
					} else if (dates.size() > 0) {
						if (dateFrom == null || dateTo == null) {
							data.put("message", "list tranfer");
							data.put("list size: ", list.size());
							data.put("list", list);
							routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
							routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
						} else {
							if (status == null) {
								data.put("message", "list tranfer with dates");
								data.put("list size: ", dates.size());
								data.put("list", dates);
								routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
								routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
							} else {
								List<Transfer> search = clipServices.findAllByProperty(
										"Select createdAt,amount,details,financialStatus FROM Transfer WHERE ((from_wallet_id ='" + walletId + "') OR (to_wallet_id ='"
												+ walletId + "')) AND (created_at BETWEEN '" + dateFrom + "' AND '"
												+ dateTo + "') AND (financial_status ='" + status + "')",
										null, 0, Transfer.class, 0);
								data.put("message", "list tranfer with status and dates");
								data.put("list size: ", search.size());
								data.put("list", search);
								routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
								routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
							}

						}
					} else {
						dateFrom = dateFormat.format(dateFormat.parse("2000-01-01"));
						dateTo = dateFormat.format(new Date());
						List<Transfer> search = clipServices
								.findAllByProperty(
										"Select createdAt,amount,details,financialStatus FROM Transfer WHERE ((from_wallet_id ='" + walletId + "') OR (to_wallet_id ='"
												+ walletId + "')) AND (created_at BETWEEN '" + dateFrom + "' AND '"
												+ dateTo + "') AND (financial_status ='" + status + "')",
										null, 0, Transfer.class, 0);
						System.out.println("102");
						data.put("message", "list transfer with status");
						data.put("list size: ", search.size());
						data.put("list", search);
						routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
						routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
					}

				} else {
					System.out.println("110");
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
		BillingHandler.clipServices = clipServices;
	}

}
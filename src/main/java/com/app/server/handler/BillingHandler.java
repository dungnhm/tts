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
import io.vertx.rxjava.ext.web.Cookie;
import io.vertx.rxjava.ext.web.RoutingContext;

public class BillingHandler implements Handler<RoutingContext>, SessionStore {
	static ClipServices clipServices;

	@SuppressWarnings("unchecked")
	@Override
	public void handle(RoutingContext routingContext) {
		routingContext.vertx().executeBlocking(future -> {
			try {
				HttpServerRequest httpServerRequest = routingContext.request();
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Cookie c = routingContext.getCookie("sessionId");
				String sessionId = c.getValue();

				String dateFrom = httpServerRequest.getParam("dateFrom");
				String dateTo = httpServerRequest.getParam("dateTo");
				String status = httpServerRequest.getParam("status");
				Gson gson = new Gson();
				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);
				String userId = loggedInUser.getId();
				List<Wallets> listWallets = clipServices
						.findAllByProperty("from Wallets Where user_id ='" + userId + "'", null, 0, Wallets.class, 0);
				String walletId = listWallets.get(0).getId();

				List<Transfer> list = clipServices.findAllByProperty("from Transfer Where (from_wallet_id ='" + walletId
						+ "') OR (to_wallet_id ='" + walletId + "')", null, 0, Transfer.class, 0);
				List<Transfer> dates = clipServices.findAllByProperty(
						"FROM Transfer WHERE ((from_wallet_id ='" + walletId + "') OR (to_wallet_id ='" + walletId
								+ "')) AND (created_at BETWEEN '" + dateFrom + "' AND '" + dateTo + "')",
						null, 0, Transfer.class, 0);

				JsonObject data = new JsonObject();

				if (list.size() > 0) {
					data.put("available", listWallets.get(0).getBalance());
					routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
					routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
					if (dateFrom == null && dateTo == null && status == null) {
						dateFrom = dateFormat.format(dateFormat.parse("2000-01-01 00:00:00"));
						dateTo = dateFormat.format(new Date());
						data.put("message", "list tranfer");
						data.put("list", list);
					} else if (dates.size() > 0) {
						if (dateFrom == null || dateTo == null) {
							data.put("message", "list tranfer");
							data.put("list", list);
						} else {
							if (status == null) {
								data.put("message", "list tranfer with dates");
								data.put("list", dates);
							} else {
								List<Transfer> search = clipServices.findAllByProperty(
										"FROM Transfer WHERE ((from_wallet_id ='" + walletId + "') OR (to_wallet_id ='"
												+ walletId + "')) AND (created_at BETWEEN '" + dateFrom + "' AND '"
												+ dateTo + "') AND (financial_status ='" + status + "')",
										null, 0, Transfer.class, 0);
								data.put("message", "list tranfer with status and dates");
								data.put("list", search);
							}
						}
					} else {
						dateFrom = dateFormat.format(dateFormat.parse("2000-01-01 00:00:00"));
						dateTo = dateFormat.format(new Date());
						List<Transfer> search = clipServices
								.findAllByProperty(
										"FROM Transfer WHERE ((from_wallet_id ='" + walletId + "') OR (to_wallet_id ='"
												+ walletId + "')) AND (created_at BETWEEN '" + dateFrom + "' AND '"
												+ dateTo + "') AND (financial_status ='" + status + "')",
										null, 0, Transfer.class, 0);
						System.out.println("102");
						data.put("message", "list transfer with status");
						data.put("list", search);
						routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
						routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
					}
				} else {
					data.put("message", "empty");
					data.put("list", "{}");
					routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.BAD_REQUEST.code());
					routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.BAD_REQUEST.reasonPhrase());
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
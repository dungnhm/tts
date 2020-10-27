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

	@Override
	public void handle(RoutingContext routingContext) {
		routingContext.vertx().executeBlocking(future -> {
			try {
				HttpServerRequest httpServerRequest = routingContext.request();
				Cookie cookie = routingContext.getCookie("sessionId");
				String dateFrom = httpServerRequest.getParam("dateFrom");
				String dateTo = httpServerRequest.getParam("dateTo");
				String status = httpServerRequest.getParam("status");

				Gson gson = new Gson();
				JsonObject data = new JsonObject();

				String sessionId = cookie.getValue();
				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);

				String userId = loggedInUser.getId();

				List<Wallets> listWallets = getWalletsByUserId(userId);

				String walletId = listWallets.get(0).getId();

				List<Transfer> list = getTransferByWalletId(walletId);

				// get Transfer By walletId and Dates
				List<Transfer> dates = getTransfer(walletId, dateFrom, dateTo);

				if (list.size() > 0) {
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
								// get Transfer by walletId and Dates and Status
								List<Transfer> search = getTransfer(walletId, dateFrom, dateTo, status);
								data.put("message", "list tranfer with status and dates");
								data.put("list", search);
							}
						}
					} else {
						dateFrom = dateFormat.format(dateFormat.parse("2000-01-01 00:00:00"));
						dateTo = dateFormat.format(new Date());
						// get Transfer by walletId and Dates and Status
						List<Transfer> search = getTransfer(walletId, dateFrom, dateTo, status);
						data.put("message", "list transfer with status");
						data.put("list", search);
					}
				} else {
					data.put("message", " ");
					data.put("list", " ");
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

	@SuppressWarnings("unchecked")
	public static List<Transfer> getTransferByWalletId(String walletId) {
		List<Transfer> list = null;
		try {
			list = clipServices.findAllByProperty(
					"from Transfer Where (from_wallet_id ='" + walletId + "') OR (to_wallet_id ='" + walletId + "')",
					null, 0, Transfer.class, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<Wallets> getWalletsByUserId(String userId) {
		List<Wallets> list = null;
		try {
			list = clipServices.findAllByProperty("from Wallets Where user_id ='" + userId + "'", null, 0,
					Wallets.class, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<Transfer> getTransfer(String walletId, String dateFrom, String dateTo) {
		List<Transfer> list = null;
		try {
			list = clipServices.findAllByProperty(
					"FROM Transfer WHERE ((from_wallet_id ='" + walletId + "') OR (to_wallet_id ='" + walletId
							+ "')) AND (created_at BETWEEN '" + dateFrom + "' AND '" + dateTo + "')",
					null, 0, Transfer.class, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<Transfer> getTransfer(String walletId, String dateFrom, String dateTo, String status) {
		List<Transfer> list = null;
		try {
			list = clipServices.findAllByProperty("FROM Transfer WHERE ((from_wallet_id ='" + walletId
					+ "') OR (to_wallet_id ='" + walletId + "')) AND (created_at BETWEEN '" + dateFrom + "' AND '"
					+ dateTo + "') AND (financial_status ='" + status + "')", null, 0, Transfer.class, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void setClipServices(ClipServices clipServices) {
		BillingHandler.clipServices = clipServices;
	}

}
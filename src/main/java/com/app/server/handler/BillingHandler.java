package com.app.server.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
				JsonObject jsonRequest = routingContext.getBodyAsJson();
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ");
				String dateFrom = jsonRequest.getString("dateFrom");
				String dateTo = jsonRequest.getString("dateTo");
				String status = jsonRequest.getString("status");
				String sessionId = jsonRequest.getString("sessionId");
				Gson gson = new Gson();
				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);
				String email = loggedInUser.getEmail();
				HttpServerResponse httpServerReponse = routingContext.response();
				// List Users(Lay Id cua Users)
				List<Users> listUsers = clipServices
						.findAllByProperty("FROM Users WHERE email = '" + email + "'", null, 0, Users.class, 0);
				String userId = loggedInUser.getId();
				System.out.println(userId);

				// LIST ALL WALLET
				List<Wallets> listWallets = clipServices
						.findAllByProperty("from Wallets Where user_id ='" + userId + "'", null, 0, Wallets.class, 0);
				String walletId = listWallets.get(0).getId();
				System.out.println(walletId);
				// LIST ALL
				List<Transfer> list = clipServices
						.findAllByProperty("from Transfer Where (from_wallet_id ='" + walletId
								+ "') OR (to_wallet_id ='" + walletId + "')", null, 0, Transfer.class, 0);
				// LIST THEO DATE
				List<Transfer> dates = clipServices.findAllByProperty(
						"FROM Transfer WHERE ((from_wallet_id ='" + walletId + "') OR (to_wallet_id ='" + walletId
								+ "')) AND (created_at BETWEEN '" + dateFrom + "' AND '" + dateTo + "')",
						null, 0, Transfer.class, 0);
				// LIST THEO CODE
				List<Transfer> search = clipServices.findAllByProperty(
						"FROM Transfer WHERE ((from_wallet_id ='" + walletId + "') OR (to_wallet_id ='" + walletId
								+ "')) AND (created_at BETWEEN '" + dateFrom + "' AND '" + dateTo
								+ "') AND (financial_status ='" + status + "')",
						null, 0, Transfer.class, 0);
				JsonObject data = new JsonObject();

				routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.BAD_REQUEST.code());
				routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.BAD_REQUEST.reasonPhrase());

				if (list.size() > 0) {
					data.put("available", listWallets.get(0).getBalance());
					if (dates.size() > 0) {

						if (dateFrom.equals("") || dateTo.equals("")) {
							data.put("message", "list tranfer");
							data.put("list", list);
							routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
							routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
						} else {
							if (!status.equals("")) {
								data.put("message", "list tranfer with status and dates");
								data.put("list", search);
								routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
								routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());

							} else {
								System.out.printf("date = ", dateFormat.format(list.get(0).getCreatedAt()));
								// Date1 is before Date2
								data.put("message", "list tranfer with dates");
								data.put("list", dates);

								routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
								routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
							}

						}
					} else {
						data.put("message", "empty");
						routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.BAD_REQUEST.code());
						routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.BAD_REQUEST.reasonPhrase());
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
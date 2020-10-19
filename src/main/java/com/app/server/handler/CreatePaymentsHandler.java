package com.app.server.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import com.app.models.ClipServices;
import com.app.pojo.Users;
import com.app.pojo.Wallets;
import com.app.session.redis.SessionStore;
import com.app.util.AppParams;
import com.google.gson.Gson;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.Cookie;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.Session;

public class CreatePaymentsHandler implements Handler<RoutingContext>, SessionStore {
	static ClipServices clipServices;

	@Override
	public void handle(RoutingContext routingContext) {
		routingContext.vertx().executeBlocking(future -> {
			try {
				Session session = routingContext.session();
				HttpServerRequest httpServerRequest = routingContext.request();
				JsonObject data = new JsonObject();
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				JsonObject jsonRequest = routingContext.getBodyAsJson();
				Cookie c = routingContext.getCookie("sessionId");
				String sessionId = c.getValue();

				JsonArray listPay = jsonRequest.getJsonArray("listPay");
				Long total = jsonRequest.getLong("total");
				Gson gson = new Gson();
				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);
				String email = loggedInUser.getEmail();
				String userId = loggedInUser.getId();
				List<Wallets> listWallets = clipServices
						.findAllByProperty("from Wallets Where user_id ='" + userId + "'", null, 0, Wallets.class, 0);
				Wallets walletPay = new Wallets();
				if (listWallets.size() > 0) {
					walletPay = listWallets.get(0);
				}

				String walletPayId = walletPay.getId();
				// check available vs total
				if (total > walletPay.getBalance()) {
					data.put("messsage", "not enough money in wallet");
					// need to add money into cards
					// walletPay.setBalance(walletPay.getBalance() + addMoney);
				}
				// enough money in card to pay
				walletPay.setBalance(walletPay.getBalance() - total);

				// update shipments
				for (int i = 0; i < listPay.size(); i++) {
					JsonObject shipment = listPay.getJsonObject(i);
					System.out.println(shipment.getString("trackingCode"));
				}
				//

				data.put("messsage", "ok");
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

	public static void setClipServices(ClipServices clipServices) {
		CreatePaymentsHandler.clipServices = clipServices;
	}

}
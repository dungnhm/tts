package com.app.server.handler;

import java.util.Date;
import java.util.List;

import com.app.models.ClipServices;
import com.app.pojo.Shipments;
import com.app.pojo.Transfer;
import com.app.pojo.Users;
import com.app.pojo.Wallets;
import com.app.session.redis.SessionStore;
import com.app.util.AppParams;
import com.google.gson.Gson;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.Cookie;
import io.vertx.rxjava.ext.web.RoutingContext;

public class CreatePaymentsHandler implements Handler<RoutingContext>, SessionStore {
	static ClipServices clipServices;

	@SuppressWarnings("unchecked")
	@Override
	public void handle(RoutingContext routingContext) {
		routingContext.vertx().executeBlocking(future -> {
			try {
				JsonObject data = new JsonObject();
				JsonObject jsonRequest = routingContext.getBodyAsJson();
				Cookie c = routingContext.getCookie("sessionId");
				String sessionId = c.getValue();

				JsonArray listPay = jsonRequest.getJsonArray("listPay");
				Long total = jsonRequest.getLong("total");
				Gson gson = new Gson();
				Date date = new Date();
				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);
				String userId = loggedInUser.getId();
				List<Wallets> listWallets = clipServices
						.findAllByProperty("from Wallets Where user_id ='" + userId + "'", null, 0, Wallets.class, 0);
				Wallets walletPay = new Wallets(); // walletPay = wallet of logged in user
				if (listWallets.size() > 0) {
					walletPay = listWallets.get(0);
				}

				// check available vs total
				if (total > walletPay.getBalance()) {
					data.put("messsage", "not enough money in wallet");
					// need to add money into cards
					// walletPay.setBalance(walletPay.getBalance() + addMoney);
				}
				// enough money in card to pay

				// update shipments
				for (int i = 0; i < listPay.size(); i++) {
					// JsonObject shipment = listPay.getJsonObject(i);
					String trackingCode = listPay.getJsonObject(i).getString("trackingCode");
					List<Shipments> shipments = clipServices.findAllByProperty(
							"from Shipments Where tracking_code ='" + trackingCode + "'", null, 0, Shipments.class, 0);
					Shipments shipment = shipments.get(0);
					String shipmentId = shipment.getId();
					List<Transfer> transfers = clipServices.findAllByProperty(
							"from Transfer Where shipment_id ='" + shipmentId + "'", null, 0, Transfer.class, 0);
					Transfer transfer = transfers.get(0);
					String walletReceiveId = transfer.getToWalletId();
					Long amount = transfer.getAmount();
					List<Wallets> walletReceives = clipServices.findAllByProperty(
							"from Wallets Where id ='" + walletReceiveId + "'", null, 0, Wallets.class, 0);
					Wallets walletReceive = walletReceives.get(0);

					walletPay.setBalance(walletPay.getBalance() - total);
					walletPay.setDueAmount(walletPay.getDueAmount() - total);
					walletPay.setSpentAmount(walletPay.getSpentAmount() + total);
					walletPay.setUpdatedAt(date);
					walletReceive.setBalance(walletReceive.getBalance() + amount);
					walletReceive.setUpdatedAt(date);
					transfer.setFinancialStatus("completed");
					transfer.setUpdatedAt(date);
					shipment.setFinancialStatus("completed");
					shipment.setUpdatedAt(date);
					shipment.setPaymentAt(date);
					clipServices.update(walletPay, walletPay.getId(), Wallets.class, 0);
					clipServices.update(walletReceive, walletReceive.getId(), Wallets.class, 0);
					clipServices.update(transfer, transfer.getId(), Transfer.class, 0);
					clipServices.update(shipment, shipment.getId(), Shipments.class, 0);
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.server.handler;

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
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.Cookie;
import io.vertx.rxjava.ext.web.RoutingContext;

public class DashboardHandler implements Handler<RoutingContext>, SessionStore {

	static ClipServices clipServices;

	@SuppressWarnings("unchecked")
	@Override
	public void handle(RoutingContext routingContext) {

		routingContext.vertx().executeBlocking(future -> {
			try {
				Cookie c = routingContext.getCookie("sessionId");
				String sessionId = c.getValue();

				JsonObject data = new JsonObject();
				JsonObject dataShipmentsStatus = new JsonObject();
				JsonObject dataBilling = new JsonObject();
				JsonObject dataLastShipments = new JsonObject();
				JsonObject dataLastTransactions = new JsonObject();
				Gson gson = new Gson();
				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);
				String email = loggedInUser.getEmail();
				String walletId = "";
				List<Shipments> listShipments = clipServices.findAllByProperty(
						"FROM Shipments WHERE shipping_status = 'New' AND created_by = '" + email + "'", null, 0,
						Shipments.class, 0);
				dataShipmentsStatus.put("new", listShipments.size());
				listShipments = clipServices.findAllByProperty(
						"FROM Shipments WHERE shipping_status = 'Processing' AND created_by = '" + email + "'", null, 0,
						Shipments.class, 0);
				dataShipmentsStatus.put("processing", listShipments.size());
				listShipments = clipServices.findAllByProperty(
						"FROM Shipments WHERE shipping_status = 'In Transit' AND created_by = '" + email + "'", null, 0,
						Shipments.class, 0);
				dataShipmentsStatus.put("inTransit", listShipments.size());
				listShipments = clipServices.findAllByProperty(
						"FROM Shipments WHERE shipping_status = 'Delivered'AND created_by = '" + email + "'", null, 0,
						Shipments.class, 0);
				dataShipmentsStatus.put("delivered", listShipments.size());

				// Lay thong tin billing
				List<Users> listUsers = clipServices.findAllByProperty("FROM Users WHERE email = '" + email + "'", null,
						0, Users.class, 0);
				if (listUsers.size() > 0) {
					String userId = listUsers.get(0).getId();
					System.out.println(userId);
					List<Wallets> listWallets = clipServices.findAllByProperty(
							"FROM Wallets WHERE user_id = '" + userId + "'", null, 0, Wallets.class, 0);
					if (listWallets.size() > 0) {
						walletId = listWallets.get(0).getId();
						dataBilling.put("dueAmount", listWallets.get(0).getDueAmount());
						dataBilling.put("availableBalance", listWallets.get(0).getBalance());
						dataBilling.put("paidAmount", listWallets.get(0).getSpentAmount());
					}
				}

				// Lay thong tin last shipments
				List<Shipments> listLastShipments = clipServices.findAllByProperty(
						"FROM Shipments WHERE created_by = '" + email + "' ORDER BY created_at DESC", null, 0,
						Shipments.class, 0);
				if (listLastShipments.size() > 0) {
					dataLastShipments.put("shipments", listLastShipments);
				}
				// Lay thong tin last transaction
				List<Transfer> listLastTransactions = clipServices.findAllByProperty(
						"from Transfer Where (from_wallet_id ='" + walletId + "') OR (to_wallet_id ='" + walletId
								+ "') ORDER BY created_at",
						null, 0, Transfer.class, 0);
				if (listLastTransactions.size() > 0) {
					dataLastTransactions.put("transactions", listLastTransactions);
				}

				data.put("shipmentStatus", dataShipmentsStatus);
				data.put("billing", dataBilling);
				data.put("lastShipments", listLastShipments);
				data.put("lastTransactions", listLastTransactions);
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
		DashboardHandler.clipServices = clipServices;
	}

}

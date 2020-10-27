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

	@Override
	public void handle(RoutingContext routingContext) {

		routingContext.vertx().executeBlocking(future -> {
			try {
				Cookie cookie = routingContext.getCookie("sessionId");

				Gson gson = new Gson();
				JsonObject data = new JsonObject();

				String sessionId = cookie.getValue();
				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);

				String email = loggedInUser.getEmail();
				String userId = loggedInUser.getId();
				String walletId = "";

				// delivery info
				JsonObject dataShipmentsStatus = new JsonObject();

				// get Shipments by Email and Status
				List<Shipments> listShipments = getShipments(email, "New");
				dataShipmentsStatus.put("new", listShipments.size());
				listShipments = getShipments(email, "Processing");
				dataShipmentsStatus.put("processing", listShipments.size());
				listShipments = getShipments(email, "In Transit");
				dataShipmentsStatus.put("inTransit", listShipments.size());
				listShipments = getShipments(email, "Delivered");
				dataShipmentsStatus.put("delivered", listShipments.size());

				// wallet info
				JsonObject dataBilling = new JsonObject();

				List<Wallets> listWallets = getWalletsByUserId(userId);

				if (listWallets.size() > 0) {
					walletId = listWallets.get(0).getId();
					dataBilling.put("dueAmount", listWallets.get(0).getDueAmount());
					dataBilling.put("availableBalance", listWallets.get(0).getBalance());
					dataBilling.put("paidAmount", listWallets.get(0).getSpentAmount());
				}

				// shipments info
				JsonObject dataLastShipments = new JsonObject();
				List<Shipments> listLastShipments = getShipmentsByEmail(email);
				if (listLastShipments.size() > 0) {
					dataLastShipments.put("shipments", listLastShipments);
				}
				// transfer info
				JsonObject dataLastTransactions = new JsonObject();
				List<Transfer> listLastTransactions = getTransferByWalletId(walletId);
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

	@SuppressWarnings("unchecked")
	public static List<Shipments> getShipments(String email, String status) {
		List<Shipments> list = null;
		try {
			list = clipServices.findAllByProperty(
					"FROM Shipments WHERE shipping_status = '" + status + "' AND created_by = '" + email + "'", null, 0,
					Shipments.class, 0);
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
	public static List<Shipments> getShipmentsByEmail(String email) {
		List<Shipments> list = null;
		try {
			list = clipServices.findAllByProperty(
					"FROM Shipments WHERE created_by = '" + email + "' ORDER BY created_at DESC", null, 0,
					Shipments.class, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
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

	public static void setClipServices(ClipServices clipServices) {
		DashboardHandler.clipServices = clipServices;
	}

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.server.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.app.models.ClipServices;
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

public class AddFundsHandler implements Handler<RoutingContext>, SessionStore {

	static ClipServices clipServices;

	@Override
	public void handle(RoutingContext routingContext) {

		routingContext.vertx().executeBlocking(future -> {
			try {
				JsonObject jsonRequest = routingContext.getBodyAsJson();
				Cookie cookie = routingContext.getCookie("sessionId");
				Long amount = Long.parseLong(jsonRequest.getString("amount"));

				Gson gson = new Gson();
				JsonObject data = new JsonObject();

				String sessionId = cookie.getValue(); // Lấy sessionId từ cookie
				Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);

				String userId = loggedInUser.getId();

				List<Wallets> listWallets = getWalletsByUserId(userId);

				if (listWallets.size() > 0) {
					Wallets resultWallets = listWallets.get(0);
					resultWallets.setBalance(resultWallets.getBalance() + amount);
					resultWallets.setUpdatedAt(new Date());
					clipServices.saveOrUpdate(resultWallets, Wallets.class, 0);

					data.put("message", "add funds successed");
					routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
					routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
				} else {
					data.put("message", "add funds failed");
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
	public static List<Wallets> getWalletsByUserId(String userId) {
		List<Wallets> list = new ArrayList<>();
		try {
			list = clipServices.findAllByProperty("from Wallets where user_id = '" + userId + "'", null, 0,
					Wallets.class, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void setClipServices(ClipServices clipServices) {
		AddFundsHandler.clipServices = clipServices;
	}

}

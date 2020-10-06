/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.server.handler;

import com.app.models.ClipServices;
import com.app.pojo.Shipments;
import com.app.pojo.Transfer;
import com.app.pojo.Users;
import com.app.pojo.Wallets;
import com.app.session.redis.SessionStore;
import static com.app.session.redis.SessionStore.jedis;
import com.app.util.AppParams;
import com.google.gson.Gson;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.Session;
import java.util.HashMap;
import java.util.List;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class DashboardHandler implements Handler<RoutingContext>, SessionStore {

    static ClipServices clipServices;

    @Override
    public void handle(RoutingContext routingContext) {

        routingContext.vertx().executeBlocking(future -> {
            try {
                Session session = routingContext.session();
                HttpServerRequest httpServerRequest = routingContext.request();
                String sessionId = httpServerRequest.getParam("sessionId");
                routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.BAD_REQUEST.code());
                routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.BAD_REQUEST.reasonPhrase());
                JsonObject data = new JsonObject();
                JsonObject dataShipmentsStatus = new JsonObject();
                JsonObject dataBilling = new JsonObject();
                JsonObject dataLastShipments = new JsonObject();
                JsonObject dataLastTransactions = new JsonObject();

                String email = jedis.hgetAll(sessionId).get("email");

//                routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.UNAUTHORIZED.code());
//                routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.UNAUTHORIZED.reasonPhrase());
                //Dem shipments status
                List<Shipments> listShipments = (List<Shipments>) clipServices.findAllByProperty("FROM Shipments WHERE shipping_status = 'New' AND created_by = '" + email + "'", null, 0, Shipments.class, 0);
                dataShipmentsStatus.put("New", listShipments.size());
                listShipments = (List<Shipments>) clipServices.findAllByProperty("FROM Shipments WHERE shipping_status = 'Processing' AND created_by = '" + email + "'", null, 0, Shipments.class, 0);
                dataShipmentsStatus.put("Processing", listShipments.size());
                listShipments = (List<Shipments>) clipServices.findAllByProperty("FROM Shipments WHERE shipping_status = 'In Transit' AND created_by = '" + email + "'", null, 0, Shipments.class, 0);
                dataShipmentsStatus.put("In Transit", listShipments.size());
                listShipments = (List<Shipments>) clipServices.findAllByProperty("FROM Shipments WHERE shipping_status = 'Delivered'AND created_by = '" + email + "'", null, 0, Shipments.class, 0);
                dataShipmentsStatus.put("Delivered", listShipments.size());

                //Lay thong tin billing
                List<Users> listUsers = (List<Users>) clipServices.findAllByProperty("FROM Users WHERE email = '" + email + "'", null, 0, Users.class, 0);
                if (listUsers.size() > 0) {
                    String userId = listUsers.get(0).getId();
                    System.out.println(userId);
                    List<Wallets> listWallets = (List<Wallets>) clipServices.findAllByProperty("FROM Wallets WHERE user_id = '" + userId + "'", null, 0, Wallets.class, 0);
                    if (listWallets.size() > 0) {
                        dataBilling.put("Due amount", listWallets.get(0).getDueAmount());
                        dataBilling.put("Available balance", listWallets.get(0).getBalance());
                        dataBilling.put("Paid amount", listWallets.get(0).getSpentAmount());
                    }
                }

                //Lay thong tin last shipments
                List<Shipments> listLastShipments = (List<Shipments>) clipServices.findAllByProperty("FROM Shipments WHERE created_by = '" + email + "' ORDER BY created_at DESC", null, 0, Shipments.class, 0);
                if (listLastShipments.size() > 0) {
                    routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
                    routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
//                    for (Shipments shipmentsResult : listLastShipments) {
//                        dataLastShipments.put("date", shipmentsResult.getCreatedAt());
//                        dataLastShipments.put("to", shipmentsResult.getToAddress());
//                        dataLastShipments.put("status", shipmentsResult.getShippingStatus());
//                        dataLastShipments.put("tracking code", shipmentsResult.getTrackingCode());
//                    }
                    dataLastShipments.put("last shipments", listLastShipments);
                }
                //Lay thong tin last transation
                List<Transfer> listLastTransactions = (List<Transfer>) clipServices.findAllByProperty("FROM Transfer ORDER BY created_at", null, 0, Transfer.class, 0);
                if (listLastTransactions.size() > 0) {
                    dataLastTransactions.put("last transactions", listLastTransactions);
                }

                data.put("shipments status", dataShipmentsStatus);
                data.put("billing", dataBilling);
                data.put("last shipments", dataLastShipments);
                data.put("last transactions", dataLastTransactions);

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

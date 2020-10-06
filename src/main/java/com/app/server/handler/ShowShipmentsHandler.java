/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.server.handler;

import com.app.models.ClipServices;
import com.app.pojo.Shipments;
import com.app.session.redis.SessionStore;
import com.app.util.AppParams;
import com.google.gson.Gson;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.Session;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class ShowShipmentsHandler implements Handler<RoutingContext>, SessionStore {

    static ClipServices clipServices;

    @Override
    public void handle(RoutingContext routingContext) {

        routingContext.vertx().executeBlocking(future -> {
            try {
                Gson gson = new Gson();
                Session session = routingContext.session();
                HttpServerRequest httpServerRequest = routingContext.request();
                JsonObject jsonRequest = routingContext.getBodyAsJson();

                String fromDate = jsonRequest.getString("fromDate");
                String toDate = jsonRequest.getString("toDate");

                JsonObject data = new JsonObject();
                //DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                //Date fromDate = dateFormat.parse(source);
                //Date toDate;
                List<Shipments> list = (List<Shipments>) clipServices.findAllByProperty("FROM Shipments WHERE created_at BETWEEN '" + fromDate + "' AND '" + toDate + "'", null, 0, Shipments.class, 0);
                //Users là class chứ ko phải là table trong database 
                System.out.println("users size: " + list.size());
                if (list.size() > 0) {
                    data.put("list", list);
                    DateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
                    System.out.println("date = " + simple.format(list.get(0).getCreatedAt()));
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
        ShowShipmentsHandler.clipServices = clipServices;
    }

}

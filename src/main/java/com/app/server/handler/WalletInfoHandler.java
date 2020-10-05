/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.server.handler;

import com.app.encode.Md5Code;
import com.app.models.ClipServices;
import com.app.pojo.Users;
import com.app.session.redis.SessionStore;
import com.app.util.AppParams;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.Session;
import java.util.List;

public class WalletInfoHandler implements Handler<RoutingContext>, SessionStore {

    static ClipServices clipServices;

    @Override
    public void handle(RoutingContext routingContext) {

        routingContext.vertx().executeBlocking(future -> {
            try {
                HttpServerRequest httpServerRequest = routingContext.request();
                String sessionId = httpServerRequest.getParam("sessionId");
                JsonObject data = new JsonObject();

                String email = jedis.hgetAll(sessionId).get("email");
                System.out.println("email nhan duoc tu sessionid: " + email);
                List<Users> list = (List<Users>) clipServices.findAllByProperty("from Users where email = '" + email + "'", null, 0, Users.class, 0);

                if (list.size() > 0) {
                    Users userResult = list.get(0);

                    data.put("id", userResult.getId());
                    data.put("name", userResult.getName());
                } else {
                    data.put("message", "fail");
                }
                //data.put("ssid2", jedis.hgetAll(sessionId).get("password"));
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
        WalletInfoHandler.clipServices = clipServices;
    }

}

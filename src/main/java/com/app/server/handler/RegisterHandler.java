/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.server.handler;

import com.app.models.ClipServices;
import com.app.pojo.Users;
import com.app.util.AppParams;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.RoutingContext;
import java.util.List;

public class RegisterHandler implements Handler<RoutingContext> {

    static ClipServices clipServices;

    @Override
    public void handle(RoutingContext routingContext) {

        routingContext.vertx().executeBlocking(future -> {
            try {
                HttpServerRequest httpServerRequest = routingContext.request();
                JsonObject jsonResponse = new JsonObject();
                //lay tham so username, password tu path
                JsonObject jsonRequest = routingContext.getBodyAsJson();
                String username = jsonRequest.getString("username");
                String password = jsonRequest.getString("password");
                JsonObject data = new JsonObject();
                data.put("username", username);
                List<Users> list = (List<Users>) clipServices.findAllByProperty("from Users where username = '" + username + "'", null, 0, Users.class, 0);
                boolean duplicate = false;
                if (list.size() > 0) {
                    duplicate = true;
                }
//                if (!duplicate) {
//                    //Users newUser = new Users(name, pass);
//                    clipServices.save(newUser, 0, Users.class, 0);
//                    data.put("status","register successed");
//                    routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.CREATED.code());
//                    routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.CREATED.reasonPhrase());
//                } else {
//                    data.put("status","register failed");
//                    routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.BAD_REQUEST.code());
//                    routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.BAD_REQUEST.reasonPhrase());
//                }
                routingContext.put(AppParams.RESPONSE_DATA, data);
                future.complete();
            } catch (Exception e) {
                routingContext.fail(e);
            }
        },
                asyncResult
                -> {
            if (asyncResult.succeeded()) {
                routingContext.next();
            } else {
                routingContext.fail(asyncResult.cause());
            }
        }
        );
    }

    public static void setClipServices(ClipServices clipServices) {
        RegisterHandler.clipServices = clipServices;
    }

}

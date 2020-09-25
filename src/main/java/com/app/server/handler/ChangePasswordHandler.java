/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.server.handler;

import com.app.encode.Md5Code;
import com.app.models.ClipServices;
import com.app.pojo.Users;
import com.app.util.AppParams;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.RoutingContext;
import java.util.List;

public class ChangePasswordHandler implements Handler<RoutingContext> {

    private static ClipServices clipServices;

    @Override
    public void handle(RoutingContext routingContext) {

        routingContext.vertx().executeBlocking(future -> {
            try {
                HttpServerRequest httpServerRequest = routingContext.request();
                JsonObject jsonResponse = new JsonObject();
                //lay tham so username, password tu path
                JsonObject jsonRequest = routingContext.getBodyAsJson();
                String username = jsonRequest.getString("username");
                String password = Md5Code.md5(jsonRequest.getString("password"));
                String newPassword = jsonRequest.getString("newPassword");
                JsonObject data = new JsonObject();
//                data.put("username", username);
//                data.put("status","change password failed");
//                routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.UNAUTHORIZED.code());
//                routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.UNAUTHORIZED.reasonPhrase());
//                List<Users> list = (List<Users>) clipServices.findAllByProperty("from Users where username = '" + username + "'", null, 0, Users.class, 0);
//                if (list.size() > 0) {
//                    Users resultUser = list.get(0);
//                    if (resultUser.getUsername().equals(username) && resultUser.getMd5Password().equals(password)) {
//                        // "login successed";
//                        Users newUser = new Users(resultUser.getId(), username, newPassword);
//                        clipServices.update(newUser, newUser.getId(), Users.class, 0);
//                        data.put("status","change password successed");
//                        routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
//                        routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
//                    }
//                }
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
        ChangePasswordHandler.clipServices = clipServices;
    }

}

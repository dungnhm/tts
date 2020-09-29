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
import java.util.UUID;

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
                String name = jsonRequest.getString("name");
                String password = jsonRequest.getString("password");
                String email = jsonRequest.getString("email");
                String reTypePassword = jsonRequest.getString("reTypePassword");
                JsonObject data = new JsonObject();
                data.put("email", email);
                List<Users> list = (List<Users>) clipServices.findAllByProperty("from Users where email = '" + email + "'", null, 0, Users.class, 0);
                //generate uuid:
                String uuid = UUID.randomUUID().toString().replace("-", "");
                //uuid.randomUUID sinh ra 36 ki tu
                boolean duplicate = false;
                if (list.size() > 0) {
                    duplicate = true;
                    data.put("reason", "email is duplicated");
                }
                if (!password.equals(reTypePassword)) {
                    duplicate = true;
                    data.put("reason", "password and retype password are not matched");
                }
                if (!duplicate) {
                    Users newUser = new Users(uuid, name, email, password);
                    clipServices.save(newUser, uuid, Users.class, 0);
                    data.put("message", "register successed");
                    routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.CREATED.code());
                    routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.CREATED.reasonPhrase());
                } else {
                    data.put("message", "register failed");
                    routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.BAD_REQUEST.code());
                    routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.BAD_REQUEST.reasonPhrase());
                }
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

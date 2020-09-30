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
import io.vertx.rxjava.ext.web.Cookie;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.Session;
import java.util.List;

public class LoginHandler implements Handler<RoutingContext> {

    static ClipServices clipServices;

    @Override
    public void handle(RoutingContext routingContext) {

        routingContext.vertx().executeBlocking(future -> {
            try {
                Session session = routingContext.session();
                HttpServerRequest httpServerRequest = routingContext.request();

                JsonObject jsonResponse = new JsonObject();
                //lay tham so username, password tu path
                JsonObject jsonRequest = routingContext.getBodyAsJson();
                //String username = jsonRequest.getString("name");
                String email = jsonRequest.getString("email");
                //String password = Md5Code.md5(jsonRequest.getString("password"));
                String password = jsonRequest.getString("password");
                //String data = "login failed";
                JsonObject data = new JsonObject();
                data.put("email", email);
                data.put("status", "login failed");
                routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.UNAUTHORIZED.code());
                routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.UNAUTHORIZED.reasonPhrase());

                List<Users> list = (List<Users>) clipServices.findAllByProperty("from Users where email = '" + email + "'", null, 0, Users.class, 0);
                //Users là class chứ ko phải là table trong database 
                System.out.println("users size: " + list.size());
                if (list.size() > 0) {
                    Users userResult = list.get(0);
                    if (userResult.getPassword().equals(password)) {
                        if (session != null) {
                            session.regenerateId();
                            System.out.println(session.regenerateId().id());
                            session.put("email", email);
                            System.out.println("session cookie: " + routingContext.getCookie("vertx-web.session").getValue());
                            // System.out.println("session cookie: " + routingContext.getCookie("vertx-web.session"));
                            Cookie cookie = routingContext.getCookie("vertx-web.session");
                            cookie.setDomain("192.168.0.226");
                        } else {
                            System.out.println("session is null");
                        }
                        data.put("status", "login successed");
                        routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
                        routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
                    }
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
        LoginHandler.clipServices = clipServices;
    }

}

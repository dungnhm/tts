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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
                String email = jsonRequest.getString("email");
                String password = jsonRequest.getString("password");
                Date createdAt =(Date) jsonRequest.getValue("createdAt");
                Date updatedAt =(Date) jsonRequest.getValue("updatedAt");
                Date lastLogin =(Date) jsonRequest.getValue("lastLogin");
                String state = jsonRequest.getString("state");
                String countryCode = jsonRequest.getString("countryCode");
                //chang pass
                String newPassword = jsonRequest.getString("newPassword");
                String confirmNewPassword = jsonRequest.getString("confirmNewPassword");
                JsonObject data = new JsonObject();
                data.put("email", email);
                data.put("status","change password failed");
                routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.UNAUTHORIZED.code());
                routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.UNAUTHORIZED.reasonPhrase());
                List<Users> list = (List<Users>) clipServices.findAllByProperty("from Users where email = '" + email + "'", null, 0, Users.class, 0);
                if (list.size() > 0) {
                    Users resultUser = list.get(0);
  
                  if (list.size() > 0) {
                    if (resultUser.getEmail().equals(email) && resultUser.getPassword().equals(password)) {
                        if(password.equals(newPassword)) {
                        	data.put("email", email);
                            data.put("status","Password and NewPassword are duplicate");
                        }
                        
                        if(!password.equals(newPassword)) {
                        	if(newPassword.equals(confirmNewPassword)) {
                        		
                                    // "login successed";
                                    Users newUser = new Users(resultUser.getId(),resultUser.getName(), resultUser.getEmail(), newPassword, resultUser.getCreatedAt(),updatedAt,lastLogin,state,countryCode);
                                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                    Date date = new Date();
                                    newUser.setUpdatedAt(date);
                                    clipServices.update(newUser, newUser.getId(), Users.class, 0);
                                    data.put("status","change password successed");
                                    routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
                                    routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
                                }else {
                                	data.put("email", email);
                                    data.put("status","NewPassword and confirmNewPassword are not matched");
                                }
                        	}
                        }
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
        ChangePasswordHandler.clipServices = clipServices;
    }

}

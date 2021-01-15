/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.dashchat.server.handler;

import com.app.dashchat.util.AppParams;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;

public class OptionHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext routingContext) {

        routingContext.vertx().executeBlocking(future -> {
            try {
                routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.NO_CONTENT.code());
                routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.NO_CONTENT.reasonPhrase());
                routingContext.put(AppParams.RESPONSE_DATA, "{}");
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

}

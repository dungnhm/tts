/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.dashchat.server.handler;

import com.app.dashchat.util.AppParams;
import com.app.dashchat.util.LoggerInterface;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;

/**
 *
 * @author ADMIN
 */
public class OrderNotifyHandler implements Handler<RoutingContext>, LoggerInterface {

    int hascache = 0;
    int mexpire = 0;

    @Override
    public void handle(RoutingContext routingContext) {
        routingContext.vertx().executeBlocking(future -> {
            String data, body, source;

            try {
                body = routingContext.getBodyAsString();
                source = routingContext.request().getParam("source");
                logger.info(source + " --> " + body);

                data = "{}";

                routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
                routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
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


}

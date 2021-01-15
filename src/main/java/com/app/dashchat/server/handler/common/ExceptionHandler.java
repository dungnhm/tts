package com.app.dashchat.server.handler.common;

import com.app.dashchat.error.exception.SystemException;
import com.app.dashchat.error.exception.UsersException;
import com.app.dashchat.pojo.MainObject;
import com.app.dashchat.util.AppConstants;
import com.app.dashchat.util.AppUtil;
import com.app.dashchat.util.LoggerInterface;
import com.app.dashchat.util.StringPool;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.web.RoutingContext;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by HungDX on 23-Apr-16.
 */
public class ExceptionHandler implements Handler<RoutingContext>, LoggerInterface {

    @Override
    public void handle(RoutingContext routingContext) {
        MainObject systemError;
        Throwable throwable = routingContext.failure();
        int statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR.code();
        String statusMessage = HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase();
        if (throwable instanceof SystemException) {
            SystemException systemException = (SystemException) throwable;
            systemError = systemException.getSystemError();
            if (throwable instanceof UsersException) {
                statusCode = HttpResponseStatus.OK.code();
                statusMessage = HttpResponseStatus.OK.reasonPhrase();
            } else {
                statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR.code();
                statusMessage = HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase();
            }
        } else {
            logger.error("[ERROR]", throwable);
            systemError = new MainObject(500, "");
        }
        Map responseBodyMap = new LinkedHashMap<>();
        responseBodyMap.put("error", systemError.getError());
        responseBodyMap.put("message", systemError.getMessage());
        String responseBody = new JsonObject(responseBodyMap).encode();
        HttpServerResponse httpServerResponse = routingContext.response();
        httpServerResponse.setStatusCode(statusCode);
        httpServerResponse.setStatusMessage(statusMessage);
        httpServerResponse.putHeader(HttpHeaders.CONTENT_TYPE.toString(), AppConstants.CONTENT_TYPE_APPLICATION_JSON);
        httpServerResponse.putHeader(HttpHeaders.CONTENT_LENGTH.toString(), AppUtil.getContentLength(responseBody));
        logger.info("[RESPONSE] " + statusCode + StringPool.DOUBLE_SPACE + statusMessage);
        logger.info("[RESPONSE] BODY: " + responseBody);
        logger.info("[RESPONSE] ****************************** DONE ******************************" + StringPool.NEW_LINE);
        httpServerResponse.end(new JsonObject(responseBody).encode());
    }

}

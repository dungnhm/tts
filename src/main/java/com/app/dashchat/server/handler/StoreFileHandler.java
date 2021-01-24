/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.dashchat.server.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import com.app.dashchat.pojo.Users;
import com.app.dashchat.session.redis.SessionStore;
import com.app.dashchat.util.AppParams;
import com.app.dashchat.util.Common;
import com.google.gson.Gson;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.MultiMap;
import io.vertx.rxjava.ext.web.Cookie;
import io.vertx.rxjava.ext.web.FileUpload;
import io.vertx.rxjava.ext.web.RoutingContext;

public class StoreFileHandler implements Handler<RoutingContext>, SessionStore {

	private static String hostDir;

	public static void setHostDir(String hostDir) {
		StoreFileHandler.hostDir = hostDir;
	}

	private static String tempDir;

	public static void setTempDir(String tempDir) {
		StoreFileHandler.tempDir = tempDir;
	}

	@Override
	public void handle(RoutingContext routingContext) {

		routingContext.vertx().executeBlocking(future -> {
			try {
				Map response = new LinkedHashMap();

				Cookie cookie = routingContext.getCookie("sessionId");
				Users loggedInUser = new Gson().fromJson(jedis.get(cookie.getValue()), Users.class);
				String sender = loggedInUser.getId();

				MultiMap attributes = routingContext.request().formAttributes();
				Set<FileUpload> uploads = routingContext.fileUploads();
				JsonObject requestBodyJson = getRequestParams(attributes);
				String type = requestBodyJson.getString("type");
				String receiver = requestBodyJson.getString("receiver");

				String filename = "";
				hostDir = "J:/CongViec/intern_dung/Test/tts/";
				tempDir = "file-uploads/";
				List<String> listFile = new ArrayList<>();
				Iterator<FileUpload> fileUploadIterator = uploads.iterator();
				if (fileUploadIterator.hasNext()) {
					FileUpload fileUpload = fileUploadIterator.next();
					String mimetype = fileUpload.contentType();
					long uploadsize = fileUpload.size();
					if (mimetype.indexOf("png") > 0) {
						LOGGER.info("== image = " + fileUpload.fileName() + " " + mimetype + " " + uploadsize);
						filename = Common.generateRandomString(new Random(), 25) + fileUpload.fileName();
						listFile.add(hostDir + tempDir + fileUpload.uploadedFileName().substring(tempDir.length()) + ".png");
					} else if (mimetype.indexOf("jpeg") > 0) {
						LOGGER.info("== image = " + fileUpload.fileName() + " " + mimetype + " " + uploadsize);
						filename = Common.generateRandomString(new Random(), 25) + fileUpload.fileName();
						listFile.add(hostDir + tempDir + fileUpload.uploadedFileName().substring(tempDir.length()) + ".jpeg");
					} else if (mimetype.indexOf("jpg") > 0) {
						LOGGER.info("== image = " + fileUpload.fileName() + " " + mimetype + " " + uploadsize);
						filename = Common.generateRandomString(new Random(), 25) + fileUpload.fileName();
						listFile.add(hostDir + tempDir + fileUpload.uploadedFileName().substring(tempDir.length()) + ".jpg");
					} else {
						LOGGER.info("!= image");
					}
				}
				
				response.put("upload", listFile);

				routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.OK.code());
				routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.OK.reasonPhrase());
				routingContext.put(AppParams.RESPONSE_DATA, response);
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

	private JsonObject getRequestParams(MultiMap params) {

		JsonObject paramMap = new JsonObject();
		for (Map.Entry entry : params.getDelegate().entries()) {
			String key = (String) entry.getKey();
			Object value = entry.getValue();
			if (value instanceof List) {
				value = (List<String>) entry.getValue();
			} else {
				value = (String) entry.getValue();
			}
			paramMap.put(key, value);
		}
		return paramMap;
	}

	private static final Logger LOGGER = Logger.getLogger(StoreFileHandler.class.getName());

}

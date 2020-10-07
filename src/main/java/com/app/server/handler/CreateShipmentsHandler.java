/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.server.handler;

import com.app.models.ClipServices;
import com.app.pojo.Addresses;
import com.app.pojo.Parcels;
import com.app.pojo.Shipments;
import com.app.pojo.Users;
import com.app.session.redis.SessionStore;
import com.app.util.AppParams;
import com.google.gson.Gson;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.Session;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CreateShipmentsHandler implements Handler<RoutingContext>, SessionStore {

    static ClipServices clipServices;

    @Override
    public void handle(RoutingContext routingContext) {

        routingContext.vertx().executeBlocking(future -> {
            try {
                Gson gson = new Gson();
                JsonObject data = new JsonObject();
                //Session session = routingContext.session();
                HttpServerRequest httpServerRequest = routingContext.request();
                JsonObject jsonRequest = routingContext.getBodyAsJson();
                //session of logging in user
                String sessionId = jsonRequest.getString("sessionId");
                Users loggedInUser = gson.fromJson(jedis.get(sessionId), Users.class);
                String email = loggedInUser.getEmail();
                System.out.println(loggedInUser.getName() + " "+ loggedInUser.getId());
                //ship to
                String name = jsonRequest.getString("name");
                String phoneNumber = jsonRequest.getString("phoneNumber");
                //ship address
                String company = jsonRequest.getString("company");
                String address1 = jsonRequest.getString("address1");
                String address2 = jsonRequest.getString("address2");
                String country = jsonRequest.getString("country");
                String state = jsonRequest.getString("state");
                String zip = jsonRequest.getString("zip");
                //package info
                float parcelHeight = Float.parseFloat(jsonRequest.getString("parcelHeight"));
                float parcelWidth = Float.parseFloat(jsonRequest.getString("parcelWidth"));
                float parcelLength = Float.parseFloat(jsonRequest.getString("parcelLength"));
                float parcelWeight = Float.parseFloat(jsonRequest.getString("parcelWeight"));
                String note = jsonRequest.getString("note");

                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();

                //create pojo object
                Addresses newAddress = new Addresses();
                Parcels newParcel = new Parcels();
                Shipments newShipment = new Shipments();
                //add newAddress into database
                String addressId = UUID.randomUUID().toString().replace("-", "");
                newAddress.setId(addressId);
                newAddress.setName(name);
                newAddress.setPhone(phoneNumber);
                newAddress.setCompany(company);
                newAddress.setAddress1(address1);
                newAddress.setAddress2(address2);
                newAddress.setCountry(country);
                newAddress.setState(state);
                newAddress.setCity(state);
                newAddress.setZip(zip);
                newAddress.setCreatedBy(email);
                newAddress.setCreatedAt(date);
                clipServices.save(newAddress, addressId, Addresses.class, 0);
                //add newShipment into database
                String shipmentId = UUID.randomUUID().toString().replace("-", "");
                newShipment.setId(shipmentId);
                newShipment.setFromAddress(address1);
                newShipment.setToAddress(address2);
                newShipment.setCarrierId("0");
                newShipment.setShippingStatus("New");
                String tracking_code = UUID.randomUUID().toString().replace("-", "");
                newShipment.setTrackingCode(tracking_code);
                newShipment.setCreatedAt(date);
                newShipment.setCreatedBy(email);
                clipServices.save(newShipment, shipmentId, Shipments.class, 0);
                //add newParcel into database
                String parcelId = UUID.randomUUID().toString().replace("-", "");
                newParcel.setId(parcelId);
                newParcel.setShipmentId(shipmentId);
                newParcel.setHeight(parcelHeight);
                newParcel.setWidth(parcelWidth);
                newParcel.setLength(parcelLength);
                newParcel.setWeight(parcelWeight);
                newParcel.setPredefinedPackage(note);
                newParcel.setCreatedBy(email);
                newParcel.setCreatedAt(date);
                clipServices.save(newParcel, parcelId, Parcels.class, 0);

                routingContext.put(AppParams.RESPONSE_CODE, HttpResponseStatus.CREATED.code());
                routingContext.put(AppParams.RESPONSE_MSG, HttpResponseStatus.CREATED.reasonPhrase());
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
        CreateShipmentsHandler.clipServices = clipServices;
    }

}

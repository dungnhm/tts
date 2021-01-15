/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.dashchat.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hungdt
 */
public class MainObject {

    private int error;
    private String message;
    private List data;
    private String urlUpdate;

    public MainObject(int error, String message) {
        this.error = error;
        this.message = message;
        this.urlUpdate = "";
    }

    public MainObject() {
        error = 0;
        message = "success";
        urlUpdate = "";
    }

    public void addData(Object object) {
        if (data == null) {
            data = new ArrayList();
        }
        data.add(object);
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }

    public String getUrlUpdate() {
        return urlUpdate;
    }

    public void setUrlUpdate(String urlUpdate) {
        this.urlUpdate = urlUpdate;
    }

}

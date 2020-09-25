/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hungdt
 */
public class MainContent {

    private String name;
    private List data;

    public MainContent() {
    }

    public void addData(Object object) {
        if (data == null) {
            data = new ArrayList();
        }
        data.add(object);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }

}

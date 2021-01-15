/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.dashchat.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author hungdt
 */
public class DCMain {

	public static ApplicationContext appContext;

	public DCMain() {
	}

	public static void main(String[] args) throws Exception {
		appContext = new ClassPathXmlApplicationContext("app-context.xml");
	}
}

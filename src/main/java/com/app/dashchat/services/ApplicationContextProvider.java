/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.dashchat.services;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author huybq0510
 */
public class ApplicationContextProvider implements ApplicationContextAware {

    public ApplicationContextProvider() {
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        AppContext.setApplicationContext(ac);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.dashchat.dao.hibernate;

import com.app.dashchat.dao.ClipDao;

/**
 *
 * @author huybq0510
 */
public class ClipDaoHibernate extends GenericDaoHibernate implements ClipDao {

//    private Class class1;
    private String db;

    public ClipDaoHibernate() {
    }

//    protected Class getEntityClass() {
//        return class1;
//    }
//
//    public void setEntityClass(String classname) {
//        try {
//            class1 = Class.forName(classname);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.info((new StringBuilder()).append("ex:").append(e).toString());
//        }
//    }
    @Override
    protected String getEntityDB() {
        return db;
    }

    @Override
    public void setEntityDB(String db) throws Exception {
        this.db = db;
    }
}

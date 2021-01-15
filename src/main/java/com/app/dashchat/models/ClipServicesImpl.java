/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.dashchat.models;

import com.app.dashchat.dao.ClipDao;
import com.app.dashchat.util.PageBean;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author huybq0510
 */
public class ClipServicesImpl implements ClipServices {

    private ClipDao clipDao;

    public ClipServicesImpl() {
    }

    public ClipDao getClipDao() {
        return clipDao;
    }

    public void setClipDao(ClipDao clipDao) {
        this.clipDao = clipDao;
    }

    @Override
    public Serializable save(Object newInstance, Serializable id, Class class1, int mexpire)
            throws Exception {
        return clipDao.save(newInstance, id, class1, mexpire);
    }

    @Override
    public void update(Object transientObject, Serializable id, Class class1, int mexpire)
            throws Exception {
        clipDao.update(transientObject, id, class1, mexpire);
    }

    @Override
    public void saveOrUpdate(Object transientObject, Class class1, int mexpire)
            throws Exception {
        clipDao.saveOrUpdate(transientObject, class1, mexpire);
    }

    @Override
    public void delete(Object persistentObject, Serializable id, Class class1, int mexpire)
            throws Exception {
        clipDao.delete(persistentObject, id, class1, mexpire);
    }

    @Override
    public Object findById(Serializable id, Class class1, int mexpire)
            throws Exception {
        return clipDao.findById(id, class1, mexpire);
    }

    @Override
    public int countContent(String query, Class class1, int mexpire)
            throws Exception {
        return clipDao.countContent(query, class1, mexpire);
    }

    @Override
    public List findAllByProperty(String sql, PageBean pageBean, int hascache, Class class1, int mexpire)
            throws Exception {
        return clipDao.findAllByProperty(sql, pageBean, hascache, class1, mexpire);
    }

    @Override
    public List findAllByProperty(String s, int hascache, Class class1, int mexpire) throws Exception {
        return clipDao.findAllByProperty(s, hascache, class1, mexpire);
    }

    @Override
    public void update(String s, Class class1, int mexpire) throws Exception {
        clipDao.update(s, class1, mexpire);
    }

    @Override
    public List callProcedure(String s, int hascache, Class class1, int mexpire) throws Exception {
        return clipDao.callProcedure(s, hascache, class1, mexpire);
    }

    @Override
    public void setEntityDB(String s) throws Exception {
        clipDao.setEntityDB(s);
    }
}

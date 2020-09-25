package com.app.dao;

import com.app.util.PageBean;
import java.io.Serializable;
import java.util.List;

public interface GenericDao {

    public abstract Serializable save(Object obj, Serializable id, Class class1, int mexpire) throws Exception;

    public abstract void update(Object obj, Serializable id, Class class1, int mexpire) throws Exception;

    public abstract void saveOrUpdate(Object obj, Class class1, int mexpire) throws Exception;

    public abstract void delete(Object obj, Serializable id, Class class1, int mexpire) throws Exception;

    public abstract Object findById(Serializable serializable, Class class1, int mexpire) throws Exception;

    public abstract int countContent(String s, Class class1, int mexpire) throws Exception;

    public abstract List findAllByProperty(String s, PageBean pagebean, int hascache, Class class1, int mexpire) throws Exception;

    public abstract List findAllByProperty(String s, int hascache, Class class1, int mexpire) throws Exception;

    public abstract void update(String s, Class class1, int mexpire) throws Exception;

    public abstract List callProcedure(String s, int hascache, Class class1, int mexpire) throws Exception;
}

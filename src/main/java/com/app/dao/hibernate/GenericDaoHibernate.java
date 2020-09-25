package com.app.dao.hibernate;

import com.app.dao.GenericDao;
import com.app.util.Common;
import com.app.util.PageBean;
import com.google.gson.Gson;
import java.io.Serializable;
import java.util.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

public abstract class GenericDaoHibernate extends HibernateDaoSupport implements GenericDao {

    private HibernateTemplate hibernateTemplateGate;

    public void setHibernateTemplateGate(HibernateTemplate hibernateTemplateGate) {
        this.hibernateTemplateGate = hibernateTemplateGate;
    }

    public HibernateTemplate getHibernateTemplateGate() {
        return hibernateTemplateGate;
    }

//    private HibernateTemplate hibernateTemplateStory;
    public HibernateTemplate getHibernateTemplateApp() {
        if (getEntityDB() == null) {
            return super.getHibernateTemplate();
        } else if (getEntityDB().equalsIgnoreCase("gate")) {
            return getHibernateTemplateGate();
        } else {
            return super.getHibernateTemplate();
        }
    }

    public GenericDaoHibernate() {
    }

    @Override
    public Serializable save(Object newInstance, Serializable id, Class class1, int mexpire) throws Exception {
//        int hasServer = 0;
//        hasServer = Common.parseInt(String.valueOf(id), 0);
//        if(hasServer == -1){
//            String skey = Common.getKey(getEntityDB() + "_" + class1.getName() + "_" + String.valueOf(id));
//            redisTemplate.opsForHash().put(skey, getEntityDB(), newInstance);
//            return 0;
//        }else{
        return getHibernateTemplateApp().save(newInstance);
//        }        
    }

    @Override
    public Object findById(Serializable id, Class class1, int mexpire) throws Exception {
        Object ret = null;
//        Object obj = null;
//        Gson gson = new Gson();
//        String temp = class1.getName();
//        if (temp.lastIndexOf(".") > 0) {
//            temp = temp.substring(temp.lastIndexOf(".") + 1);
//        }
//        String skey = Common.getKey(getEntityDB() + "_" + temp + "_" + String.valueOf(id));
//        String data;
//        try {
//            obj = redisTemplate.opsForValue().get(skey);
//            if (obj != null) {
//                ret = gson.fromJson(String.valueOf(obj), class1);
//            }
//        } catch (Exception e) {
//            ret = null;
//            logger.error("", e);
//        }
        if (ret == null) {
            ret = getHibernateTemplateApp().get(class1, id);
//            data = gson.toJson(ret);
//            redisTemplate.opsForValue().set(skey, data);
//            redisTemplate.expire(skey, mexpire, TimeUnit.MINUTES);
        }
        return ret;
    }

    @Override
    public void update(Object transientObject, Serializable id, Class class1, int mexpire) throws Exception {
        getHibernateTemplateApp().update(transientObject);
//        String skey = Common.getKey(getEntityDB() + "_" + class1.getName() + "_" + String.valueOf(id));
//        List list = new ArrayList();
//        list.add(transientObject);
//        redisTemplate.opsForHash().put(skey, getEntityDB(), list);
//        redisTemplate.expire(skey, mexpire, TimeUnit.MINUTES);
    }

    @Override
    public void saveOrUpdate(Object transientObject, Class class1, int mexpire) throws Exception {
        getHibernateTemplateApp().saveOrUpdate(transientObject);
    }

    @Override
    public void delete(Object persistentObject, Serializable id, Class class1, int mexpire) throws Exception {
        getHibernateTemplateApp().delete(persistentObject);
        String skey = Common.getKey(getEntityDB() + "_" + class1.getName() + "_" + String.valueOf(id));
//        redisTemplate.opsForHash().delete(skey, getEntityDB());
    }

    @Override
    public int countContent(String query, Class class1, int mexpire) throws Exception {
        List list = getHibernateTemplateApp().find(query);
        return Common.parseInt(String.valueOf(list.get(0)), 0);
    }

    @Override
    public List findAllByProperty(final String sql, final PageBean pageBean, int hascache, final Class class1, int mexpire)
            throws Exception {
        List ret = null;
        Object obj;
//        String temp = class1.getName();
//        if (temp.lastIndexOf(".") > 0) {
//            temp = temp.substring(temp.lastIndexOf(".") + 1);
//        }

//        Vpnserver
//        if (temp.matches("Vpnserver|Vpnprofiles")) {
//            if (temp.indexOf("Vpnserver") >= 0) {
//                obj = mongoTemplate.findAll(Vpnserver.class, "Vpnserver");
//            } else {
//                org.springframework.data.mongodb.core.query.Query searchProfileQuery = new org.springframework.data.mongodb.core.query.Query();
//                searchProfileQuery.addCriteria(
//                        new Criteria().andOperator(
//                                Criteria.where("countryShort").is(sql),
//                                Criteria.where("active").is(1)
//                        )
//                );
//
//                obj = mongoTemplate.findOne(searchProfileQuery, Vpnprofiles.class, "Vpnprofiles");
//            }
//        } else {
            obj = getHibernateTemplateApp().execute((Session session) -> {
                Query query;
                query = session.createQuery(sql);
                if ((pageBean != null) && (pageBean.getCount() != -1)) {
                    final int first = (pageBean.getPage() - 1) * pageBean.getPageSize();
                    final int max = pageBean.getPageSize();
                    query.setFirstResult(first);
                    query.setMaxResults(max);
                }
                return query.list();
        });
//        }

        if (obj != null) {
            ret = (ArrayList) obj;
        }
        if (ret == null) {
            ret = new ArrayList();
        }
        return ret;
    }

    @Override
    public List findAllByProperty(
            final String sql, int hascache, Class class1, int mexpire)
            throws Exception {
        List ret = null;
        Object obj;
        String temp = class1.getName();
        if (temp.lastIndexOf(".") > 0) {
            temp = temp.substring(temp.lastIndexOf(".") + 1);
        }
        String skey = getEntityDB() + "_" + temp + "_" + sql;
        String data;
//        MainObject mainObject;
        Gson gson = new Gson();
//        if (hascache == 1) {
//            skey = Common.getKey(skey);
//            try {
//                obj = (List) redisTemplate.opsForValue().get(skey);
//                if (obj != null) {
//                    mainObject = gson.fromJson(String.valueOf(obj), MainObject.class);
////                    if (class1.getName().indexOf("Category") >= 0) {
////                        ret = mainObject.getListCategory();
////                    } else if (class1.getName().indexOf("Clip") >= 0) {
//                        ret = mainObject.getListClip();
////                    }
//                }
//            } catch (Exception e) {
//                logger.error("", e);
//                ret = null;
//            }
//        }
        if (ret == null) {
            obj = getHibernateTemplateApp().execute((Session session) -> {
                Query query = session.createQuery(sql);
                return query.list();
            });
            if (obj != null) {
                ret = (ArrayList) obj;
            }
//            if (hascache == 1) {
//                mainObject = new MainObject();
////                if (class1.getName().indexOf("Category") >= 0) {
////                    mainObject.setListCategory(ret);
////                } else if (class1.getName().indexOf("Clip") >= 0) {
//                    mainObject.setListClip(ret);
////                }
//                data = gson.toJson(mainObject);
//                redisTemplate.opsForValue().set(skey, data);
//                redisTemplate.expire(skey, mexpire, TimeUnit.MINUTES);
//            }
        }
        if (ret == null) {
            ret = new ArrayList();
        }
        return ret;
    }

    @Override
    public void update(final String sql, Class class1, int mexpire) {
        getHibernateTemplateApp().execute((Session session) -> {
            Query query = session.createQuery(sql);//SQL
            return query.executeUpdate();
        });
    }

    @Override
    public List callProcedure(final String sql, int hascache, final Class class1, int mexpire) {
        Object obj;
//        MainObject mainObject = new MainObject();
        List ret = null;
//        Gson gson = new Gson();
//        String temp = class1.getName();
//        if (temp.lastIndexOf(".") > 0) {
//            temp = temp.substring(temp.lastIndexOf(".") + 1);
//        }
//        String skey = getEntityDB() + "_" + temp + "_" + sql, data;
//        skey = Common.getKey(skey);
//        logger.info("hascache: " + hascache + "|" + skey);
//        if (hascache == 1) {
//            try {
//                obj = redisTemplate.opsForValue().get(skey);
//                if (obj != null) {
//                    data = String.valueOf(obj);
//                    mainObject = gson.fromJson(data, MainObject.class);
//                    if (mainObject != null) {
//                        ret = mainObject.getListClip();
//                    }
//                }
//            } catch (Exception e) {
//                ret = null;
//                logger.error("", e);
//            }
//        }
        if (ret == null) {
//            logger.info("Load from db");
            obj = getHibernateTemplateApp().execute((Session session) -> {
                
                Query query = session.createNativeQuery(sql, class1);//, class1
                return query.list();
            });
            if (obj != null) {
                ret = (ArrayList) obj;
            }
//            if (hascache == 1) {
//                mainObject.setListClip(ret);
//                data = gson.toJson(mainObject);
//                redisTemplate.opsForValue().set(skey, data);
//                redisTemplate.expire(skey, mexpire, TimeUnit.MINUTES);
//            }
        }
//        else {
//            logger.info("Load from cache");
//        }
        if (ret == null) {
            ret = new ArrayList();
        }
        return ret;
    }

    protected abstract String getEntityDB();
}

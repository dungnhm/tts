package com.app.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PageBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private int count;
    private int pageSize;
    private int pageCount;
    private int page;
    private String actionName;
    private List listPage;
    private String date;
    private Integer competition;
    private int status;
    private String username;

    public PageBean() {
        count = 0;
        pageSize = 20;
        pageCount = 0;
        page = 1;
        listPage = new ArrayList();
    }

    public PageBean(int count) {
        this.count = 0;
        pageSize = 10;
        pageCount = 0;
        page = 1;
        this.count = count;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        if (pageSize != 0) {
            pageCount = count / pageSize;
            if (count % pageSize != 0) {
                pageCount++;
            }
        }
        this.count = count;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page >= 1 ? page : 1;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List getListPage() {
        return listPage;
    }

    public void setListPage(List listPage) {
        this.listPage = listPage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getCompetition() {
        return competition;
    }

    public void setCompetition(Integer competition) {
        this.competition = competition;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

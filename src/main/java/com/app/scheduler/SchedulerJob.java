/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

/**
 *
 * @author ADMIN
 */
public class SchedulerJob implements Job {

    private String id;
    private String groupId;
    private String cronExpression;
    private JobListener listener;
    

    public void setId(String id) {
        this.id = id;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public void setListener(JobListener listener) {
        this.listener = listener;
    }

    public String getId() {
        return id;
    }

    public String getGroupId() {
        return groupId;
    }

    public JobListener getListener() {
        return listener;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        this.execute(jobExecutionContext);
    }

}

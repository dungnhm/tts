/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.scheduler;

import com.app.services.LoggerInterface;
import java.util.Date;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author ADMIN
 */
public class SchedulerJobOrderImport extends SchedulerJob implements LoggerInterface {

    static final String JOB_KEY = "OrderImport";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("SchedulerJobOrderImport start at " + new Date());

        try {
        } catch (Exception e) {
            logger.error("", e);
        }
    }

}

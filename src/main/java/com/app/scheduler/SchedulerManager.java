/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.scheduler;

import com.app.services.LoggerInterface;
import java.util.List;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author ADMIN
 */
public class SchedulerManager implements LoggerInterface {

    private static List<SchedulerJob> jobList;

    public void setExecutionJobs(List<SchedulerJob> executionJobs) {
        this.jobList = executionJobs;
    }

    public void init() {

        try {

            logger.info("Initializing scheduler for " + jobList.size() + " jobs...");

            Scheduler scheduler = new StdSchedulerFactory().getScheduler();

            for (SchedulerJob job : jobList) {

                JobKey jobKey = new JobKey(job.getId(), job.getGroupId());

                JobDetail jobDetail = JobBuilder.newJob(job.getClass()).withIdentity(jobKey).build();

                ScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());

                logger.info("[" + jobDetail.getKey().getGroup() + "." + jobDetail.getKey().getName() + "] " + job.getCronExpression());

                TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger();

                triggerBuilder.withSchedule(scheduleBuilder);

                Trigger trigger = triggerBuilder.build();

                scheduler.scheduleJob(jobDetail, trigger);

                logger.info("[" + jobDetail.getKey().getGroup() + "." + jobDetail.getKey().getName() + "] Init OK!");
            }

            scheduler.start();

        } catch (SchedulerException e) {
            logger.error("", e);
        }
    }
}

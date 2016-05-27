package org.carlspring.strongbox.crontask.quartz;

import org.carlspring.strongbox.crontask.CronTaskNotFoundException;
import org.carlspring.strongbox.crontask.configuration.CronTaskConfiguration;

import java.util.HashMap;
import java.util.Map;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

/**
 * @author Yougeshwar
 */
@Service
public class CronJobSchedulerService
{

    private final Logger logger =
            LoggerFactory.getLogger(CronJobSchedulerService.class);

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    private Map<String, CronTaskStruct> map = new HashMap<>();

    public void scheduleJob(CronTaskConfiguration cronTaskConfiguration)
            throws ClassNotFoundException, SchedulerException
    {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        if (map.containsKey(cronTaskConfiguration.getName()))
        {
            CronTaskStruct cronTaskStruct = map.get(cronTaskConfiguration.getName());
            JobDetail jobDetail = cronTaskStruct.getJobDetail();
            org.quartz.Trigger oldTrigger = cronTaskStruct.getTrigger();

            org.quartz.Trigger newTrigger = TriggerBuilder.newTrigger().withIdentity(
                    cronTaskConfiguration.getName()).withSchedule(
                    CronScheduleBuilder.cronSchedule(cronTaskConfiguration.getCronExpression())).build();

            scheduler.addJob(jobDetail, true, true);
            scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);

            cronTaskStruct.setTrigger(newTrigger);
        }
        else
        {
            JobDetail jobDetail = JobBuilder.newJob(
                    (Class<? extends Job>) Class.forName(cronTaskConfiguration.getJobClass())).withIdentity(
                    cronTaskConfiguration.getName()).build();

            org.quartz.Trigger trigger = TriggerBuilder.newTrigger().withIdentity(
                    cronTaskConfiguration.getName()).withSchedule(
                    CronScheduleBuilder.cronSchedule(cronTaskConfiguration.getCronExpression())).build();

            scheduler.scheduleJob(jobDetail, trigger);

            CronTaskStruct cronTaskStruct = new CronTaskStruct();
            cronTaskStruct.setJobDetail(jobDetail);
            cronTaskStruct.setTrigger(trigger);
            map.put(cronTaskConfiguration.getName(), cronTaskStruct);
        }

        if (!scheduler.isStarted())
        {
            logger.info("Scheduler started");
            scheduler.start();
        }
        logger.info("Job scheduled successfully");
    }

    public void deleteJob(CronTaskConfiguration cronTaskConfiguration)
            throws ClassNotFoundException, SchedulerException, CronTaskNotFoundException
    {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        if (!map.containsKey(cronTaskConfiguration.getName()))
        {
            throw new CronTaskNotFoundException("Cron Task not found on given name");
        }

        CronTaskStruct cronTaskStruct = map.get(cronTaskConfiguration.getName());
        JobDetail jobDetail = cronTaskStruct.getJobDetail();
        org.quartz.Trigger trigger = cronTaskStruct.getTrigger();

        scheduler.unscheduleJob(trigger.getKey());
        scheduler.deleteJob(jobDetail.getKey());

        map.remove(cronTaskConfiguration.getName());

        logger.info("Job un-scheduled successfully");
    }
}

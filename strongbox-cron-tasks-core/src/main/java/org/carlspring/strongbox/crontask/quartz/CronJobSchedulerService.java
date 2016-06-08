package org.carlspring.strongbox.crontask.quartz;

import org.carlspring.strongbox.crontask.api.jobs.GroovyCronJob;
import org.carlspring.strongbox.crontask.exceptions.CronTaskNotFoundException;
import org.carlspring.strongbox.crontask.configuration.CronTaskConfiguration;

import java.util.*;

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

    private Map<String, CronTaskStruct> jobsMap = new HashMap<>();

    public void scheduleJob(CronTaskConfiguration cronTaskConfiguration)
            throws ClassNotFoundException, SchedulerException
    {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        if (jobsMap.containsKey(cronTaskConfiguration.getName()))
        {
            CronTaskStruct cronTaskStruct = jobsMap.get(cronTaskConfiguration.getName());
            JobDetail jobDetail = cronTaskStruct.getJobDetail();
            org.quartz.Trigger oldTrigger = cronTaskStruct.getTrigger();

            org.quartz.Trigger newTrigger = TriggerBuilder.newTrigger().withIdentity(
                    cronTaskConfiguration.getName()).withSchedule(
                    CronScheduleBuilder.cronSchedule(
                            cronTaskConfiguration.getProperty("cronExpression").toString())).build();

            scheduler.addJob(jobDetail, true, true);
            scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);

            cronTaskStruct.setTrigger(newTrigger);
            cronTaskStruct.setScriptName(cronTaskConfiguration.getProperty("fileName"));
        }
        else
        {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("configuration", cronTaskConfiguration);

            JobDetail jobDetail = JobBuilder.newJob(
                    (Class<? extends Job>) Class.forName(
                            cronTaskConfiguration.getProperty("jobClass"))).withIdentity(
                    cronTaskConfiguration.getName()).setJobData(jobDataMap).build();

            org.quartz.Trigger trigger;
            trigger = TriggerBuilder.newTrigger().withIdentity(
                    cronTaskConfiguration.getName()).withSchedule(
                    CronScheduleBuilder.cronSchedule(
                            cronTaskConfiguration.getProperty("cronExpression"))).build();

            scheduler.scheduleJob(jobDetail, trigger);

            CronTaskStruct cronTaskStruct = new CronTaskStruct();
            cronTaskStruct.setJobDetail(jobDetail);
            cronTaskStruct.setTrigger(trigger);
            cronTaskStruct.setScriptName(cronTaskConfiguration.getProperty("fileName"));
            jobsMap.put(cronTaskConfiguration.getName(), cronTaskStruct);
        }

        if (!scheduler.isStarted())
        {
            logger.debug("Scheduler started");
            scheduler.start();
        }
        logger.debug("Job scheduled successfully");
    }

    public void deleteJob(CronTaskConfiguration cronTaskConfiguration)
            throws ClassNotFoundException, SchedulerException, CronTaskNotFoundException
    {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        if (!jobsMap.containsKey(cronTaskConfiguration.getName()))
        {
            throw new CronTaskNotFoundException("Cron Task not found on given name");
        }

        CronTaskStruct cronTaskStruct = jobsMap.get(cronTaskConfiguration.getName());
        JobDetail jobDetail = cronTaskStruct.getJobDetail();
        org.quartz.Trigger trigger = cronTaskStruct.getTrigger();

        scheduler.unscheduleJob(trigger.getKey());
        scheduler.deleteJob(jobDetail.getKey());

        jobsMap.remove(cronTaskConfiguration.getName());

        logger.debug("Job un-scheduled successfully");
    }

    public List<String> getGroovyScriptsName()
    {
        List<String> list = Collections.emptyList();
        for (CronTaskStruct struct : jobsMap.values())
        {
            if (struct.getScriptName() != null && !struct.getScriptName().isEmpty() &&
                struct.getScriptName().endsWith(".groovy"))
            {
                list.add(struct.getScriptName());
            }
        }
        return list;
    }
}

package org.carlspring.strongbox.crontask.test;

import org.carlspring.strongbox.crontask.api.jobs.JavaCronJob;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * @author Yougeshwar
 */
public class MyTask
        extends JavaCronJob
{

    private final Logger logger = LoggerFactory.getLogger(MyTask.class);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext)
            throws JobExecutionException
    {
        logger.debug("My Rest Task scheduled job");

        try
        {
            getSchedulerFactoryBean().getScheduler().deleteJob(getCronTask().getJobDetail().getKey());
        }
        catch (SchedulerException e)
        {
            logger.error("Stop job error", e);
            e.printStackTrace();
        }

    }
}

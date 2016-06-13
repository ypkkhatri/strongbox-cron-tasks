package org.carlspring.strongbox.crontask.test;

import org.carlspring.strongbox.crontask.api.jobs.JavaCronJob;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author Yougeshwar
 */
public class MyTask
        extends JavaCronJob
{

    private final Logger logger = LoggerFactory.getLogger(MyTask.class);

    @Autowired
    private Scheduler schedulerFactoryBean;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext)
            throws JobExecutionException
    {
        logger.debug("My Rest Task scheduled job");

        try
        {
            schedulerFactoryBean.interrupt(jobExecutionContext.getJobDetail().getKey());
        }
        catch (UnableToInterruptJobException e)
        {
            logger.error("Stop job error", e);
            e.printStackTrace();
        }

    }
}

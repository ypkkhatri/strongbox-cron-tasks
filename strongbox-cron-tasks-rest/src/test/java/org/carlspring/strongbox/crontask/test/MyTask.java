package org.carlspring.strongbox.crontask.test;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author Yougeshwar
 */
public class MyTask
        extends QuartzJobBean
{
    private final Logger logger =
            LoggerFactory.getLogger(MyTask.class);
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException
    {
        logger.debug("My Rest Task scheduled job");
    }
}

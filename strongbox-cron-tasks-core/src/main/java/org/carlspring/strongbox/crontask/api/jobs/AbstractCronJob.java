package org.carlspring.strongbox.crontask.api.jobs;

import org.carlspring.strongbox.crontask.configuration.CronTaskConfiguration;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author carlspring
 */
public abstract class AbstractCronJob extends QuartzJobBean
{

    private CronTaskConfiguration configuration = new CronTaskConfiguration();


    public CronTaskConfiguration getConfiguration()
    {
        return configuration;
    }

    public void setConfiguration(CronTaskConfiguration configuration)
    {
        this.configuration = configuration;
    }

}

package org.carlspring.strongbox.crontask.quartz;

import org.quartz.JobDetail;
import org.quartz.Trigger;

/**
 * @author Yougeshwar
 */
public class CronTaskStruct
{
    private JobDetail jobDetail;
    private Trigger trigger;

    public JobDetail getJobDetail()
    {
        return jobDetail;
    }

    public void setJobDetail(JobDetail jobDetail)
    {
        this.jobDetail = jobDetail;
    }

    public Trigger getTrigger()
    {
        return trigger;
    }

    public void setTrigger(Trigger trigger)
    {
        this.trigger = trigger;
    }
}

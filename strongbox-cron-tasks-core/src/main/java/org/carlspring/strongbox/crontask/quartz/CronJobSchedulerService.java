package org.carlspring.strongbox.crontask.quartz;

import org.carlspring.strongbox.crontask.configuration.CronTaskConfiguration;
import org.quartz.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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

    public void scheduleJob(CronTaskConfiguration cronTaskConfiguration)
            throws ClassNotFoundException
    {
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass((Class<? extends Job>) Class.forName(cronTaskConfiguration.getClassName()));
        Map<String, Object> map = new HashMap<>();
        map.put("name", "JOB");
        map.put("count", 1);
        jobDetailFactoryBean.setJobDataAsMap(map);
        jobDetailFactoryBean.setGroup(cronTaskConfiguration.getName());
        jobDetailFactoryBean.setName(cronTaskConfiguration.getName());

        CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
        cronTriggerFactoryBean.setJobDetail(jobDetailFactoryBean.getObject());
        cronTriggerFactoryBean.setStartDelay(3000);
        cronTriggerFactoryBean.setCronExpression(cronTaskConfiguration.getCronExpression());
        cronTriggerFactoryBean.setGroup(cronTaskConfiguration.getName());
        cronTriggerFactoryBean.setName(cronTaskConfiguration.getName());

        schedulerFactoryBean.setTriggers(cronTriggerFactoryBean.getObject());
    }
}

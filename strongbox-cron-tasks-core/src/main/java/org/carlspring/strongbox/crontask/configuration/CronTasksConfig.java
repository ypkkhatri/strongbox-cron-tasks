package org.carlspring.strongbox.crontask.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
@ComponentScan({ "org.carlspring.strongbox.crontask" })
public class CronTasksConfig
{

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean()
    {
        return new SchedulerFactoryBean();
    }
}

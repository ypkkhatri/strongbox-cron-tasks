package org.carlspring.strongbox.crontask.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.orient.commons.repository.config.EnableOrientRepositories;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableOrientRepositories(basePackages = "org.carlspring.strongbox.crontask.repository")
@ComponentScan({ "org.carlspring.strongbox.crontask" })
public class CronTasksConfig
{

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean()
    {
        return new SchedulerFactoryBean();
    }

}

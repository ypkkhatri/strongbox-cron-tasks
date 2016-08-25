package org.carlspring.strongbox.config;

import org.carlspring.strongbox.crontask.domain.CronTaskConfiguration;

import javax.annotation.PostConstruct;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.orient.commons.repository.config.EnableOrientRepositories;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@ComponentScan({ "org.carlspring.strongbox.config",
                 "org.carlspring.strongbox.crontask",
                 "org.carlspring.strongbox.services"
               })
@EnableOrientRepositories(basePackages = "org.carlspring.strongbox.crontask.repository")
@Import({ DataServiceConfig.class
        })
public class CronTasksConfig
{

    @Autowired
    private OObjectDatabaseTx databaseTx;


    @Bean
    public SchedulerFactoryBean schedulerFactoryBean()
    {
        return new SchedulerFactoryBean();
    }

    private synchronized OObjectDatabaseTx getDatabaseTx()
    {
        databaseTx.activateOnCurrentThread();
        return databaseTx;
    }

    @PostConstruct
    @Transactional
    public synchronized void init()
    {
        // register all domain entities
        getDatabaseTx().getEntityManager().registerEntityClasses(CronTaskConfiguration.class.getPackage().getName());
    }

}

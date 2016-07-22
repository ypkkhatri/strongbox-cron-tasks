package org.carlspring.strongbox.crontask.configuration;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.carlspring.strongbox.CommonConfig;
import org.carlspring.strongbox.config.DataServiceConfig;
import org.carlspring.strongbox.crontask.domain.CronTaskConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.orient.commons.repository.config.EnableOrientRepositories;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Configuration
@ComponentScan({ "org.carlspring.strongbox.crontask" })
@EnableOrientRepositories(basePackages = "org.carlspring.strongbox.crontask.repository")
@Import({ DataServiceConfig.class,
          CommonConfig.class })
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

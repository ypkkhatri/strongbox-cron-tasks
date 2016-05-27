package org.carlspring.strongbox.crontask;

import org.carlspring.strongbox.crontask.configuration.CronTaskConfiguration;
import org.carlspring.strongbox.crontask.configuration.CronTasksConfig;
import org.carlspring.strongbox.crontask.services.CronTaskConfigurationService;

import org.carlspring.strongbox.crontask.test.MyTask;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CronTasksConfig.class })
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class })
@FixMethodOrder(MethodSorters.JVM)
public class CronTaskConfigurationServiceTest
{

    @Autowired
    private CronTaskConfigurationService cronTaskConfigurationService;

    @Test
    public void testCronTaskConfiguration()
    {
        addConfig();
//        updateConfig();
//        deleteConfig();
    }

    public void addConfig()
    {
        String name = "Cron-Task-1";
        CronTaskConfiguration cronTaskConfiguration = new CronTaskConfiguration();
        cronTaskConfiguration.setName(name);
        cronTaskConfiguration.setClassName(MyTask.class.getName());
        cronTaskConfiguration.setCronExpression("0 0/1 * 1/1 * ? *");

        cronTaskConfigurationService.saveConfiguration(cronTaskConfiguration);

        CronTaskConfiguration obj = cronTaskConfigurationService.getConfiguration(name);
        assertNotNull(obj);
    }

    public void updateConfig() // Update
    {
        String name = "Cron-Task-1";
        CronTaskConfiguration cronTaskConfiguration = cronTaskConfigurationService.getConfiguration(name);

        assertNotNull(cronTaskConfiguration);

        cronTaskConfiguration.setClassName("org.springframework.scheduling.quartz.JobDetailBeanNew");
        cronTaskConfiguration.setCronExpression("0 0 0/1 1/1 2 0 1");

        cronTaskConfigurationService.saveConfiguration(cronTaskConfiguration);
    }

    public void deleteConfig()
    {
        String name = "Cron-Task-1";
        CronTaskConfiguration cronTaskConfiguration = cronTaskConfigurationService.getConfiguration(name);

        assertNotNull(cronTaskConfiguration);

        cronTaskConfigurationService.deleteConfiguration(cronTaskConfiguration);

        cronTaskConfiguration = cronTaskConfigurationService.getConfiguration(name);
        assertNull(cronTaskConfiguration);
    }

}

package org.carlspring.strongbox.crontask.test;

import org.carlspring.strongbox.crontask.configuration.CronTaskConfiguration;
import org.carlspring.strongbox.crontask.configuration.CronTasksConfig;
import org.carlspring.strongbox.crontask.services.CronTaskConfigurationService;
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
    public void addConfig()
    {
        String name = "Cron-Task-1";
        CronTaskConfiguration cronTaskConfiguration = new CronTaskConfiguration();
        cronTaskConfiguration.setName(name);
        cronTaskConfiguration.addProperty("JobDetailBean", "org.springframework.scheduling.quartz.JobDetailBean");
        cronTaskConfiguration.addProperty("CronTriggerBean", "org.springframework.scheduling.quartz.CronTriggerBean");
        cronTaskConfiguration.addProperty("cronExpression", "0 0 0/1 1/1 * ? *");

        cronTaskConfigurationService.saveConfiguration(cronTaskConfiguration);

        CronTaskConfiguration obj = cronTaskConfigurationService.getConfiguration(name);
        assertNotNull(obj);
    }

    @Test
    public void updateConfig()
    {
        String name = "Cron-Task-1";
        CronTaskConfiguration cronTaskConfiguration = cronTaskConfigurationService.getConfiguration(name);

        assertNotNull(cronTaskConfiguration);

        cronTaskConfiguration.addProperty("JobDetailBean", "org.springframework.scheduling.quartz.JobDetailBeanNew");
        cronTaskConfiguration.addProperty("CronTriggerBean",
                                          "org.springframework.scheduling.quartz.CronTriggerBeanNew");
        cronTaskConfiguration.addProperty("cronExpression", "0 0 0/1 1/1 2 0 1");

        cronTaskConfigurationService.saveConfiguration(cronTaskConfiguration);
    }

    @Ignore
    @Test
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

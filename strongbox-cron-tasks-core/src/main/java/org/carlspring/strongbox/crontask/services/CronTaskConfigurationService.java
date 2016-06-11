package org.carlspring.strongbox.crontask.services;

import org.carlspring.strongbox.crontask.api.jobs.AbstractCronJob;
import org.carlspring.strongbox.crontask.configuration.CronTaskConfiguration;
import org.carlspring.strongbox.crontask.configuration.CronTaskConfigurationRepository;
import org.carlspring.strongbox.crontask.exceptions.CronTaskException;
import org.carlspring.strongbox.crontask.exceptions.CronTaskNotFoundException;
import org.carlspring.strongbox.crontask.quartz.CronJobSchedulerService;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CronTaskConfigurationService
{

    private final Logger logger = LoggerFactory.getLogger(CronTaskConfigurationService.class);

    @Autowired
    private CronTaskConfigurationRepository cronTaskConfigurationRepository;

    @Autowired
    private CronJobSchedulerService cronJobSchedulerService;

    public void saveConfiguration(CronTaskConfiguration cronTaskConfiguration)
            throws ClassNotFoundException,
                   SchedulerException,
                   CronTaskException,
                   IllegalAccessException,
                   InstantiationException
    {
        logger.debug("CronTaskConfigurationService.saveConfiguration()");

        if(!cronTaskConfiguration.contain("cronExpression"))
        {
            throw new CronTaskException("cronExpression property does not exists");
        }

        cronTaskConfigurationRepository.saveConfiguration(cronTaskConfiguration);

        if (cronTaskConfiguration.contain("jobClass"))
        {
            Class c = Class.forName(cronTaskConfiguration.getProperty("jobClass"));
            Object classInstance = c.newInstance();

            logger.debug("> " + c.getSuperclass().getCanonicalName());

            if (!(classInstance instanceof AbstractCronJob))
            {
                throw new CronTaskException(c + " does not extend " + AbstractCronJob.class);
            }

            cronJobSchedulerService.scheduleJob(cronTaskConfiguration);
        }
    }

    public void deleteConfiguration(CronTaskConfiguration cronTaskConfiguration)
            throws SchedulerException,
                   CronTaskNotFoundException,
                   ClassNotFoundException
    {
        logger.debug("CronTaskConfigurationService.deleteConfiguration()");

        cronTaskConfigurationRepository.deleteConfiguration(cronTaskConfiguration);
        cronJobSchedulerService.deleteJob(cronTaskConfiguration);
    }

    public CronTaskConfiguration getConfiguration(String name)
    {
        logger.debug("CronTaskConfigurationService.getConfiguration()");

        return cronTaskConfigurationRepository.getConfiguration(name);
    }

    public List<CronTaskConfiguration> getConfigurations()
    {
        logger.debug("CronTaskConfigurationService.getConfigurations()");

        return cronTaskConfigurationRepository.getConfigurations();
    }

    public List<String> getGroovyScriptsName()
    {
        logger.debug("CronTaskConfigurationService.getGroovyScriptsName");

        return cronJobSchedulerService.getGroovyScriptsName();
    }

}

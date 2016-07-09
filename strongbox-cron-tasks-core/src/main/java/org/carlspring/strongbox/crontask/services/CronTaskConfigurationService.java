package org.carlspring.strongbox.crontask.services;

import org.carlspring.strongbox.crontask.api.jobs.AbstractCronJob;
import org.carlspring.strongbox.crontask.domain.CronTaskConfiguration;
import org.carlspring.strongbox.crontask.exceptions.CronTaskException;
import org.carlspring.strongbox.crontask.exceptions.CronTaskNotFoundException;
import org.carlspring.strongbox.crontask.quartz.CronJobSchedulerService;
import org.carlspring.strongbox.crontask.quartz.GroovyScriptNames;

import org.apache.commons.collections.IteratorUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CronTaskConfigurationService
{

    private final Logger logger = LoggerFactory.getLogger(CronTaskConfigurationService.class);

    @Autowired
    private CronTaskDataService cronTaskDataService;

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

        cronTaskDataService.save(cronTaskConfiguration);

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

        cronTaskDataService.delete(cronTaskConfiguration);
        cronJobSchedulerService.deleteJob(cronTaskConfiguration);
    }

    public CronTaskConfiguration getConfiguration(String name)
    {
        logger.debug("CronTaskConfigurationService.getConfiguration()");

        Optional<CronTaskConfiguration> optional = cronTaskDataService.findByName(name);
        return optional.isPresent() ? optional.get() : null;
    }

    public List<CronTaskConfiguration> getConfigurations()
    {
        logger.debug("CronTaskConfigurationService.getConfigurations()");

        Optional<Iterable<CronTaskConfiguration>> optional = cronTaskDataService.findAll();

        return (List<CronTaskConfiguration>) (optional.isPresent() ? optional.get() : IteratorUtils.toList(optional.get().iterator()));
    }

    public GroovyScriptNames getGroovyScriptsName()
    {
        logger.debug("CronTaskConfigurationService.getGroovyScriptsName");

        return cronJobSchedulerService.getGroovyScriptsName();
    }

}

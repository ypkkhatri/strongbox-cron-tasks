package org.carlspring.strongbox.crontask.services;

import org.carlspring.strongbox.crontask.exceptions.CronTaskException;
import org.carlspring.strongbox.crontask.exceptions.CronTaskNotFoundException;
import org.carlspring.strongbox.crontask.configuration.CronTaskConfiguration;
import org.carlspring.strongbox.crontask.configuration.CronTaskConfigurationRepository;
import org.carlspring.strongbox.crontask.quartz.CronJobSchedulerService;

import org.quartz.Job;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CronTaskConfigurationService
{

    private final Logger logger =
            LoggerFactory.getLogger(CronTaskConfigurationService.class);

    @Autowired
    private CronTaskConfigurationRepository cronTaskConfigurationRepository;

    @Autowired
    private CronJobSchedulerService cronJobSchedulerService;

    public void saveConfiguration(CronTaskConfiguration cronTaskConfiguration)
            throws ClassNotFoundException, SchedulerException, CronTaskException
    {
        logger.info("CronTaskConfigurationService.saveConfiguration()");

        try
        {
            Class jobClass = (Class<? extends Job>) Class.forName(cronTaskConfiguration.getJobClass());
        }
        catch (Exception ex)
        {
            throw new CronTaskException("Job class not implemented by org.quartz.Job interface");
        }
        cronTaskConfigurationRepository.saveConfiguration(cronTaskConfiguration);
        cronJobSchedulerService.scheduleJob(cronTaskConfiguration);
    }

    public void deleteConfiguration(CronTaskConfiguration cronTaskConfiguration)
            throws SchedulerException, CronTaskNotFoundException, ClassNotFoundException
    {
        logger.info("CronTaskConfigurationService.deleteConfiguration()");

        cronTaskConfigurationRepository.deleteConfiguration(cronTaskConfiguration);
        cronJobSchedulerService.deleteJob(cronTaskConfiguration);
    }

    public CronTaskConfiguration getConfiguration(String name)
    {
        logger.info("CronTaskConfigurationService.getConfiguration()");

        return cronTaskConfigurationRepository.getConfiguration(name);
    }

    public List<CronTaskConfiguration> getConfigurations()
    {
        logger.info("CronTaskConfigurationService.getConfigurations()");

        return cronTaskConfigurationRepository.getConfigurations();
    }


}

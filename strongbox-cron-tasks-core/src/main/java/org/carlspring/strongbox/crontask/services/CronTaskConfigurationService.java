package org.carlspring.strongbox.crontask.services;

import org.carlspring.strongbox.crontask.configuration.CronTaskConfiguration;
import org.carlspring.strongbox.crontask.configuration.CronTaskConfigurationRepository;
import org.carlspring.strongbox.crontask.quartz.CronJobSchedulerService;
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
    {
        logger.info("CronTaskConfigurationService.saveConfiguration()");

        cronTaskConfigurationRepository.updateConfiguration(cronTaskConfiguration);
        try
        {
            cronJobSchedulerService.scheduleJob(cronTaskConfiguration);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public void deleteConfiguration(CronTaskConfiguration cronTaskConfiguration)
    {
        logger.info("CronTaskConfigurationService.deleteConfiguration()");

        cronTaskConfigurationRepository.deleteConfiguration(cronTaskConfiguration);
    }

    public void deleteConfiguration(Object id)
    {
        logger.info("CronTaskConfigurationService.deleteConfiguration()");

        cronTaskConfigurationRepository.deleteConfiguration(id);
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

package org.carlspring.strongbox.crontask.services;

import org.carlspring.strongbox.crontask.configuration.Configuration;
import org.carlspring.strongbox.crontask.configuration.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CronTaskConfigurationService
{

    @Autowired
    private ConfigurationRepository configurationRepository;

    public void saveConfiguration(Configuration configuration)
    {
        configurationRepository.updateConfiguration(configuration);
    }

    public Configuration getConfiguration()
    {
        return configurationRepository.getConfiguration();
    }
}

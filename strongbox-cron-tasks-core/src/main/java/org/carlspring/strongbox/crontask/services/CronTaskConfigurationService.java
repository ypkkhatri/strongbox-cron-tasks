package org.carlspring.strongbox.crontask.services;

import org.carlspring.strongbox.crontask.domain.CronTaskConfiguration;
import org.carlspring.strongbox.crontask.exceptions.CronTaskException;
import org.carlspring.strongbox.crontask.exceptions.CronTaskNotFoundException;
import org.carlspring.strongbox.crontask.quartz.GroovyScriptNames;

import java.util.List;

import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

@Service
public interface CronTaskConfigurationService
{


    void saveConfiguration(CronTaskConfiguration cronTaskConfiguration)
            throws ClassNotFoundException,
                   SchedulerException,
                   CronTaskException,
                   IllegalAccessException,
                   InstantiationException;

    void deleteConfiguration(CronTaskConfiguration cronTaskConfiguration)
            throws SchedulerException,
                   CronTaskNotFoundException,
                   ClassNotFoundException;

    CronTaskConfiguration getConfiguration(String name);

    List<CronTaskConfiguration> getConfigurations();

    GroovyScriptNames getGroovyScriptsName();

}

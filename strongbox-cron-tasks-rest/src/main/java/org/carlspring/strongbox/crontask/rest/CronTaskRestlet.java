package org.carlspring.strongbox.crontask.rest;

import org.carlspring.strongbox.crontask.services.CronTaskConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Yougeshwar
 * */

public class CronTaskRestlet
{
    @Autowired
    private CronTaskConfigurationService cronTaskConfigurationService;
}

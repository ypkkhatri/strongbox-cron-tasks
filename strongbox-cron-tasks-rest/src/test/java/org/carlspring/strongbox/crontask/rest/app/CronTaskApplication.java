package org.carlspring.strongbox.crontask.rest.app;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CronTaskApplication
        extends ResourceConfig
{

    private static final Logger logger = LoggerFactory.getLogger(CronTaskApplication.class);


    public CronTaskApplication()
    {
        if (logger.isDebugEnabled())
        {
            register(JacksonFeature.class);
        }
    }

}

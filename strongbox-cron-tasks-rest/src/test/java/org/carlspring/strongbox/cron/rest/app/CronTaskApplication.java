package org.carlspring.strongbox.cron.rest.app;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import java.util.Set;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

/**
 * @author Yougeshwar
 */
@ApplicationPath("webresources")
public class CronTaskApplication
        extends Application
{

    public CronTaskApplication()
    {
    }

    @Override
    public Set<Class<?>> getClasses()
    {
        Set<Class<?>> resources = new java.util.HashSet<>();
        resources.add(MultiPartFeature.class);

        return resources;
    }

}

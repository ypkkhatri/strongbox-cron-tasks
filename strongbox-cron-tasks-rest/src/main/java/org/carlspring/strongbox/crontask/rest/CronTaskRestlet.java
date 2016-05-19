package org.carlspring.strongbox.crontask.rest;

import org.carlspring.strongbox.crontask.configuration.CronTaskConfiguration;
import org.carlspring.strongbox.crontask.services.CronTaskConfigurationService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Yougeshwar
 */

@Component
public class CronTaskRestlet
{

    private static final Logger logger = LoggerFactory.getLogger(CronTaskRestlet.class);

    @Autowired
    private CronTaskConfigurationService cronTaskConfigurationService;

    @PUT
    @Path("/crontask")
    @Produces(MediaType.TEXT_PLAIN)
    public Response saveConfiguration(@QueryParam("name") String name,
                                      @QueryParam("key") String key,
                                      @QueryParam("value") String value)
    {
        CronTaskConfiguration config = cronTaskConfigurationService.getConfiguration(name);
        if (config == null)
        {
            config = new CronTaskConfiguration();
        }

        config.setName(name);
        config.addProperty(key, value);

        cronTaskConfigurationService.saveConfiguration(config);

        return Response.ok("The cron task configuration was saved successfully.").build();
    }

    @DELETE
    @Path("/crontask")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteConfiguration(@QueryParam("name") String name)
    {
        CronTaskConfiguration config = cronTaskConfigurationService.getConfiguration(name);
        if (config == null)
        {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Configuration not found by this name!")
                           .build();
        }

        cronTaskConfigurationService.deleteConfiguration(config);

        return Response.ok("The cron task configuration was deleted successfully.").build();
    }

    @GET
    @Path("/crontask")
    @Produces(MediaType.APPLICATION_XML)
    public Response getConfiguration(@QueryParam("name") String name)
    {
        CronTaskConfiguration config = cronTaskConfigurationService.getConfiguration(name);
        if (config == null)
        {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Configuration not found by this name!")
                           .build();
        }

        return Response.ok(config).build();
    }


    @GET
    @Path("/crontask/all")
    @Produces(MediaType.APPLICATION_XML)
    public Response getConfigurations()
    {
        List<CronTaskConfiguration> configList = cronTaskConfigurationService.getConfigurations();
        if (configList == null || configList.isEmpty())
        {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("There are no configurations")
                           .build();
        }

        return Response.ok(configList).build();
    }


}

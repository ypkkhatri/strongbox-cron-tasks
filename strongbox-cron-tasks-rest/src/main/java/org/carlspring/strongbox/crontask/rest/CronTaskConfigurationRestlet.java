package org.carlspring.strongbox.crontask.rest;

import org.carlspring.strongbox.crontask.exceptions.CronTaskException;
import org.carlspring.strongbox.crontask.exceptions.CronTaskNotFoundException;
import org.carlspring.strongbox.crontask.configuration.CronTaskConfiguration;
import org.carlspring.strongbox.crontask.services.CronTaskConfigurationService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Yougeshwar
 */

@Component
@Path("/configuration/crontasks")
public class CronTaskConfigurationRestlet
{

    private static final Logger logger = LoggerFactory.getLogger(CronTaskConfigurationRestlet.class);

    @Autowired
    private CronTaskConfigurationService cronTaskConfigurationService;

    @PUT
    @Path("/crontask")
    public Response saveConfiguration(@QueryParam("name") String name,
                                      @QueryParam("jobClass") String jobClass,
                                      @QueryParam("cronExpression") String cronExpression)
    {
        logger.info("Save Cron Task config call");
        CronTaskConfiguration config = cronTaskConfigurationService.getConfiguration(name);
        if (config == null)
        {
            config = new CronTaskConfiguration();
        }

        config.setName(name);
        config.setJobClass(jobClass);
        config.setCronExpression("0 0/1 * 1/1 * ? *");

        try
        {
            cronTaskConfigurationService.saveConfiguration(config);
        }
        catch (ClassNotFoundException | SchedulerException | CronTaskException ex)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }

        return Response.ok().build();
    }

    @DELETE
    @Path("/crontask")
    public Response deleteConfiguration(@QueryParam("name") String name)
    {
        CronTaskConfiguration config = cronTaskConfigurationService.getConfiguration(name);
        if (config == null)
        {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Configuration not found by this name!")
                           .build();
        }

        try
        {
            cronTaskConfigurationService.deleteConfiguration(config);
        }
        catch (ClassNotFoundException | SchedulerException | CronTaskNotFoundException ex)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }

        return Response.ok().build();
    }

    @GET
    @Path("/crontask")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfiguration(@QueryParam("name") String name)
    {
        CronTaskConfiguration config = cronTaskConfigurationService.getConfiguration(name);
        if (config == null)
        {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Cron task config not found by this name!")
                           .build();
        }

        return Response.ok(config).build();
    }

    @GET
    @Path("/crontasks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfigurations()
    {
        List<CronTaskConfiguration> configList = cronTaskConfigurationService.getConfigurations();
        if (configList == null || configList.isEmpty())
        {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("There are no cron task configs")
                           .build();
        }

        return Response.ok(configList).build();
    }

}

package org.carlspring.strongbox.crontask.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.carlspring.strongbox.crontask.exceptions.CronTaskException;
import org.carlspring.strongbox.crontask.exceptions.CronTaskNotFoundException;
import org.carlspring.strongbox.crontask.configuration.CronTaskConfiguration;
import org.carlspring.strongbox.crontask.services.CronTaskConfigurationService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

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
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response saveConfiguration(CronTaskConfiguration cronTaskConfiguration)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            logger.info(objectMapper.writeValueAsString(cronTaskConfiguration));
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        logger.info("Save Cron Task config call");
        try
        {
            cronTaskConfigurationService.saveConfiguration(cronTaskConfiguration);
        }
        catch (ClassNotFoundException | SchedulerException | CronTaskException | InstantiationException | IllegalAccessException e)
        {
            logger.trace(e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
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
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
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

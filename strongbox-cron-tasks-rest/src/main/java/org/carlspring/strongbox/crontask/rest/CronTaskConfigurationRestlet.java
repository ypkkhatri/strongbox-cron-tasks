package org.carlspring.strongbox.crontask.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.carlspring.strongbox.crontask.api.jobs.GroovyCronJob;
import org.carlspring.strongbox.crontask.exceptions.CronTaskException;
import org.carlspring.strongbox.crontask.exceptions.CronTaskNotFoundException;
import org.carlspring.strongbox.crontask.configuration.CronTaskConfiguration;
import org.carlspring.strongbox.crontask.services.CronTaskConfigurationService;
import org.carlspring.strongbox.crontask.utils.FileUtils;
import org.carlspring.strongbox.resource.ConfigurationResourceResolver;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.InputStream;
import java.util.List;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
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
            logger.debug(objectMapper.writeValueAsString(cronTaskConfiguration));
            logger.debug("Save Cron Task config call");

            cronTaskConfigurationService.saveConfiguration(cronTaskConfiguration);

            return Response.ok().build();
        }
        catch (ClassNotFoundException | SchedulerException | CronTaskException |
               InstantiationException | IllegalAccessException | JsonProcessingException e)
        {
            logger.trace(e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
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

    @POST
    @Path("/crontask/{name}/upload/groovy")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadGroovyScript(@PathParam("name") String name,
                           @FormDataParam("file") InputStream inputStream,
                           @FormDataParam("file") FormDataContentDisposition formDataContentDisposition)
    {
        String fileName = formDataContentDisposition.getFileName();
        if (!fileName.endsWith(".groovy"))
        {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Uploaded file must be groovy")
                           .build();
        }

        CronTaskConfiguration cronTaskConfiguration = cronTaskConfigurationService.getConfiguration(name);
        if (cronTaskConfiguration == null)
        {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Configuration not found by this name!")
                           .build();
        }

        String path = ConfigurationResourceResolver.getVaultDirectory() + "/etc/conf/cron/groovy";
        cronTaskConfiguration.addProperty("fileName", fileName);
        cronTaskConfiguration.addProperty("jobClass", GroovyCronJob.class.getName());
        cronTaskConfiguration.addProperty("script.path", path + "/" + fileName);

        logger.info("Upload script");
        try
        {
            FileUtils.writeToFile(inputStream, path);
            cronTaskConfigurationService.saveConfiguration(cronTaskConfiguration);
        }
        catch (ClassNotFoundException | SchedulerException | CronTaskException | InstantiationException | IllegalAccessException e)
        {
            logger.trace(e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

        return Response.ok().build();
    }

}

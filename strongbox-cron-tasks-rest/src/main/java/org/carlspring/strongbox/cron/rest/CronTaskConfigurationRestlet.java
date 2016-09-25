package org.carlspring.strongbox.cron.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.carlspring.strongbox.cron.api.jobs.GroovyCronJob;
import org.carlspring.strongbox.cron.domain.CronTaskConfiguration;
import org.carlspring.strongbox.cron.exceptions.CronTaskException;
import org.carlspring.strongbox.cron.exceptions.CronTaskNotFoundException;
import org.carlspring.strongbox.cron.quartz.GroovyScriptNames;
import org.carlspring.strongbox.cron.services.CronTaskConfigurationService;
import org.carlspring.strongbox.resource.ConfigurationResourceResolver;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@Api(value = "/configuration/crontasks")
public class CronTaskConfigurationRestlet
{

    private static final Logger logger = LoggerFactory.getLogger(CronTaskConfigurationRestlet.class);

    @Autowired
    private CronTaskConfigurationService cronTaskConfigurationService;

    @PUT
    @Path("/cron")
    @Consumes({ MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_XML })
    @ApiOperation(value = "Used to save the configuration", position = 0)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "The configuration was saved successfully."),
                            @ApiResponse(code = 400, message = "An error occurred.") })
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
    @Path("/cron")
    @ApiOperation(value = "Used to delete the configuration", position = 1)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "The configuration was deleted successfully."),
                            @ApiResponse(code = 400, message = "An error occurred.") })
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
            if (config.contain("jobClass"))
            {
                Class c = Class.forName(config.getProperty("jobClass"));
                Object classInstance = c.newInstance();

                logger.debug("> " + c.getSuperclass().getCanonicalName());

                if (classInstance instanceof GroovyCronJob)
                {
                    File file = new File(config.getProperty("script.path"));
                    if (file.exists())
                    {
                        //noinspection ResultOfMethodCallIgnored
                        file.delete();
                    }
                }
            }
        }
        catch (ClassNotFoundException | SchedulerException | CronTaskNotFoundException |
                       InstantiationException | IllegalAccessException ex)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }

        return Response.ok().build();
    }

    @GET
    @Path("/cron")
    @Produces({ MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_XML })
    @ApiOperation(value = "Used to get the configuration on given cron task name", position = 2)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "The configuration retrieved successfully."),
                            @ApiResponse(code = 400, message = "An error occurred.") })
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
    @Path("/")
    @Produces({ MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_XML })
    @ApiOperation(value = "Used to get list of all the configurations", position = 3)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "The all configurations retrieved successfully."),
                            @ApiResponse(code = 400, message = "An error occurred.") })
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

    @PUT
    @Path("/cron/groovy")
    @ApiOperation(value = "Used to upload groovy script for groovy cron task", position = 4)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "The groovy script uploaded successfully."),
                            @ApiResponse(code = 400, message = "An error occurred.") })
    public Response uploadGroovyScript(@QueryParam("cronName") String cronName,
                                       @Context HttpHeaders headers,
                                       @Context HttpServletRequest request,
                                       InputStream is)
    {
        String fileName = headers.getRequestHeader("fileName").get(0);
        if (!fileName.endsWith(".groovy"))
        {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Uploaded file must be groovy")
                           .build();
        }

        CronTaskConfiguration cronTaskConfiguration = cronTaskConfigurationService.getConfiguration(cronName);
        if (cronTaskConfiguration == null)
        {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Configuration not found by this name!")
                           .build();
        }
        logger.info(">> CRON NAME: " + cronTaskConfiguration.getName());
        logger.info(">> Properties: " + cronTaskConfiguration.getProperties());
        String path = ConfigurationResourceResolver.getVaultDirectory() + "/etc/conf/cron/groovy";

        cronTaskConfiguration.addProperty("fileName", fileName);
        cronTaskConfiguration.addProperty("jobClass", GroovyCronJob.class.getName());
        cronTaskConfiguration.addProperty("script.path", path + "/" + fileName);

        try
        {
            storeGroovyCronTask(is, path, fileName);
            cronTaskConfigurationService.saveConfiguration(cronTaskConfiguration);
        }
        catch (ClassNotFoundException | SchedulerException | CronTaskException | InstantiationException | IllegalAccessException e)
        {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

        return Response.ok().build();
    }

    @GET
    @Path("/groovy/names")
    @Produces({ MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_XML })
    @ApiOperation(value = "Used to get all groovy scripts names", position = 5)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "The groovy scripts named retrieved successfully."),
                            @ApiResponse(code = 400, message = "An error occurred.") })
    public Response getGroovyScriptsName()
    {
        GroovyScriptNames groovyScriptNames = cronTaskConfigurationService.getGroovyScriptsName();

        return Response.ok(groovyScriptNames).build();
    }

    private void storeGroovyCronTask(InputStream is,
                                     String dirPath,
                                     String fileName)
            throws CronTaskException
    {
        File dir = new File(dirPath);

        if (!dir.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }

        File file = new File(dirPath + "/" + fileName);

        try (OutputStream out = new FileOutputStream(file))
        {
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = is.read(bytes)) != -1)
            {
                out.write(bytes, 0, read);
            }
            out.flush();
        }
        catch (IOException e)
        {
            throw new CronTaskException(e);
        }
    }

}

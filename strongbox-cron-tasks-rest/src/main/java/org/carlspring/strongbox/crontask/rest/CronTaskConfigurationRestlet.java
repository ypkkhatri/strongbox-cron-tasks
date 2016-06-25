package org.carlspring.strongbox.crontask.rest;

import org.carlspring.strongbox.crontask.api.jobs.GroovyCronJob;
import org.carlspring.strongbox.crontask.domain.CronTaskConfiguration;
import org.carlspring.strongbox.crontask.exceptions.CronTaskException;
import org.carlspring.strongbox.crontask.exceptions.CronTaskNotFoundException;
import org.carlspring.strongbox.crontask.quartz.GroovyScriptNames;
import org.carlspring.strongbox.crontask.services.CronTaskConfigurationService;
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
public class CronTaskConfigurationRestlet
{

    private static final Logger logger = LoggerFactory.getLogger(CronTaskConfigurationRestlet.class);

    @Autowired
    private CronTaskConfigurationService cronTaskConfigurationService;

    @PUT
    @Path("/crontask")
    @Consumes({ MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_XML })
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
    @Path("/crontask")
    @Produces({ MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_XML })
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
    @Path("/crontask/groovy")
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

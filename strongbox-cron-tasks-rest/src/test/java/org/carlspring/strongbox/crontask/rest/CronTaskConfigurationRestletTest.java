package org.carlspring.strongbox.crontask.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.carlspring.strongbox.crontask.configuration.CronTaskConfiguration;
import org.carlspring.strongbox.crontask.configuration.CronTasksConfig;
import org.carlspring.strongbox.crontask.exceptions.CronTaskNotFoundException;
import org.carlspring.strongbox.crontask.test.MyTask;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CronTasksConfig.class })
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class })
public class CronTaskConfigurationRestletTest
{

    private static final Logger logger = LoggerFactory.getLogger(CronTaskConfigurationRestlet.class);

    protected TestClient client;

    private final String cronName = "CRJ001";

    @Test
    public void testCronTaskConfiguration()
            throws ClassNotFoundException, SchedulerException, CronTaskNotFoundException, UnsupportedEncodingException
    {
        client = TestClient.getTestInstance();
        saveJavaConfig("0 0/1 * 1/1 * ? *");
//        saveConfig("0 0 12 1/1 * ? *");
        deleteConfig();
    }

    @After
    public void tearDown()
            throws Exception
    {
        if (client != null)
        {
            client.close();
        }
    }

    public void saveJavaConfig(String cronExpression)
            throws UnsupportedEncodingException
    {
        logger.info("Cron Expression: " + cronExpression);

        String path = client.getContextBaseUrl() +
                      "/configuration/crontasks/crontask";

        CronTaskConfiguration cronTaskConfiguration = new CronTaskConfiguration();
        cronTaskConfiguration.setName(cronName);
        cronTaskConfiguration.addProperty("cronExpression", cronExpression);
        cronTaskConfiguration.addProperty("jobClass", MyTask.class.getName());

//        String payload = toJSON(cronTaskConfiguration);
//        logger.info(payload);
        WebTarget resource = client.getClientInstance().target(path);

        Response response = resource.request(MediaType.APPLICATION_JSON)
                                    .put(Entity.entity(cronTaskConfiguration, MediaType.APPLICATION_JSON));

        int status = response.getStatus();
        if (Response.ok().build().getStatus() != status)
        {
            logger.error(response.readEntity(String.class));
        }
        assertEquals("Failed to schedule job!", Response.ok().build().getStatus(), status);

//        String url = client.getContextBaseUrl() +
//                     "/configuration/crontasks/crontask?" +
//                     "name=" + cronName;
//        resource = client.getClientInstance().target(url);
//
//        response = resource.request(MediaType.APPLICATION_JSON).get();
//
//        status = response.getStatus();
//
//        assertEquals("Failed to get cron task config!", Response.ok().build().getStatus(), status);
    }

    public void deleteConfig()
    {
        String path = "/configuration/crontasks/crontask?" +
                      "name=" + cronName;

        Response response = client.delete(path);
        assertEquals("Failed to delete job!", Response.ok().build().getStatus(), response.getStatus());

        String url = client.getContextBaseUrl() +
                     "/configuration/crontasks/crontask?" +
                     "name=" + cronName;
        WebTarget resource = client.getClientInstance().target(url);

        response = resource.request(MediaType.APPLICATION_JSON).get();

        int status = response.getStatus();

        assertEquals("Cron task config exists!", Response.status(Response.Status.BAD_REQUEST).build().getStatus(),
                     status);
    }

    private String toJSON(Object object)
    {

        ObjectMapper mapper = new ObjectMapper();
        try
        {
            return mapper.writeValueAsString(object);
        }
        catch (JsonProcessingException e)
        {
            logger.error("JSON Error:", e);
        }
        return "{}";
    }
}

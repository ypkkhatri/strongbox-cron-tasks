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
import javax.xml.bind.JAXBException;
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
            throws ClassNotFoundException,
                   SchedulerException,
                   CronTaskNotFoundException,
                   UnsupportedEncodingException,
                   JAXBException
    {
        client = TestClient.getTestInstance();
        saveJavaConfig("0 0/1 * 1/1 * ? *");
        saveJavaConfig("0 0/2 * 1/1 * ? *");
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
            throws UnsupportedEncodingException, JAXBException
    {
        logger.debug("Cron Expression: " + cronExpression);

        String url = client.getContextBaseUrl() + "/configuration/crontasks/crontask";

        CronTaskConfiguration configuration = new CronTaskConfiguration();
        configuration.setName(cronName);
        configuration.addProperty("cronExpression", cronExpression);
        configuration.addProperty("jobClass", MyTask.class.getName());

        WebTarget resource = client.getClientInstance().target(url);

        Response response = resource.request(MediaType.APPLICATION_JSON)
                                    .put(Entity.entity(configuration, MediaType.APPLICATION_JSON));

        int status = response.getStatus();
        if (Response.ok().build().getStatus() != status)
        {
            logger.error(response.readEntity(String.class));
        }

        assertEquals("Failed to schedule job!", Response.ok().build().getStatus(), status);

        /**
         * Retrieve saved configuration
         * */
        url = client.getContextBaseUrl() +
              "/configuration/crontasks/crontask?" +
              "name=" + cronName;
        resource = client.getClientInstance().target(url);

        response = resource.request(MediaType.APPLICATION_JSON).get();

        status = response.getStatus();

        assertEquals("Failed to get cron task config!", Response.ok().build().getStatus(), status);
    }

    public void deleteConfig()
    {
        String path = "/configuration/crontasks/crontask?" +
                      "name=" + cronName;

        Response response = client.delete(path);
        assertEquals("Failed to delete job!", Response.ok().build().getStatus(), response.getStatus());

        /**
         * Retrieve deleted configuration
         * */
        String url = client.getContextBaseUrl() +
                     "/configuration/crontasks/crontask?" +
                     "name=" + cronName;
        WebTarget resource = client.getClientInstance().target(url);

        response = resource.request(MediaType.APPLICATION_JSON).get();

        int status = response.getStatus();

        assertEquals("Cron task config exists!", Response.status(Response.Status.BAD_REQUEST).build().getStatus(),
                     status);
    }

}

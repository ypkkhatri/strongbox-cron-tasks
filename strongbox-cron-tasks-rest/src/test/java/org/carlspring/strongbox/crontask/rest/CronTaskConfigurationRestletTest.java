package org.carlspring.strongbox.crontask.rest;

import org.carlspring.strongbox.crontask.CronTaskNotFoundException;
import org.carlspring.strongbox.crontask.configuration.CronTasksConfig;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
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
        saveConfig("0 0/1 * 1/1 * ? *");
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

    public void saveConfig(String cronExpression)
            throws UnsupportedEncodingException
    {
        logger.info("Cron Expression: " + cronExpression);

        String path = client.getContextBaseUrl() +
                      "/configuration/crontasks/crontask?" +
                      "name=" + cronName + "&" +
                      "jobClass=org.carlspring.strongbox.crontask.test.MyTask&" +
                      "cronExpression=" + URLEncoder.encode(cronExpression, "UTF-8");

        WebTarget resource = client.getClientInstance().target(path);

        Response response = resource.request(MediaType.TEXT_PLAIN)
                                    .put(Entity.entity("Save", MediaType.TEXT_PLAIN));

        int status = response.getStatus();

        assertEquals("Failed to schedule job!", Response.ok().build().getStatus(), status);

        String url = client.getContextBaseUrl() +
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

        String url = client.getContextBaseUrl() +
                     "/configuration/crontasks/crontask?" +
                     "name=" + cronName;
        WebTarget resource = client.getClientInstance().target(url);

        response = resource.request(MediaType.APPLICATION_JSON).get();

        int status = response.getStatus();

        assertEquals("Cron task config exists!", Response.status(Response.Status.BAD_REQUEST).build().getStatus(), status);
    }

}

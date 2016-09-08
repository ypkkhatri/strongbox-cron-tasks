package org.carlspring.strongbox.crontask.rest;

import org.carlspring.strongbox.client.CronTaskClient;
import org.carlspring.strongbox.crontask.domain.CronTaskConfiguration;
import org.carlspring.strongbox.config.CronTasksConfig;
import org.carlspring.strongbox.crontask.exceptions.CronTaskNotFoundException;
import org.carlspring.strongbox.crontask.tasks.MyTask;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import java.io.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

    private static final Logger logger = LoggerFactory.getLogger(CronTaskConfigurationRestletTest.class);

    protected CronTaskClient client;

    private final String cronName1 = "CRJ001";
    private final String cronName2 = "CRJG001";


    @Before
    public void setUp()
            throws Exception
    {
        client = CronTaskClient.getTestInstanceLoggedInAsAdmin();
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

    @Test
    public void testJavaCronTaskConfiguration()
            throws ClassNotFoundException,
                   SchedulerException,
                   CronTaskNotFoundException,
                   UnsupportedEncodingException,
                   JAXBException
    {
        saveJavaConfig("0 0/1 * 1/1 * ? *");

        /**
         * Remove comments to test cron job execution
         * */
        // saveJavaConfig("0 0/2 * 1/1 * ? *");

        deleteConfig(cronName1);
    }

    @Test
    public void testGroovyCronTaskConfiguration()
            throws ClassNotFoundException,
                   SchedulerException,
                   CronTaskNotFoundException,
                   UnsupportedEncodingException,
                   JAXBException
    {
        saveGroovyConfig("0 0/1 * 1/1 * ? *");
        uploadGroovyScript();

        /**
         * Remove comments to test cron job execution *
         * */
        // listOfGroovyScriptsName();
        // saveGroovyConfig("0 0/2 * 1/1 * ? *");

        deleteConfig(cronName2);
    }

    public void saveJavaConfig(String cronExpression)
            throws UnsupportedEncodingException, JAXBException
    {
        logger.debug("Cron Expression: " + cronExpression);

        String url = client.getContextBaseUrl() + "/crontask";

        CronTaskConfiguration configuration = new CronTaskConfiguration();
        configuration.setName(cronName1);
        configuration.addProperty("cronExpression", cronExpression);
        configuration.addProperty("jobClass", MyTask.class.getName());

        WebTarget resource = client.getClientInstance().target(url);
        client.setupAuthentication(resource);

        Response response = resource.request(MediaType.APPLICATION_JSON)
                                    .put(Entity.entity(configuration, MediaType.APPLICATION_JSON));

        int status = response.getStatus();
        if (Response.ok().build().getStatus() != status)
        {
            logger.error(response.readEntity(String.class));
        }

        assertEquals("Failed to schedule job!", Response.ok().build().getStatus(), status);

        /**
         * Retrieve saved config
         * */
        url = client.getContextBaseUrl() +
              "/crontask?" +
              "name=" + cronName1;
        resource = client.getClientInstance().target(url);
        client.setupAuthentication(resource);

        response = resource.request(MediaType.APPLICATION_JSON).get();

        status = response.getStatus();

        assertEquals("Failed to get cron task config!", Response.ok().build().getStatus(), status);
    }

    public void saveGroovyConfig(String cronExpression)
            throws UnsupportedEncodingException, JAXBException
    {
        logger.debug("Cron Expression: " + cronExpression);

        String url = client.getContextBaseUrl() + "/crontask";

        CronTaskConfiguration configuration = new CronTaskConfiguration();
        configuration.setName(cronName2);
        configuration.addProperty("cronExpression", cronExpression);

        WebTarget resource = client.getClientInstance().target(url);
        client.setupAuthentication(resource);

        Response response = resource.request(MediaType.APPLICATION_JSON)
                                    .put(Entity.entity(configuration, MediaType.APPLICATION_JSON));

        int status = response.getStatus();
        if (Response.ok().build().getStatus() != status)
        {
            logger.error(response.readEntity(String.class));
        }

        assertEquals("Failed to schedule job!", Response.ok().build().getStatus(), status);

        /**
         * Retrieve saved config
         * */
        url = client.getContextBaseUrl() + "/crontask?name=" + cronName2;

        resource = client.getClientInstance().target(url);
        client.setupAuthentication(resource);

        response = resource.request(MediaType.APPLICATION_JSON).get();

        status = response.getStatus();

        assertEquals("Failed to get cron task config!", Response.ok().build().getStatus(), status);

    }

    public void deleteConfig(String cronName)
    {
        String path = "/crontask?name=" + cronName;

        Response response = client.delete(path);
        assertEquals("Failed to delete job!", Response.ok().build().getStatus(), response.getStatus());

        /**
         * Retrieve deleted config
         * */
        String url = client.getContextBaseUrl() +
                     "/crontask?" +
                     "name=" + cronName;
        WebTarget resource = client.getClientInstance().target(url);
        client.setupAuthentication(resource);

        response = resource.request(MediaType.APPLICATION_JSON).get();

        int status = response.getStatus();

        assertEquals("Cron task config exists!",
                     Response.status(Response.Status.BAD_REQUEST).build().getStatus(),
                     status);
    }

    public void uploadGroovyScript()
    {
        String fileName = "GroovyTask.groovy";
        String contentDisposition = "attachment; filename=\"" + fileName + "\"";

        File file = new File("target/test-classes/groovy/" + fileName);

        String path = client.getContextBaseUrl() + "/crontask/groovy?cronName=" + cronName2;

        Response response = null;
        try
        {
            InputStream is = new FileInputStream(file);
            WebTarget resource = client.getClientInstance().target(path);
            client.setupAuthentication(resource);
            response = resource.request(MediaType.APPLICATION_OCTET_STREAM)
                               .header("Content-Disposition", contentDisposition)
                               .header("fileName", fileName)
                               .put(Entity.entity(is, MediaType.APPLICATION_OCTET_STREAM));

        }
        catch (FileNotFoundException e)
        {
            logger.error("Error: ", e);
        }

        int status = response.getStatus();

        assertEquals("Failed to upload groovy script!", Response.ok().build().getStatus(), status);
    }

    public void listOfGroovyScriptsName()
    {
        /**
         * Retrieve list of Groovy scripts file name
         * */
        String url = client.getContextBaseUrl() +
                     "/groovy/names";
        WebTarget resource = client.getClientInstance().target(url);
        client.setupAuthentication(resource);

        Response response = resource.request(MediaType.APPLICATION_JSON).get();

        int status = response.getStatus();

        assertEquals("Failed to get groovy scripts names!", Response.ok().build().getStatus(), status);
    }

}

package org.carlspring.strongbox.crontask.rest;

import org.carlspring.strongbox.crontask.configuration.CronTaskConfiguration;
import org.carlspring.strongbox.crontask.configuration.CronTasksConfig;
import org.carlspring.strongbox.crontask.exceptions.CronTaskNotFoundException;
import org.carlspring.strongbox.crontask.test.MyTask;

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

    protected TestClient client;

    private final String cronName = "CRJ001";


    @Before
    public void setUp()
            throws Exception
    {
        client = TestClient.getTestInstance();
        ;
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
//        saveJavaConfig("0 0/2 * 1/1 * ? *"); // Remove comments to test cron job execution
        deleteConfig();
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
//        listOfGroovyScriptsName();
//        saveGroovyConfig("0 0/2 * 1/1 * ? *"); // Remove comments to test cron job execution
        deleteConfig();
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

    public void saveGroovyConfig(String cronExpression)
            throws UnsupportedEncodingException, JAXBException
    {
        logger.debug("Cron Expression: " + cronExpression);

        String url = client.getContextBaseUrl() + "/configuration/crontasks/crontask";

        CronTaskConfiguration configuration = new CronTaskConfiguration();
        configuration.setName(cronName);
        configuration.addProperty("cronExpression", cronExpression);

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
        url = client.getContextBaseUrl() + "/configuration/crontasks/crontask?name=" + cronName;

        resource = client.getClientInstance().target(url);

        response = resource.request(MediaType.APPLICATION_JSON).get();

        status = response.getStatus();

        assertEquals("Failed to get cron task config!", Response.ok().build().getStatus(), status);

    }

    public void deleteConfig()
    {
        String path = "/configuration/crontasks/crontask?name=" + cronName;

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

        assertEquals("Cron task config exists!",
                     Response.status(Response.Status.BAD_REQUEST).build().getStatus(),
                     status);
    }

    public void uploadGroovyScript()
    {
        String fileName = "GroovyTask.groovy";
        String contentDisposition = "attachment; filename=\"" + fileName + "\"";

        File file = new File("target/test-classes/groovy/" + fileName);

        String path = client.getContextBaseUrl() + "/configuration/crontasks/crontask/groovy?cronName=" + cronName;

        Response response = null;
        try
        {
            InputStream is = new FileInputStream(file);
            WebTarget resource = client.getClientInstance().target(path);
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
                     "/configuration/crontasks/groovy/names";
        WebTarget resource = client.getClientInstance().target(url);

        Response response = resource.request(MediaType.APPLICATION_JSON).get();

        int status = response.getStatus();

        assertEquals("Failed to get groovy scripts names!", Response.ok().build().getStatus(), status);
    }

}

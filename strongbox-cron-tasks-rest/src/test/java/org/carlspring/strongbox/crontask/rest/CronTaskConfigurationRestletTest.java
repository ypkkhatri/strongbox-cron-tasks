package org.carlspring.strongbox.crontask.rest;

import org.carlspring.strongbox.crontask.configuration.CronTaskConfiguration;
import org.carlspring.strongbox.crontask.configuration.CronTasksConfig;
import org.carlspring.strongbox.crontask.exceptions.CronTaskNotFoundException;
import org.carlspring.strongbox.crontask.test.MyTask;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.UnsupportedEncodingException;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
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
//        saveJavaConfig("0 0/1 * 1/1 * ? *");
//        saveJavaConfig("0 0/2 * 1/1 * ? *");
//        deleteConfig();
        saveGroovyConfig("0 0/1 * 1/1 * ? *");
        uploadGroovyScript();
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

    public void saveGroovyConfig(String cronExpression)
            throws UnsupportedEncodingException, JAXBException
    {
        logger.debug("Cron Expression: " + cronExpression);

        String url = client.getContextBaseUrl() + "/configuration/crontasks/crontask";

        CronTaskConfiguration configuration = new CronTaskConfiguration();
        configuration.setName(cronName);
        configuration.addProperty("cronExpression", cronExpression);
//        configuration.addProperty("jobClass", MyTask.class.getName());

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

    public void uploadGroovyScript()
    {
        File file = new File("./" + "src\\test\\resources\\GroovyTask.groovy");

        String path = client.getContextBaseUrl() + "/configuration/crontasks/crontask/" + cronName + "/upload/groovy";

        WebTarget resource = client.getClientInstance().target(path);
        client.getClientInstance().property("Content-Type", MediaType.MULTIPART_FORM_DATA);
        Invocation.Builder builder = resource.request();

        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.bodyPart(new FileDataBodyPart("file", file, MediaType.APPLICATION_OCTET_STREAM_TYPE));
        formDataMultiPart.bodyPart(new FormDataBodyPart("name", file.getName()));
//        multipartFormDataOutput.addFormData("uploadedFile", new FileInputStream(filePath), MediaType.MULTIPART_FORM_DATA_TYPE, filename);
        GenericEntity<FormDataMultiPart> genericEntity = new GenericEntity<FormDataMultiPart>(formDataMultiPart)
        {
        };

        Response response = builder.post(Entity.entity(genericEntity, MediaType.MULTIPART_FORM_DATA_TYPE));

        int status = response.getStatus();

        assertEquals("Failed to upload groovy script!", Response.ok().build().getStatus(), status);
    }

}

package org.carlspring.strongbox.client;

import org.carlspring.strongbox.cron.domain.CronTaskConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import java.io.Closeable;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author mtodorov
 * @author Yougeshwar
 */
public class CronTaskClient extends ArtifactClient
        implements Closeable
{

    private static final Logger logger = LoggerFactory.getLogger(CronTaskClient.class);

    public CronTaskClient()
    {
    }

    public static CronTaskClient getTestInstanceLoggedInAsAdmin()
    {
        return getTestInstance("admin", "password");
    }

    public static CronTaskClient getTestInstance(String username,
                                                 String password)
    {
        CronTaskClient client = new CronTaskClient();
        client.setUsername(username);
        client.setPassword(password);
        client.setContextBaseUrl("http://" + client.getHost() + ":" + client.getPort() + "/configuration/crontasks");

        return client;
    }

    public Response saveCronConfig(CronTaskConfiguration configuration)
            throws UnsupportedEncodingException, JAXBException
    {
        String url = getContextBaseUrl() + "/cron";
        WebTarget resource = getClientInstance().target(url);
        setupAuthentication(resource);

        return resource.request(MediaType.APPLICATION_JSON)
                       .put(Entity.entity(configuration, MediaType.APPLICATION_JSON));
    }

    public Response getCronConfig(String cronName)
    {
        String url = getContextBaseUrl() +
                     "/cron?" +
                     "name=" + cronName;

        WebTarget resource = getClientInstance().target(url);
        setupAuthentication(resource);

        return resource.request(MediaType.APPLICATION_JSON).get();
    }

    public Response uploadCronScript(String cronName, String fileName, InputStream is)
    {
        String path = getContextBaseUrl() + "/cron/groovy?cronName=" + cronName;

        String contentDisposition = "attachment; filename=\"" + fileName + "\"";

        WebTarget resource = getClientInstance().target(path);
        setupAuthentication(resource);
        return resource.request(MediaType.APPLICATION_OCTET_STREAM)
                       .header("Content-Disposition", contentDisposition)
                       .header("fileName", fileName)
                       .put(Entity.entity(is, MediaType.APPLICATION_OCTET_STREAM));

    }

    public Response getGroovyScriptsName() {
        String url = getContextBaseUrl() +
                    "/groovy/names";
        WebTarget resource = getClientInstance().target(url);
        setupAuthentication(resource);

        return resource.request(MediaType.APPLICATION_JSON).get();
    }

    public Response deleteCronConfig(String cronName)
    {
        String url = getContextBaseUrl() + "/cron?name=" + cronName;

        WebTarget resource = getClientInstance().target(url);
        setupAuthentication(resource);

        return resource.request().delete();
    }

}

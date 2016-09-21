package org.carlspring.strongbox.client;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.Closeable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public Response delete(String path)
    {
        @SuppressWarnings("ConstantConditions")
        String url = getContextBaseUrl() + path;

        WebTarget resource = getClientInstance().target(url);
        setupAuthentication(resource);

        Response response = resource.request().delete();

        handleFailures(response, "Failed to delete artifact!");

        return response;
    }

    private void handleFailures(Response response, String message)
    {
        int status = response.getStatus();
        if (status != 200)
        {
            Object entity = response.getEntity();

            if (entity != null && entity instanceof String)
            {
                logger.error(message);
                logger.error((String) entity);
            }
        }
    }
}

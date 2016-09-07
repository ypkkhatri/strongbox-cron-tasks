package org.carlspring.strongbox.crontask.rest;

import javax.ws.rs.ServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mtodorov
 * @author Yougeshwar
 */
public class TestClient
        implements Closeable
{

    private static final Logger logger = LoggerFactory.getLogger(TestClient.class);

    protected String username = "maven";
    protected String password = "password";
    private String protocol = "http";

    private String host = System.getProperty("strongbox.host") != null ?
                          System.getProperty("strongbox.host") :
                          "localhost";

    private int port = System.getProperty("strongbox.port") != null ?
                       Integer.parseInt(System.getProperty("strongbox.port")) :
                       48080;

    private String contextBaseUrl = "/config/crontasks";

    private Client client;


    public TestClient()
    {
    }

    public static TestClient getTestInstanceLoggedInAsAdmin()
    {
        return getTestInstance("admin", "password");
    }

    public static TestClient getTestInstance(String username,
                                             String password)
    {
        String host = System.getProperty("strongbox.host") != null ?
                      System.getProperty("strongbox.host") :
                      "localhost";

        int port = System.getProperty("strongbox.port") != null ?
                   Integer.parseInt(System.getProperty("strongbox.port")) :
                   48080;

        TestClient client = new TestClient();
        client.setPort(port);
        client.setUsername(username);
        client.setPassword(password);
        client.setContextBaseUrl("http://" + host + ":" + client.getPort());

        return client;
    }

    public Client getClientInstance()
    {
        if (client == null)
        {
            ClientConfig config = getClientConfig();
            client = ClientBuilder.newClient(config);

            return client;
        }
        else
        {
            return client;
        }
    }

    private ClientConfig getClientConfig()
    {
        ClientConfig config = new ClientConfig();
        config.connectorProvider(new ApacheConnectorProvider());

        return config;
    }

    @Override
    public void close()
    {
        if (client != null)
        {
            client.close();
        }
    }

    public InputStream getResource(String path)
            throws IOException
    {
        return getResource(path, 0);
    }

    public InputStream getResource(String path, long offset)
            throws IOException
    {
        String url = getContextBaseUrl() + (!path.startsWith("/") ? "/" : "") + path;

        logger.debug("Getting " + url + "...");

        WebTarget resource = getClientInstance().target(url);
        setupAuthentication(resource);

        Invocation.Builder request = resource.request();
        Response response;

        if (offset > 0)
        {
            response = request.header("Range", "bytes=" + offset + "-").get();
        }
        else
        {
            response = request.get();
        }

        return response.readEntity(InputStream.class);
    }

    public Response getResourceWithResponse(String path)
            throws IOException
    {
        String url = getContextBaseUrl() + (!path.startsWith("/") ? "/" : "") + path;

        logger.debug("Getting " + url + "...");

        WebTarget resource = getClientInstance().target(url);
        setupAuthentication(resource);

        return resource.request(MediaType.TEXT_PLAIN).get();
    }

    public Response delete(String path)
    {
        @SuppressWarnings("ConstantConditions")
        String url = getContextBaseUrl() + (path.endsWith("/") ? "" : "/") + path;

        WebTarget resource = getClientInstance().target(url);
        setupAuthentication(resource);

        Response response = resource.request().delete();

        handleFailures(response, "Failed to delete artifact!");

        return response;
    }

    public boolean pathExists(String path)
    {
        String url = getContextBaseUrl() + (path.startsWith("/") ? path : '/' + path);

        logger.debug("Path to artifact: " + url);

        WebTarget resource = getClientInstance().target(url);
        setupAuthentication(resource);

        Response response = resource.request(MediaType.TEXT_PLAIN).get();

        return response.getStatus() == 200;
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

    public void setupAuthentication(WebTarget target)
    {
        if (username != null && password != null)
        {
            logger.trace("[setupAuthentication] " + username + "@" + password);
            target.register(HttpAuthenticationFeature.basic(username, password));
        }
        else
        {
            throw new ServerErrorException("Unable to setup authentication", Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }

    public String getContextBaseUrl()
    {
        if (contextBaseUrl == null)
        {
            contextBaseUrl = protocol + "://" + host + ":" + port;
        }

        return contextBaseUrl;
    }

    public void setContextBaseUrl(String contextBaseUrl)
    {
        this.contextBaseUrl = contextBaseUrl;
    }

}

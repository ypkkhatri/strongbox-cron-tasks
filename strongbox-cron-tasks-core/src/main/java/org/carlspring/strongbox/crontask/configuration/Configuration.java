package org.carlspring.strongbox.crontask.configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yougeshwar
 */
public class Configuration
{
    private Map<String, Object> properties = new HashMap<>();

    public Configuration()
    {
    }

    public void addProperty(String key, Object value)
    {
        properties.put(key, value);
    }

    public Object getProperty(String key)
    {
        return properties.get(key);
    }

    public Object removeProperty(String key)
    {
        return properties.remove(key);
    }
}

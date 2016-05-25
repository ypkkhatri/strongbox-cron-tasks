package org.carlspring.strongbox.crontask.configuration;

import javax.persistence.Id;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yougeshwar
 */
public class CronTaskConfiguration
{
    @Id
    private Object id;
    private String name;
    private Map<String, Object> properties = new HashMap<>();

    public CronTaskConfiguration()
    {
    }

    public Object getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Map<String, Object> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, Object> properties)
    {
        this.properties = properties;
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

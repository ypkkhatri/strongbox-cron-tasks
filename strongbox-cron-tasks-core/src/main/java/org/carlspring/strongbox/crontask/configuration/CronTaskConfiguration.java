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
    private String className;
    private String cronExpression;

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

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getCronExpression()
    {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression)
    {
        this.cronExpression = cronExpression;
    }

    //    public Map<String, Object> getProperties()
//    {
//        return properties;
//    }
//
//    public void setProperties(Map<String, Object> properties)
//    {
//        this.properties = properties;
//    }
//
//    public void addProperty(String key, Object value)
//    {
//        properties.put(key, value);
//    }
//
//    public Object getProperty(String key)
//    {
//        return properties.get(key);
//    }
//
//    public Object removeProperty(String key)
//    {
//        return properties.remove(key);
//    }
}

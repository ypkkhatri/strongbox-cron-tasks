package org.carlspring.strongbox.crontask.configuration;

import javax.persistence.Id;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Yougeshwar
 */
public class CronTaskConfiguration
{
    /**
     * RID: #<cluster-id>:<cluster-position>
     * */
    @Id
    private Object id;

    private String name;
    private String jobClass;
    private String cronExpression;

    private Map<String, Object> properties = new LinkedHashMap<>();


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

    public String getJobClass()
    {
        return jobClass;
    }

    public void setJobClass(String jobClass)
    {
        this.jobClass = jobClass;
    }

    public String getCronExpression()
    {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression)
    {
        this.cronExpression = cronExpression;
    }

    public Map<String, Object> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, Object> properties)
    {
        this.properties = properties;
    }

    private void addProperty(String key, Object value)
    {
        properties.put(key, value);
    }

    private void removeProperty(String key)
    {
        properties.remove(key);
    }

}

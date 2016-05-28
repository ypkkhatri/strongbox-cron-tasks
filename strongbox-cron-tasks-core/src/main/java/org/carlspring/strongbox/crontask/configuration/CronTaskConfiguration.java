package org.carlspring.strongbox.crontask.configuration;

import javax.persistence.Id;

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

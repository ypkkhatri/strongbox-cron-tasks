package org.carlspring.strongbox.crontask.configuration;

import org.carlspring.strongbox.crontask.api.jobs.GroovyCronJob;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yougeshwar
 */
@XmlRootElement(name = "cronTaskConfiguration")
public class CronTaskConfiguration
{

    /**
     * RID: #<cluster-id>:<cluster-position>
     */
    @Id
    private Object id;

    @XmlElement
    private String name;

    @XmlElement
    private Map<String, String> properties = new HashMap<>();


    public CronTaskConfiguration()
    {
        properties.put("jobClass", GroovyCronJob.class.getName());
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

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, String> properties)
    {
        this.properties = properties;
    }

    public String getProperty(String key)
    {
        return this.properties.get(key);
    }

    public void addProperty(String key,
                             String value)
    {
        properties.put(key, value);
    }

    public void removeProperty(String key)
    {
        properties.remove(key);
    }

    public boolean contain(String key) {
        return properties.containsKey(key);
    }
}

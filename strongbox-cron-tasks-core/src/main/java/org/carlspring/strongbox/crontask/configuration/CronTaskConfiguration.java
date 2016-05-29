package org.carlspring.strongbox.crontask.configuration;

import org.carlspring.strongbox.crontask.api.jobs.GroovyCronJob;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedHashMap;
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
    @XmlElement
    private Object id;

    @XmlElement
    private String name;

    @XmlElement
    private Map<String, Object> properties = new LinkedHashMap<>();


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

    public Map<String, Object> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, Object> properties)
    {
        this.properties = properties;
    }

    public Object getProperty(String key)
    {
        return this.properties.get(key);
    }

    public void addProperty(String key,
                             Object value)
    {
        properties.put(key, value);
    }

    public void removeProperty(String key)
    {
        properties.remove(key);
    }

}

package org.carlspring.strongbox.crontask.configuration;

import org.carlspring.strongbox.crontask.adpaters.MapAdapter;
import org.carlspring.strongbox.crontask.api.jobs.GroovyCronJob;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yougeshwar
 */
@XmlRootElement(name = "cronTaskConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class CronTaskConfiguration
{

    /**
     * RID: #<cluster-id>:<cluster-position>
     */
    @Id
    private Object id;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "properties")
    @XmlJavaTypeAdapter(MapAdapter.class)
    private Map<String, String> properties = new HashMap<>();

    public CronTaskConfiguration()
    {
        properties.put("jobClass", GroovyCronJob.class.getName());
    }

    public Object getId()
    {
        return id;
    }

    public void setId(Object id)
    {
        this.id = id;
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

    public Object getProperty(String key)
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

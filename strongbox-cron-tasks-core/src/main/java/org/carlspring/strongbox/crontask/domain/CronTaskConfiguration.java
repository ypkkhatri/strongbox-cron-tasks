package org.carlspring.strongbox.crontask.domain;

import org.carlspring.strongbox.data.domain.GenericEntity;

import javax.xml.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yougeshwar
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CronTaskConfiguration
        extends GenericEntity
{

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "properties")
    private Map<String, String> properties = new HashMap<>();


    public CronTaskConfiguration()
    {
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
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

package org.carlspring.strongbox.crontask.adpaters;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author Yougeshwar
 */
public class MapElements
{

    @XmlElement
    public String key;

    @XmlElement
    public String value;

    private MapElements()
    {
        //Required by JAXB
    }

    public MapElements(String key,
                       String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}

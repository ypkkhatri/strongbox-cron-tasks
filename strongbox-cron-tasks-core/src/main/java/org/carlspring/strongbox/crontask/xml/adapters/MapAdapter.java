package org.carlspring.strongbox.crontask.xml.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yougeshwar
 */
public class MapAdapter
        extends XmlAdapter<MapElements[], Map<String, String>>
{

    public MapElements[] marshal(Map<String, String> map)
            throws Exception
    {
        MapElements[] mapElements = new MapElements[map.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : map.entrySet())
        {
            mapElements[i++] = new MapElements(entry.getKey(), entry.getValue());
        }

        return mapElements;
    }

    public Map<String, String> unmarshal(MapElements[] arg0)
            throws Exception
    {
        Map<String, String> map = new HashMap<>();
        for (MapElements mapElements : arg0)
        {
            map.put(mapElements.key, mapElements.value);
        }
        return map;
    }
}
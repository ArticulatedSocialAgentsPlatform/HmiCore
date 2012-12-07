package hmi.animation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.BiMap;

/**
 * Unit tests for the RenamingXMLMap
 * @author hvanwelbergen
 *
 */
public class RenamingXMLMapTest
{
    @Test
    public void test()
    {
        //@formatter:off
        String renamingContent=
        "<renamingMap xmlns=\"http://www.asap-project.org/renaming\">"+
        "<rename src=\"BipKevin Pelvis\" dst=\"HumanoidRoot\"/>"+
        "<rename src=\"BipKevin Spine\" dst=\"vt10\"/>"+
        "<rename src=\"BipKevin R Toe0Nub\" dst=\"r_forefoot_tip\"/>"+
        "</renamingMap>";
        //@formatter:on
        
        RenamingXMLMap m = new RenamingXMLMap();
        m.readXML(renamingContent);        
        BiMap<String,String> renamingMap = m.getRenamingMap();
        assertEquals("HumanoidRoot",renamingMap.get("BipKevin Pelvis"));
        assertEquals("vt10",renamingMap.get("BipKevin Spine"));
        assertEquals("r_forefoot_tip",renamingMap.get("BipKevin R Toe0Nub"));        
        assertEquals("BipKevin R Toe0Nub", renamingMap.inverse().get("r_forefoot_tip"));
    }
}

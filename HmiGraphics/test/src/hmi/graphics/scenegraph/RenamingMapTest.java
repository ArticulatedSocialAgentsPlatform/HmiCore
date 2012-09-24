package hmi.graphics.scenegraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import hmi.graphics.collada.Collada;
import hmi.graphics.util.RenamingMap;

import java.io.IOException;

import org.junit.Test;

import com.google.common.collect.BiMap;

/**
 * Unit tests for the RenamingMap utility
 * @author hvanwelbergen
 *
 */
public class RenamingMapTest
{
    @Test
    public void test()
    {
        //@formatter:off
        String renamingContent=
        "BipKevin_Pelvis           HumanoidRoot\n"+
        "BipKevin_Spine                    vt10\n"+
        "BipKevin_R_Toe0Nub      r_forefoot_tip\n";
        //@formatter:on
        
        BiMap<String,String> renamingMap = RenamingMap.renamingMap(renamingContent);
        assertEquals("HumanoidRoot",renamingMap.get("BipKevin_Pelvis"));
        assertEquals("vt10",renamingMap.get("BipKevin_Spine"));
        assertEquals("r_forefoot_tip",renamingMap.get("BipKevin_R_Toe0Nub"));
        
        assertEquals("BipKevin_R_Toe0Nub", renamingMap.inverse().get("r_forefoot_tip"));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testOdd()
    {
        //@formatter:off
        String renamingContent=
        "BipKevin_Pelvis           HumanoidRoot\n"+
        "BipKevin_Spine                    vt10\n"+
        "BipKevin_R_Toe0Nub      \n";
        //@formatter:on        
        RenamingMap.renamingMap(renamingContent);
    }
    
    @Test
    public void testFromFile() throws IOException
    {
        Collada col = Collada.forResource("billierenaming_collada.xml");
        assertNotNull(col.getRenamingList());
        BiMap<String,String> renamingMap = RenamingMap.renamingMap(col.getRenamingList());
        assertEquals("l_index1",renamingMap.get("BipKevin_L_Finger1"));
        assertEquals("skulltop",renamingMap.get("BipKevin_HeadNub"));
    }
}

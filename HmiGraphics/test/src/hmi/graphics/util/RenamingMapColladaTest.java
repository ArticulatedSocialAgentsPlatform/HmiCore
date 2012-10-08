package hmi.graphics.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import hmi.animation.RenamingMap;
import hmi.graphics.collada.Collada;

import java.io.IOException;

import org.junit.Test;

import com.google.common.collect.BiMap;

/**
 * Unit tests for the RenamingMap utility through collada
 * @author hvanwelbergen
 *
 */
public class RenamingMapColladaTest
{
    @Test
    public void testFromColladaFile() throws IOException
    {
        Collada col = Collada.forResource("billierenaming_collada.xml");
        assertNotNull(col.getRenamingList());
        BiMap<String,String> renamingMap = RenamingMap.renamingMap(col.getRenamingList());
        assertEquals("l_index1",renamingMap.get("BipKevin_L_Finger1"));
        assertEquals("skulltop",renamingMap.get("BipKevin_HeadNub"));
    }
}

package hmi.graphics.scenegraph;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import hmi.graphics.collada.Collada;
import hmi.graphics.collada.Extra;
import hmi.xml.XMLTokenizer;

import org.junit.Test;

/**
 * Unit tests for extra
 * @author hvanwelbergen
 * 
 */
public class ExtraTest
{
    @Test
    public void testElckerlycProfile() throws IOException
    {
        //@formatter:off
        String str = 
        "<extra xmlns=\"http://www.collada.org/2005/11/COLLADASchema\">"+
        "   <technique profile=\"Elckerlyc\">"+
        "      <renaming>"+
        "   BipKevin_Pelvis HumanoidRoot"+
        "   BipKevin_Spine  vt10"+
        "   BipKevin_Spine1 vt6"+
        "   BipKevin_Spine2 vt3"+
        "   BipKevin_Spine3 vt1"+
        "       </renaming>"+
        "   </technique>"+
        "</extra>";
        //@formatter:on
        XMLTokenizer tok = new XMLTokenizer(str);
        
        Extra extra = new Extra(new Collada(),tok);        
        assertNotNull(extra.getElckerlycProfile().getRenamingList());
    }
}

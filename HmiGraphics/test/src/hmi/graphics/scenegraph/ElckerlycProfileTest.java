package hmi.graphics.scenegraph;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import hmi.graphics.collada.Collada;
import hmi.graphics.collada.ElckerlycProfile;
import hmi.xml.XMLTokenizer;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the ElckerlycProfile (=just renaming for now)
 * @author hvanwelbergen
 * 
 */
public class ElckerlycProfileTest
{
    private ElckerlycProfile prof;
    private String renamingContent;
    
    @Before
    public void setup() throws IOException
    {
        //@formatter:off
        renamingContent=
        "BipKevin_Pelvis                     HumanoidRoot"+
        "BipKevin_Spine                    vt10"+
        "BipKevin_R_Toe0Nub      r_forefoot_tip";
        String str = "<renaming>" +
                        renamingContent+
        		     "</renaming>";
        //@formatter:on
        prof = new ElckerlycProfile(new Collada(), new XMLTokenizer(str));
        
    }

    @Test
    public void test()
    {
        assertEquals(renamingContent, prof.getRenamingList());
    }
}

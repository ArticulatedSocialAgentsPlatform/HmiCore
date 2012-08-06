package hmi.physics.assembler;

import hmi.animation.VJoint;
import hmi.graphics.opengl.GLShaderProgramLoader;
import hmi.graphics.opengl.GLTextureLoader;
import hmi.graphics.util.HumanoidLoader;
import hmi.physics.PhysicalHumanoid;
import hmi.physics.mixed.MixedSystem;
import hmi.physics.ode.OdeHumanoid;
import hmi.util.Resources;

import java.io.IOException;

import org.junit.Test;
import org.odejava.HashSpace;
import org.odejava.Odejava;
import org.odejava.World;

/**
 * Unit test cases for the MixedSystemAssembler
 * @author Herwin
 *
 */
public class MixedSystemAssemblerTest
{
    private VJoint human;
    private void loadDaeHuman() throws IOException
    {
        String textureDir = "Humanoids/armandia/maps";
        String shaderDir = "shaders";
        String daeFile = "bin/armandia_boring_neckfix_light_toplevel.bin";

        if (textureDir!=null)GLTextureLoader.addTextureDirectory(textureDir);   
        if (shaderDir!=null)GLShaderProgramLoader.addShaderDirectory(shaderDir);
        HumanoidLoader model = new HumanoidLoader("Armandia", "Humanoids/armandia", daeFile, "ARMANDIA");
        human = model.getAvatarAnimationRootJoint();        
    }
    
    @Test
    public void test() throws IOException
    {
        float g[]={0,-9.8f,0};
        loadDaeHuman();
        Odejava.init();        
        PhysicalHumanoid ph = new OdeHumanoid("Armandia", new World(), new HashSpace());         
        MixedSystem m = new MixedSystem(g,ph);
        MixedSystemAssembler msa = new MixedSystemAssembler(human,ph,m);
        Resources res = new Resources("");
        msa.readXML(res.getReader("mixedsystemtest/armandialowerbody.xml"));        
    }
}

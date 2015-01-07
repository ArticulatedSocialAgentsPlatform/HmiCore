/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
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

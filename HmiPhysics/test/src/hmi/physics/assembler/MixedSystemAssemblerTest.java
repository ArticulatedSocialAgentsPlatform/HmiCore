/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
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

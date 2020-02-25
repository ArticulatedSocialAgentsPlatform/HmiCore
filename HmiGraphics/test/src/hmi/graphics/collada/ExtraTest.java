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
package hmi.graphics.collada;

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

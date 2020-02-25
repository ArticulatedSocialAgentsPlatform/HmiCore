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

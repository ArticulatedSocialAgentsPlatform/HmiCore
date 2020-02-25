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
package hmi.animation;

import static org.junit.Assert.assertEquals;

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
        BiMap<String,String> renamingMap = RenamingMap.renamingMapFromFileOnClasspath("billierenaming.txt");
        assertEquals("HumanoidRoot", renamingMap.get("BipKevin_Pelvis"));
        assertEquals("r_thumb3", renamingMap.get("BipKevin_R_Finger02"));
    }
}

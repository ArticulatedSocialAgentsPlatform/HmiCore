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

import org.junit.Test;

import com.google.common.collect.BiMap;

/**
 * Unit tests for the RenamingXMLMap
 * @author hvanwelbergen
 *
 */
public class RenamingXMLMapTest
{
    @Test
    public void test()
    {
        //@formatter:off
        String renamingContent=
        "<renamingMap xmlns=\"http://www.asap-project.org/renaming\">"+
        "<rename src=\"BipKevin Pelvis\" dst=\"HumanoidRoot\"/>"+
        "<rename src=\"BipKevin Spine\" dst=\"vt10\"/>"+
        "<rename src=\"BipKevin R Toe0Nub\" dst=\"r_forefoot_tip\"/>"+
        "</renamingMap>";
        //@formatter:on
        
        RenamingXMLMap m = new RenamingXMLMap();
        m.readXML(renamingContent);        
        BiMap<String,String> renamingMap = m.getRenamingMap();
        assertEquals("HumanoidRoot",renamingMap.get("BipKevin Pelvis"));
        assertEquals("vt10",renamingMap.get("BipKevin Spine"));
        assertEquals("r_forefoot_tip",renamingMap.get("BipKevin R Toe0Nub"));        
        assertEquals("BipKevin R Toe0Nub", renamingMap.inverse().get("r_forefoot_tip"));
    }
}

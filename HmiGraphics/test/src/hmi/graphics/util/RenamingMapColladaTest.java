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

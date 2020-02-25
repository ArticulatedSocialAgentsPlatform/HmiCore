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

/**
 * Test cases for HandDOF
 * @author hvanwelbergen
 *
 */
public class HandDOFTest
{
    private static final double PRECISION = 0.001;
    @Test
    public void testXML()
    {
        HandDOF hExp = new HandDOF();
        hExp.PIPPinkyFlexion = 1;
        hExp.PIPRingFlexion = 2;
        hExp.PIPMiddleFlexion = 3;
        hExp.PIPIndexFlexion = 4;
        hExp.IPThumbFlexion = 5;
        hExp.MCPPinkyFlexion = 6;
        hExp.MCPRingFlexion = 7;
        hExp.MCPMiddleFlexion = 8;
        hExp.MCPIndexFlexion = 9;
        hExp.MCPThumbFlexion = 10;
        hExp.MCPPinkyAbduction = 11;
        hExp.MCPRingAbduction = 12;
        hExp.MCPMiddleAbduction = 13;
        hExp.MCPIndexAbduction = 14;
        hExp.TMCFlexion = 15;
        hExp.TMCAbduction = 16;
        StringBuilder buf = new StringBuilder();
        hExp.appendXML(buf);        
        
        HandDOF hDof = new HandDOF();
        hDof.readXML(buf.toString());
        
        assertEquals(hExp.PIPPinkyFlexion, hDof.PIPPinkyFlexion, PRECISION);
        assertEquals(hExp.PIPRingFlexion, hDof.PIPRingFlexion, PRECISION);
        assertEquals(hExp.PIPMiddleFlexion, hDof.PIPMiddleFlexion, PRECISION);
        assertEquals(hExp.PIPIndexFlexion, hDof.PIPIndexFlexion, PRECISION);
        assertEquals(hExp.IPThumbFlexion, hDof.IPThumbFlexion, PRECISION);
        assertEquals(hExp.MCPPinkyFlexion, hDof.MCPPinkyFlexion, PRECISION);
        assertEquals(hExp.MCPRingFlexion, hDof.MCPRingFlexion, PRECISION);
        assertEquals(hExp.MCPMiddleFlexion, hDof.MCPMiddleFlexion, PRECISION);
        assertEquals(hExp.MCPIndexFlexion, hDof.MCPIndexFlexion, PRECISION);
        assertEquals(hExp.MCPThumbFlexion, hDof.MCPThumbFlexion, PRECISION);
        assertEquals(hExp.MCPPinkyAbduction, hDof.MCPPinkyAbduction, PRECISION);
        assertEquals(hExp.MCPRingAbduction, hDof.MCPRingAbduction, PRECISION);
        assertEquals(hExp.MCPMiddleAbduction, hDof.MCPMiddleAbduction, PRECISION);
        assertEquals(hExp.MCPIndexAbduction, hDof.MCPIndexAbduction, PRECISION);
        assertEquals(hExp.TMCFlexion, hDof.TMCFlexion, PRECISION);
        assertEquals(hExp.TMCAbduction, hDof.TMCAbduction, PRECISION);
    }
}

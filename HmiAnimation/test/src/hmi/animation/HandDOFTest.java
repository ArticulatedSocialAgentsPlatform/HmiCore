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

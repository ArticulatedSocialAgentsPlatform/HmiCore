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

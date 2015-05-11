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

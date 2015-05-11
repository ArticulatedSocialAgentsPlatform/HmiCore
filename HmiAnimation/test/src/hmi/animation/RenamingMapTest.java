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

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
package hmi.tts.util;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

/**
 * Unit tests for the BMLTextUtil
 * @author welberge
 */
public class BMLTextUtilTest
{
    @Test
    public void testStrip()
    {
        String stripped = BMLTextUtil.stripSyncs("blah blah<sync id=\"id1\"/>");
        assertEquals("blah blah ",stripped);
    }
    
    @Test
    public void testStripWithNamespace()
    {
        String stripped = BMLTextUtil.stripSyncs("blah blah<sync xmlns=\"http://blah.com\" id=\"id1\"/>");
        assertEquals("blah blah ",stripped);
    }
    
    @Test
    public void testStripWithPrefixedNamespace()
    {
        String stripped = BMLTextUtil.stripSyncs("blah blah<sync xmlns:ns=\"http://blah.com\" id=\"id1\"/>");
        assertEquals("blah blah ",stripped);
    }
    
    @Test
    public void testBMLToSSML()
    {
        String ssml = BMLTextUtil.BMLToSSML("blah blah<sync id=\"id1\"/>");
        assertEquals("blah blah<mark name=\"id1\"/>",ssml);
    }
    
    @Test
    public void testBMLToSSML2()
    {
        String ssml = BMLTextUtil.BMLToSSML("blah blah<sync id=\"id1\"></sync>");
        assertEquals("blah blah<mark name=\"id1\"></mark>",ssml);
    }
    
    @Test
    public void testBMLToSSML3()
    {
        String ssml = BMLTextUtil.BMLToSSML("blah blah<sync xmlns:ns=\"http://blah.com\" id=\"id1\"></sync>");
        assertEquals("blah blah<mark name=\"id1\"></mark>",ssml);
    }
    
    @Test
    public void testGetSyncAndOffsetMidSync()
    {
        List<SyncAndOffset> sAndO = BMLTextUtil.getSyncAndOffsetList("Hello <sync id=\"s1\"/> world.", 2);
        assertEquals("s1", sAndO.get(0).getSync());
        assertEquals(1, sAndO.get(0).getOffset());        
    }
    
    @Test
    public void testGetSyncAndOffsetEndSync()
    {
        List<SyncAndOffset> sAndO = BMLTextUtil.getSyncAndOffsetList("Hello world.<sync id=\"s1\"/>", 2);
        assertEquals("s1", sAndO.get(0).getSync());
        assertEquals(2, sAndO.get(0).getOffset());
    }
    
    @Test
    public void testGetSyncAndOffsetListStartSync()
    {
        List<SyncAndOffset> sAndO = BMLTextUtil.getSyncAndOffsetList("<sync id=\"s1\"/>Hello world.", 2);
        assertEquals("s1", sAndO.get(0).getSync());
        assertEquals(0, sAndO.get(0).getOffset());
    }
    
    @Test
    public void testGetSyncAndOffsetMultipleSyncs()
    {
        List<SyncAndOffset> sAndO = BMLTextUtil.getSyncAndOffsetList("<sync id=\"sStart\"/>Hello <sync id=\"s1\"/> " +
        		"pretty <sync id=\"s2\"/> world.<sync id=\"sEnd\"/>", 3);
        assertEquals("sStart", sAndO.get(0).getSync());
        assertEquals(0, sAndO.get(0).getOffset());        
        assertEquals("s1", sAndO.get(1).getSync());
        assertEquals(1, sAndO.get(1).getOffset());
        assertEquals("s2", sAndO.get(2).getSync());
        assertEquals(2, sAndO.get(2).getOffset());
        assertEquals("sEnd", sAndO.get(3).getSync());
        assertEquals(3, sAndO.get(3).getOffset());
    }
}

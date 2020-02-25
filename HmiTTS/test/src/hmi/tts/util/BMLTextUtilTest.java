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

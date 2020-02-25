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
/*
 * Resources JUnit test
 */

package hmi.util;

import static org.junit.Assert.*;
import org.junit.*;
import java.io.*;

/**
 * JUnit test for hmi.util.Resources
 */
public class ResourcesTest
{

    public ResourcesTest()
    {
    }

    @Before
    public void setUp()
    { // common initialization, executed for every test.
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testRead() throws IOException
    {
        Resources testRes = new Resources("ResourcesTests");
        assertEquals(testRes.getResourceDirectory(), "ResourcesTests");
        String test1txt = null;
        test1txt = testRes.read("res1.txt");
        assertEquals("res1 content"+OS.getNewline()+"line2"+OS.getNewline(), test1txt);
    }

    @Test
    public void testReadTopLevel() throws IOException
    {
        Resources testRes = new Resources("");
        assertEquals(testRes.getResourceDirectory(), "");
        String test1txt = null;
        test1txt = testRes.read("restoplevel.txt");
        assertEquals(test1txt, "toplevel content\r\nline2\r\n");
    }

    @Test
    public void testGetReader() throws IOException
    {
        Resources testRes = new Resources("ResourcesTests");
        BufferedReader restest1 = testRes.getReader("res1.txt");
        String line1 = restest1.readLine();
        assertEquals(line1, "res1 content");
        String line2 = restest1.readLine();
        assertEquals(line2, "line2");
    }

    
    @Test
    public void testDirResources() throws IOException
    {
        String sharedprojectdir = System.getProperty("shared.project.root");
        File resDir = new File(sharedprojectdir + "/HmiCore/HmiUtil/test/resource/ResourcesTests");
        Resources testRes = new Resources(resDir);
        BufferedReader restest1 = testRes.getReader("res1.txt");
        String line1 = restest1.readLine();
        assertEquals(line1, "res1 content");
        String line2 = restest1.readLine();
        assertEquals(line2, "line2");
    }    
}

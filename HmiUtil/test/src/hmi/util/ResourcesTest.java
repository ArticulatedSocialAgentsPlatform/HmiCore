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

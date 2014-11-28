/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
/*
 * ConfigList JUnit test
 */

package hmi.animation;

import hmi.xml.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.primitives.Floats;

import static org.junit.Assert.*;
import org.hamcrest.collection.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * JUnit test for ConfigList
 */
public class ConfigListTest
{
    private static final float CONFIG_PRECISION = 0.0001f;
    private float[] c0, c1, c2, c3, c4, c5;
    private ConfigList clist;

    public ConfigListTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {

    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void init1()
    {
        c0 = new float[] { 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f };
        c1 = new float[] { 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f };
        c2 = new float[] { 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f };
        c3 = new float[] { 4f, 5f, 6f, 7f, 8f, 9f, 10f, 11f };
        c4 = new float[] { 5f, 6f, 7f, 8f, 9f, 10f, 11f, 12f };
        c5 = new float[] { 6f, 7f, 8f, 9f, 10f, 11f, 12f, 13f };
        clist = new ConfigList(8);
    }

    @Test
    public void testBasics()
    {
        assertEquals(0, clist.size());
        assertEquals(8, clist.getConfigSize());
    }

    @Test
    public void testAddConfig()
    {
        clist.addConfig(7.7, c0);
        clist.addConfig(3.3, c1);
        clist.addConfig(5.5, c2);
        clist.addConfig(8.8, c3);
        assertEquals(4, clist.size());
        assertEquals(3.3, clist.getStartTime(), CONFIG_PRECISION);
        assertEquals(8.8, clist.getEndTime(), CONFIG_PRECISION);

        assertEquals(3.3, clist.getTime(0), CONFIG_PRECISION);
        assertEquals(5.5, clist.getTime(1), CONFIG_PRECISION);
        assertEquals(7.7, clist.getTime(2), CONFIG_PRECISION);
        assertEquals(8.8, clist.getTime(3), CONFIG_PRECISION);
        assertTrue(c1 == clist.getConfig(0));
        assertTrue(c2 == clist.getConfig(1));
        assertTrue(c0 == clist.getConfig(2));
        assertTrue(c3 == clist.getConfig(3));

        clist.addConfig(2.2, c4);
        assertEquals(5, clist.size(), CONFIG_PRECISION);
        assertEquals(2.2, clist.getStartTime(), CONFIG_PRECISION);
        assertEquals(2.2, clist.getTime(0), CONFIG_PRECISION);
        assertEquals(3.3, clist.getTime(1), CONFIG_PRECISION);
        assertTrue(c4 == clist.getConfig(0));
        assertTrue(c1 == clist.getConfig(1));

        clist.addConfig(5.5, c5);
        assertEquals(6, clist.size());
        assertEquals(2.2, clist.getStartTime(), CONFIG_PRECISION);
        assertEquals(2.2, clist.getTime(0), CONFIG_PRECISION);
        assertEquals(3.3, clist.getTime(1), CONFIG_PRECISION);
        assertEquals(5.5, clist.getTime(2), CONFIG_PRECISION);
        assertEquals(5.5, clist.getTime(3), CONFIG_PRECISION);
        assertEquals(7.7, clist.getTime(4), CONFIG_PRECISION);
        assertEquals(8.8, clist.getTime(5), CONFIG_PRECISION);

        assertTrue(c4 == clist.getConfig(0));
        assertTrue(c1 == clist.getConfig(1));
        assertTrue(c2 == clist.getConfig(2));
        assertTrue(c5 == clist.getConfig(3));
        assertTrue(c0 == clist.getConfig(4));
        assertTrue(c3 == clist.getConfig(5));
    }

    @Test
    public void testSubConfig()
    {
        clist.addConfig(0.0, c0);
        clist.addConfig(1.1, c1);
        clist.addConfig(2.2, c2);
        clist.addConfig(3.3, c3);
        clist.addConfig(4.4, c4);
        clist.addConfig(5.4, c5);
        ConfigList cl = clist.subConfigList(2, 5);
        assertEquals(8, cl.getConfigSize());
        assertEquals(3, cl.size());
        assertArrayEquals(c2, cl.getConfig(0), CONFIG_PRECISION);
    }

    @Test
    public void test1ConfigFromXML() throws java.io.IOException
    {
        String str = "<ConfigList configSize=\"8\">0.0 1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0</ConfigList>";
        XMLTokenizer tokenizer = new XMLTokenizer(str);
        ConfigList clistDecoded = new ConfigList(tokenizer);
        assertEquals(1, clistDecoded.size());
        assertEquals(0.0f, clistDecoded.getTime(0), CONFIG_PRECISION);
        assertThat(Floats.asList(clistDecoded.getConfig(0)), IsIterableContainingInOrder.contains(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f));
    }

    @Test
    public void test1ConfigToXML() throws java.io.IOException
    {
        clist.addConfig(0.0, c0);
        String encoded = clist.toXMLString();
        XMLTokenizer tokenizer = new XMLTokenizer(encoded);
        ConfigList clistDecoded = new ConfigList(tokenizer);
        assertEquals(1, clistDecoded.size());
        assertEquals(0.0f, clistDecoded.getTime(0), CONFIG_PRECISION);
        assertThat(Floats.asList(clistDecoded.getConfig(0)), IsIterableContainingInOrder.contains(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f));
    }

    @Test
    public void testXML() throws java.io.IOException
    {
        clist.addConfig(0.0, c0);
        clist.addConfig(1.1, c1);
        clist.addConfig(2.2, c2);
        clist.addConfig(3.3, c3);
        clist.addConfig(4.4, c4);
        assertTrue(clist.size() == 5);

        String encoded = clist.toXMLString();
        // System.out.println("ConfigListTest.testXML: encode =\n" + encoded);

        XMLTokenizer tokenizer = new XMLTokenizer(encoded);
        ConfigList clistDecoded = new ConfigList(tokenizer);
        // ConfigList clistDecoded = ConfigList.getConfigList(8, tokenizer);

        // System.out.println("ConfigListTest.testXML: decoded size ="
        // + clistDecoded.size());

        assertEquals(5, clistDecoded.size());
        assertEquals(0.0, clistDecoded.getTime(0), CONFIG_PRECISION);
        assertEquals(1.1f, clistDecoded.getTime(1), CONFIG_PRECISION);
        assertEquals(2.2f, clistDecoded.getTime(2), CONFIG_PRECISION);
        assertEquals(3.3f, clistDecoded.getTime(3), CONFIG_PRECISION);
        assertEquals(4.4f, clistDecoded.getTime(4), CONFIG_PRECISION);

        assertEquals(c0[0], clistDecoded.getConfig(0)[0], CONFIG_PRECISION);
        assertEquals(c0[1], clistDecoded.getConfig(0)[1], CONFIG_PRECISION);
        assertEquals(c2[3], clistDecoded.getConfig(2)[3], CONFIG_PRECISION);
        assertEquals(c4[0], clistDecoded.getConfig(4)[0], CONFIG_PRECISION);
        assertEquals(c4[7], clistDecoded.getConfig(4)[7], CONFIG_PRECISION);

    }
}

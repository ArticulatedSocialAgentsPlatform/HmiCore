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
 * Diff JUnit test
 */

package hmi.util;

import org.junit.*;

/**
 * JUnit test for hmi.util.Diff
 */
public class DiffTest {
    
    public DiffTest() {
    }

    @Before
    public void setUp()  { // common initialization, executed for every test.
    }

    @After
    public void tearDown() {
    }

    @Test
    public void diffTest() {       
       int[] a0 = null; int[] b0 = null;
       int[] a1 = new int[] { 1, 2, 3};
       int[] b1 = new int[] { 1, 4, 5};
       String d1 = Diff.showDiff("a0/b0", a0, b0);
       //System.out.println("d1=" + d1);
       
       String d2 = Diff.showDiff("a1/b0", a1, b0);
       //System.out.println("d2=" + d2);
       
       String d3 = Diff.showDiff("a1/b1", a1, b1);
       //System.out.println("d3=" + d3);
       
       
       
       String d4 = Diff.showDiff("a1/a1", a1, a1);
       //System.out.println("d4=" + d4);
       
       
       float[] f1 = new float[] {1.0f, 2.0f, 3.0f, 4.0f};
       float[] f2 = new float[] {1.0f, 2.0f, 3.0f};
       
       String d5 = Diff.showDiff("f1/f2", f1, f2);
       //System.out.println("d5=" + d5);
       
       String[] s1 = new String[] {"aap", "noot", "mies"};
       String[] s2 = new String[] {"aap", "noot", "nies"};
       String d6 = Diff.showDiff("s1/s2", s1, s2);
       //System.out.println("d6=" + d6);
       
       
     
    } 

  
}

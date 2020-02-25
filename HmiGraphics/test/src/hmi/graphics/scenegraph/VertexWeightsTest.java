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
 * VertexWeights JUnit test
 */

package hmi.graphics.scenegraph;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.*;
import hmi.util.*;
import java.io.*;
import hmi.xml.*;

/**
 * JUnit test for hmi.graphics.scenegraph.VertexWeights
 */
public class VertexWeightsTest {
    
    public VertexWeightsTest() {
    }

    @Before
    public void setUp()  { // common initialization, executed for every test.
    }

    @After
    public void tearDown() {
    }

    @Test
    public void basics() {       
       VertexWeights vw = new VertexWeights();
       assertTrue(vw.getJointIndices() == null);
       assertTrue(vw.getJointWeights() == null);
       assertTrue(vw.getJCounts() == null);
    } 

     @Test
    public void xmlTest0() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("vertexweights0.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       VertexWeights vw1 = new VertexWeights(tokenizer);
       String  encoding = vw1.toXMLString();
       //System.out.println("vw1:\n" + encoding);
       XMLTokenizer tokenizer2 = new XMLTokenizer(encoding);
       VertexWeights vw2 = new VertexWeights(tokenizer2);
       assertTrue(vw2.getJointIndices() != null);
       assertTrue(vw2.getJointIndices().length == 0);
       assertTrue(vw2.getJointWeights() != null);
       assertTrue(vw2.getJointWeights().length == 0);
       assertTrue(vw2.getJCounts() != null);
       assertTrue(vw2.getJCounts().length == 0);
    }
  
  
    @Test
    public void xmlTest1() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("vertexweights1.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       VertexWeights vw1 = new VertexWeights(tokenizer);
       String  encoding = vw1.toXMLString();
       //System.out.println("vw1:\n" + encoding);
       XMLTokenizer tokenizer2 = new XMLTokenizer(encoding);
       VertexWeights vw2 = new VertexWeights(tokenizer2);
       assertTrue(vw2.getJointIndices() != null);
       assertTrue(vw2.getJointIndices().length == 72);
       assertTrue(vw2.getJointWeights() != null);
       assertTrue(vw2.getJointWeights().length == 72);
       assertTrue(vw2.getJCounts() != null);
       assertTrue(vw2.getJCounts().length == 52);
    }
  
  
    @Test
    public void binaryTest0() throws IOException {       
       VertexWeights vw1 = new VertexWeights();
       String tmpdir = System.getProperty("java.io.tmpdir");
       DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir+"/vwbintest.dat")));
       vw1.writeBinary(dataOut);
       dataOut.close();
       DataInputStream dataIn = new DataInputStream(new BufferedInputStream(new FileInputStream(tmpdir+"/vwbintest.dat")));
       VertexWeights vw2 = new VertexWeights();
       vw2.readBinary(dataIn);
       dataIn.close();
       
       assertTrue(vw2.getJointIndices() == null);
       assertTrue(vw2.getJointWeights() == null);
       assertTrue(vw2.getJCounts() == null);
    } 
  
    @Test
    public void binaryTest1() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("vertexweights1.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       VertexWeights vw1 = new VertexWeights(tokenizer);
    
       String tmpdir = System.getProperty("java.io.tmpdir");
       DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir+"/vwbintest.dat")));
       vw1.writeBinary(dataOut);
       dataOut.close();
       DataInputStream dataIn = new DataInputStream(new BufferedInputStream(new FileInputStream(tmpdir+"/vwbintest.dat")));
       VertexWeights vw2 = new VertexWeights();
       vw2.readBinary(dataIn);
       dataIn.close();
     
       assertTrue(vw2.getJointIndices() != null);
       assertTrue(vw2.getJointIndices().length == 72);
       assertTrue(vw2.getJointWeights() != null);
       assertTrue(vw2.getJointWeights().length == 72);
       assertTrue(vw2.getJCounts() != null);
       assertTrue(vw2.getJCounts().length == 52);
    }
  
}

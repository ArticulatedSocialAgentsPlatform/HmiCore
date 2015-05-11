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

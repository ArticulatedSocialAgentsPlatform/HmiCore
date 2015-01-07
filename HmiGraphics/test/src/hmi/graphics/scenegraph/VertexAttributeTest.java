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
 * VertexAttribute JUnit test
 */

package hmi.graphics.scenegraph;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.*;
import hmi.util.*;
import java.io.*;
import hmi.xml.*;

/**
 * JUnit test for hmi.graphics.scenegraph.VertexAttribute
 */
public class VertexAttributeTest {
    
    public VertexAttributeTest() {
    }

    @Before
    public void setUp()  { // common initialization, executed for every test.
    }

    @After
    public void tearDown() {
    }

    @Test
    public void basics() {       
       VertexAttribute va = new VertexAttribute();
       assertTrue(va.getName() == "");
       assertTrue(va.getAttributeValueSize() == -1);
       assertTrue(va.getNrOfValues() == -1);
       assertTrue(va.getVertexDataSize() == -1);
       assertTrue( ! va.hasIndex() );
       assertTrue(va.getIndexData() == null);
       assertTrue(va.getNrOfIndices() == -1);
    } 


    @Test
    public void xmlTest2() throws IOException {     
       VertexAttribute va0 = new VertexAttribute();
       Resources res = new Resources("scenegraph");
       Reader vareader = res.getReader("vertexattribute.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(vareader);
       VertexAttribute va = new VertexAttribute(tokenizer);
       String  encoding = va.toXMLString();
       //System.out.println("va:\n" + encoding);
       XMLTokenizer tokenizer2 = new XMLTokenizer(encoding);
       VertexAttribute mcPos = new VertexAttribute(tokenizer2);
       //System.out.println("gmeshdecoded:\n" + gmesh2);
       assertTrue(mcPos.getName() == "mcPosition");
       
       String diff1 = va.showDiff(va0);
      // System.out.println("diff1=" + diff1);
       assertTrue(diff1 != "");
       
       va0.setName(va.getName());
       String diff2 = va.showDiff(va0);
      // System.out.println("diff2=" + diff2);
        assertTrue(diff2 != "");
       
       
       assertTrue(mcPos.getAttributeValueSize() == 3);
       assertTrue(mcPos.getNrOfValues() == 16);
       assertTrue(mcPos.getVertexDataSize() == 48);
       assertTrue( ! mcPos.hasIndex() );
       assertTrue(mcPos.getIndexData() == null);
       assertTrue(mcPos.getNrOfIndices() == -1);
    } 


    @Test
    public void binaryTest() throws IOException {
       Resources res = new Resources("scenegraph");
       Reader vareader = res.getReader("vertexattribute.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(vareader);
       VertexAttribute va = new VertexAttribute(tokenizer);
    
       String tmpdir = System.getProperty("java.io.tmpdir");
     
    //   System.out.println("userdir=" + userdir + " tmpdir=" + tmpdir);
       DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir+"/vabintest.dat")));
       va.writeBinary(dataOut);
       dataOut.close();
       DataInputStream dataIn = new DataInputStream(new BufferedInputStream(new FileInputStream(tmpdir+"/vabintest.dat")));
       VertexAttribute vain = new VertexAttribute();
       vain.readBinary(dataIn);
       dataIn.close();
       
     //  System.out.println("vain.getName()=" + vain.getName());
       assertTrue(vain.getName() == "mcPosition");
       assertTrue(vain.getAttributeValueSize() == 3);
       assertTrue(vain.getNrOfValues() == 16);
       assertTrue(vain.getVertexDataSize() == 48);
       assertTrue(vain.getNrOfIndices() == -1);
      // System.out.println("binaryDecoded: " + vain);
       
       
    }
  
}

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
 * GScene JUnit test
 */

package hmi.graphics.scenegraph;

import static org.junit.Assert.*;
import org.junit.*;
import hmi.util.*;
import hmi.xml.*;
import java.io.*;

/**
 * JUnit test for hmi.graphics.scenegraph.GScene
 */
public class GSceneTest {
    
    public GSceneTest() {
    }

    @Before
    public void setUp()  { // common initialization, executed for every test.
    }

    @After
    public void tearDown() {
    }

    @Test
    public void basics() {       
       GScene gscene = new GScene("dummy");
       assertTrue(gscene.getRootNodes().isEmpty());
       assertTrue(gscene.getSkeletonRoots().isEmpty());
       assertTrue(gscene.getSkinnedMeshes().isEmpty());
    } 

    @Test
    public void xmlTest1() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader basictest1 = res.getReader("gscene1.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(basictest1);
       GScene gscene1 = new GScene(tokenizer);
       String  encoding1 = gscene1.toXMLString();
//       System.out.println("xmlTest1:\n" + encoding1);
       tokenizer = new XMLTokenizer(encoding1);
       GScene gscene2 = new GScene(tokenizer);
       String  encoding2 = gscene2.toXMLString();
       assertTrue(encoding1.equals(encoding2));
       
    } 
  
  
  
    @Test
    public void xmlTest2() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader treetest1 = res.getReader("gscene3.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(treetest1);
       GScene gscene3 = new GScene(tokenizer);
       //hmi.util.Console.delay(6000);
       
       assertTrue(gscene3.getRootNodes() != null);
       assertTrue( ! gscene3.getRootNodes().isEmpty());
       
       assertTrue(gscene3.getSkeletonRoots() != null);
       assertTrue(gscene3.getSkeletonRoots().isEmpty());
       assertTrue(gscene3.getSkinnedMeshes() != null);
       assertTrue(gscene3.getSkinnedMeshes().isEmpty());
       
       gscene3.collectSkinnedMeshes();
       assertTrue( ! gscene3.getSkinnedMeshes().isEmpty());
       assertTrue( gscene3.getSkinnedMeshes().size() == 1);
       
       assertTrue(gscene3.getSkeletonRoots().isEmpty());
     
       gscene3.resolveSkinnedMeshJoints();
       assertTrue(gscene3.getSkeletonRoots().isEmpty());
       
       String  encoding = gscene3.toXMLString();
       //System.out.println("gscene3:\n" + encoding);
      
    } 
  
}

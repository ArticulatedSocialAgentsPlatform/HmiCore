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
 * GNode JUnit test
 */

package hmi.graphics.scenegraph;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.*;
import hmi.util.*;
import hmi.xml.*;
import hmi.math.*;
import java.io.*;

/**
 * JUnit test for hmi.graphics.scenegraph.GNode
 */
public class GNodeTest {
    
    public GNodeTest() {
    }

    @Before
    public void setUp()  { // common initialization, executed for every test.
    }

    @After
    public void tearDown() {
    }


    GNode gnode = new GNode("N");
    GNode gnode1 = new GNode("N1");
    GNode gnode2 = new GNode("N2");
    GNode gnode3 = new GNode("N3");
         
    GNode gnode11 = new GNode("N11");
    GNode gnode12 = new GNode("N12");
    GNode gnode13 = new GNode("N13");
         
    GNode gnode21 = new GNode("N21");
    GNode gnode22 = new GNode("N22");
    GNode gnode23 = new GNode("N23");
         
    GNode gnode111 = new GNode("N111");
    GNode gnode112 = new GNode("N112");
    GNode gnode113 = new GNode("N113");
         
    GNode gnode211 = new GNode("N211");
    GNode gnode212 = new GNode("N212");
    GNode gnode213 = new GNode("N213");

    /* build a simple proper tree, without "instancing" or "dag links"  */
    private void buildTree1() {
       gnode.addChildNode(gnode1); 
       gnode.addChildNode(gnode2); 
       gnode.addChildNode(gnode3); 
      
       gnode1.addChildNode(gnode11);
       gnode1.addChildNode(gnode12);
       gnode1.addChildNode(gnode13);
   
       gnode2.addChildNode(gnode21);
       gnode2.addChildNode(gnode22);
       gnode2.addChildNode(gnode23);
    
       gnode11.addChildNode(gnode111);
       gnode11.addChildNode(gnode112);
       gnode11.addChildNode(gnode113);
     
       gnode21.addChildNode(gnode211);
       gnode21.addChildNode(gnode212);
       gnode21.addChildNode(gnode213);     
    }

  
  
    @Test
    /* test basic attributes */
    public void basics() {       
       GNode gnod = new GNode();
       assertTrue(gnod.getId() == null);
       assertTrue(gnod.getSid() == null);
       assertTrue(gnod.getName() == null);
       assertTrue(gnod.getType() == null);
       assertTrue(gnod.getParent() == null);
       assertTrue(gnod.getChildNodes() != null);
       assertTrue(gnod.getChildNodes().isEmpty());
       assertTrue(gnod.getGShapes().isEmpty());
       assertTrue(gnod.getVJoint() != null);
    } 
  
    @Test
    /* test set/get Id */
    public void testSetGetId() { 
       GNode gnode1 = new GNode();
       GNode gnode2 = new GNode("GN");
       gnode1.setId("GN");
       assertTrue(gnode1.getId().equals("GN")); 
       assertTrue(gnode1.getId() == "GN"); // id's are interned, so this is ok
       assertTrue(gnode2.getId().equals("GN")); 
       assertTrue(gnode2.getId() == "GN"); // id's are interned, so this is ok
       assertTrue(gnode1.getId() == gnode2.getId());
       
       assertTrue(gnode1.getSid() == null);  // setting id's does not auto-set sid or name
       assertTrue(gnode1.getName() == null);
       assertTrue(gnode2.getSid() == null);
       assertTrue(gnode2.getName() == null);
       
       gnode1.setSid("S1");     // set sid and name, and test using getSid, getName
       gnode1.setName("GNOD1");
       assertTrue(gnode1.getId() == "GN"); // not changed
       assertTrue(gnode1.getSid() == "S1");
       assertTrue(gnode1.getName() == "GNOD1");
    } 
      
  
    @Test
    /* test getId for interned/non-interned Strings */
    public void basicTree1() {       
       assertTrue(gnode.getId() == "N");
       assertTrue(gnode.getSid() == null);
       assertTrue(gnode.getName() == null);
       assertTrue(gnode.getType() == null);
       
       assertTrue(gnode1.getId() == "N1");
       assertTrue(gnode2.getId() == "N2");
       assertTrue(gnode3.getId() == "N3");
       
       // GNode id's are interned Strings, and can be compared to other interned String by means of ==
       String n2 = "N2";
       String suffix13 = "13";
       String node213 = n2 + suffix13;                // node213 is not an interned String
       String node213intern = node213.intern();       // node213intern is equal to node213, but also interned
       assertFalse(gnode213.getId() == node213);      // id's cannot compared to non-interned String by means of ==
       assertTrue(gnode213.getId().equals(node213));  // of course, equals always works
       assertTrue(gnode213.getId() == node213intern); // but if it *is* interned, == can be used as well. 
    } 
    
    @Test
    /* Test the straightforward building of trees, by means of addChildNode, check by means of getParent */
    public void basicTree2() { 
      
       buildTree1();      
       assertTrue(gnode.getParent() == null);
       assertTrue(gnode1.getParent() == gnode);
       assertTrue(gnode2.getParent() == gnode);
       assertTrue(gnode3.getParent() == gnode);
       assertTrue(gnode11.getParent() == gnode1);
       assertTrue(gnode12.getParent() == gnode1);
       assertTrue(gnode13.getParent() == gnode1);
       assertTrue(gnode21.getParent() == gnode2);
       assertTrue(gnode22.getParent() == gnode2);
       assertTrue(gnode23.getParent() == gnode2);
       assertTrue(gnode111.getParent() == gnode11);
       assertTrue(gnode112.getParent() == gnode11);
       assertTrue(gnode113.getParent() == gnode11);
       assertTrue(gnode211.getParent() == gnode21);
       assertTrue(gnode212.getParent() == gnode21);
       assertTrue(gnode213.getParent() == gnode21);
       
       assertTrue(gnode213.getParent().getParent() == gnode2);
       assertTrue(gnode213.getParent().getParent().getParent() == gnode);
       assertTrue(gnode213.getParent().getParent().getParent().getParent() == null);
    } 
    
    
    @Test
    /* Test the getChildNodes method  */
    public void getChildNodesTest() {  
       buildTree1();
       ArrayList<GNode> expectedchildren1 = new ArrayList<GNode>();
       expectedchildren1.add(gnode11);
       expectedchildren1.add(gnode12);
       expectedchildren1.add(gnode13);
       assertTrue(gnode1.getChildNodes().equals(expectedchildren1));
       
       ArrayList<GNode> expectedchildren21 = new ArrayList<GNode>();
       expectedchildren21.add(gnode211);
       expectedchildren21.add(gnode212);
       expectedchildren21.add(gnode213);
       assertTrue(gnode21.getChildNodes().equals(expectedchildren21));
    }  
    
    @Test
    public void xmlTest0() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader basictest0 = res.getReader("gnode0.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(basictest0);
       GNode gnode0 = new GNode(tokenizer);
       assertTrue(gnode0.getId() == null);
       assertTrue(gnode0.getSid() == null);
       assertTrue(gnode0.getName() == null);
       assertTrue(gnode0.getType() == null);
       
       String  encoding0 = gnode0.toXMLString();
       //System.out.println("GNode.xmlTest0:\n" + encoding0);
       assertTrue(encoding0.equals("<gnode translation=\"0.0 0.0 0.0\" rotation=\"1.0 0.0 0.0 0.0\" scale=\"1.0 1.0 1.0\">\n</gnode>"));   
    } 
    
    @Test
    public void xmlTest1() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader basictest1 = res.getReader("gnode1.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(basictest1);
       GNode gnode1 = new GNode(tokenizer);
       assertTrue(gnode1.getId() == "gnod1");
       assertTrue(gnode1.getSid() == "nod1");
       assertTrue(gnode1.getName() == "First GNode");
       assertTrue(gnode1.getType() == null);
       
       String  encoding1 = gnode1.toXMLString();
      // System.out.println("GNode.xmlTest1:\n" + encoding1);
       tokenizer = new XMLTokenizer(encoding1);
       GNode gnode2 = new GNode(tokenizer);
       String  encoding2 = gnode2.toXMLString();
       assertTrue(encoding1.equals(encoding2));
    } 
    
    @Test
    public void xmlTest2() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader basictest1 = res.getReader("gnode2.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(basictest1);
       GNode gnode2 = new GNode(tokenizer);
       assertTrue(gnode2.getId() == "gnod2");
       assertTrue(gnode2.getSid() == null);
       assertTrue(gnode2.getName() == null);
       assertTrue(gnode2.getType() == "Joint");
       
       String  encoding2 = gnode2.toXMLString();
      // System.out.println("GNode.xmlTest2:\n" + encoding2);
       tokenizer = new XMLTokenizer(encoding2);
       GNode gnode22 = new GNode(tokenizer);
       String  encoding22 = gnode22.toXMLString();
       assertTrue(encoding2.equals(encoding22));
    } 
    
    @Test
    public void xmlTreeTest1() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader treetest1 = res.getReader("gnodetree1.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(treetest1);
       GNode gnodetree = new GNode(tokenizer);
       assertTrue(gnodetree.getId()   == "gnod");
       assertTrue(gnodetree.getSid()  == null);
       assertTrue(gnodetree.getName() == null);
       assertTrue(gnodetree.getType() == null);
       
       String  encoding = gnodetree.toXMLString();
       //System.out.println("GNode.xmlTreeTest1:\n" + encoding); 
    } 
  
    @Test
    public void xmlTreeTest2() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("gnodetree2.xml");
       XMLTokenizer tokenizer1 = new XMLTokenizer(reader);
       GNode gnodetree1 = new GNode(tokenizer1);
       
       String  encoding = gnodetree1.toXMLString();
       XMLTokenizer tokenizer2 = new XMLTokenizer(encoding);
       GNode gnodetree2 = new GNode(tokenizer2);
       assertTrue(gnodetree2.getId()   == "gnod");
       GNode gnod = gnodetree2;
       GNode gnod22 = gnod.getPartById("gnod22");
       assertTrue(gnod22 != null);
       assertTrue(gnod22.getSid() == "b");
       GShape gshape22 = gnod22.getGShapes().get(0);
       assertTrue(gshape22.getName() == "shape22");
       GMaterial gmat = gshape22.getGMaterial();
       assertTrue(gmat != null);
       assertTrue(gmat.getName() == "coinoeil");
       assertTrue(gshape22.getGMesh() == null);
       //System.out.println("GNode.xmlTreeTest2:\n" + encoding);
    } 


    @Test
    public void xmlTreeTest3() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("gnodetree3.xml");
       XMLTokenizer tokenizer1 = new XMLTokenizer(reader);
       GNode gnodetree1 = new GNode(tokenizer1);
       String  encoding = gnodetree1.toXMLString();
       XMLTokenizer tokenizer2 = new XMLTokenizer(encoding);
       GNode gnodetree2 = new GNode(tokenizer2);
       //System.out.println("GNode.xmlTreeTest3:\n" + encoding);
       
       assertTrue(gnodetree2.getId()   == "gnod");
       GNode gnod = gnodetree2;
       
       GNode gnod1 = gnod.getPartById("gnod1");
       GNode gnod11 = gnod.getPartById("gnod11");
       GNode gnod2 = gnod.getPartById("gnod2");
       GNode gnod22 = gnod.getPartById("gnod22");
       assertTrue(gnod1 != null);
       assertTrue(gnod11 != null);
       assertTrue(gnod2 != null);
       assertTrue(gnod22 != null);
       
       float[] tr1 = gnod1.getTranslation();
       float[] tr11 = gnod11.getTranslation();
       float[] tr2 = gnod2.getTranslation();
       float[] tr22 = gnod22.getTranslation();
       float eps = 0.001f;
       assertTrue(Vec3f.epsilonEquals(tr1,  new float[]{0f, 0f, 0f}, eps));
       //System.out.println("tr11=" + Vec3f.toString(tr11));
       assertTrue(Vec3f.epsilonEquals(tr11, new float[]{0f, 0.09399916f, 0.011828302f}, eps));
       assertTrue(Vec3f.epsilonEquals(tr2,  new float[]{0.00833803f, -0.07531502f, 0.025381647f}, eps));
       assertTrue(Vec3f.epsilonEquals(tr22, new float[]{0.00833803f, -0.07531502f, 0.025381647f,}, eps));
       
       float[] rot2 = gnod2.getRotation();
       assertTrue(Quat4f.epsilonEquals(rot2, new float[]{0f, 0f, 1f, 0f}, eps));
       float[] scale2 = gnod2.getScale();
       assertTrue(Vec3f.epsilonEquals(scale2,  new float[]{1f, 2f, 3f}, eps));
      
    } 

    @Test
    public void diffTest2() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader1 = res.getReader("gnodetree2.xml");
       XMLTokenizer tokenizer1 = new XMLTokenizer(reader1);
       GNode gnodetree1 = new GNode(tokenizer1);
       Reader reader2 = res.getReader("gnodetree2-diff.xml");
       XMLTokenizer tokenizer2 = new XMLTokenizer(reader2);
       GNode gnodetree2 = new GNode(tokenizer2);
       
       String diff = gnodetree1.showDiff(gnodetree2);
       //System.out.println("diff: " + diff);
       assertTrue(diff != "");
       
    } 




    @Test
    public void binaryTest2() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("gnodetree2.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       GNode gnod1 = new GNode(tokenizer);
       assertTrue(gnod1.getId()== "gnod");
       
       String tmpdir = System.getProperty("java.io.tmpdir");
       DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir+"/gnodebintest.dat")));
       gnod1.writeBinary(dataOut);
       dataOut.close();
       DataInputStream dataIn = new DataInputStream(new BufferedInputStream(new FileInputStream(tmpdir+"/gnodebintest.dat")));
       GNode gnod2 = new GNode();
       gnod2.readBinary(dataIn);
       dataIn.close();
       
     //  System.out.println("gnodedecoded:\n" + gnod2);
       assertTrue(gnod2.getId()== "gnod");
       assertTrue(gnod2.getSid()== null);
       assertTrue(gnod2.getName()== null);
       
       
       GNode gnod22 = gnod2.getPartById("gnod22");
       assertTrue(gnod22 != null);
       assertTrue(gnod22.getSid() == "b");
       GShape gshape22 = gnod22.getGShapes().get(0);
       assertTrue(gshape22.getName().equals("shape22"));
       assertTrue(gshape22.getName() == "shape22");
       GMaterial gmat = gshape22.getGMaterial();
       assertTrue(gmat != null);
       assertTrue(gmat.getName() == "coinoeil");
       assertTrue(gshape22.getGMesh() == null);
    }
 
    @Test
    public void binaryTest3() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("gnodetree3.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       GNode gnodtree = new GNode(tokenizer);
       assertTrue(gnodtree.getId()== "gnod");
       
       String tmpdir = System.getProperty("java.io.tmpdir");
       DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir+"/gnodebintest.dat")));
       gnodtree.writeBinary(dataOut);
       dataOut.close();
       DataInputStream dataIn = new DataInputStream(new BufferedInputStream(new FileInputStream(tmpdir+"/gnodebintest.dat")));
       GNode gnod = new GNode();
       gnod.readBinary(dataIn);
       dataIn.close();
       
       GNode gnod1 = gnod.getPartById("gnod1");
       GNode gnod11 = gnod.getPartById("gnod11");
       GNode gnod2 = gnod.getPartById("gnod2");
       GNode gnod22 = gnod.getPartById("gnod22");
       assertTrue(gnod1 != null);
       assertTrue(gnod11 != null);
       assertTrue(gnod2 != null);
       assertTrue(gnod22 != null);
       
       float[] tr1 = gnod1.getTranslation();
       float[] tr11 = gnod11.getTranslation();
       float[] tr2 = gnod2.getTranslation();
       float[] tr22 = gnod22.getTranslation();
       float eps = 0.001f;
       assertTrue(Vec3f.epsilonEquals(tr1,  new float[]{0f, 0f, 0f}, eps));
       //System.out.println("tr11=" + Vec3f.toString(tr11));
       assertTrue(Vec3f.epsilonEquals(tr11, new float[]{0f, 0.09399916f, 0.011828302f}, eps));
       assertTrue(Vec3f.epsilonEquals(tr2,  new float[]{0.00833803f, -0.07531502f, 0.025381647f}, eps));
       assertTrue(Vec3f.epsilonEquals(tr22, new float[]{0.00833803f, -0.07531502f, 0.025381647f,}, eps));
       
       float[] rot2 = gnod2.getRotation();
       assertTrue(Quat4f.epsilonEquals(rot2, new float[]{0f, 0f, 1f, 0f}, eps));
       float[] scale2 = gnod2.getScale();
       assertTrue(Vec3f.epsilonEquals(scale2,  new float[]{1f, 2f, 3f}, eps));
    }
         
}



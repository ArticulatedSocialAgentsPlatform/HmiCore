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
 * GShape JUnit test
 */

package hmi.graphics.scenegraph;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.*;
import hmi.util.*;
import hmi.xml.*;
import java.io.*;

/**
 * JUnit test for hmi.graphics.scenegraph.GShape
 */
public class GShapeTest {
    
    public GShapeTest() {
    }

    @Before
    public void setUp()  { // common initialization, executed for every test.
    }

    @After
    public void tearDown() {
    }

    @Test
    public void basics() {       
       GShape gshape = new GShape();
       assertTrue(gshape.getName() == "");
       assertTrue(gshape.getGMesh() == null);
       assertTrue(gshape.getGMaterial() == null);
    } 

    @Test
    public void xmlTest2() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("gshape2.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       GShape gshape1 = new GShape(tokenizer);
       String  encoding = gshape1.toXMLString();
       //System.out.println("gshape1:\n" + encoding);
       XMLTokenizer tokenizer2 = new XMLTokenizer(encoding);
       GShape gshape2 = new GShape(tokenizer2);
       //System.out.println("gshapedecoded:\n" + gshape2);
     
       assertTrue(gshape2.getName() == "ey02-mesh");
       GMesh gm2 = gshape2.getGMesh();
       assertTrue(gm2 != null);
     
      // Generic GMesh tests:
       assertTrue(gm2.getId().equals("ey02-mesh"));
       assertTrue(gm2.getId() == "ey02-mesh");  // id is assumed to be interned
       assertTrue(gm2.getMeshType() == GMesh.MeshType.Triangles);
       assertTrue(gm2.getNrOfVertices() == 16);
       assertTrue(gm2.getIndexData() != null);
       assertTrue(gm2.getIndexData().length == 66);
       assertTrue(gm2.getNrOfAttributes() == 3);
       assertTrue(gm2.getVertexAttributeList().size() == 3);
       assertTrue(gm2.getMorphTargets() == null);
       assertTrue(gm2.hasUnifiedIndexData() );  
       assertTrue(gm2.getVertexAttribute("mcPosition") != null);
       assertTrue(gm2.getVertexAttribute("mcNormal") != null);
       
       
       // Generic GMaterial tests:
       GMaterial gmat2 = gshape2.getGMaterial();
       assertTrue(gmat2 != null);
       assertTrue(gmat2.getName() == "coinoeil");
       assertTrue(gmat2.getShader() == "blinnTextured1");
       
       assertTrue(gmat2.getDiffuseColor()[0] == 0.0f);
       assertTrue(gmat2.getDiffuseColor()[3] == 1.0f);
       
    }

    @Test
    public void xmlTest3() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("gshape3.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       GShape gshape1 = new GShape(tokenizer);
       String  encoding = gshape1.toXMLString();
       //System.out.println("gshape1:\n" + encoding);
       XMLTokenizer tokenizer2 = new XMLTokenizer(encoding);
       GShape gshape2 = new GShape(tokenizer2);
       //System.out.println("gshapedecoded:\n" + gshape2);
     
       assertTrue(gshape2.getName() == "ey02-mesh");
       GSkinnedMesh gsm2 = (GSkinnedMesh) gshape2.getGMesh();
       assertTrue(gsm2 != null);
     
      // Generic GMesh tests:
       assertTrue(gsm2.getId().equals("ey02-mesh"));
       assertTrue(gsm2.getId() == "ey02-mesh");  // id is assumed to be interned
       assertTrue(gsm2.getMeshType() == GMesh.MeshType.Triangles);
       assertTrue(gsm2.getNrOfVertices() == 16);
       assertTrue(gsm2.getIndexData() != null);
       assertTrue(gsm2.getIndexData().length == 66);
       assertTrue(gsm2.getNrOfAttributes() == 3);
       assertTrue(gsm2.getVertexAttributeList().size() == 3);
       assertTrue(gsm2.getMorphTargets() == null);
       assertTrue(gsm2.hasUnifiedIndexData() );  
       assertTrue(gsm2.getVertexAttribute("mcPosition") != null);
       assertTrue(gsm2.getVertexAttribute("mcNormal") != null);
       
       
       // Generic GMaterial tests:
       GMaterial gmat2 = gshape2.getGMaterial();
       assertTrue(gmat2 != null);
       assertTrue(gmat2.getName() == "coinoeil");
       assertTrue(gmat2.getShader() == "blinnTextured1");
       
       assertTrue(gmat2.getDiffuseColor()[0] == 0.0f);
       assertTrue(gmat2.getDiffuseColor()[3] == 1.0f);
       
       
        // GSkinnedMesh specific tests:
       
       assertTrue(gsm2.getSkeletonIds() != null);
       assertTrue(gsm2.getSkeletonIds().length == 1);
       assertTrue(gsm2.getSkeletonIds()[0].equals("Bip01_Bassin-node"));
       assertTrue(gsm2.getJointSIDs() != null);
       assertTrue(gsm2.getJointSIDs().length == 100);
       assertTrue(gsm2.getJointSIDs()[0].equals("HumanoidRoot"));
       assertTrue(gsm2.getJointNames().length == 100);
       assertTrue(gsm2.getJointNames()[0].equals("Bip01_Bassin"));
       
       assertTrue(gsm2.getParentIndex() != null);
       assertTrue(gsm2.getParentIndex().length == 100);
       
       assertTrue(gsm2.getInvBindMatrices() == null);
//       assertTrue(gsm2.getInvBindMatrices().length == 100);
//       for (int i=0; i<100; i++) assertTrue(gsm2.getInvBindMatrices()[i].length == 16);
       
       VertexWeights vw2 = gsm2.getVertexWeights();
       assertTrue(vw2 != null);
       
       assertTrue(vw2.getJointIndices() != null);
       assertTrue(vw2.getJointIndices().length == 72);
       assertTrue(vw2.getJointWeights() != null);
       assertTrue(vw2.getJointWeights().length == 72);
       assertTrue(vw2.getJCounts() != null);
       assertTrue(vw2.getJCounts().length == 52);
       
       
       
    }


    @Test
    public void binaryTest2() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("gshape2.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       GShape gshape1 = new GShape(tokenizer);
       
       String tmpdir = System.getProperty("java.io.tmpdir");
       DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir+"/gshapebintest.dat")));
       gshape1.writeBinary(dataOut);
       dataOut.close();
       DataInputStream dataIn = new DataInputStream(new BufferedInputStream(new FileInputStream(tmpdir+"/gshapebintest.dat")));
       GShape gshape2 = new GShape();
       gshape2.readBinary(dataIn);
       dataIn.close();
       
     //  System.out.println("gshapedecoded:\n" + gshape2);
       assertTrue(gshape2.getName().equals("ey02-mesh"));
       
       GMesh gm2 = gshape2.getGMesh();
       assertTrue(gm2 != null);
     
      // Generic GMesh tests:
       assertTrue(gm2.getId().equals("ey02-mesh"));
       assertTrue(gm2.getId() == "ey02-mesh");  // id is assumed to be interned
       assertTrue(gm2.getMeshType() == GMesh.MeshType.Triangles);
       assertTrue(gm2.getNrOfVertices() == 16);
       assertTrue(gm2.getIndexData() != null);
       assertTrue(gm2.getIndexData().length == 66);
       assertTrue(gm2.getNrOfAttributes() == 3);
       assertTrue(gm2.getVertexAttributeList().size() == 3);
       assertTrue(gm2.getMorphTargets() == null);
       assertTrue(gm2.hasUnifiedIndexData() );  
       assertTrue(gm2.getVertexAttribute("mcPosition") != null);
       assertTrue(gm2.getVertexAttribute("mcNormal") != null);
       
       
       // Generic GMaterial tests:
       GMaterial gmat2 = gshape2.getGMaterial();
       assertTrue(gmat2 != null);
       assertTrue(gmat2.getName() == "coinoeil");
       assertTrue(gmat2.getShader() == "blinnTextured1");
       
       assertTrue(gmat2.getDiffuseColor()[0] == 0.0f);
       assertTrue(gmat2.getDiffuseColor()[3] == 1.0f);
       
   }
   
    @Test
    public void binaryTest3() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("gshape3.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       GShape gshape1 = new GShape(tokenizer);
       
       String tmpdir = System.getProperty("java.io.tmpdir");
       DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir+"/gshapebintest.dat")));
       gshape1.writeBinary(dataOut);
       dataOut.close();
       DataInputStream dataIn = new DataInputStream(new BufferedInputStream(new FileInputStream(tmpdir+"/gshapebintest.dat")));
       GShape gshape2 = new GShape();
       gshape2.readBinary(dataIn);
       dataIn.close();
       
     //  System.out.println("gshapedecoded:\n" + gshape2);
       assertTrue(gshape2.getName().equals("ey02-mesh"));
       
       GSkinnedMesh gsm2 = (GSkinnedMesh) gshape2.getGMesh();
       assertTrue(gsm2 != null);
     
      // Generic GMesh tests:
       assertTrue(gsm2.getId().equals("ey02-mesh"));
       assertTrue(gsm2.getId() == "ey02-mesh");  // id is assumed to be interned
       assertTrue(gsm2.getMeshType() == GMesh.MeshType.Triangles);
       assertTrue(gsm2.getNrOfVertices() == 16);
       assertTrue(gsm2.getIndexData() != null);
       assertTrue(gsm2.getIndexData().length == 66);
       assertTrue(gsm2.getNrOfAttributes() == 3);
       assertTrue(gsm2.getVertexAttributeList().size() == 3);
       assertTrue(gsm2.getMorphTargets() == null);
       assertTrue(gsm2.hasUnifiedIndexData() );  
       assertTrue(gsm2.getVertexAttribute("mcPosition") != null);
       assertTrue(gsm2.getVertexAttribute("mcNormal") != null);
       
       // Generic GMaterial tests:
       GMaterial gmat2 = gshape2.getGMaterial();
       assertTrue(gmat2 != null);
       assertTrue(gmat2.getName() == "coinoeil");
       assertTrue(gmat2.getShader() == "blinnTextured1");
       
       assertTrue(gmat2.getDiffuseColor()[0] == 0.0f);
       assertTrue(gmat2.getDiffuseColor()[3] == 1.0f);
       
         // GSkinnedMesh specific tests:
       
       assertTrue(gsm2.getSkeletonIds() != null);
       assertTrue(gsm2.getSkeletonIds().length == 1);
       assertTrue(gsm2.getSkeletonIds()[0].equals("Bip01_Bassin-node"));
       assertTrue(gsm2.getJointSIDs() != null);
       assertTrue(gsm2.getJointSIDs().length == 100);
       assertTrue(gsm2.getJointSIDs()[0].equals("HumanoidRoot"));
       assertTrue(gsm2.getJointNames().length == 100);
       assertTrue(gsm2.getJointNames()[0].equals("Bip01_Bassin"));
       
       assertTrue(gsm2.getParentIndex() != null);
       assertTrue(gsm2.getParentIndex().length == 100);
       
       assertTrue(gsm2.getInvBindMatrices() == null);
//       assertTrue(gsm2.getInvBindMatrices().length == 100);
//       for (int i=0; i<100; i++) assertTrue(gsm2.getInvBindMatrices()[i].length == 16);
       
       VertexWeights vw2 = gsm2.getVertexWeights();
       assertTrue(vw2 != null);
       
       assertTrue(vw2.getJointIndices() != null);
       assertTrue(vw2.getJointIndices().length == 72);
       assertTrue(vw2.getJointWeights() != null);
       assertTrue(vw2.getJointWeights().length == 72);
       assertTrue(vw2.getJCounts() != null);
       assertTrue(vw2.getJCounts().length == 52);
     
   }
   
  
}

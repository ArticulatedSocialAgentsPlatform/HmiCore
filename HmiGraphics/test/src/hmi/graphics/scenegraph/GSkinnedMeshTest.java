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
 * GSkinnedMesh JUnit test
 */

package hmi.graphics.scenegraph;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.*;
import hmi.util.*;
import java.io.*;
import hmi.xml.*;

/**
 * JUnit test for hmi.graphics.scenegraph.GSkinnedMesh
 */
public class GSkinnedMeshTest {
    
    public GSkinnedMeshTest() {
    }

    @Before
    public void setUp()  { // common initialization, executed for every test.
    }

    @After
    public void tearDown() {
    }

    @Test
    public void basics() {       
       new GSkinnedMesh();
    } 


    @Test
    public void xmlTest2() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader gmeshreader = res.getReader("gskinnedmesh1.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(gmeshreader);
       GSkinnedMesh gsm1 = new GSkinnedMesh(tokenizer);
       String  encoding = gsm1.toXMLString();
       //System.out.println("gsm1:\n" + encoding);
       XMLTokenizer tokenizer2 = new XMLTokenizer(encoding);
       GSkinnedMesh gsm2 = new GSkinnedMesh(tokenizer2);
     //  System.out.println("gmeshdecoded:\n" + gsm2);
     
      // Genericc GMesh tests:
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
       VertexAttribute mcPos =  gsm2.getVertexAttribute("mcPosition") ;
       assertTrue(mcPos.getName() == "mcPosition");
       assertTrue(mcPos.getAttributeValueSize() == 3);
       assertTrue(mcPos.getNrOfValues() == 16);
       assertTrue(mcPos.getVertexDataSize() == 48);
       assertTrue( ! mcPos.hasIndex() );
       assertTrue(mcPos.getIndexData() == null);
       assertTrue(mcPos.getNrOfIndices() == -1);
       
       assertTrue(gsm2.getVertexAttribute("mcNormal") != null);
       
       assertTrue(gsm2.getVertexAttribute("texCoord1") != null);
       VertexAttribute texCoord1 =  gsm2.getVertexAttribute("texCoord1") ;
       assertTrue(texCoord1.getName() == "texCoord1");
       assertTrue(texCoord1.getAttributeValueSize() == 2);
       assertTrue(texCoord1.getNrOfValues() == 16);
       assertTrue(texCoord1.getVertexDataSize() == 32);
       assertTrue( ! texCoord1.hasIndex() );
       assertTrue(texCoord1.getIndexData() == null);
       assertTrue(texCoord1.getNrOfIndices() == -1);
       
       assertTrue(gsm2.getVertexAttribute("texCoord2") == null);
       
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
       
       assertTrue(gsm2.getInvBindMatrices() != null);
       assertTrue(gsm2.getInvBindMatrices().length == 100);
       for (int i=0; i<100; i++) assertTrue(gsm2.getInvBindMatrices()[i].length == 16);
       
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
    public void xmlTest3() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader gmeshreader = res.getReader("gskinnedmesh3.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(gmeshreader);
       GSkinnedMesh gsm1 = new GSkinnedMesh(tokenizer);
       String  encoding = gsm1.toXMLString();
       //System.out.println("gsm1:\n" + encoding);
       XMLTokenizer tokenizer2 = new XMLTokenizer(encoding);
       GSkinnedMesh gsm2 = new GSkinnedMesh(tokenizer2);
     //  System.out.println("gmeshdecoded:\n" + gsm2);
     
      // Genericc GMesh tests:
       assertTrue(gsm2.getId().equals("ey02-mesh"));
       assertTrue(gsm2.getId() == "ey02-mesh");  // id is assumed to be interned
       assertTrue(gsm2.getMeshType() == GMesh.MeshType.Triangles);
       assertTrue(gsm2.getNrOfVertices() == 16);
       assertTrue(gsm2.getIndexData() != null);
       assertTrue(gsm2.getIndexData().length == 66);
       assertTrue(gsm2.getNrOfAttributes() == 3);
       assertTrue(gsm2.getVertexAttributeList().size() == 3);
    
       assertTrue(gsm2.hasUnifiedIndexData() );  
       
       assertTrue(gsm2.getVertexAttribute("mcPosition") != null);
       VertexAttribute mcPos =  gsm2.getVertexAttribute("mcPosition") ;
       assertTrue(mcPos.getName() == "mcPosition");
       assertTrue(mcPos.getAttributeValueSize() == 3);
       assertTrue(mcPos.getNrOfValues() == 16);
       assertTrue(mcPos.getVertexDataSize() == 48);
       assertTrue( ! mcPos.hasIndex() );
       assertTrue(mcPos.getIndexData() == null);
       assertTrue(mcPos.getNrOfIndices() == -1);
       
       assertTrue(gsm2.getVertexAttribute("mcNormal") != null);
       
       assertTrue(gsm2.getVertexAttribute("texCoord1") != null);
       VertexAttribute texCoord1 =  gsm2.getVertexAttribute("texCoord1") ;
       assertTrue(texCoord1.getName() == "texCoord1");
       assertTrue(texCoord1.getAttributeValueSize() == 2);
       assertTrue(texCoord1.getNrOfValues() == 16);
       assertTrue(texCoord1.getVertexDataSize() == 32);
       assertTrue( ! texCoord1.hasIndex() );
       assertTrue(texCoord1.getIndexData() == null);
       assertTrue(texCoord1.getNrOfIndices() == -1);
       
       assertTrue(gsm2.getVertexAttribute("texCoord2") == null);
       
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
       
       assertTrue(gsm2.getInvBindMatrices() != null);
       assertTrue(gsm2.getInvBindMatrices().length == 100);
       for (int i=0; i<100; i++) assertTrue(gsm2.getInvBindMatrices()[i].length == 16);
       
       VertexWeights vw2 = gsm2.getVertexWeights();
       assertTrue(vw2 != null);
       
       assertTrue(vw2.getJointIndices() != null);
       assertTrue(vw2.getJointIndices().length == 72);
       assertTrue(vw2.getJointWeights() != null);
       assertTrue(vw2.getJointWeights().length == 72);
       assertTrue(vw2.getJCounts() != null);
       assertTrue(vw2.getJCounts().length == 52);
       
       // Morph data additions:
       assertTrue(gsm2.getMorphTargets() != null);
       assertTrue(gsm2.getMorphTargets().length == 3);
       float[][] morphData = gsm2.getMorphData("mcPosition");
       assertTrue(morphData != null);
       assertTrue(morphData.length == 3);
       assertTrue(morphData[0] != null);
       assertTrue(morphData[0].length == 3*16);
       assertTrue(morphData[1].length == 3*16);
       assertTrue(morphData[2].length == 3*16);
       
    } 
  
    @Test
    public void diffTest2() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader3 = res.getReader("gskinnedmesh3.xml");
       XMLTokenizer tokenizer3 = new XMLTokenizer(reader3);
       GSkinnedMesh gmesh3 = new GSkinnedMesh(tokenizer3);
    
       Reader reader3diff = res.getReader("gskinnedmesh3-diff.xml");
       XMLTokenizer tokenizer3diff = new XMLTokenizer(reader3diff);
       GSkinnedMesh gmesh3diff = new GSkinnedMesh(tokenizer3diff);
    
    
       String diff = gmesh3.showDiff(gmesh3diff);
       //System.out.println("diff=" + diff);
       assertTrue(diff != "");
      
       Reader reader3diffgmesh = res.getReader("gskinnedmesh3-diffgmesh.xml");
       XMLTokenizer tokenizer3diffgmesh = new XMLTokenizer(reader3diffgmesh);
       GSkinnedMesh gmesh3diffgmesh = new GSkinnedMesh(tokenizer3diffgmesh);
       String diff2 = gmesh3.showDiff(gmesh3diffgmesh);
       //System.out.println("diff2=" + diff2);
       assertTrue(diff2 != "");
    }
  
  
    @Test
    public void binaryTest1() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("gskinnedmesh1.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       GSkinnedMesh gsm1 = new GSkinnedMesh(tokenizer);
       String tmpdir = System.getProperty("java.io.tmpdir");
       DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir+"/gskinnedmeshbintest.dat")));
       gsm1.writeBinary(dataOut);
       dataOut.close();
       DataInputStream dataIn = new DataInputStream(new BufferedInputStream(new FileInputStream(tmpdir+"/gskinnedmeshbintest.dat")));
       GSkinnedMesh gsm2 = new GSkinnedMesh();
       gsm2.readBinary(dataIn);
       dataIn.close();
      // System.out.println("gskinnedmeshdecoded:\n" + gsm2);
       assertTrue(gsm2.getId().equals("ey02-mesh"));
       assertTrue(gsm2.getId() == "ey02-mesh");  // id is assumed to be interned
       assertTrue(gsm2.getMeshType() == GMesh.MeshType.Triangles);
       assertTrue(gsm2.getNrOfVertices() == 16);
       assertTrue(gsm2.getIndexData() != null);
       assertTrue(gsm2.getIndexData().length == 66);
       assertTrue(gsm2.getNrOfAttributes() == 3);
       assertTrue(gsm2.getVertexAttributeList().size() == 3);
      
       assertTrue(gsm2.hasUnifiedIndexData() );  
       
       assertTrue(gsm2.getVertexAttribute("mcPosition") != null);
       assertTrue(gsm2.getVertexAttribute("mcNormal") != null);
       
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
       
       assertTrue(gsm2.getInvBindMatrices() != null);
       assertTrue(gsm2.getInvBindMatrices().length == 100);
       for (int i=0; i<100; i++) assertTrue(gsm2.getInvBindMatrices()[i].length == 16);
       
       VertexWeights vw2 = gsm2.getVertexWeights();
       assertTrue(vw2 != null);
       
       assertTrue(vw2.getJointIndices() != null);
       assertTrue(vw2.getJointIndices().length == 72);
       assertTrue(vw2.getJointWeights() != null);
       assertTrue(vw2.getJointWeights().length == 72);
       assertTrue(vw2.getJCounts() != null);
       assertTrue(vw2.getJCounts().length == 52);
       
       
       
//       assertTrue(gsm2.getMorphTargets() != null);
//       assertTrue(gsm2.getMorphTargets().length == 3);
//       float[][] morphData = gsm2.getMorphData("mcPosition");
//       assertTrue(morphData != null);
//       assertTrue(morphData.length == 3);
//       assertTrue(morphData[0] != null);
//       assertTrue(morphData[0].length == 3*16);
//       assertTrue(morphData[1].length == 3*16);
//       assertTrue(morphData[2].length == 3*16);
        // System.out.println("gmeshdecoded:\n" + gm2);
       
    } 
  
  
    @Test(timeout=10000)
    public void binaryTest3() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("gskinnedmesh3.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       GSkinnedMesh gsm1 = new GSkinnedMesh(tokenizer);
       String tmpdir = System.getProperty("java.io.tmpdir");
       DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir+"/gskinnedmeshbintest.dat")));
       gsm1.writeBinary(dataOut);
       dataOut.close();
       DataInputStream dataIn = new DataInputStream(new BufferedInputStream(new FileInputStream(tmpdir+"/gskinnedmeshbintest.dat")));
       GSkinnedMesh gsm2 = new GSkinnedMesh();
       gsm2.readBinary(dataIn);
       dataIn.close();
      // System.out.println("gskinnedmeshdecoded:\n" + gsm2);
       assertTrue(gsm2.getId().equals("ey02-mesh"));
       assertTrue(gsm2.getId() == "ey02-mesh");  // id is assumed to be interned
       assertTrue(gsm2.getMeshType() == GMesh.MeshType.Triangles);
       assertTrue(gsm2.getNrOfVertices() == 16);
       assertTrue(gsm2.getIndexData() != null);
       assertTrue(gsm2.getIndexData().length == 66);
       assertTrue(gsm2.getNrOfAttributes() == 3);
       assertTrue(gsm2.getVertexAttributeList().size() == 3);
      
       assertTrue(gsm2.hasUnifiedIndexData() );  
       
       assertTrue(gsm2.getVertexAttribute("mcPosition") != null);
       assertTrue(gsm2.getVertexAttribute("mcNormal") != null);
       
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
       
       assertTrue(gsm2.getInvBindMatrices() != null);
       assertTrue(gsm2.getInvBindMatrices().length == 100);
       for (int i=0; i<100; i++) assertTrue(gsm2.getInvBindMatrices()[i].length == 16);
       
       VertexWeights vw2 = gsm2.getVertexWeights();
       assertTrue(vw2 != null);
       
       assertTrue(vw2.getJointIndices() != null);
       assertTrue(vw2.getJointIndices().length == 72);
       assertTrue(vw2.getJointWeights() != null);
       assertTrue(vw2.getJointWeights().length == 72);
       assertTrue(vw2.getJCounts() != null);
       assertTrue(vw2.getJCounts().length == 52);
       
       
       
       assertTrue(gsm2.getMorphTargets() != null);
       assertTrue(gsm2.getMorphTargets().length == 3);
       float[][] morphData = gsm2.getMorphData("mcPosition");
       assertTrue(morphData != null);
       assertTrue(morphData.length == 3);
       assertTrue(morphData[0] != null);
       assertTrue(morphData[0].length == 3*16);
       assertTrue(morphData[1].length == 3*16);
       assertTrue(morphData[2].length == 3*16);
       //  System.out.println("gmeshdecoded:\n" + gsm2);
       
    } 
  
  
}

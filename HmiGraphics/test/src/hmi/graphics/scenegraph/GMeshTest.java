/*
 * GMesh JUnit test
 */

package hmi.graphics.scenegraph;

import static org.junit.Assert.*;
import org.junit.*;
import java.util.*;
import hmi.util.*;
import java.io.*;
import hmi.xml.*;

/**
 * JUnit test for hmi.graphics.scenegraph.GMesh
 */
public class GMeshTest {
    
    public GMeshTest() {
    }

    @Before
    public void setUp()  { // common initialization, executed for every test.
    }

    @After
    public void tearDown() {
    }

    @Test
    public void basics() {       
       GMesh gmesh = new GMesh();
       assertTrue(gmesh.getId() == null);
       assertTrue(gmesh.getMeshType() == GMesh.MeshType.Undefined);
       
       // base mesh attribute list is defined and empty
       assertTrue(gmesh.getVertexAttribute("mcPosition") == null);
       assertTrue(gmesh.getVertexAttributeList() != null);
       assertTrue(gmesh.getVertexAttributeList().isEmpty());
       assertTrue(gmesh.getNrOfAttributes() == 0);
       
       // morph target lists are unallocated, i.e. null
       assertTrue(gmesh.getMorphTargets() == null);
       assertTrue(gmesh.getVertexAttributeList(1) == null);
       assertTrue(gmesh.getVertexAttribute(1, "mcPosition") == null);
       
       assertTrue(gmesh.hasUnifiedIndexData() );  // default setting, before setting indexed attribute data
       assertTrue(gmesh.getIndexData() == null );
       assertTrue(gmesh.getNrOfVertices() == -1); // no explicit unify yet
       
       gmesh.setId("mesh");
       assertTrue(gmesh.getId().equals("mesh"));
       
    } 

    
  
    @Test
    public void xmlTest2() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader gmeshreader = res.getReader("gmesh2.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(gmeshreader);
       GMesh gmeshread = new GMesh(tokenizer);
       String  encoding = gmeshread.toXMLString();
      // System.out.println("gmesh2:\n" + encoding);
       XMLTokenizer tokenizer2 = new XMLTokenizer(encoding);
       GMesh gmesh2 = new GMesh(tokenizer2);
     //  System.out.println("gmeshdecoded:\n" + gmesh2);
       assertTrue(gmesh2.getId().equals("ey02-mesh"));
       assertTrue(gmesh2.getId() == "ey02-mesh");  // id is assumed to be interned
       assertTrue(gmesh2.getMeshType() == GMesh.MeshType.Triangles);
       assertTrue(gmesh2.getNrOfVertices() == 16);
       assertTrue(gmesh2.getIndexData() != null);
       assertTrue(gmesh2.getIndexData().length == 66);
       assertTrue(gmesh2.getNrOfAttributes() == 3);
       assertTrue(gmesh2.getVertexAttributeList().size() == 3);
       assertTrue(gmesh2.getMorphTargets() == null);
       assertTrue(gmesh2.hasUnifiedIndexData() );  
       
       assertTrue(gmesh2.getVertexAttribute("mcPosition") != null);
       VertexAttribute mcPos =  gmesh2.getVertexAttribute("mcPosition") ;
       assertTrue(mcPos.getName() == "mcPosition");
       assertTrue(mcPos.getAttributeValueSize() == 3);
       assertTrue(mcPos.getNrOfValues() == 16);
       assertTrue(mcPos.getVertexDataSize() == 48);
       assertTrue( ! mcPos.hasIndex() );
       assertTrue(mcPos.getIndexData() == null);
       assertTrue(mcPos.getNrOfIndices() == -1);
       
       assertTrue(gmesh2.getVertexAttribute("mcNormal") != null);
       
       assertTrue(gmesh2.getVertexAttribute("texCoord1") != null);
       VertexAttribute texCoord1 =  gmesh2.getVertexAttribute("texCoord1") ;
       assertTrue(texCoord1.getName() == "texCoord1");
       assertTrue(texCoord1.getAttributeValueSize() == 2);
       assertTrue(texCoord1.getNrOfValues() == 16);
       assertTrue(texCoord1.getVertexDataSize() == 32);
       assertTrue( ! texCoord1.hasIndex() );
       assertTrue(texCoord1.getIndexData() == null);
       assertTrue(texCoord1.getNrOfIndices() == -1);
       
       assertTrue(gmesh2.getVertexAttribute("texCoord2") == null);
    } 
  
    @Test
    public void xmlTest3() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader gmeshreader = res.getReader("gmesh3.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(gmeshreader);
       GMesh gm1 = new GMesh(tokenizer);
       String  encoding = gm1.toXMLString();
      // System.out.println("gm1:\n" + encoding);
       XMLTokenizer tokenizer2 = new XMLTokenizer(encoding);
       GMesh gm2 = new GMesh(tokenizer2);
      // System.out.println("gmeshdecoded:\n" + gm2);
       assertTrue(gm2.getId().equals("ey02-mesh"));
       assertTrue(gm2.getId() == "ey02-mesh");  // id is assumed to be interned
       assertTrue(gm2.getMeshType() == GMesh.MeshType.Triangles);
       assertTrue(gm2.getNrOfVertices() == 16);
       assertTrue(gm2.getIndexData() != null);
       assertTrue(gm2.getIndexData().length == 66);
       assertTrue(gm2.getNrOfAttributes() == 3);
       assertTrue(gm2.getVertexAttributeList().size() == 3);
      
       assertTrue(gm2.hasUnifiedIndexData() );  
       
       assertTrue(gm2.getVertexAttribute("mcPosition") != null);
       assertTrue(gm2.getVertexAttribute("mcNormal") != null);
       
       
       assertTrue(gm2.getMorphTargets() != null);
       assertTrue(gm2.getMorphTargets().length == 3);
       float[][] morphData = gm2.getMorphData("mcPosition");
       assertTrue(morphData != null);
       assertTrue(morphData.length == 3);
       assertTrue(morphData[0] != null);
       assertTrue(morphData[0].length == 3*16);
       assertTrue(morphData[1].length == 3*16);
       assertTrue(morphData[2].length == 3*16);
       
       
    } 
  
    @Test
    public void diffTest1() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader gmeshreader = res.getReader("gmesh2.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(gmeshreader);
       GMesh gmesh1 = new GMesh(tokenizer);
       String  encoding = gmesh1.toXMLString();
      // System.out.println("gmesh1:\n" + encoding);
       XMLTokenizer tokenizer2 = new XMLTokenizer(encoding);
       GMesh gmesh2 = new GMesh(tokenizer2);
  
       String diff1 = gmesh1.showDiff(gmesh2);
       //System.out.println("diff1=" + diff1);
       assertTrue(diff1 == "");
       gmesh2.setId("XX");
       
       String diff2 = gmesh1.showDiff(gmesh2);
       //System.out.println("diff2=" + diff2);
       assertTrue(diff2 != "");
       
       Reader reader2 = res.getReader("gmesh2-diff.xml");
       XMLTokenizer tokenizer3 = new XMLTokenizer(reader2);
       GMesh gmesh2diff = new GMesh(tokenizer3);
       String diff3 = gmesh1.showDiff(gmesh2diff);
       //System.out.println("diff3=" + diff3);
       assertTrue(diff3 != "");
    }
  
    @Test
    public void diffTest2() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader3 = res.getReader("gmesh3.xml");
       XMLTokenizer tokenizer3 = new XMLTokenizer(reader3);
       GMesh gmesh3 = new GMesh(tokenizer3);
    
       Reader reader3diff = res.getReader("gmesh3-diff.xml");
       XMLTokenizer tokenizer3diff = new XMLTokenizer(reader3diff);
       GMesh gmesh3diff = new GMesh(tokenizer3diff);
    
    
       String diff = gmesh3.showDiff(gmesh3diff);
       //System.out.println("diff=" + diff);
       assertTrue(diff != "");
      
    }
  
  
    @Test
    public void binaryTest2() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("gmesh2.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       GMesh gmesh1 = new GMesh(tokenizer);
       
       String tmpdir = System.getProperty("java.io.tmpdir");
       DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir+"/gmeshbintest.dat")));
       gmesh1.writeBinary(dataOut);
       dataOut.close();
       DataInputStream dataIn = new DataInputStream(new BufferedInputStream(new FileInputStream(tmpdir+"/gmeshbintest.dat")));
       GMesh gmesh2 = new GMesh();
       gmesh2.readBinary(dataIn);
       dataIn.close();
       
     //  System.out.println("gmeshdecoded:\n" + gmesh2);
       assertTrue(gmesh2.getId().equals("ey02-mesh"));
       assertTrue(gmesh2.getId() == "ey02-mesh");  // id is assumed to be interned
       assertTrue(gmesh2.getMeshType() == GMesh.MeshType.Triangles);
       assertTrue(gmesh2.getNrOfVertices() == 16);
       assertTrue(gmesh2.getIndexData() != null);
       assertTrue(gmesh2.getIndexData().length == 66);
       assertTrue(gmesh2.getNrOfAttributes() == 3);
       assertTrue(gmesh2.getVertexAttributeList().size() == 3);
       assertTrue(gmesh2.getMorphTargets() == null);
       assertTrue(gmesh2.hasUnifiedIndexData() );  
       
       assertTrue(gmesh2.getVertexAttribute("mcPosition") != null);
       VertexAttribute mcPos =  gmesh2.getVertexAttribute("mcPosition") ;
       assertTrue(mcPos.getName() == "mcPosition");
       assertTrue(mcPos.getAttributeValueSize() == 3);
       assertTrue(mcPos.getNrOfValues() == 16);
       assertTrue(mcPos.getVertexDataSize() == 48);
       assertTrue( ! mcPos.hasIndex() );
       assertTrue(mcPos.getIndexData() == null);
       assertTrue(mcPos.getNrOfIndices() == -1);
       
       assertTrue(gmesh2.getVertexAttribute("mcNormal") != null);
       
       assertTrue(gmesh2.getVertexAttribute("texCoord1") != null);
       VertexAttribute texCoord1 =  gmesh2.getVertexAttribute("texCoord1") ;
       assertTrue(texCoord1.getName() == "texCoord1");
       assertTrue(texCoord1.getAttributeValueSize() == 2);
       assertTrue(texCoord1.getNrOfValues() == 16);
       assertTrue(texCoord1.getVertexDataSize() == 32);
       assertTrue( ! texCoord1.hasIndex() );
       assertTrue(texCoord1.getIndexData() == null);
       assertTrue(texCoord1.getNrOfIndices() == -1);
       
       assertTrue(gmesh2.getVertexAttribute("texCoord2") == null);
    } 
  
    @Test
    public void binaryTest3() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("gmesh3.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       GMesh gmesh1 = new GMesh(tokenizer);
       String tmpdir = System.getProperty("java.io.tmpdir");
       DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir+"/gmeshbintest.dat")));
       gmesh1.writeBinary(dataOut);
       dataOut.close();
       DataInputStream dataIn = new DataInputStream(new BufferedInputStream(new FileInputStream(tmpdir+"/gmeshbintest.dat")));
       GMesh gm2 = new GMesh();
       gm2.readBinary(dataIn);
       dataIn.close();
      // System.out.println("gmeshdecoded:\n" + gm2);
       assertTrue(gm2.getId().equals("ey02-mesh"));
       assertTrue(gm2.getId() == "ey02-mesh");  // id is assumed to be interned
       assertTrue(gm2.getMeshType() == GMesh.MeshType.Triangles);
       assertTrue(gm2.getNrOfVertices() == 16);
       assertTrue(gm2.getIndexData() != null);
       assertTrue(gm2.getIndexData().length == 66);
       assertTrue(gm2.getNrOfAttributes() == 3);
       assertTrue(gm2.getVertexAttributeList().size() == 3);
      
       assertTrue(gm2.hasUnifiedIndexData() );  
       
       assertTrue(gm2.getVertexAttribute("mcPosition") != null);
       assertTrue(gm2.getVertexAttribute("mcNormal") != null);
       
       
       assertTrue(gm2.getMorphTargets() != null);
       assertTrue(gm2.getMorphTargets().length == 3);
       float[][] morphData = gm2.getMorphData("mcPosition");
       assertTrue(morphData != null);
       assertTrue(morphData.length == 3);
       assertTrue(morphData[0] != null);
       assertTrue(morphData[0].length == 3*16);
       assertTrue(morphData[1].length == 3*16);
       assertTrue(morphData[2].length == 3*16);
        // System.out.println("gmeshdecoded:\n" + gm2);
       
    } 
  
  
}

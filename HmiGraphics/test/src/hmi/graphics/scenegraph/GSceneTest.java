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

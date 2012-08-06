/*
 * GMaterial JUnit test
 */

package hmi.graphics.scenegraph;

import static org.junit.Assert.*;
import org.junit.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import hmi.util.*;
import hmi.xml.XMLTokenizer;

/**
 * JUnit test for hmi.graphics.scenegraph.GMaterial
 */
public class GMaterialTest {
    
    public GMaterialTest() {
    }

    @Before
    public void setUp()  { // common initialization, executed for every test.
    }

    @After
    public void tearDown() {
    }

    @Test
    public void basics() {       
       GMaterial gmat = new GMaterial();
       assertTrue(gmat.getName() == "");
       assertTrue(gmat.getShader() == "");
    } 
    
    @Test
    public void xmlTest0() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("gmaterial0.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       GMaterial gmat1 = new GMaterial(tokenizer);
       String  encoding = gmat1.toXMLString();
       //System.out.println("gmat1:\n" + encoding);
       XMLTokenizer tokenizer2 = new XMLTokenizer(encoding);
       GMaterial gmat2 = new GMaterial(tokenizer2);
       
       assertTrue(gmat2.getName() == "");
      
       assertTrue(gmat2.getShader().equals(""));
       assertTrue(gmat2.getShader() == "");
       
       assertTrue(gmat2.getDiffuseColor()[0] == 0.0f);
       assertTrue(gmat2.getDiffuseColor()[3] == 1.0f);
    } 
    
    @Test
    public void xmlTest1() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("gmaterial1.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       GMaterial gmat1 = new GMaterial(tokenizer);
       String  encoding = gmat1.toXMLString();
       //System.out.println("gmat1:\n" + encoding);
       XMLTokenizer tokenizer2 = new XMLTokenizer(encoding);
       GMaterial gmat2 = new GMaterial(tokenizer2);
       
       assertTrue(gmat2.getName() == "coinoeil");
      
       assertTrue(gmat2.getShader().equals(""));
       assertTrue(gmat2.getShader() == "");
       
       assertTrue(gmat2.getDiffuseColor()[0] == 0.0f);
       assertTrue(gmat2.getDiffuseColor()[3] == 1.0f);
       
       assertTrue(gmat2.getEmissionColor()[0] == 0.0f);
       assertTrue(gmat2.getEmissionColor()[3] == 1.0f);
       
       assertTrue(gmat2.getAmbientColor()[0] == 0.0f);
       assertTrue(gmat2.getAmbientColor()[1] == 0.0f);
       assertTrue(gmat2.getAmbientColor()[2] == 0.0f);
       assertTrue(gmat2.getAmbientColor()[3] == 1.0f);
       
       assertTrue(gmat2.getSpecularColor()[0] == 0.0f);
       assertTrue(gmat2.getShininess() == 0.0f);
       
       assertTrue( ! gmat2.isTransparencyEnabled() );
    } 
    
    @Test
    public void xmlTest2() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("gmaterial2.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       GMaterial gmat1 = new GMaterial(tokenizer);
       String  encoding = gmat1.toXMLString();
       //System.out.println("gmat1:\n" + encoding);
       XMLTokenizer tokenizer2 = new XMLTokenizer(encoding);
       GMaterial gmat2 = new GMaterial(tokenizer2);
       //System.out.println("gmaterial decoded:\n" + gmat2);
       assertTrue(gmat2.getName() == "coinoeil");
       assertTrue(gmat2.getShader() == "blinnTextured1");
       
       assertTrue(gmat2.getDiffuseColor()[0] == 0.0f);
       assertTrue(gmat2.getDiffuseColor()[3] == 1.0f);
       
       assertTrue(gmat2.getEmissionColor()[0] == 0.0f);
       assertTrue(gmat2.getEmissionColor()[3] == 1.0f);
       
       assertTrue(gmat2.getAmbientColor()[0] == 0.588f);
       assertTrue(gmat2.getAmbientColor()[1] == 0.6f);
       assertTrue(gmat2.getAmbientColor()[2] == 0.7f);
       assertTrue(gmat2.getAmbientColor()[3] == 1.0f);
       
       assertTrue(gmat2.getSpecularColor()[0] == 0.9f);
       assertTrue(gmat2.getShininess() == 79.0f);
       
       assertTrue( ! gmat2.isTransparencyEnabled() );
    } 
    
    
    @Test
    public void diffTest() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("gmaterial3.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       GMaterial gmat1 = new GMaterial(tokenizer);
       String  encoding = gmat1.toXMLString();
       
       XMLTokenizer tokenizer2 = new XMLTokenizer(encoding);
       GMaterial gmat2 = new GMaterial(tokenizer2);
       String diff1 = gmat1.showDiff(gmat2);
       //System.out.println("diff1=" + diff1);
       assertTrue(diff1 == "");
       gmat2.getDiffuseTexture().setImageFileName("XX");
       
       String diff2 = gmat1.showDiff(gmat2);
       //System.out.println("diff2=" + diff2);
       assertTrue(diff2 != "");
       
    }
    
    @Test
    public void xmlTest3() throws IOException {       
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("gmaterial3.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       GMaterial gmat1 = new GMaterial(tokenizer);
       String  encoding = gmat1.toXMLString();
       //System.out.println("gmat1:\n" + encoding);
       XMLTokenizer tokenizer2 = new XMLTokenizer(encoding);
       GMaterial gmat2 = new GMaterial(tokenizer2);
       //System.out.println("gmaterial decoded:\n" + gmat2);
       assertTrue(gmat2.getName() == "coinoeil");
       assertTrue(gmat2.getShader() == "blinnTextured1");
       
       assertTrue(gmat2.getDiffuseColor()[0] == 0.0f);
       assertTrue(gmat2.getDiffuseColor()[3] == 1.0f);
       
       assertTrue(gmat2.getEmissionColor()[0] == 0.0f);
       assertTrue(gmat2.getEmissionColor()[3] == 1.0f);
       
       assertTrue(gmat2.getAmbientColor()[0] == 0.588f);
       assertTrue(gmat2.getAmbientColor()[1] == 0.6f);
       assertTrue(gmat2.getAmbientColor()[2] == 0.7f);
       assertTrue(gmat2.getAmbientColor()[3] == 1.0f);
       
       assertTrue(gmat2.getSpecularColor()[0] == 0.9f);
       assertTrue(gmat2.getShininess() == 79.0f);
       
       assertTrue(  gmat2.isTransparencyEnabled() );
       
       GTexture gtex1 = gmat2.getDiffuseTexture();
       assertTrue(gtex1 != null);
       GTexture gtex2 = gmat2.getTransparentTexture();
       assertTrue(gtex2 != null);
       
       
       assertTrue(gtex1.getImageFileName() == "visage_2008_low.jpg");
       assertTrue(gtex1.getWrapS().equals("REPEAT"));
       assertTrue(gtex1.getRepeatS() == 3.0f);
       assertTrue(gtex1.getRepeatT() == 1.0f);
       assertTrue(gtex1.getOffsetS() == 0.0f);
       
       assertTrue(gtex2.getImageFileName() == "visage_2008_trans.jpg");
       assertTrue(gtex2.getWrapS().equals("REPEAT"));
       assertTrue(gtex2.getRepeatS() == 1.0f);
       assertTrue(gtex2.getRepeatT() == 1.0f);
       assertTrue(gtex2.getOffsetS() == 0.5f);
       
    } 
    
    
    @Test
    public void binaryTest0() throws IOException {
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("gmaterial0.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       GMaterial gmat1 = new GMaterial(tokenizer);
    
       String tmpdir = System.getProperty("java.io.tmpdir");
     
    //   System.out.println("userdir=" + userdir + " tmpdir=" + tmpdir);
       DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir+"/gmatbintest.dat")));
       gmat1.writeBinary(dataOut);
       dataOut.close();
       DataInputStream dataIn = new DataInputStream(new BufferedInputStream(new FileInputStream(tmpdir+"/gmatbintest.dat")));
       GMaterial gmat2 = new GMaterial();
       gmat2.readBinary(dataIn);
       dataIn.close();
       
       assertTrue(gmat2.getName() == "");
       assertTrue(gmat2.getShader() == "");
       
       assertTrue(gmat2.getDiffuseColor()[0] == 0.0f);
       assertTrue(gmat2.getDiffuseColor()[3] == 1.0f);
    }
    
    @Test
    public void binaryTest1() throws IOException {
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("gmaterial1.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       GMaterial gmat1 = new GMaterial(tokenizer);
    
       String tmpdir = System.getProperty("java.io.tmpdir");
     
    //   System.out.println("userdir=" + userdir + " tmpdir=" + tmpdir);
       DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir+"/gmatbintest.dat")));
       gmat1.writeBinary(dataOut);
       dataOut.close();
       DataInputStream dataIn = new DataInputStream(new BufferedInputStream(new FileInputStream(tmpdir+"/gmatbintest.dat")));
       GMaterial gmat2 = new GMaterial();
       gmat2.readBinary(dataIn);
       dataIn.close();
       
     
       assertTrue(gmat2.getName() == "coinoeil");
       assertTrue(gmat2.getShader() == "");
       
       assertTrue(gmat2.getDiffuseColor()[0] == 0.0f);
       assertTrue(gmat2.getDiffuseColor()[3] == 1.0f);
       
       assertTrue(gmat2.getEmissionColor()[0] == 0.0f);
       assertTrue(gmat2.getEmissionColor()[3] == 1.0f);
       
       assertTrue(gmat2.getAmbientColor()[0] == 0.0f);
       assertTrue(gmat2.getAmbientColor()[1] == 0.0f);
       assertTrue(gmat2.getAmbientColor()[2] == 0.0f);
       assertTrue(gmat2.getAmbientColor()[3] == 1.0f);
       
       assertTrue(gmat2.getSpecularColor()[0] == 0.0f);
       assertTrue(gmat2.getShininess() == 0.0f);
       
       assertTrue(  ! gmat2.isTransparencyEnabled() );
       
       GTexture gtex1 = gmat2.getDiffuseTexture();
       assertTrue(gtex1 == null);
       GTexture gtex2 = gmat2.getTransparentTexture();
       assertTrue(gtex2 == null);
    }

    
    @Test
    public void binaryTest3() throws IOException {
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("gmaterial3.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       GMaterial gmat1 = new GMaterial(tokenizer);
    
       String tmpdir = System.getProperty("java.io.tmpdir");
     
    //   System.out.println("userdir=" + userdir + " tmpdir=" + tmpdir);
       DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir+"/gmatbintest.dat")));
       gmat1.writeBinary(dataOut);
       dataOut.close();
       DataInputStream dataIn = new DataInputStream(new BufferedInputStream(new FileInputStream(tmpdir+"/gmatbintest.dat")));
       GMaterial gmat2 = new GMaterial();
       gmat2.readBinary(dataIn);
       dataIn.close();
      
       assertTrue(gmat2.getName() == "coinoeil");
       assertTrue(gmat2.getShader() == "blinnTextured1");
       
       assertTrue(gmat2.getDiffuseColor()[0] == 0.0f);
       assertTrue(gmat2.getDiffuseColor()[3] == 1.0f);
       
       assertTrue(gmat2.getEmissionColor()[0] == 0.0f);
       assertTrue(gmat2.getEmissionColor()[3] == 1.0f);
       
       assertTrue(gmat2.getAmbientColor()[0] == 0.588f);
       assertTrue(gmat2.getAmbientColor()[1] == 0.6f);
       assertTrue(gmat2.getAmbientColor()[2] == 0.7f);
       assertTrue(gmat2.getAmbientColor()[3] == 1.0f);
       
       assertTrue(gmat2.getSpecularColor()[0] == 0.9f);
       assertTrue(gmat2.getShininess() == 79.0f);
       
       assertTrue(  gmat2.isTransparencyEnabled() );
       
       GTexture gtex1 = gmat2.getDiffuseTexture();
       assertTrue(gtex1 != null);
       GTexture gtex2 = gmat2.getTransparentTexture();
       assertTrue(gtex2 != null);
     
       assertTrue(gtex1.getImageFileName() == "visage_2008_low.jpg");
       assertTrue(gtex1.getWrapS().equals("REPEAT"));
       assertTrue(gtex1.getRepeatS() == 3.0f);
       assertTrue(gtex1.getRepeatT() == 1.0f);
       assertTrue(gtex1.getOffsetS() == 0.0f);
       
       assertTrue(gtex2.getImageFileName() == "visage_2008_trans.jpg");
       assertTrue(gtex2.getWrapS().equals("REPEAT"));
       assertTrue(gtex2.getRepeatS() == 1.0f);
       assertTrue(gtex2.getRepeatT() == 1.0f);
       assertTrue(gtex2.getOffsetS() == 0.5f);
    }


    @Test
    public void binaryTest4() throws IOException {
       Resources res = new Resources("scenegraph");
       Reader reader = res.getReader("gmaterial4.xml");
       XMLTokenizer tokenizer = new XMLTokenizer(reader);
       GMaterial gmat1 = new GMaterial(tokenizer);
    
       String tmpdir = System.getProperty("java.io.tmpdir");
     
    //   System.out.println("userdir=" + userdir + " tmpdir=" + tmpdir);
       DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpdir+"/gmatbintest.dat")));
       gmat1.writeBinary(dataOut);
       dataOut.close();
       DataInputStream dataIn = new DataInputStream(new BufferedInputStream(new FileInputStream(tmpdir+"/gmatbintest.dat")));
       GMaterial gmat2 = new GMaterial();
       gmat2.readBinary(dataIn);
       dataIn.close();

     
       assertTrue(gmat2.getName() == "coinoeil");
       assertTrue(gmat2.getShader() == "blinnTextured1");
       
       assertTrue(gmat2.getDiffuseColor()[0] == 0.0f);
       assertTrue(gmat2.getDiffuseColor()[3] == 1.0f);
       
       assertTrue(gmat2.getEmissionColor()[0] == 0.0f);
       assertTrue(gmat2.getEmissionColor()[3] == 1.0f);
       
       assertTrue(gmat2.getAmbientColor()[0] == 0.0f);
       assertTrue(gmat2.getAmbientColor()[1] == 0.0f);
       assertTrue(gmat2.getAmbientColor()[2] == 0.0f);
       assertTrue(gmat2.getAmbientColor()[3] == 1.0f);
       
       assertTrue(gmat2.getSpecularColor()[0] == 0.0f);
       assertTrue(gmat2.getShininess() == 0.0f);
       
       assertTrue( ! gmat2.isTransparencyEnabled() );
       
       GTexture gtex1 = gmat2.getDiffuseTexture();
       assertTrue(gtex1 == null);
       GTexture gtex2 = gmat2.getTransparentTexture();
       assertTrue(gtex2 != null);
     
       
       assertTrue(gtex2.getImageFileName() == "visage_2008_trans.jpg");
       assertTrue(gtex2.getWrapS().equals("REPEAT"));
       assertTrue(gtex2.getRepeatS() == 1.0f);
       assertTrue(gtex2.getRepeatT() == 1.0f);
       assertTrue(gtex2.getOffsetS() == 0.5f);
       
    }

  
}

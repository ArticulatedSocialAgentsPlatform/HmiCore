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


package hmi.graphics.scenegraph;
import hmi.util.BinUtil;
import hmi.util.BinaryExternalizable;
import hmi.util.Diff;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A GShape combines a GMesh and a GMaterial.
 * @author Job Zwiers
 */
public class GShape extends XMLStructureAdapter implements BinaryExternalizable, Diff.Differentiable {

  
   private GMesh gmesh;
   private GMaterial gmaterial;
   private String name = "";
   private static Logger logger = LoggerFactory.getLogger(GShape.class.getName());

   /**
    * Creates a new GShape.
    */
   public GShape() {
   }
  
   /**
    * Creates a new GShape with specified GMesh and GMaterial.
    */
   public GShape(GMesh gmesh, GMaterial gmaterial, String name) {
      this.gmesh = gmesh;
      this.gmaterial = gmaterial;
      this.name = name;
   }
  
   /**
    * Recreate a GShape from XML
    */
   public GShape(XMLTokenizer tokenizer) throws IOException {
       this();
       readXML(tokenizer);  
   }
  
   /**
    * show differences
    */
   public String showDiff(Object gshapeObj) {
      GShape gshape = (GShape) gshapeObj;
      if (gshape==null) return "GShape " + name + ", diff: null GShape";
      String diff = Diff.showDiff("GShape, name", name, gshape.name);
      if (diff != "") return diff;
      diff = Diff.showDiff("GShape " + name + ", diff gmaterial", gmaterial, gshape.gmaterial);
      if (diff != "") return diff;
      diff = Diff.showDiff("GShape " + name + ", diff gmesh", gmesh, gshape.gmesh);
      if (diff != "") return diff;
      return "";
   }
  
  
   /**
    * Sets the GMesh component.
    */
   public void setGMesh(GMesh gmesh) {
      this.gmesh = gmesh;
   }
   
   /**
    * Returns the GMesh component.
    */
   public GMesh getGMesh() {
      return gmesh;
   }
   
   /**
    * Sets the GMaterial component.
    */
   public void setGMaterial(GMaterial gmaterial) {
      this.gmaterial = gmaterial;
   }
     
   /**
    * Returns the GMaterial component.
    */
   public GMaterial getGMaterial() {
      return gmaterial;
   }  
   
   /**
    * Sets the name.
    */
   public void setName(String name) {
      this.name = name;
   }
   
   /**
    * Returns the name.
    */
   public String getName() {
      return name;
   }
    
   public void affineTransform(float[] mat4x4) { 
      logger.debug("GShape "+ name + ": affineTransform");
      if (gmesh != null) gmesh.affineTransform(mat4x4);
   } 
    
   /**
    * appends the (optional) name attribute to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      appendAttribute(buf, "name", name);
      return buf;
   } 
    
   /**
    * decodes the name XML attribute.
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      name     = getOptionalAttribute("name", attrMap);
      if (name != null) name = name.intern();
      super.decodeAttributes(attrMap, tokenizer);
   }

   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, gmaterial);
      appendXMLStructure(buf, fmt, gmesh);
      return buf;
   }

   /**
    *
    */
   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();  
         if (tag.equals(GMaterial.xmlTag())) {
             gmaterial = new GMaterial(tokenizer);  
         } else if (tag.equals(GMesh.xmlTag())) {
             gmesh = new GMesh(tokenizer);   
         } else if (tag.equals(GSkinnedMesh.xmlTag())) {
             gmesh = new GSkinnedMesh(tokenizer);   
         } else {
            System.out.println(tokenizer.getErrorMessage("GShape: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }   
   }


   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "gshape";
 
   /**
    * The XML Stag for XML encoding
    */
   public static String xmlTag() { return XMLTAG; }
 
   /**
    * returns the XML Stag for XML encoding
    */
   @Override
   public String getXMLTag() {
      return XMLTAG;
   }  
      
   /**
    * Writes a binary encoding to dataOut
    */
   public void writeBinary(DataOutput dataOut) throws IOException { 
      dataOut.writeUTF(name);
      BinUtil.writeOptionalBinary(dataOut, gmaterial);
      if (gmesh == null) {
         dataOut.writeInt(-1);
      } else {
          dataOut.writeInt( (gmesh instanceof GSkinnedMesh) ? 2 : 1);
          gmesh.writeBinary(dataOut);
      } 
   }
    
   /**
    * Reads a binary encoding from dataIn
    */ 
   public void readBinary(DataInput dataIn) throws IOException {
      name = dataIn.readUTF().intern();
      gmaterial = BinUtil.readOptionalBinary(dataIn, GMaterial.class);
      int gmeshClass = dataIn.readInt();
      if (gmeshClass < 0) {
         gmesh = null;
         return;
      } else if (gmeshClass == 1) {
         gmesh = new GMesh();
      } else {
         gmesh = new GSkinnedMesh();
      }
      gmesh.readBinary(dataIn);
   }    
      
} 

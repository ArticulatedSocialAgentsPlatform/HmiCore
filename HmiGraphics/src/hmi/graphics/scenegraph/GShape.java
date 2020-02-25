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

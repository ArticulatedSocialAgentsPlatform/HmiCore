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
import hmi.util.BinaryExternalizable;
import hmi.util.Diff;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
 
/** 
 * A GTexture is the hmi.graphics.scenegraph representation of a texture, defined
 * by its attributes like file name, wrap mode etcetera.
 * @author Job Zwiers
 */
public class GTexture extends XMLStructureAdapter implements BinaryExternalizable, Diff.Differentiable {
  
  
  // public enum WrapType {REPEAT, MIRRORED_REPEAT, CLAMP_TO_EDGE, CLAMP_TO_BORDER};
  
   private String name = "";
   private String wrapS = "REPEAT";
   private String wrapT = "REPEAT"; 
   private String wrapR = "REPEAT";

//   private int mirrorU = 0; // from Maya profile
//   private int mirrorV = 0;
//   private int wrapU = 1;
//   private int wrapV = 1;

// Note: repeatX/offsetX is also present in GTexture. 
// It could be GTexture specific, but for the time being we guess it is GTexture specific, i.e. all textures for one material
// will use the same repeatX/offsetX settings. This has to be handled by the (material specific) shader anyway.
//The info is left here for possible future extensions.
   private float repeatS = 1.0f; // multiplier to apply on the texture coordinates before sampling. 
   private float repeatT = 1.0f;
   private float repeatR = 1.0f;
   
   private float offsetS = 0.0f;  // offset to add on the texture coordinates before sampling.
   private float offsetT = 0.0f;
   private float offsetR = 0.0f;
   
//   private float rotateUV = 0.0f; // angle of rotation to apply to the texture coordinates before sampling
   
   
   private String imageFile = "";
   
  
   /**
    * Creates a new GTexture, with default settins, and empty imageFile name
    */
   public GTexture() {
   }
  
  
   /**
    * Creates a new GTexture and reconstructs it from an XMLTokenizer stream
    */
   public GTexture(XMLTokenizer tokenizer) throws IOException {
      this();
      readXML(tokenizer);  
   }
  
   /**
    * show differences
    */
   public String showDiff(Object gtObj) {
      GTexture gt = (GTexture) gtObj;
      if (gt==null) return "GTexture " + name + ", diff: null GTexture";
      String diff = Diff.showDiff("GTexture", name, gt.name);
      if (diff != "") return diff;
      diff = Diff.showDiff("GTexture " + name + ", diff imageFile", imageFile, gt.imageFile);
      if (diff != "") return diff;
      diff = Diff.showDiff("GTexture " + name + ", diff wrapS", wrapS, gt.wrapS);
      if (diff != "") return diff;
      diff = Diff.showDiff("GTexture " + name + ", diff wrapT", wrapT, gt.wrapT);
      if (diff != "") return diff;
      diff = Diff.showDiff("GTexture " + name + ", diff wrapR", wrapR, gt.wrapR);
      if (diff != "") return diff;
      diff = Diff.showDiff("GTexture " + name + ", diff repeatS", repeatS, gt.repeatS);
      if (diff != "") return diff;
      diff = Diff.showDiff("GTexture " + name + ", diff offsetS", offsetS, gt.offsetS);
      if (diff != "") return diff;
      diff = Diff.showDiff("GTexture " + name + ", diff repeatT", repeatT, gt.repeatT);
      if (diff != "") return diff;
      diff = Diff.showDiff("GTexture " + name + ", diff offsetT", offsetT, gt.offsetT);
      if (diff != "") return diff;
      diff = Diff.showDiff("GTexture " + name + ", diff repeatR", repeatR, gt.repeatR);
      if (diff != "") return diff;
      diff = Diff.showDiff("GTexture " + name + ", diff offsetR", offsetR, gt.offsetR);
      if (diff != "") return diff;
      return "";
   }
  
  
   /** Sets the wrap mode for the S coordinate  */
   public void setWrapS(String wt) { wrapS = wt; }
   
   /** returns the wrap mode for the S coordinate  */
   public String getWrapS() { return wrapS; }
   
   /** Sets the wrap mode for the T coordinate */
   public void setWrapT(String wt) { wrapT = wt; }
   
   /** returns the wrap mode for the T coordinate */
   public String getWrapT() { return wrapT; }
   
   /** Sets the wrap mode for the R coordinate  */
   public void setWrapR(String wt) { wrapR = wt; }
   
   /** returns the wrap mode for the R coordinate */
   public String getWrapR() { return wrapR; }
  
  
   /** Sets the repeat factor for the S coordinate  */
   public void setRepeatS(float r) { repeatS = r; }
   
   /** Returns the repeat factor for the S coordinate  */
   public float getRepeatS() { return repeatS; }
   
    /** Sets the repeat factor for the T coordinate  */
   public void setRepeatT(float r) { repeatT = r; }
   
   /** Returns the repeat factor for the T coordinate  */
   public float getRepeatT() { return repeatT; }
   
    /** Sets the repeat factor for the R coordinate  */
   public void setRepeatR(float r) { repeatR = r; }
   
   /** Returns the repeat factor for the R coordinate  */
   public float getRepeatR() { return repeatR; }
   
   
   /** Sets the offset for the S coordinate  */
   public void setOffsetS(float r) { offsetS = r; }
   
   /** Returns the offset for the S coordinate  */
   public float getOffsetS() { return offsetS; }
   
    /** Sets the offsetr for the T coordinate  */
   public void setOffsetT(float r) { offsetT = r; }
   
   /** Returns the offset for the T coordinate  */
   public float getOffsetT() { return offsetT; }
   
    /** Sets the offset for the R coordinate  */
   public void setOffsetR(float r) { offsetR = r; }
   
   /** Returns the offset for the R coordinate  */
   public float getOffsetR() { return offsetR; }
   
   
  
   /** Sets the texture file name  */
   public void setImageFileName(String imageFile) { this.imageFile = imageFile; }
  
   /** returns the texture file name */
   public String getImageFileName() { return imageFile; }
  
  
   /**
    * appends the id and sid XML attributes to buf.
    */
   @Override
   public StringBuilder appendAttributeString(StringBuilder buf, XMLFormatting fmt) {
      appendAttribute(buf, "imageFile", imageFile);
      if ( ! wrapS.equals("REPEAT")) appendAttribute(buf, "wrapS", wrapS);
      if ( ! wrapT.equals("REPEAT")) appendAttribute(buf, "wrapT", wrapT);
      if ( ! wrapR.equals("REPEAT")) appendAttribute(buf, "wrapR", wrapR);
      if (  repeatS != 1.0f) appendAttribute(buf, "repeatS", repeatS);
      if (  repeatT != 1.0f) appendAttribute(buf, "repeatT", repeatT);
      if (  repeatR != 1.0f) appendAttribute(buf, "repeatR", repeatR);
      if (  offsetS != 0.0f) appendAttribute(buf, "offsetS", offsetS);
      if (  offsetT != 0.0f) appendAttribute(buf, "offsetT", offsetT);
      if (  offsetR != 0.0f) appendAttribute(buf, "offsetR", offsetR);
      return buf;
   } 
    
   /**
    * decodes the id and sid XML attributes.
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      imageFile  = getRequiredAttribute("imageFile", attrMap, tokenizer);
      if (imageFile != null) imageFile = imageFile.intern();
      wrapS= getOptionalAttribute("wrapS", attrMap, "REPEAT");
      wrapT= getOptionalAttribute("wrapT", attrMap, "REPEAT");
      wrapR= getOptionalAttribute("wrapR", attrMap, "REPEAT");
      repeatS= getOptionalFloatAttribute("repeatS", attrMap, 1.0f);
      repeatT= getOptionalFloatAttribute("repeatT", attrMap, 1.0f);
      repeatR= getOptionalFloatAttribute("repeatR", attrMap, 1.0f);
      offsetS= getOptionalFloatAttribute("offsetS", attrMap, 0.0f);
      offsetT= getOptionalFloatAttribute("offsetT", attrMap, 0.0f);
      offsetR= getOptionalFloatAttribute("offsetR", attrMap, 0.0f);
      super.decodeAttributes(attrMap, tokenizer);
   }


   /** always returns false: no XML content part */
   public boolean hasContent() {
      return false;
   }



   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "gtexture";
 
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
      dataOut.writeUTF(imageFile);
      dataOut.writeUTF(wrapS);   
      dataOut.writeUTF(wrapT); 
      dataOut.writeUTF(wrapR); 
      dataOut.writeFloat(repeatS); 
      dataOut.writeFloat(repeatT);
      dataOut.writeFloat(repeatR);
      dataOut.writeFloat(offsetS);
      dataOut.writeFloat(offsetT);
      dataOut.writeFloat(offsetR);
   }
    
   /**
    * Reads a binary encoding from dataIn
    */ 
   public void readBinary(DataInput dataIn) throws IOException {
      imageFile = dataIn.readUTF().intern();  
      wrapS = dataIn.readUTF().intern();  
      wrapT = dataIn.readUTF().intern();  
      wrapR = dataIn.readUTF().intern();  
      repeatS = dataIn.readFloat();
      repeatT = dataIn.readFloat();
      repeatR = dataIn.readFloat();
      offsetS = dataIn.readFloat();
      offsetT = dataIn.readFloat();
      offsetR = dataIn.readFloat();
   } 

      
} 

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
/**
 * A GMaterial defines the appearance, or material, for some GMesh
 * Currently, this is just basic OpenGL material settings, like
 * the ambient, diffuse and specular color, and the specular shininess. 
 * @author Job Zwiers
 */
public class GMaterial extends XMLStructureAdapter implements BinaryExternalizable, Diff.Differentiable {
   /** The name of the material */
   private String name = "";    
   private String shader = "";
   private float[] emissionColor = new float[] {0f, 0f, 0f, 1f};
   private float[] ambientColor  = new float[] {0f, 0f, 0f, 1f};
   private float[] diffuseColor  = new float[] {0f, 0f, 0f, 1f};
   private float[] specularColor = new float[] {0f, 0f, 0f, 1f};
   private float shininess = 0f;
   private float[] transparentColor = null;
   
   
   // Collada  reflectiveness and index of refraction etc not supported.
   
   private GTexture diffuseTexture;
   private GTexture transparentTexture;
   
  
   private String opaqueMode = ""; /*  a String like "A_ONE" or "RGB_ZERO" */
   private boolean transparencyEnabled = false;
   
   //private float transparency;

   // GMaterial repeats/offsets are derived from GTexture repeats and offsets. 
   // Currently we use shaders that accept only one set of such repeat/offset parameters, so 
   // we transfer such settings from the GTextures(s) to the GMaterial. 
   private float repeatS = 1.0f; // multiplier to apply on (all!) texture(s) coordinates before sampling. 
   private float repeatT = 1.0f;
   private float offsetS = 0.0f;  // offset to add on (all!) texture(s) coordinates before sampling.
   private float offsetT = 0.0f;
   private boolean repeatSettingsTransferred = false; // used to check consistency between repeat/offset settings when more than one texture is used.

   private static Logger logger = LoggerFactory.getLogger(GMaterial.class.getName());


   /**
    * Constructor for a default material with name = &quot;&quot;, and default settings for colors and textures.
    */
   public GMaterial() {
      name="";
   }
  
   /**
    * Constructor for a default material with specified name and default settings for colors and textures.
    */
   public GMaterial(String name) {
      this();
      this.name = name;  
   }
  
   /**
    * Constructor that reads from a scenegraph  XML encoding
    */
   public GMaterial(XMLTokenizer tokenizer) throws IOException {
       this();
       readXML(tokenizer);  
   }
   
   
   /**
    * show differences
    */
   public String showDiff(Object gmObj) {
      GMaterial gm = (GMaterial) gmObj;
      if (gm==null) return "GMaterial " + name + ", diff: null GMaterial";
      String diff = Diff.showDiff("GMaterial", name, gm.name);
      if (diff != "") return diff;
      diff = Diff.showDiff("GMaterial " + name + ", diff shader", shader, gm.shader);
      if (diff != "") return diff;
      diff = Diff.showDiff("GMaterial " + name + ", diff emissionColor", emissionColor, gm.emissionColor);
      if (diff != "") return diff;
      diff = Diff.showDiff("GMaterial " + name + ", diff ambientColor", ambientColor, gm.ambientColor);
      if (diff != "") return diff;
      diff = Diff.showDiff("GMaterial " + name + ", diff diffuseColor", diffuseColor, gm.diffuseColor);
      if (diff != "") return diff;
      diff = Diff.showDiff("GMaterial " + name + ", diff specularColor", specularColor, gm.specularColor);
      if (diff != "") return diff;
      diff = Diff.showDiff("GMaterial " + name + ", diff shininess", shininess, gm.shininess);
      if (diff != "") return diff;
      diff = Diff.showDiff("GMaterial " + name + ", diff opaqueMode", opaqueMode, gm.opaqueMode);
      if (diff != "") return diff;
      diff = Diff.showDiff("GMaterial " + name + ", diff transparencyEnabled", transparencyEnabled, gm.transparencyEnabled);
      if (diff != "") return diff;
      diff = Diff.showDiff("GMaterial " + name + ", diff repeatS", repeatS, gm.repeatS);
      if (diff != "") return diff;
      diff = Diff.showDiff("GMaterial " + name + ", diff offsetS", offsetS, gm.offsetS);
      if (diff != "") return diff;
      diff = Diff.showDiff("GMaterial " + name + ", diff repeatT", repeatT, gm.repeatT);
      if (diff != "") return diff;
      diff = Diff.showDiff("GMaterial " + name + ", diff offsetT", offsetT, gm.offsetT);
      if (diff != "") return diff;
      diff = Diff.showDiff("GMaterial " + name + ", diff diffuseTexture", diffuseTexture, gm.diffuseTexture);
      if (diff != "") return diff;
      diff = Diff.showDiff("GMaterial " + name + ", diff transparentTexture", transparentTexture, gm.transparentTexture);
      if (diff != "") return diff;
      return "";
   }
   
   
   public void setName(String name) {
      this.name = name; 
   }
   
   public String getName() {
      return name;
   }
   
   public void setShader(String shader) {
      logger.debug("GMaterial.setShader " + shader + " for " + name);
      this.shader = shader.intern();  
   }
   
   public String getShader() {
      return shader;
   }
  
   public void setEmissionColor(float[] emissionColor) {
      this.emissionColor = emissionColor; 
   }
  
   public float[] getEmissionColor() {
      return emissionColor; 
   }
   
   public void setAmbientColor(float[] ambientColor) {
      this.ambientColor = ambientColor; 
   }
  
   public float[] getAmbientColor() {
      return ambientColor; 
   }
   
   public void setDiffuseColor(float[] diffuseColor) {
      this.diffuseColor = diffuseColor; 
   }
  
   public float[] getDiffuseColor() {
      return diffuseColor; 
   }
   
   public void setSpecularColor(float[] specularColor) {
      this.specularColor = specularColor; 
   }
  
   public float[] getSpecularColor() {
      return specularColor; 
   }
  
   public void setShininess(float shininess) {
      this.shininess = shininess; 
   }
  
   public float getShininess() {
      return shininess; 
   }
  
   public void setDiffuseTexture(GTexture texture) {
      this.diffuseTexture = texture;
   }
   
   public GTexture getDiffuseTexture() {
      return diffuseTexture;
   }
  
   public void setTransparencyEnabled(boolean mode) {
      transparencyEnabled = mode;
   }
  
   public boolean isTransparencyEnabled() {
      return transparencyEnabled;
   }
   
   public void setTransparentColor(float[] transparentColor) {
      this.transparentColor = transparentColor;
   }
   
   public float[] getTransparentColor() {
      return transparentColor;
   }
   
   
   public void setTransparentTexture(GTexture texture) {
      this.transparentTexture = texture;
   }
   
   public GTexture getTransparentTexture() {
      return transparentTexture;
   }
  
   public void setOpaqueMode(String opaqueMode) {
      this.opaqueMode = opaqueMode;
   }
  
   public String getOpaqueMode() {
      return opaqueMode;
   }
  
  
    /* Copy repeat factors and offsets from GTexture, and verify for consistency for multitexturing */
    /* return a warning message in the form of a String, or null when there is no warning */
    public  String transferRepeatSettings(GTexture gtex) {
      if ( ! repeatSettingsTransferred) {
         repeatS = gtex.getRepeatS(); 
         repeatT = gtex.getRepeatT(); 
         offsetS = gtex.getOffsetS();
         offsetT = gtex.getOffsetT();
         repeatSettingsTransferred = true;
         return null;
      } else {
        
         if (repeatS != gtex.getRepeatS() || repeatT != gtex.getRepeatT()) {
//             System.out.println("repeatS = " + repeatS + " gtex.repeatS=" + gtex.getRepeatS() + " gtex: " + gtex.getImageFileName());
//             System.out.println("repeatT = " + repeatS + " gtex.repeatT=" + gtex.getRepeatT());
            
            return "Material " + name + " has textures with different repeat factors"; 
         }
         if (offsetS != gtex.getOffsetS() || offsetT != gtex.getOffsetT()) {
             return "Material " + name + " has textures with different offsets"; 
         }
         return null;
      }
    }
 
  
  
//    /** Sets the repeat factor for the S coordinate  */
//   public void setRepeatS(float r) { repeatS = r; }
//   
   /** Returns the repeat factor for the S coordinate  */
   public float getRepeatS() { return repeatS; }
//   
//    /** Sets the repeat factor for the T coordinate  */
//   public void setRepeatT(float r) { repeatT = r; }
//   
   /** Returns the repeat factor for the T coordinate  */
   public float getRepeatT() { return repeatT; }
//   
//  
//   /** Sets the offset for the S coordinate  */
//   public void setOffsetS(float r) { offsetS = r; }
//   
   /** Returns the offset for the S coordinate  */
   public float getOffsetS() { return offsetS; }
//   
//    /** Sets the offset for the T coordinate  */
//   public void setOffsetT(float r) { offsetT = r; }
//   
   /** Returns the offset for the T coordinate  */
   public float getOffsetT() { return offsetT; }
   
  
   private static final int LIGHT_ATTRIBUTE_SIZE = 4;
   
   

   /**
    * appends the XML attributes to buf.
    */
   @Override
   public StringBuilder appendAttributeString(StringBuilder buf, XMLFormatting fmt) {
      appendAttribute(buf, "name", name);
      appendAttribute( buf, "shader", shader);
      appendAttribute( buf, "emission", emissionColor, ' ', fmt, LIGHT_ATTRIBUTE_SIZE);
      appendAttribute( buf, "ambient", ambientColor, ' ', fmt, LIGHT_ATTRIBUTE_SIZE);
      appendAttribute( buf, "diffuse", diffuseColor, ' ', fmt, LIGHT_ATTRIBUTE_SIZE);
      appendAttribute( buf, "specular", specularColor, ' ', fmt, LIGHT_ATTRIBUTE_SIZE);
      if (shininess != 0.0f) appendAttribute( buf, "shininess", shininess);
      if ( ! opaqueMode.equals("")) appendAttribute( buf, "opaqueMode", opaqueMode);
      if (transparencyEnabled) appendAttribute(buf, "transparencyEnabled", transparencyEnabled);
      // We do NOT encode repeat and offset attributes, since these are stored inside the GTextures
      return buf;
   } 
    
   /**
    * decodes the XML attributes.
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      name   = getOptionalAttribute("name", attrMap, "").intern();
      shader = getOptionalAttribute("shader", attrMap, "").intern();
      String em = getOptionalAttribute("emission", attrMap);
      if (em != null) emissionColor = decodeFloatArray(em, emissionColor);
      String am = getOptionalAttribute("ambient", attrMap);
      if (am != null) ambientColor = decodeFloatArray(am, ambientColor);
      String df = getOptionalAttribute("diffuse", attrMap);
      if (df != null) diffuseColor = decodeFloatArray(df, diffuseColor);
      String sp = getOptionalAttribute("specular", attrMap);
      if (sp != null) specularColor = decodeFloatArray(sp, specularColor);
      String sh = getOptionalAttribute("shininess", attrMap);
      if (sh != null) shininess = decodeFloat(sh);
      opaqueMode = getOptionalAttribute("opaqueMode", attrMap, "").intern();
      transparencyEnabled = getOptionalBooleanAttribute("transparencyEnabled", attrMap, false);
      super.decodeAttributes(attrMap, tokenizer);
   }
   
   
   /**
    * appends the GTexture content section
    */
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      if (diffuseTexture != null) {
         appendSTag(buf, "diffuse", fmt);
            appendXMLStructure(buf, fmt.indent(), diffuseTexture);
         appendETag(buf, "diffuse", fmt.unIndent());
      }
      if (transparentTexture != null) {
         appendSTag(buf, "transparent", fmt);
            appendXMLStructure(buf, fmt, transparentTexture);
         appendETag(buf, "transparent", fmt);
      }
      return buf;
   }

   /**
    * decodes the GTexture content section
    */
   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();  
         if (tag.equals("diffuse")) {
            tokenizer.takeSTag("diffuse");
            diffuseTexture = new GTexture(tokenizer);
            transferRepeatSettings(diffuseTexture);
            tokenizer.takeETag("diffuse");
         } else if (tag.equals("transparent")) {
            tokenizer.takeSTag("transparent");
             transparentTexture = new GTexture(tokenizer);
             transferRepeatSettings(transparentTexture);
             tokenizer.takeETag("transparent");
         } else {
            logger.error(tokenizer.getErrorMessage("GMaterial: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }      
   }

 
  /**
   * The XML encoding has a content part only if at least one texture is being used
   */
   public boolean hasContent() {
      return diffuseTexture != null || transparentTexture != null;
   }


   /**
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "gmaterial";
   
   /**
    * The XML Stag for XML encoding of this Class
    */
   public static String xmlTag() { return  XMLTAG; }  

   /**
    * returns the XML Stag for XML encoding
    */
   @Override
   public String getXMLTag() {
      return XMLTAG;
   } 
    
   // some bit masks, to be "OR-ed" for in a binary encoding to denote which textures are actually being used/stored
   private static final int DIFFUSE_TEXTURE_BIT = 1;
   private static final int TRANSPARENT_TEXTURE_BIT = 2;

   /**
    * Writes a binary encoding to dataOut
    */
   public void writeBinary(DataOutput dataOut)  throws IOException {
      dataOut.writeUTF(name);
      dataOut.writeUTF(shader);
      for (int i=0; i<4; i++) dataOut.writeFloat(emissionColor[i]);
      for (int i=0; i<4; i++) dataOut.writeFloat(ambientColor[i]);
      for (int i=0; i<4; i++) dataOut.writeFloat(diffuseColor[i]);
      for (int i=0; i<4; i++) dataOut.writeFloat(specularColor[i]);
      dataOut.writeFloat(shininess);
      dataOut.writeUTF(opaqueMode);
      dataOut.writeBoolean(transparencyEnabled);
      
      int texBits = 0;  // 0 means: no textures at all
      if (diffuseTexture != null)     texBits |= DIFFUSE_TEXTURE_BIT;  // set diffuseTexture bit
      if (transparentTexture != null) texBits |= TRANSPARENT_TEXTURE_BIT;  // set transparentTexture bit
      dataOut.writeInt(texBits);
      if ((texBits & DIFFUSE_TEXTURE_BIT) != 0 ) {
         diffuseTexture.writeBinary(dataOut); 
      }
      if ((texBits & TRANSPARENT_TEXTURE_BIT) != 0 ) {
         transparentTexture.writeBinary(dataOut); 
      }
   }
    
   /**
    * Reads a binary encoding from dataIn
    */ 
   public void readBinary(DataInput dataIn) throws IOException {
      name = dataIn.readUTF().intern(); 
      shader = dataIn.readUTF().intern();  
      for (int i=0; i<4; i++) emissionColor[i] = dataIn.readFloat();
      for (int i=0; i<4; i++) ambientColor[i]  = dataIn.readFloat();
      for (int i=0; i<4; i++) diffuseColor[i]  = dataIn.readFloat();
      for (int i=0; i<4; i++) specularColor[i] = dataIn.readFloat();
      shininess = dataIn.readFloat();
      opaqueMode = dataIn.readUTF().intern();
      transparencyEnabled = dataIn.readBoolean();
      int texBits = dataIn.readInt();
      if ((texBits & DIFFUSE_TEXTURE_BIT) != 0 ) {
         diffuseTexture = new GTexture();
         diffuseTexture.readBinary(dataIn); 
         transferRepeatSettings(diffuseTexture);
      }
      if ((texBits & TRANSPARENT_TEXTURE_BIT) != 0 ) {
         transparentTexture = new GTexture();
         transparentTexture.readBinary(dataIn); 
         transferRepeatSettings(transparentTexture);
      }
   }  
      
} 

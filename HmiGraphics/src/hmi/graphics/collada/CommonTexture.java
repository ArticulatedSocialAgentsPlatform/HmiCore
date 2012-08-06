/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package hmi.graphics.collada;

import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.HashMap;

/** 
 * Storage of graphical representations, like raster data.
 * @author Job Zwiers
 */
public class CommonTexture extends ColladaElement {
 
   // attributes: id, name inherited from ColladaElement
   private String sampler2D; 
   private String texCoord = null;
   
   
   // child elements:
   private Extra extra;         // optional
   
   
   public CommonTexture() {
      super();
   }
   
   public CommonTexture(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }
 
   /**
    * appends the XML content
    */
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {  
      appendXMLStructure(buf, fmt, extra); 
      return buf;
   }

   /**
    * decodes the XML content
    */
   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Extra.xmlTag()))  {     // could include <technique profile="Maya">, for instance           
            extra = new Extra(getCollada(), tokenizer);               
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("CommonTexture: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }
      addColladaNode(extra);
   }
   
   /**
    * Returns the texture attribute, that refers to an Sampler2D actually.
    */
   public String getSampler2D() {
      return sampler2D;
   }
   
   /**
    * Returns tghe texCoord attribute
    */
   public String getTexCoord() {
      return texCoord;
   }
   
   public Max3DProfile getMax3DProfile() { return extra==null ? null : extra.getMax3DProfile();}
   
   public FColladaProfile getFColladaProfile() { return extra==null ? null : extra.getFColladaProfile();}
   
   public MayaProfile getMayaProfile() {  return extra==null ? null : extra.getMayaProfile();}
   
   public RenderMonkeyProfile getRenderMonkeyProfileProfile() { return extra==null ? null : extra.getRenderMonkeyProfileProfile();}
   

   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "texture", sampler2D);
      appendAttribute(buf, "texCoord", texCoord);
      return buf;
   }


   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {
      sampler2D = getRequiredAttribute("texture", attrMap, tokenizer);
      texCoord = getRequiredAttribute("texcoord", attrMap, tokenizer);
      super.decodeAttributes(attrMap, tokenizer);
   }

   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "texture";
 
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


}

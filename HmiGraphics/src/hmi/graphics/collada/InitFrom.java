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
public class InitFrom extends ColladaElement {
 
   private String imageRef; // ref to image (for init_from in surface, or  file name (for init_from in image)
   private int mip = 0;     // default miplevel, for surface only
   private int slice = 0;   // default slice, used for 3D textures for surface only
   private String face = null;// default: "POSITIVE_X"; used for cube maps for surface only
  
 
   public InitFrom() {
      super();
   }
   
   public InitFrom(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }
 
   public String getImageId() {
      return imageRef;
   }
 
   public String getImageFile() {
      return imageRef;
   }
   
   public int getMip() {
      return mip;
   }
   
   public int getSlice() {
      return slice;
   }
 
   public String getFace() {
      return face;
   }
       
   /**
    * appends the XML content
    */
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {  
      appendNewLine(buf);
      appendSpaces(buf, fmt);
      appendTab(buf);
      buf.append(imageRef);
      return buf;
   }

   /**
    * decodes the XML content
    */
   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      imageRef = tokenizer.takeCharData().trim();
   }
   
   /**
    * appends a String of attributes to buf.
    */
@Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      if (mip > 0) appendAttribute(buf, "mip", mip);
      if (slice > 0)  appendAttribute(buf, "slice", slice);
      if (face != null) appendAttribute(buf, "face", face);  
      return buf;
   }


   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {
      mip = getOptionalIntAttribute("height", attrMap, 0);
      slice = getOptionalIntAttribute("width", attrMap, 0);
      face = getOptionalAttribute("face", attrMap);
      super.decodeAttributes(attrMap, tokenizer);
   }

   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "init_from";
 
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

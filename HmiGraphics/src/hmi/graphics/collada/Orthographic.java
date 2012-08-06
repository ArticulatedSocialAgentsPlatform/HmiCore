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

/** 
 * Describes the field of view of an orthographic camera.
 * @author Job Zwiers
 */
public class Orthographic extends ColladaElement {
 
   // attributes: none
   
   // child elements
   private XMag         xmag;
   private YMag         ymag;
   private AspectRatio  aspectRatio;
   private ZNear        znear;
   private ZFar         zfar;
  
     
   public Orthographic(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer);  
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, xmag);
      appendXMLStructure(buf, fmt, ymag);
      appendXMLStructure(buf, fmt, aspectRatio);
      appendXMLStructure(buf, fmt, znear);
      appendXMLStructure(buf, fmt, zfar);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(XMag.xmlTag()))  {                
                 xmag = new XMag(getCollada(), tokenizer);             
         } else if (tag.equals(YMag.xmlTag()))  {                
                 ymag = new YMag(getCollada(), tokenizer);  
         } else if (tag.equals(AspectRatio.xmlTag()))  {                
                 aspectRatio = new AspectRatio(getCollada(), tokenizer);  
         } else if (tag.equals(ZNear.xmlTag()))  {                
                 znear = new ZNear(getCollada(), tokenizer);  
         } else if (tag.equals(ZFar.xmlTag()))  {                
                 zfar = new ZFar(getCollada(), tokenizer);  
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Orthographic: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }
      addColladaNode(xmag);   
      addColladaNode(ymag); 
      addColladaNode(aspectRatio); 
      addColladaNode(znear); 
      addColladaNode(zfar);   
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "orthographic";
 
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

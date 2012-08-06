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
 * Describes the field of view of an Perspective camera.
 * @author Job Zwiers
 */
public class Perspective extends ColladaElement {
 
   // attributes: none
   
   // child elements
   private XFov         xfov;
   private YFov         yfov;
   private AspectRatio aspectRatio;
   private ZNear        znear;
   private ZFar         zfar;
  
     
   public Perspective(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer);     
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, xfov);
      appendXMLStructure(buf, fmt, yfov);
      appendXMLStructure(buf, fmt, aspectRatio);
      appendXMLStructure(buf, fmt, znear);
      appendXMLStructure(buf, fmt, zfar);
      
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(XFov.xmlTag()))  {                
                 xfov = new XFov(getCollada(), tokenizer);             
         } else if (tag.equals(YFov.xmlTag()))  {                
                 yfov = new YFov(getCollada(), tokenizer);  
         } else if (tag.equals(AspectRatio.xmlTag()))  {                
                 aspectRatio = new AspectRatio(getCollada(), tokenizer);  
         } else if (tag.equals(ZNear.xmlTag()))  {                
                 znear = new ZNear(getCollada(), tokenizer);  
         } else if (tag.equals(ZFar.xmlTag()))  {                
                 zfar = new ZFar(getCollada(), tokenizer);  
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Perspective: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }
      addColladaNode(xfov);   
      addColladaNode(yfov); 
      addColladaNode(aspectRatio); 
      addColladaNode(znear); 
      addColladaNode(zfar);   
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "perspective";
 
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

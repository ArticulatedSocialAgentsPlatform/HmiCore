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
import java.util.ArrayList;

/** 
 * Declares a view into the scene graph.
 * @author Job Zwiers
 */
public class Camera extends ColladaElement {
 
   // attributes: id, name inherited from ColladaElement
   
   // child elements
   private Asset asset;
   private Optics optics; // required element
   private Imager imager;
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
     
   public Camera(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);     
      readXML(tokenizer); 
   }
 
    @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, asset);
      appendXMLStructure(buf, fmt, optics);
      appendXMLStructure(buf, fmt, imager);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Asset.xmlTag()))  {                
                 asset = new Asset(getCollada(), tokenizer); 
         } else if (tag.equals(Optics.xmlTag()))  {                
                 optics = new Optics(getCollada(), tokenizer);
         } else if (tag.equals(Imager.xmlTag()))  {                
                 imager = new Imager(getCollada(), tokenizer);
         } else if (tag.equals(Extra.xmlTag()))  {                
                 Extra extra = new Extra(getCollada(), tokenizer);
                 extras.add(extra);
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Camera: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }
      addColladaNode(asset);
      addColladaNode(optics);   
      addColladaNode(imager);   
      addColladaNodes(extras);  
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "camera";
 
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

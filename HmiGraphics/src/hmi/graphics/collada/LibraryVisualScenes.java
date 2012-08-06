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
import java.util.List;


/** 
 * The Collada library element for visual_scene elements.
 * @author Job Zwiers
 */
public class LibraryVisualScenes extends Library<VisualScene> {
 
   // child elements:
   private Asset asset;
   private ArrayList<VisualScene> visualScenes = new ArrayList<VisualScene>();
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   public LibraryVisualScenes() {
      super();
   }
   
   public LibraryVisualScenes(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }
 
   
   public List<VisualScene> getLibContentList() {
      return visualScenes;
   }
      
 

 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, asset);
      appendXMLStructureList(buf, fmt, visualScenes);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Asset.xmlTag()))  {                
                 asset = new Asset(getCollada(), tokenizer);   
         } else if (tag.equals(VisualScene.xmlTag()))  {                
                 visualScenes.add(new VisualScene(getCollada(), tokenizer)); 
         } else if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));    
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("LibraryVisualScenes: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      } 
      addColladaNode(asset);
      addColladaNodes(visualScenes);   
      addColladaNodes(extras);       
   }


   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "library_visual_scenes";
 
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

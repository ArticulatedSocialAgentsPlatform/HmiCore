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
 * @author Job Zwiers 
 */
public class PhysicsScene extends ColladaElement {
 
   // attributes: id, name, inherited from ColladaElement
   
   // child elements:
   private Asset asset;
   private TechniqueCore technique;
   private TechniqueCommon techniqueCommon;
   // instance_force_field, instance_physics_model ..
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   public PhysicsScene() {
      super();
   }
   
   public PhysicsScene(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);   
      readXML(tokenizer); 
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, asset);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Asset.xmlTag()))  {                
                 asset = new Asset(getCollada(), tokenizer);   
         } else if (tag.equals(TechniqueCore.xmlTag()))  {                
                 technique = new TechniqueCore(getCollada(), tokenizer); 
         } else if (tag.equals(TechniqueCommon.xmlTag()))  {                
                 techniqueCommon = new TechniqueCommon(getCollada(), tokenizer); 
         } else if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));    
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("PhysicsScene: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }  
      addColladaNode(asset);
      addColladaNode(technique);
      addColladaNode(techniqueCommon);
      addColladaNodes(extras);
   }


   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "physics_scene";
 
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

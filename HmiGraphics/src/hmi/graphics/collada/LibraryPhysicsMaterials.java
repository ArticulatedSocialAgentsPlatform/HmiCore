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
import java.util.HashMap;
import java.util.List;


/** 
 * @author Job Zwiers 
 */
public class LibraryPhysicsMaterials extends Library<PhysicsMaterial> {
 
   // attributes:
   // child elements:
   private Asset asset;
   private ArrayList<PhysicsMaterial> physicsMaterials = new ArrayList<PhysicsMaterial>();
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   
   public LibraryPhysicsMaterials(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }
 
 
   public List<PhysicsMaterial> getLibContentList() {
      return physicsMaterials;
   }
 
   /**
    * appends the XML content
    */
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {      
      return buf;
   }

   /**
    * decodes the XML content
    */
   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Asset.xmlTag()))  {                
                 asset = new Asset(getCollada(), tokenizer);   
         } else if (tag.equals(PhysicsMaterial.xmlTag()))  {                
                 physicsMaterials.add(new PhysicsMaterial(getCollada(), tokenizer)); 
         } else if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));  
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("LibraryPhysicsMaterials: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      } 
      addColladaNode(asset);
      addColladaNodes(physicsMaterials);   
      addColladaNodes(extras);       
   }
   
   

   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      return buf;
   }


   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {
      super.decodeAttributes(attrMap, tokenizer);
   }


 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "library_physics_materials";
 
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

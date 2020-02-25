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
public class LibraryForceFields extends Library<ForceField> {
 
   // attributes:
   
   // child elements:
  
   private Asset asset;
   private ArrayList<ForceField> forceFields = new ArrayList<ForceField>();
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   
   public LibraryForceFields(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer);  
   }
 
   public List<ForceField> getLibContentList() {
      return null;
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
         } else if (tag.equals(ForceField.xmlTag()))  {                
                 forceFields.add(new ForceField(getCollada(), tokenizer)); 
         } else if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));    
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("LibraryForceFields: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      } 
      addColladaNode(asset);
      addColladaNodes(forceFields);   
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
   private static final String XMLTAG = "library_force_fields";
 
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

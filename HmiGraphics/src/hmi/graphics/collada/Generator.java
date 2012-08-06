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
 * Describes a procedural surface generator
 * @author Job Zwiers
 */
public class Generator extends ColladaElement {
 
   // attributes: none
   
   // child elements:
   private ArrayList<Annotate> annotateList = new ArrayList<Annotate>();
   private Code code;
   private ShaderInclude include;
   private Name name;
   private ArrayList<Setparam> setparamList = new ArrayList<Setparam>();
   
   public Generator() {
      super();
   }
   
   public Generator(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);     
      readXML(tokenizer);  
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructureList(buf, fmt, annotateList);
      appendXMLStructure(buf, fmt, code);
      appendXMLStructure(buf, fmt, include);
      appendXMLStructure(buf, fmt, name);
      appendXMLStructureList(buf, fmt, setparamList);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Annotate.xmlTag()))  {                
            annotateList.add(new Annotate(getCollada(), tokenizer));  
         } else if (tag.equals(Code.xmlTag()))  {
            code = new Code(getCollada(), tokenizer);
         } else if (tag.equals(Include.xmlTag()))  {
            include = new ShaderInclude(getCollada(), tokenizer);
         } else if (tag.equals(Name.xmlTag()))  {
            name = new Name(getCollada(), tokenizer);
         } else if (tag.equals(Setparam.xmlTag()))  {                
            setparamList.add(new Setparam(getCollada(), tokenizer)); 
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Generator: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }  
      addColladaNodes(annotateList);
      addColladaNode(code);
      addColladaNode(include);
      addColladaNode(name);
      addColladaNodes(setparamList);
   }


   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "generator";
 
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

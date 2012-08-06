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

/** 
 * Declares and prepares a shader for execution in the renderering pipeline of a pass
 * @author Job Zwiers
 */
public class Shader extends ColladaElement {
 
   // attributes: 
   private String stage; // optional, platform specific., VERTEXPROGRAM or FRAGMENTPROGRAM
   
   // child elements:
   private ArrayList<Annotate> annotateList = new ArrayList<Annotate>();
   private CompilerTarget compilerTarget;       // optional
   private Name name;                             // required
   private CompilerOptions compilerOptions;     // optional
   private ArrayList<Bind> bindList = new ArrayList<Bind>();
   
   public Shader() {
      super();
   }
   
   public Shader(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer);   
   }
  
   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "stage", stage);
      return buf;
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {     
      stage = getOptionalAttribute("stage", attrMap);
      super.decodeAttributes(attrMap, tokenizer);
   }

   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructureList(buf, fmt, annotateList);
      appendXMLStructure(buf, fmt, compilerTarget);
      appendXMLStructure(buf, fmt, name);
      appendXMLStructure(buf, fmt, compilerOptions);
      appendXMLStructureList(buf, fmt, bindList);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Annotate.xmlTag()))  {                
                 annotateList.add(new Annotate(getCollada(), tokenizer));  
         } else if (tag.equals(CompilerTarget.xmlTag()))  {                
                 compilerTarget = new CompilerTarget(getCollada(), tokenizer); 
         } else if (tag.equals(Name.xmlTag()))  {                
                 name = new Name(getCollada(), tokenizer); 
         } else if (tag.equals(CompilerOptions.xmlTag()))  {                
                 compilerOptions = new CompilerOptions(getCollada(), tokenizer); 
         } else if (tag.equals(Bind.xmlTag()))  {                
                 bindList.add(new Bind(getCollada(), tokenizer));    
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Shader: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }  
      addColladaNodes(annotateList);
      addColladaNode(compilerTarget);
      addColladaNode(name);
      addColladaNode(compilerOptions);
      addColladaNodes(bindList);
   }

   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "shader";
 
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

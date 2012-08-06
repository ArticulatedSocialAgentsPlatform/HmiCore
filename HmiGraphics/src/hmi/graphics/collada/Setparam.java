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
 * Assigns a new value to a previously defined parameter.
 * Currently regarded as associating ref --> ValueType
 * @author Job Zwiers
 */
public class Setparam extends ColladaElement {
   
   // attributes: 
   private String ref;     // required
   private String program; // optional in technique for GLSL and CG, not valid in GLES , generator, or instance_effect
   
   // child elements:
   private ArrayList<Annotate> annotateList = new ArrayList<Annotate>();
   
   private ParamValue value;
   
//   // exactly  one of the  following elements is possible (incomplete list), and determines the param type:
//   public ValueType value_type;
//   public Usertype usertype;
//   public ParamArray array;
//   public Surface surface;
//   public Sampler1D sampler1D;
//   public Sampler2D sampler2D;
//   public Sampler3D sampler3D;
   
   
   public Setparam() {
      super();
   }
   
   public Setparam(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }
 
   public String getRef() {
      return ref;
   }
   
 
     public ParamValue getParamValue() {
      return value;
   }
 
 
   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "ref", ref);
      appendAttribute(buf, "program", program);
      return buf;
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {     
      ref       = getRequiredAttribute("ref", attrMap, tokenizer);
      program   = getOptionalAttribute("program", attrMap);
      super.decodeAttributes(attrMap, tokenizer);
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendNewLine(buf, fmt);
      appendXMLStructureList(buf, fmt, annotateList);
      value.appendContent(buf, fmt);     

      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Annotate.xmlTag()))  {                
                 annotateList.add(new Annotate(getCollada(), tokenizer));     
         } else {
            value.decodeContent(tokenizer);        
//         } else if (ValueType.xmlTags.contains(tag))  {                
//                 value_type = new ValueType(getCollada(), tokenizer);
//         } else if (tag.equals(Surface.xmlTag()))  {                
//                 surface = new Surface(getCollada(), tokenizer);
//         } else if (tag.equals(Sampler1D.xmlTag()))  {                
//                 sampler1D = new Sampler1D(getCollada(), tokenizer);
//         } else if (tag.equals(Sampler2D.xmlTag()))  {                
//                 sampler2D = new Sampler2D(getCollada(), tokenizer);         
//         } else if (tag.equals(Sampler3D.xmlTag()))  {                
//                 sampler3D = new Sampler3D(getCollada(), tokenizer);
//         } else {         
//            getCollada().warning(tokenizer.getErrorMessage("Setparam: skip : " + tokenizer.getTagName()));
//            tokenizer.skipTag();
         }
      }    
      addColladaNodes(annotateList);
      addColladaNode(value);
//      addColladaNode(value_type);
//      addColladaNode(surface);
//      addColladaNode(sampler1D);
//      addColladaNode(sampler2D);
//      addColladaNode(sampler3D);
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "setparam";
 
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

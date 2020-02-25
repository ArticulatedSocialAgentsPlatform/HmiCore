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

/** 
 * Creates a new named param object in the FX runtime ans assigns it a type, an initial value
 * and additional attributes at declaration time.
 * @author Job Zwiers
 */
public class Newparam extends ColladaElement {
   
   // attributes: sid, inherited from ColladaElement. Identifier for this parameter: required
  
   // child elements:
   private ArrayList<Annotate> annotateList = new ArrayList<Annotate>();
   private Semantic semantic; // optional
   private Modifier modifier;  // optional
   // exactly  one of the three following elements is required, and determines the param type:
//   public ValueType value_type;
//   public Usertype usertype;
//   public ParamArray array;
//   public Surface surface;
//   public Sampler1D sampler1D;
//   public Sampler2D sampler2D;
//   public Sampler3D sampler3D;
   
   private ParamValue value = new ParamValue();
    
   
   public Newparam() {
      super();
   }
   
   public Newparam(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }
 
   public ParamValue getParamValue() {
      return value;
   }

   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendNewLine(buf, fmt);
      appendXMLStructureList(buf, fmt, annotateList);
      appendXMLStructure(buf, fmt, semantic);
      appendXMLStructure(buf, fmt, modifier); 
      value.appendContent(buf, fmt);     
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Annotate.xmlTag()))  {                
                 annotateList.add(new Annotate(getCollada(), tokenizer));  
         } else if (tag.equals(Semantic.xmlTag()))  {                
                 semantic = new Semantic(getCollada(), tokenizer); 
         } else if (tag.equals(Modifier.xmlTag()))  {                
                 modifier = new Modifier(getCollada(), tokenizer);    
         } else {
            value.decodeContent(tokenizer);        
                 
//         } else if (tag.equals(Usertype.xmlTag()))  {                
//                 usertype = new Usertype(getCollada(), tokenizer); 
//         } else if (tag.equals(ParamArray.xmlTag()))  {                
//                 array = new ParamArray(getCollada(), tokenizer);
//         } else if (ValueType.xmlTags.contains(tag))  {                
//                 value_type = new ValueType(getCollada(), tokenizer);
//         } else if (tag.equals(Surface.xmlTag()))  {                
//                 surface = new Surface(getCollada(), tokenizer);
//         } else if (tag.equals(Sampler1D.xmlTag())) {
//            sampler1D = new Sampler1D(getCollada(), tokenizer);  
//         } else if (tag.equals(Sampler2D.xmlTag())) {
//            sampler2D = new Sampler2D(getCollada(), tokenizer);
//         } else if (tag.equals(Sampler3D.xmlTag())) {
//            sampler3D = new Sampler3D(getCollada(), tokenizer);         
//         } else {         
//            getCollada().warning(tokenizer.getErrorMessage("Newparam: skip : " + tokenizer.getTagName()));
//            tokenizer.skipTag();
         }
      }    
      addColladaNodes(annotateList);
      addColladaNode(semantic);
      addColladaNode(modifier);
//      addColladaNode(usertype);
//      addColladaNode(array); 
//      addColladaNode(value_type);
//      addColladaNode(surface);
//      addColladaNode(sampler1D);
//      addColladaNode(sampler2D);
//      addColladaNode(sampler3D);
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "newparam";
 
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

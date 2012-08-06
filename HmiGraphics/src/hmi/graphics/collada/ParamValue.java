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
import java.util.HashMap;

/** 
 * Represents a value for a Newparm or Setparam element.
 * @author Job Zwiers
 */
public class ParamValue extends ColladaElement {
   
  
   // exactly  one of the  following elements is possible (incomplete list), and determines the param type:
   private ValueType valueType;
   private Usertype usertype;
   private ParamArray array;
   private Surface surface;
   private Sampler1D sampler1D;
   private Sampler2D sampler2D;
   private Sampler3D sampler3D;
   
   /** Param types, like Sampler1D, ParamArray, ValueType etc. */
   public enum Type {ValueType, UserType, ParamArray, Surface, Sampler1D, Sampler2D, Sampler3D};
   
   private Type type;
   
   public ParamValue() {
      super();
   }
   
   public ParamValue(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }
 
  
   
   public ValueType getValueType() {
      return valueType;
   }
 
   public Sampler1D getSampler1D() {
      return sampler1D;
   }
   
   public Sampler2D getSampler2D() {
      return sampler2D;
   }
 
   public Sampler3D getSampler3D() {
      return sampler3D;
   }
 
   public Surface getSurface() {
      return surface;
   }
   
   public Usertype getUsertype() {
      return usertype;
   }
 
   public ParamArray getParamArray() {
      return array;
   }
 
   public Type getType() {
      return type;
   }
   
//   
//   public void setValue_Type(ValueType vt) {
//      valueType = vt;
//      type = Type.ValueType;
//   }
//

   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
//      appendAttribute(buf, "ref", ref);
//      appendAttribute(buf, "program", program);
      return buf;
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {     
//      ref       = getRequiredAttribute("ref", attrMap, tokenizer);
//      program   = getOptionalAttribute("program", attrMap);
      super.decodeAttributes(attrMap, tokenizer);
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
   //   appendNewLine(buf, fmt);
      appendXMLStructure(buf, fmt, valueType);
      appendXMLStructure(buf, fmt, usertype);
      appendXMLStructure(buf, fmt, array);
      appendXMLStructure(buf, fmt, surface);
      appendXMLStructure(buf, fmt, sampler1D);
      appendXMLStructure(buf, fmt, sampler2D);
      appendXMLStructure(buf, fmt, sampler3D);
      
      return buf;  
   }




   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         
         if (tag.equals(Usertype.xmlTag()))  {                
                 usertype = new Usertype(getCollada(), tokenizer); 
                 type = Type.UserType;
         } else if (tag.equals(ParamArray.xmlTag()))  {                
                 array = new ParamArray(getCollada(), tokenizer);
                 type = Type.ParamArray;
         } else if (ValueType.hasTag(tag))  {                
                 valueType = new ValueType(getCollada(), tokenizer);
                 type = Type.ValueType;
         } else if (tag.equals(Surface.xmlTag()))  {                
                 surface = new Surface(getCollada(), tokenizer);
                 type = Type.Surface;
         } else if (tag.equals(Sampler1D.xmlTag()))  {                
                 sampler1D = new Sampler1D(getCollada(), tokenizer);
                 type = Type.Sampler1D;
         } else if (tag.equals(Sampler2D.xmlTag()))  {                
                 sampler2D = new Sampler2D(getCollada(), tokenizer);    
                 type = Type.Sampler2D;     
         } else if (tag.equals(Sampler3D.xmlTag()))  {                
                 sampler3D = new Sampler3D(getCollada(), tokenizer);
                 type = Type.Sampler3D;
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Setparam: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }    
   
      addColladaNode(usertype);
      addColladaNode(array); 
      addColladaNode(valueType);
      addColladaNode(surface);
      addColladaNode(sampler1D);
      addColladaNode(sampler2D);
      addColladaNode(sampler3D);
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "paramvalue";
 
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

/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/

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

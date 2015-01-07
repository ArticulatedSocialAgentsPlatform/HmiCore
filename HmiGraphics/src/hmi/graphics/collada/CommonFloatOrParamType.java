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
import java.util.Map;

/** 
 * Scalar attribute, mainly for fixed-function shader elements inside profile_COMMON effects.
 * @author Job Zwiers
 */
public class CommonFloatOrParamType extends ColladaElement {
 
   private ColladaFloat colladaFloat; // either colladaFloat or param must be defined.
   private Param param;  // reference to a float parameter
   
   public CommonFloatOrParamType() {
      super();
   }
     
   public CommonFloatOrParamType(Collada collada)  {
      super(collada); 
   }
  
  
   /**
    * returns the parameter ref attribute, if defined
    */
   public String getParamRef() {
       if (param == null) return null;
       return param.getRef();        
   }

   /**
    * Return the float[4] array with rgba values, if defined
    */
   public float getFloat() {
      if (colladaFloat == null) return 0.0f;
      return colladaFloat.getFloatVal();
   }

   /** 
    * Returns the float[4] color array if it is defined, or else, tries
    * to look it up in the specified parameter map
    */
   public float getFloat(Map<String, ValueType> paramDefs) {
       if (colladaFloat != null) return colladaFloat.getFloatVal();
       if (param == null) {
         // produce warning
         getCollada().warning("Collada float: no value nor parameter defined");
         return 0.0f;
       }
       ValueType vt = paramDefs.get(param.getRef());
       if (vt.getBaseType() != ValueType.BaseType.Float || vt.getSize() != 1) {
         getCollada().warning("Collada Float parameter with wrong base type or wrong size:" + vt.getBaseType() + vt.getSize());
         return 0.0f;
       }
       return vt.getFloats()[0];
   }
  
  
  
  

   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, colladaFloat);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      String tag = tokenizer.getTagName();
      if (tag.equals(ColladaFloat.xmlTag()))  {                
          colladaFloat = new ColladaFloat(getCollada(), tokenizer);
      } else if (tag.equals(Param.xmlTag())) {
          param = new Param(getCollada(), tokenizer);
      } else {         
            getCollada().warning(tokenizer.getErrorMessage("CommonFloatOrParamType: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
      }
      addColladaNode(colladaFloat);
      addColladaNode(param);
   }
 
//   /**
//    * The XML Stag for XML encoding
//    */
//   public static String XMLTag = "commonfloatorparam";
//
// 
//   /**
//    * returns the XML Stag for XML encoding
//    */
//   @Override
//   public String getXMLTag() {
//      return XMLTag;
//   }
}

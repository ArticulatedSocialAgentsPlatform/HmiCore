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

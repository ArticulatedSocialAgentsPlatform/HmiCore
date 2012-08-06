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
import java.util.Map;

/** 
 * CommonColor attributes of fixed-function shader elements inside profile_COMMON effects.
 * This class is used as a base class for others, like Ambient, Emission, ...
 * @author Job Zwiers 
 */
public class CommonColorOrTextureType extends ColladaElement {
 
 
   private CommonColor color;  // color param and texture are mutually exclusive
   private Param param;         // a reference (by means of the ref attribute to (the sid of) 
                                // some earlier float4 parameter definition (newparam, possibly redefined by setparam)
   private CommonTexture texture; // seems to be specific to CommonColorOrTextureType only?
   
   public CommonColorOrTextureType() {
      super();
   }
     
     
   /**
    * The constructor used to invoke the ColladaElement constructor, passing in the Collada parameter
    */  
   public CommonColorOrTextureType(Collada collada)  {
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
   public float[] getColor4f() {
      if (color == null) return null;
      return color.getVec();
   }

   private static final int BASETYPESIZE = 4;

   /** 
    * Returns the float[4] color array if it is defined, or else, tries
    * to look it up in the specified parameter map
    */
   public float[] getColor4f(Map<String, ParamValue> paramDefs) {
       if (color != null) return color.getVec();
       if (param == null || paramDefs==null) {
         // produce warning--> replaced by warning in MaterialTranslator. (null colors are sometimes ok, for instance, when a texture is being used.
         //getCollada().warning("Shader " + getXMLTag() + ": neither a color nor a parameter defined");
         return null;
       }
       ParamValue val = paramDefs.get(param.getRef());
       if ( val.getType() != ParamValue.Type.ValueType) {
         getCollada().warning("Shader color parameter with wrong type:" + val.getType());
         return null;
       } else {
         ValueType vt = val.getValueType();
         if ( vt == null || vt.getBaseType() != ValueType.BaseType.Float || vt.getSize() != BASETYPESIZE) {
            getCollada().warning("Shader color parameter with wrong base type or size:" + vt.getBaseType() + vt.getSize());
            return null;
         } else {
            return vt.getFloats();
         }
       }
       
   }

   /**
    * Returns the texture, possibly null
    */
   public CommonTexture getTexture() {
       return texture;  
   }


   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, color);
      appendXMLStructure(buf, fmt, param);
      appendXMLStructure(buf, fmt, texture);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      String tag = tokenizer.getTagName();   
      if (tag.equals(CommonColor.xmlTag()))  {                
          color = new CommonColor(getCollada(), tokenizer);
      } else if (tag.equals(CommonTexture.xmlTag())) {
          texture = new CommonTexture(getCollada(), tokenizer); 
      } else if (tag.equals(Param.xmlTag())) {
          param = new Param(getCollada(), tokenizer); 
      } else {      
            getCollada().warning(tokenizer.getErrorMessage("CommonColorOrTextureType: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
      }
      addColladaNode(color);
      addColladaNode(texture);
      addColladaNode(param);
   }
 
 
}

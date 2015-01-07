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

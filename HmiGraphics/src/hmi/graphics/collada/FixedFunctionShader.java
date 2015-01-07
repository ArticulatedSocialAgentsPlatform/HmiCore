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
 * Shared implementation of FixedFunctionShader for standard Collada and Collada-FX
 * Base class for Phong, Blinn, Lambert, and Constant
 * @author Job Zwiers 
 */
public class FixedFunctionShader extends ColladaElement {
 
   private Emission emission;
   private Ambient ambient;
   private Diffuse diffuse; // could be a color, a parmeter, but could be a texture!
   private Specular specular;
   private Reflective reflective;
   private Transparent transparent;
   private Shininess shininess;
   private Reflectivity reflectivity;   
   private Transparency transparency;
   private IndexOfRefraction indexOfRefraction;
   
   private ShaderType shaderType;
   
   /**
    * A FixedFunction shader has one of the well known types, like Lambert, Phong, Blinn 
    */
   public enum ShaderType {Constant, Lambert, Phong, Blinn, Eye};
   
   public FixedFunctionShader() {
      super();
   }
   
   public FixedFunctionShader(ShaderType type) {
      super();
      shaderType = type;
   }
   
   public FixedFunctionShader(Collada collada, ShaderType type) {
      super(collada);  
      shaderType = type;    
   }
  
   public ShaderType getShaderType() { return shaderType; }
  
  
   public float[] getEmissionColor(Map<String, ParamValue> paramDefs) {
      return (emission == null) ? null : emission.getColor4f(paramDefs);      
   }
   
   public float[] getAmbientColor(Map<String, ParamValue> paramDefs) {
      return (ambient == null) ? null : ambient.getColor4f(paramDefs);      
   }
   
   public float[] getDiffuseColor(Map<String, ParamValue> paramDefs) {
      return (diffuse == null) ? null : diffuse.getColor4f(paramDefs);   
   }
   
   public CommonTexture getDiffuseTexture() {
      return (diffuse == null) ? null : diffuse.getTexture();   
   }
   
   
   public float[] getSpecularColor(Map<String, ParamValue> paramDefs) {
      return (specular == null) ? null : specular.getColor4f(paramDefs);      
   }
   
   public float[] getReflectiveColor(Map<String, ParamValue> paramDefs) {
      return (reflective == null) ? null : reflective.getColor4f(paramDefs);    
   }
   
   public boolean isTransparencyEnabled() {
      
      return isTransparencyEnabled(null);
   }
   
   public boolean isTransparencyEnabled( Map<String, ParamValue> paramMap) {
      if (transparency == null && transparent==null) return false;
      if (transparency != null && transparency.getTransparency() < 1.0) {
         logger.warn("Collada FixedFunctionShader with transparency = " + transparency.getTransparency() + " (NOT SUPPORTED)");   
         return true;
      }
      if (transparent != null) {
         if (transparent.getTexture() != null) return true;
         float[] col = transparent.getColor4f(paramMap);
         if (col != null) {
            String opaque = transparent.getOpaqueMode();
            if (opaque.equals("A_ONE")) {
               return (col[3] < 1.0f); // so if alpha == 1, it is NOT transparent after all.
            } else if (opaque.equals("RGB_ZERO")) { // rgb = (0, 0, 0) means opaque
               return col[0] != 0.0f || col[1] != 0.0f || col[2] != 0.0f;
               //return true; // use sometimes, in case of Collada exporter bugs
            } else {
                logger.warn("FixedFunctionShader: cannot determine transparency mode for opaque mode " + opaque);  
                return false;
            }       
         }   
      } 
      // else : transparency == 1.0 and/or transparent == null: not transparent
      return false;
   }
   
   
   /**
    * Equivalent to getTransparentColor(null)
    */
   public float[] getTransparentColor() {
      return (transparent == null) ? null : transparent.getColor4f(); 
   }      
      

   /**
    * returns the color from the Transparent element, either directly, or by parameter lookup
    * in the specified paremDefs map (if non-null)
    */
   public float[] getTransparentColor(Map<String, ParamValue> paramDefs) {
      return (transparent == null) ? null : transparent.getColor4f(paramDefs);    
   }
   
   public CommonTexture getTransparentTexture() {
      return (transparent == null) ? null : transparent.getTexture();   
   }
   
   public String getOpaqueMode() {
      return (transparent == null) ? null : transparent.getOpaqueMode();  
   }
   
   public float getShininess() {
      return (shininess==null) ? 0.0f : shininess.getFloat();  
   }
   
   public float getReflectivity() {
      return (reflectivity==null) ? 0.0f : reflectivity.getFloat();  
   }
   
   public float getTransparency() {
      return (transparency==null) ? 1.0f : transparency.getTransparency();   // 1.0 is the Collada default
   }
  
    public float getIndexOfRefraction() {
      return (indexOfRefraction==null) ? 0.0f : indexOfRefraction.getFloat(); 
   }
  
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, emission);
      appendXMLStructure(buf, fmt, ambient);
      appendXMLStructure(buf, fmt, diffuse);
      appendXMLStructure(buf, fmt, specular);  
      appendXMLStructure(buf, fmt, reflective); 
      appendXMLStructure(buf, fmt, transparent); 
      appendXMLStructure(buf, fmt, shininess); 
      appendXMLStructure(buf, fmt, reflectivity);    
      appendXMLStructure(buf, fmt, transparency);  
      appendXMLStructure(buf, fmt, indexOfRefraction); 
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Emission.xmlTag()))  {                
                 emission = new Emission(getCollada(), tokenizer);  
         } else if (tag.equals(Ambient.xmlTag()))  {                
                 ambient = new Ambient(getCollada(), tokenizer);  
         } else if (tag.equals(Diffuse.xmlTag()))  {                
                 diffuse = new Diffuse(getCollada(), tokenizer);     
         } else if (tag.equals(Specular.xmlTag()))  {                
                 specular = new Specular(getCollada(), tokenizer);   
         } else if (tag.equals(Reflective.xmlTag()))  {                
                 reflective = new Reflective(getCollada(), tokenizer);   
         } else if (tag.equals(Transparent.xmlTag()))  {                
                 transparent = new Transparent(getCollada(), tokenizer);                     
         } else if (tag.equals(Shininess.xmlTag()))  {                
                 shininess = new Shininess(getCollada(), tokenizer);   
         } else if (tag.equals(Reflectivity.xmlTag()))  {                
                 reflectivity = new Reflectivity(getCollada(), tokenizer);   
         } else if (tag.equals(Transparency.xmlTag()))  {                
                 transparency = new Transparency(getCollada(), tokenizer);     
         } else if (tag.equals(IndexOfRefraction.xmlTag()))  {                
                 indexOfRefraction = new IndexOfRefraction(getCollada(), tokenizer);     
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("FixedFunctionShader: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }   
      addColladaNode(emission);
      addColladaNode(ambient);
      addColladaNode(diffuse);
      addColladaNode(specular); 
      addColladaNode(reflective); 
      addColladaNode(transparent);    
      addColladaNode(shininess);  
      addColladaNode(reflectivity); 
      addColladaNode(transparency); 
      addColladaNode(indexOfRefraction);   
   }

 

}

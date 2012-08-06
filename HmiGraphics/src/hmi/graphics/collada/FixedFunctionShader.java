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
   public enum ShaderType {Constant, Lambert, Phong, Blinn};
   
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

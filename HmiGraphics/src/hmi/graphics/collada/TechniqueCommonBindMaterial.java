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
 * The TechniqueCommon for BindMaterial, that contain a list of  InstanceMaterial.
 * @author Job Zwiers
 */
public class TechniqueCommonBindMaterial extends ColladaElement {
   
  private static final int LISTSIZE = 4; 
   
  private ArrayList<InstanceMaterial> instanceMaterials = new ArrayList<InstanceMaterial>(LISTSIZE);
       
   public TechniqueCommonBindMaterial() {
      super();
   }
   

   public TechniqueCommonBindMaterial(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }


   /**
    * returns the InstanceMaterial for the specified material symbol,
    * or null, when there is no such InstanceMaterial
    */
   public InstanceMaterial getInstanceMaterial(String materialId) {
      for ( InstanceMaterial im : instanceMaterials) {
          if (im.getSymbol().equals(materialId)) return im;
      }
      return null;
   }


   /**
    * Searches for the targer corrsponding to a specified symbol, as defined
    * by the list of InstanceMaterial for this TechniqueCommon element
    */
   public String getTarget(String symbol) {
      for ( InstanceMaterial im : instanceMaterials) {
          if (im.getSymbol().equals(symbol)) return im.getTarget();
      }
      return null;
   }

   public HashMap<String, String> getSymbolTargetMap() {
      HashMap<String, String> map = new HashMap<String, String>();
      for ( InstanceMaterial im : instanceMaterials) {
           map.put(im.getSymbol(), im.getTarget());
      } 
      return map;
   }


   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      String prof   = getOptionalAttribute("profile", attrMap); // a null value will be interpreted as "COMMON"
      if (prof != null && ! prof.equals("COMMON") ) {  
         getCollada().warning("common_profile with profile attribute: " + prof + "  (ignored)");
      }
      super.decodeAttributes(attrMap, tokenizer);
   }
 

   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructureList(buf, fmt, instanceMaterials);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(InstanceMaterial.xmlTag()))  {                
                 instanceMaterials.add(new InstanceMaterial(getCollada(), tokenizer));                
         } else {      
            getCollada().warning(tokenizer.getErrorMessage("TechniqueCommonBindMaterial: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }
      addColladaNodes(instanceMaterials);   
      
   }


   /*
    * The XML Stag for XML encoding
    * NB. This is a shared tag with other Technique_Common_XYZ classes!!!
    */
   private static final String XMLTAG = "technique_common";
 
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

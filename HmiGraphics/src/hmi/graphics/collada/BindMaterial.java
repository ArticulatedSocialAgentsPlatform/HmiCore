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
 * Declaration of generic control information, for either Skins or Morphs.
 * binds a specific material to a piece of geometry, binding varying 
 * uniform parameters at the same time.
 * @author Job Zwiers
 */
public class BindMaterial extends ColladaElement {
    
   private ArrayList<Param> params;
   private ArrayList<Extra> extras;
   private TechniqueCommonBindMaterial techniqueCommon; // special purpose TechniqueCommon
   private ArrayList<TechniqueCore> techniques;   // (FX)
   

   /**
    * Default constructor
    */   
   public BindMaterial() {
      super();
   }

   /**
    * Constructor used to create a BindMaterial Object from XML
    */   
   public BindMaterial(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }
   
//   /**
//    * Returns the list of Instance_Materials inside the techniqueCommon element
//    */
//   public List<InstanceMaterial> getInstanceMaterials() {
//      return techniqueCommon.instanceMaterials;
//   }

  

   /**
    * returns the InstanceMaterial (from the TechniqueCommon)  for the specified material symbol,
    * or null, when there is no such InstanceMaterial
    */
   public InstanceMaterial getInstanceMaterial(String materialId) {
      return techniqueCommon.getInstanceMaterial(materialId);
   }

   /**
    * Returns the material target for the specified symbol, from the list
    * of InstanceMaterial inside the TechniqueCommon element.
    */
   public String getMaterialTarget(String symbol) {
      if (techniqueCommon == null) return null;
      return techniqueCommon.getTarget(symbol);
   }

   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, techniqueCommon);
      appendXMLStructureList(buf, fmt, params);
      appendXMLStructureList(buf, fmt, techniques);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(TechniqueCommonBindMaterial.xmlTag()))  {                
                 techniqueCommon = new TechniqueCommonBindMaterial(getCollada(), tokenizer);  
         } else if (tag.equals(Param.xmlTag()))  {                
                 params.add(new Param(getCollada(), tokenizer));   
         } else if (tag.equals(TechniqueCore.xmlTag()))  {                
                 techniques.add(new TechniqueCore(getCollada(), tokenizer));              
         } else if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));    
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("BindMaterial: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }  
      addColladaNode(techniqueCommon); 
      addColladaNodes(params);   
      addColladaNodes(techniques);         
      addColladaNodes(extras);      
   }

 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "bind_material";
 
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

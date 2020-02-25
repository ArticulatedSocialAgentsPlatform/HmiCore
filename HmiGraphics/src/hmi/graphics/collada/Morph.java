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
import java.util.HashMap;

/** 
 * Declares the data required to blend between sets of static meshes.
 * @author Job Zwiers
 */
public class Morph extends ColladaElement {
 
   // attributes: 
   private String source;  // base mesh url of form #url. required.
   private String method;  // either NORMALIZED (default) or RELATIVE.
   
   //child elements: 
   // at least two Inputs are required, with semantics= "MORPH_TARGET" and semantics="MORPH_WEIGHT".
   // at least two sources are required: morph targets, and morph weights.
   private ArrayList<Source> sources = new ArrayList<Source>();
   private Targets targets; // required
   private ArrayList<Extra> extras = new ArrayList<Extra>();

   /**
    * Default constructor
    */        
   public Morph() {
      super();
   }

   /**
    * Constructor used to create a Morph Object from XML
    */         
   public Morph(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }
 
   /**
    * returns the Geometry from the libraries, for the source url for the base geometry of the morph
    */
   public Geometry getBaseGeometry() {
      if (getCollada() == null || getCollada().getLibrariesGeometries() == null) return null;
       return getCollada().getLibItem(getCollada().getLibrariesGeometries(), source);     
   }
 
 
   /**
    * returns the Geometry from the libraries, for the url for the target geometries of the morph
    */
   public Geometry[] getTargetGeometries() {
      if (getCollada() == null || getCollada().getLibrariesGeometries() == null) return null;
      String[] targetIds = getMorphTargetIds();
      if (targetIds == null) return null;
      Geometry[] result = new Geometry[targetIds.length];
      for (int i=0; i<result.length; i++) {
         result[i] = getCollada().getLibItem(getCollada().getLibrariesGeometries(), targetIds[i]);     
      }
      return result;
   }
 

   /**
    * Returns the Id of the base mesh
    */
   public String getMorphBaseId() {
      return urlToId(source);  
   }


   /**
    * Returns the morphing method: either NORMALIZED or RELATIVE.
    */
   public String getMorphMethod() {
      return method;
   }

   /**
    * Returns a String array with the ids of the morh targets (i.e. ids of Meshes)
    */
   public String[] getMorphTargetIds() {
      if (targets == null) {
          getCollada().warning("Morph without targets");
          return new String[0]; 
      }
      String srcId = urlToId(targets.getMorphTargetSource());
      for (Source src : sources) {
          if (src.getId().equals(srcId)) {
               src.resolve();
               return src.getHomogeneousIDREFData();     
          }
      }
      return new String[0];  
   }

      /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "source", source);
      appendAttribute(buf, "method", method);      
      return buf;
   }


   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {
      source = getRequiredAttribute("source", attrMap, tokenizer);
      method = getOptionalAttribute("method", attrMap, "NORMALIZED");
      super.decodeAttributes(attrMap, tokenizer);
   }

   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructureList(buf, fmt, sources);
      appendXMLStructure(buf, fmt, targets);
      appendXMLStructureList(buf, fmt, extras);  
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Source.xmlTag()))  {                
            sources.add(new Source(getCollada(), tokenizer)); 
         } else if (tag.equals(Targets.xmlTag()))  {    
            targets = new Targets(getCollada(), tokenizer);
         } else if (tag.equals(Extra.xmlTag()))  {                
            extras.add(new Extra(getCollada(), tokenizer)); 
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Morph: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }    
      addColladaNodes(sources);
      addColladaNode(targets);
      addColladaNodes(extras);  
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "morph";
 
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

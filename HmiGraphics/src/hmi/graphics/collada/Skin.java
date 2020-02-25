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
public class Skin extends ColladaElement {
 
   // attributes: 
   private String source;  // uri reference to the base/bind-shape mesh (static or morphed), form: #url. required.

   //child elements: 

   private BindShapeMatrix bindShapeMatrix; // optional
   // at least three sources are required: Joints, Weights, "Inv_bind_mats"
   private ArrayList<Source> sources = new ArrayList<Source>();
   private Joints joints; // required
   private ColladaVertexWeights vertexWeights; // required
   private ArrayList<Extra> extras = new ArrayList<Extra>();

   /**
    * Default constructor
    */        
   public Skin() {
      super();
   }


   /**
    * Constructor used to create a Skin Object from XML
    */         
   public Skin(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }
 
   /**
    * returns the source
    */
   public String getSource() {
      return source;  
   }
 
   /**
    * Returns the joint names, or null if not defined.
    */
   public String[] getJointSIDs() {
       if (joints == null) {
           getCollada().warning("Collada Translator: skin for " + source + " has no joints"); 
           return null;
       }
       return joints.getJointSIDs();  
   }
 
   public float[] getInvBindMatrices() {
    if (joints == null) {
           getCollada().warning("Collada Translator: skin for " + source + " has no joints"); 
           return null;
       }
       return joints.getInvBindMatrices();  
   }
 
 
   public int[] getJointIndices() {
      if (vertexWeights == null) {
           getCollada().warning("Collada Translator: skin for " + source + " has no vertex weights"); 
           return null;
      }  
      return vertexWeights.getJointIndices();
   }
 
   public float[] getJointWeights() {
      if (vertexWeights == null) {
           getCollada().warning("Collada Translator: skin for " + source + " has no vertex weights"); 
           return null;
      }  
      return vertexWeights.getJointWeights();
   }
 
 
   public int[] getVCount() {
      if (vertexWeights == null) {
           getCollada().warning("Collada Translator: skin for " + source + " has no vertex weights"); 
           return null;
      }  
      return vertexWeights.getVCount();
   }   
   
 
   public float[] getBindShapeMatrix() {
      return bindShapeMatrix.getMat4f();
   }
 
 
   /**
    * returns the Geometry from the libraries, for the source url
    * This will return null when the source url refers to a morph controller, rather than a geometry
    */
   public Geometry getGeometry() {
      if (getCollada() == null || getCollada().getLibrariesGeometries() == null) return null;
       return getCollada().getLibItem(getCollada().getLibrariesGeometries(), source);     
   }
 
   /**
    * returns the (morph-) Controller from the libraries, for the source url,
    * or null, when the source url refers to a geometry.
    */
   public Controller getController() {
      if (getCollada() == null || getCollada().getLibrariesControllers() == null) return null;
       return getCollada().getLibItem(getCollada().getLibrariesControllers(), source);     
   }
 
   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "source", source);   
      return buf;
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {
      source = getOptionalAttribute("source", attrMap);
      super.decodeAttributes(attrMap, tokenizer);
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, bindShapeMatrix);
      appendXMLStructureList(buf, fmt, sources);
      appendXMLStructure(buf, fmt, joints);
      appendXMLStructure(buf, fmt, vertexWeights);
      appendXMLStructureList(buf, fmt, extras);  
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(BindShapeMatrix.xmlTag()))  {                
            bindShapeMatrix = new BindShapeMatrix(getCollada(), tokenizer);
         } else if (tag.equals(Source.xmlTag()))  {    
            sources.add(new Source(getCollada(), tokenizer)); 
         } else if (tag.equals(Joints.xmlTag()))  {                
            joints = new Joints(getCollada(), tokenizer);
         } else if (tag.equals(ColladaVertexWeights.xmlTag()))  {                
            vertexWeights = new ColladaVertexWeights(getCollada(), tokenizer);
         } else if (tag.equals(Extra.xmlTag()))  {                
            extras.add(new Extra(getCollada(), tokenizer)); 
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Skin: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }    
      addColladaNode(bindShapeMatrix);
      addColladaNodes(sources);
      addColladaNode(joints);
      addColladaNode(vertexWeights);
      addColladaNodes(extras);  
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "skin";
 
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

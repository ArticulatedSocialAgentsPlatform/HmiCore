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


/** 
 * Provides a static declaration of all the render states ans settings for one rendering pipeline.
 * @author Job Zwiers
 */
public class Pass extends ColladaElement {
 
   // attributes: sid inherited from ColladaElement
   
   // child elements:
   private ArrayList<Annotate>       annotateList       = new ArrayList<Annotate>();
   private ArrayList<ColorTarget>    colorTargetList    = new ArrayList<ColorTarget>();
   private ArrayList<DepthTarget>    depthTargetList    = new ArrayList<DepthTarget>();
   private ArrayList<StencilTarget>  stencilTargetList  = new ArrayList<StencilTarget>();
   private ArrayList<ColorClear>     colorClearList     = new ArrayList<ColorClear>();
   private ArrayList<DepthClear>     depthClearList     = new ArrayList<DepthClear>();
   private ArrayList<StencilClear>   stencilClearList   = new ArrayList<StencilClear>();
   private ArrayList<Draw>           drawList           = new ArrayList<Draw>();
   private ArrayList<Shader>         shaderList         = new ArrayList<Shader>();
   private ArrayList<Extra>          extras             = new ArrayList<Extra>();
   
   public Pass() {
      super();
   }
   
   public Pass(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }

   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructureList(buf, fmt, annotateList);
      appendXMLStructureList(buf, fmt, colorTargetList);
      appendXMLStructureList(buf, fmt, depthTargetList);
      appendXMLStructureList(buf, fmt, stencilTargetList);
      appendXMLStructureList(buf, fmt, colorClearList);
      appendXMLStructureList(buf, fmt, depthClearList);
      appendXMLStructureList(buf, fmt, stencilClearList);
      appendXMLStructureList(buf, fmt, drawList);
      appendXMLStructureList(buf, fmt, shaderList);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Annotate.xmlTag()))  {                
                 annotateList.add(new Annotate(getCollada(), tokenizer));  
         } else if (tag.equals(ColorTarget.xmlTag()))  {                
                 colorTargetList.add(new ColorTarget(getCollada(), tokenizer)); 
         } else if (tag.equals(DepthTarget.xmlTag()))  {                
                 depthTargetList.add(new DepthTarget(getCollada(), tokenizer)); 
         } else if (tag.equals(StencilTarget.xmlTag()))  {                
                 stencilTargetList.add(new StencilTarget(getCollada(), tokenizer)); 
         } else if (tag.equals(ColorClear.xmlTag()))  {                
                 colorClearList.add(new ColorClear(getCollada(), tokenizer)); 
         } else if (tag.equals(DepthClear.xmlTag()))  {                
                 depthClearList.add(new DepthClear(getCollada(), tokenizer)); 
         } else if (tag.equals(StencilClear.xmlTag()))  {                
                 stencilClearList.add(new StencilClear(getCollada(), tokenizer)); 
         } else if (tag.equals(Draw.xmlTag()))  {                
                 drawList.add(new Draw(getCollada(), tokenizer));         
         } else if (tag.equals(Shader.xmlTag()))  {                
                 shaderList.add(new Shader(getCollada(), tokenizer)); 
         } else if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));    
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Pass: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }  
      addColladaNodes(annotateList);
      addColladaNodes(colorTargetList);
      addColladaNodes(depthTargetList);
      addColladaNodes(stencilTargetList);
      addColladaNodes(colorClearList);
      addColladaNodes(depthClearList);
      addColladaNodes(stencilClearList);
      addColladaNodes(drawList);
      addColladaNodes(shaderList);
      addColladaNodes(extras);
   }


   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "pass";
 
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

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

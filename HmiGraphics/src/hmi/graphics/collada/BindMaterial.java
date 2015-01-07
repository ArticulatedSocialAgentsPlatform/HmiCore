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

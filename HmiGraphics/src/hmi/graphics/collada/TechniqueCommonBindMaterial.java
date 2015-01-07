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

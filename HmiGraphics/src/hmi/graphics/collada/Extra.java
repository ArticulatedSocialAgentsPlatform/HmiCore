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
import java.util.List;

/** 
 * @author Job Zwiers
 */
public class Extra extends ColladaElement {
 
   // attributes: id, name inherited from ColladaElement
   private String type;
  
   
   // child elements:
   private Asset asset;         // optional
   private List<TechniqueCore> techniques = new ArrayList<TechniqueCore>(4); // required element(s)
   // possible profiles inside <tecnique profile="..."> :
   private Max3DProfile max3DProfile;
   private FColladaProfile fcolladaProfile;
   private MayaProfile mayaProfile;
   private RenderMonkeyProfile rendermonkeyProfile;
   private ElckerlycProfile elckerlycProfile;
   
   public Extra() {
      super();
   }
   
   public Extra(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);   
      readXML(tokenizer);  
   }
 
 
 
   public Max3DProfile getMax3DProfile() { return max3DProfile; }
   
   public FColladaProfile getFColladaProfile() { return fcolladaProfile; }
   
   public MayaProfile getMayaProfile() {  return mayaProfile; }
   
   public RenderMonkeyProfile getRenderMonkeyProfileProfile() { return rendermonkeyProfile; }
   
   public ElckerlycProfile getElckerlycProfile() { return elckerlycProfile; }
   
 
   /**
    * appends the XML content
    */
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {  
      appendXMLStructure(buf, fmt, asset); 
      for (TechniqueCore technique : techniques) {
         appendXMLStructure(buf, fmt, technique);    
      }
      return buf;
   }

   /**
    * decodes the XML content
    */
   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Asset.xmlTag()))  {                
                 asset = new Asset(getCollada(), tokenizer);             
         } else if (tag.equals(TechniqueCore.xmlTag()))  {                
                 TechniqueCore  technique = new TechniqueCore(getCollada(), tokenizer);  
                 techniques.add(technique);
                 if (technique.getProfile().equals("MAX3D")) {
                    max3DProfile = technique.getMax3DProfile();
                 } else if (technique.getProfile().equals("MAYA")) {
                    mayaProfile = technique.getMayaProfile();
                 } else if (technique.getProfile().equals("FCOLLADA")) {
                     fcolladaProfile = technique.getFColladaProfile();
                 } else if (technique.getProfile().equals("RenderMonkey")) {
                    rendermonkeyProfile = technique.getRendermonkeyProfile();  
                 } else if (technique.getProfile().equals("Elckerlyc")) {
                    elckerlycProfile = technique.getElckerlycProfile();
                 }          
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Extra: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }
   }
   
   

   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "type", type);
      return buf;
   }


   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {
      type = getOptionalAttribute("type", attrMap);
      super.decodeAttributes(attrMap, tokenizer);
   }

   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "extra";
 
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

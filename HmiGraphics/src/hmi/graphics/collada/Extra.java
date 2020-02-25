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

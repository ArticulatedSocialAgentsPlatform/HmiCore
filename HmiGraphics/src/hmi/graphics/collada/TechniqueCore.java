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
import hmi.xml.XMLStructure;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/** 
 * Implementation of TechniqueCore for core Collada  (but NOT for Collada-FX)
 * Basically, anything is allowed here, as long as it's XML. Need not even be Collada xml
 * @author Job Zwiers
 */
public class TechniqueCore extends ColladaElement {
 
   private String profile; 
   private String xmlns;

   
   private ArrayList<XMLStructure> children ; // anything that we don't know  goes in here
   private ArrayList<Extra> extras;
   private Asset asset;
    
   private Max3DProfile max3DProfile;
   private FColladaProfile fcolladaProfile;
   private MayaProfile mayaProfile;
   private RenderMonkeyProfile rendermonkeyProfile;
   private ElckerlycProfile elckerlycProfile;

   public TechniqueCore() {
      super();
   }
   
   public TechniqueCore(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }
   
   public String getProfile() { return profile; }
   
   public Max3DProfile getMax3DProfile() { return max3DProfile; }
   
   public FColladaProfile getFColladaProfile() { return fcolladaProfile; }
   
   public MayaProfile getMayaProfile() {  return mayaProfile; }
   
   public RenderMonkeyProfile getRendermonkeyProfile() { return rendermonkeyProfile; }
   
   public ElckerlycProfile getElckerlycProfile() { return elckerlycProfile; }
 
   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "profile", profile);
      appendAttribute(buf, "xmlns", xmlns);
      return buf;
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      profile   = getRequiredAttribute("profile", attrMap, tokenizer); 
      xmlns = getOptionalAttribute("xmlns", attrMap);
      super.decodeAttributes(attrMap, tokenizer);
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, asset);
      if (profile.equals("FCOLLADA")) {
         fcolladaProfile.appendContent(buf, fmt);
      } else if (profile.equals("MAX3D")) {
         max3DProfile.appendContent(buf, fmt);
      } else if (profile.equals("MAYA")) {
         mayaProfile.appendContent(buf, fmt);
      } else if (profile.equals("RenderMonkey")) {
         rendermonkeyProfile.appendContent(buf, fmt);
      } else if (profile.equals("Elckerlyc")) {
         elckerlycProfile.appendContent(buf, fmt);
      } 
      appendXMLStructureList(buf, fmt, extras);
      appendXMLStructureList(buf, fmt, children);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      if (profile.equals("FCOLLADA")) {
         fcolladaProfile = new FColladaProfile(getCollada(), tokenizer);
      } else if (profile.equals("MAX3D")) {
         max3DProfile = new Max3DProfile(getCollada(), tokenizer);
      } else if (profile.equals("MAYA")) {
         mayaProfile = new MayaProfile(getCollada(), tokenizer);
      } else if (profile.equals("RenderMonkey")) {
         rendermonkeyProfile = new RenderMonkeyProfile(getCollada(), tokenizer);
      } else if (profile.equals("Elckerlyc")) {
         elckerlycProfile = new ElckerlycProfile(getCollada(), tokenizer);
      } else {
         while (tokenizer.atSTag()) {
            String tag = tokenizer.getTagName();
            if (tag.equals(Asset.xmlTag()))  {                
                    asset = new Asset(getCollada(), tokenizer);  
            } else if (tag.equals(Extra.xmlTag()))  {   
               if (extras == null)  extras = new ArrayList<Extra>();          
                    extras.add(new Extra(getCollada(), tokenizer));                
            } else {         
               getCollada().warning(tokenizer.getErrorMessage("Technique profile " + profile + ": skip : " + tokenizer.getTagName()));
               tokenizer.skipTag();
            }
         }   
      
      }
      addColladaNode(asset);
      addColladaNode(fcolladaProfile);
      addColladaNode(max3DProfile);
      addColladaNode(mayaProfile);
      addColladaNode(rendermonkeyProfile);
      addColladaNodes(extras);
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "technique";
 
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

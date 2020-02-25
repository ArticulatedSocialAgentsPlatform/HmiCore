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
 * DEclares the instantiation of a Collada material resource.
 * @author Job Zwiers
 */
public class InstanceEffect extends ColladaElement {
 
   // attributes: sid, name, inherited from ColladaElement
   private String url;
   
   // child elements:
   private ArrayList<TechniqueHint> techniqueHintList;
   private ArrayList<Setparam> setparamList;
   private ArrayList<Extra> extras;
   
   public InstanceEffect() {
      super();
   }
   
   public InstanceEffect(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer);      
   }
 
   /**  returns the url  */
   public String getURL() {
      return url;
   }

   /** returns the setparamList  */
   public ArrayList<Setparam>  getSetParamList() {
      return setparamList;
   }

   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "url", url);
      return buf;
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      url     = getRequiredAttribute("url", attrMap, tokenizer);
      super.decodeAttributes(attrMap, tokenizer);
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructureList(buf, fmt, techniqueHintList);
      appendXMLStructureList(buf, fmt, setparamList);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(TechniqueHint.xmlTag()))  {       
            if (techniqueHintList == null) techniqueHintList = new ArrayList<TechniqueHint>();      
            techniqueHintList.add(new TechniqueHint(getCollada(), tokenizer));  
         } else if (tag.equals(Setparam.xmlTag()))  {
            if (setparamList == null) setparamList = new ArrayList<Setparam>();
            setparamList.add(new Setparam(getCollada(), tokenizer));
         } else if (tag.equals(Extra.xmlTag()))  {    
            if (extras == null) extras = new ArrayList<Extra>();            
            extras.add(new Extra(getCollada(), tokenizer)); 
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("InstanceEffect: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }  
      addColladaNodes(techniqueHintList);
      addColladaNodes(setparamList);
      addColladaNodes(extras);
   }
 
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "instance_effect";
 
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

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
 * Instantiation of a Collada Controller resource.
 * @author Job Zwiers
 */
public class InstanceController extends ColladaElement {
 
   // attributes:
   // sid and name from ColladaElement
   private String url;
   
   // child elements:
   private BindMaterial bindMaterial;
   private ArrayList<Skeleton> skeletons = new ArrayList<Skeleton>();
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   public InstanceController() {
      super();
   }
   
   public InstanceController(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);    
      readXML(tokenizer); 
   }
 
   /**  returns the url  */
   public String getURL() {
      return url;
   }
 
   /**
    * Returns the  Controller referenced by the url, from a getCollada() library_controllers.
    */
   public Controller getController() {
      if (getCollada() == null || getCollada().getLibrariesControllers() == null) return null;
      return getCollada().getLibItem(getCollada().getLibrariesControllers(), url);
   }
 
 
 
   /**
    * Returns the BindMaterial, which could be null
    */
   public BindMaterial getBindMaterial() {
      return bindMaterial;
   }
   
   /**
    * Returns the list of skeletons
    */
   public List<Skeleton> getSkeletons() {
      return skeletons;
   }
 
 
   public String[] getSkeletonIds() {
      String[] result = new String[skeletons.size()];
      int i=0;
      for (Skeleton skel : skeletons) {
         result[i] = skel.getId();
         i++;
       }
      return result; 
   }
 
   /**
    * Returns the list of Skeleton urls
    */
   public List<String> getSkeletonURLs() {
       ArrayList<String> result = new ArrayList<String>(skeletons.size());
       for (Skeleton skel : skeletons) {
           result.add(skel.getURL()); 
       }
       return result;  
   }
 
   /**
    * returns true false, to denote whether there are any child elements.
    */
   @Override
   public boolean hasContent() { return (bindMaterial != null || skeletons.size() > 0 || extras.size() > 0); }

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
       appendXMLStructureList(buf, fmt, skeletons);
       appendXMLStructure(buf, fmt, bindMaterial);
       appendXMLStructureList(buf, fmt, extras);
       return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(BindMaterial.xmlTag()))  {                
                 bindMaterial = new BindMaterial(getCollada(), tokenizer);   
         } else if (tag.equals(Skeleton.xmlTag()))  {  
                 skeletons.add(new Skeleton(getCollada(), tokenizer));
         } else if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));    
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("InstanceController: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }  
      addColladaNode(bindMaterial);  
      addColladaNodes(skeletons); 
      addColladaNodes(extras);      
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "instance_controller";
 
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

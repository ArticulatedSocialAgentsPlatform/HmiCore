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

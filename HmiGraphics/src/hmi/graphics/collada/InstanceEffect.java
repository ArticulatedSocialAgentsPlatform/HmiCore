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

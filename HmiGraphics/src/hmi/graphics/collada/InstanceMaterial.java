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
 * Declares the instantiation of a Collada material resource.
 * @author Job Zwiers
 */
public class InstanceMaterial extends ColladaElement {
 
   // attributes: sid, name, inherited from ColladaElement
   private String target; // url  required
   private String symbol;  // required
   
   // child elements:
   private ArrayList<Bind> bindList = new ArrayList<Bind>();
   private ArrayList<BindVertexInput> bindVertexInputList = new ArrayList<BindVertexInput>();
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   public InstanceMaterial() {
      super();
   }
   
   public InstanceMaterial(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);  
      readXML(tokenizer);      
   }
 
   public String getSymbol() {
      return symbol;
   }
   
   public String getTarget() {
      return target;
   }
   
   public ArrayList<Bind> getBindList() {
      return bindList;
   }
   
   public ArrayList<BindVertexInput> getBindVertexInputList() {
      return bindVertexInputList;
   }
 

   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "target", target);
      appendAttribute(buf, "symbol", symbol);
      return buf;
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      target = getRequiredAttribute("target", attrMap, tokenizer);
      symbol = getRequiredAttribute("symbol", attrMap, tokenizer);
      super.decodeAttributes(attrMap, tokenizer);
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructureList(buf, fmt, bindList);
      appendXMLStructureList(buf, fmt, bindVertexInputList);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Bind.xmlTag()))  {                
            bindList.add(new Bind(getCollada(), tokenizer));  
         } else if (tag.equals(BindVertexInput.xmlTag()))  {
            bindVertexInputList.add(new BindVertexInput(getCollada(), tokenizer));
         } else if (tag.equals(Extra.xmlTag()))  {                
            extras.add(new Extra(getCollada(), tokenizer)); 
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("InstanceMaterial: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }  
      addColladaNodes(bindList);
      addColladaNodes(bindVertexInputList);
      addColladaNodes(extras);
   }
 
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "instance_material";
 
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

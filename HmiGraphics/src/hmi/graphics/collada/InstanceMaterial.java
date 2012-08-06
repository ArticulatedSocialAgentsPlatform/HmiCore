/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

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

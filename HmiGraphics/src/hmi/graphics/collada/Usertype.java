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

/** 
 * Creates an instance of a structured class.
 * @author Job Zwiers
 */
public class Usertype extends ColladaElement {
   
   // attributes: name inherited from ColladaElement
   //private String source; 
   
   // child elements:
   private ArrayList<ParamArray> arrays = new ArrayList<ParamArray>();
   private ArrayList<ValueType> valueTypes = new ArrayList<ValueType>();
   private ArrayList<ConnectParam> connectParams = new ArrayList<ConnectParam>();
   private ArrayList<Usertype> usertypes = new ArrayList<Usertype>();
   private ArrayList<Setparam> setparams = new ArrayList<Setparam>();
   
   public Usertype() {
      super();
   }
   
   public Usertype(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendNewLine(buf, fmt);
      appendXMLStructureList(buf, fmt, arrays);
      appendXMLStructureList(buf, fmt, valueTypes);
      appendXMLStructureList(buf, fmt, connectParams);
      appendXMLStructureList(buf, fmt, usertypes);
      appendXMLStructureList(buf, fmt, setparams);
      
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(ConnectParam.xmlTag()))  {                
                 connectParams.add(new ConnectParam(getCollada(), tokenizer));     
         } else if (tag.equals(Usertype.xmlTag()))  {                
                 usertypes.add(new Usertype(getCollada(), tokenizer)); 
         } else if (tag.equals(Setparam.xmlTag()))  {                
                 setparams.add(new Setparam(getCollada(), tokenizer));
         } else if (ValueType.hasTag(tag))  {                
                 valueTypes.add(new ValueType(getCollada(), tokenizer));
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Usertype: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }    
      addColladaNodes(connectParams);
      addColladaNodes(usertypes);
      addColladaNodes(setparams); 
      addColladaNodes(valueTypes);
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "usertype";
 
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

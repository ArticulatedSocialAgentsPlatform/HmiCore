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

/** 
 * Creates a parameter of a one-dimensional array type.
 * @author Job Zwiers
 */
public class ParamArray extends ColladaElement {
   
   // attributes: 
   //private int length; 
   
   // child elements:
   private ArrayList<ValueType> valueTypes = new ArrayList<ValueType>();
   private ArrayList<ConnectParam> connectParams = new ArrayList<ConnectParam>();
   private ArrayList<Usertype> usertypes = new ArrayList<Usertype>();
   private ArrayList<ParamArray> arrays = new ArrayList<ParamArray>();
    
   
   public ParamArray() {
      super();
   }
   
   public ParamArray(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendNewLine(buf, fmt);
      appendXMLStructureList(buf, fmt, valueTypes);
      //appendXMLStructureList(buf, fmt, connectParams);
      //appendXMLStructureList(buf, fmt, usertypes);
      appendXMLStructureList(buf, fmt, arrays);
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
         } else if (tag.equals(ParamArray.xmlTag()))  {                
                 arrays.add(new ParamArray(getCollada(), tokenizer));
         } else if (ValueType.hasTag(tag))  {                
                 valueTypes.add(new ValueType(getCollada(), tokenizer));
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Array: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }    
      addColladaNodes(connectParams);
      addColladaNodes(usertypes);
      addColladaNodes(arrays); 
      addColladaNodes(valueTypes);
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "array";
 
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

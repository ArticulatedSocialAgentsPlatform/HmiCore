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

import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.HashMap;


/** 
 * References a predefined parameter in shader binding declarations.
 * @author Job Zwiers
 */
public class Param extends ColladaElement {
 
   // attributes: name, sid inherited from ColladaElement
  private String semantic;
  private String type;
  private String ref; // seems to be used only when Param is a child of a CommonFloatOrParamType element.
   
   
   public Param() {
      super();
   }
   
   public Param(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer);    
   }
   
   /** Return semantic */
   public String getSemantics() { return semantic; }
      
   /** Return type */         
   public String getType() { return type; }
   
   /** Return ref */
   public String getRef() { return ref; }
  
   private static final int FLOAT4X4SIZE = 16;
  
   public int getSize() {
       if (type == null) return 1;
       if (type.equals("int")) return 1;
       if (type.equals("float")) return 1;
       if (type.equals("double")) return 1;
       if (type.equals("Name")) return 1;
       if (type.equals("float4x4")) return FLOAT4X4SIZE;  
       if (type.equals("IDREF")) return 1;   
       logger.error("Collada Param.getSize: unknown param type: " + type);
       return 1;
         
   }
 
 
   /**
    * returns false, to denote that Params are encoded by means of empty XML elements
    */
   @Override
   public boolean hasContent() { return false; }

   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "semantic", semantic);
      appendAttribute(buf, "type", type);
      appendAttribute(buf, "ref", ref);
      return buf;
   }


   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      semantic = getOptionalAttribute("semantic", attrMap);
      type     = getOptionalAttribute("type", attrMap);
      ref      = getOptionalAttribute("ref", attrMap);
      super.decodeAttributes(attrMap, tokenizer); // takes care of possible name attribute
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "param";
 
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

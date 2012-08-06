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

import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.HashMap;


/** 
 * DEclares the instantiation of a Collada material resource.
 * @author Job Zwiers
 */
public class TechniqueHint extends ColladaElement {
 
   // attributes: 
   private String platform; // optional
   private String ref;      // required
   private String profile;  // optional
   
  
   
   public TechniqueHint() {
      super();
   }
   
   public TechniqueHint(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);  
      readXML(tokenizer);   
   }
 

   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "platform", platform);
      appendAttribute(buf, "ref", ref);
      appendAttribute(buf, "profile", profile);
      return buf;
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {   
      platform  = getOptionalAttribute("platform", attrMap);   
      ref       = getRequiredAttribute("ref", attrMap, tokenizer);
      profile   = getOptionalAttribute("profile", attrMap);
      super.decodeAttributes(attrMap, tokenizer);
   }
 
   @Override
   public boolean hasContent() { return false; }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "technique_hint";
 
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

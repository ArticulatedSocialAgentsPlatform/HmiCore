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
 * Declares an output channel of an animation.
 * @author Job Zwiers
 */
public class Channel extends ColladaElement {
 
   // attributes: 
   private String source; // location of the animation sampler of form #url-fragment
   private String target; // location of the element boud to the output of the sampler (path name, like Box/Trans.X)
   
   
   public Channel() {
      super();
   }
   
   public Channel(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);      
      readXML(tokenizer);  
   }
 
   /**
    * returns false (no child elements)
    */
   @Override
   public boolean hasContent() { return false; }

   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "source", source);
      appendAttribute(buf, "target", target);
      return buf;
   }


   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      source     = getRequiredAttribute("source", attrMap, tokenizer);
      target     = getRequiredAttribute("target", attrMap, tokenizer);
      super.decodeAttributes(attrMap, tokenizer);
   }
 
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "channel";
 
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

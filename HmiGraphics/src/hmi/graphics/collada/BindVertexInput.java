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
 *renaming of parameters (See InstanceMaterial)
 * @author Job Zwiers
 */
public class BindVertexInput extends ColladaElement {
 
   // attributes: 
   private String semantic;       // required
   private String inputSemantic; // required
   private int inputSet;         // optional
      
   public BindVertexInput() {
      super();
   }
   
   public BindVertexInput(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);  
      readXML(tokenizer);     
   }
 
 
   public String getSemantic() {
      return semantic;
   }
 
 
   public String getInputSemantic() {
      return inputSemantic;
   }
 
   public int getInputSet() {
      return inputSet;
   }
 
   /**
    * returns false, to denote that Bind_Vertex_Inputs are encoded by means of empty XML elements
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
      appendAttribute(buf, "input_semantic", inputSemantic);
      if (inputSet != 0) appendAttribute(buf, "input_set", inputSet);      
      return buf;
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      semantic       = getRequiredAttribute("semantic", attrMap, tokenizer);
      inputSemantic = getRequiredAttribute("input_semantic", attrMap, tokenizer);
      inputSet       = getOptionalIntAttribute("input_set", attrMap, -1);
      super.decodeAttributes(attrMap, tokenizer);
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "bind_vertex_input";
 
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

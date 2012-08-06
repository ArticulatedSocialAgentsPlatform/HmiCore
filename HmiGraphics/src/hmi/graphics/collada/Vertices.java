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
 * @author Job Zwiers
 */
public class Vertices extends ColladaElement {
    
   // attributes: id, name
   private ArrayList<Input> inputs = new ArrayList<Input>();   
   private ArrayList<Extra> extras;
   

   /**
    * Default constructor
    */      
   public Vertices() {
      super();
   }

   /**
    * Constructor used to create a Vertices Object from XML
    */      
   public Vertices(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }
 
   public ArrayList<Input> getInputs() {
       return inputs;  
   }
 
   /**
    * returns the Collada Source for some specified semantics, like &quot;POSITION&quot;
    * (At least the POSITION semantic should be defined for vertices)
    */
   public Source getSource(String semantic) {
      for (Input inp : inputs) {
          if (inp.getSemantic().equals(semantic)) {
             Source source = getCollada().getSource(inp.getSource());
             if (source == null) {
                 throw new RuntimeException("Vertices.getSource: Input with unresolved Source"); 
             }
             return source;
          }
      }  
      throw new RuntimeException("Vertices.getSource: No Input defined for Semantic: "+semantic); 
      //return null;
   }

   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructureList(buf, fmt, inputs);
      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Input.xmlTag()))  {    
               Input inp = new Input(getCollada(), tokenizer);
               if (inp.getSemantic() == null) {
                   throw new RuntimeException("Vertices Input without semantic attribute");  
               }
               if (inp.getOffset() >= 0) {
                   throw new RuntimeException("Vertices Input cannot have offset attributes");  
               }        
               inputs.add(inp); 
         } else if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));    
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Vertices: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }  
      addColladaNodes(inputs); 
      addColladaNodes(extras); 
         
   }

 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "vertices";
 
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

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

/** 
 * Declares the binding of geometric primitives and vertex attributes for a mesh element.
 * @author Job Zwiers
 */
public class Lines extends PrimitiveMeshElement {
    
   
   private P p;
    
   /**
    * Default constructor
    */     
   public Lines() {
      super();
      setMeshType(Mesh.MeshType.Lines);
   }

   /**
    * Constructor used to create a PolyList Object from XML
    */      
   public Lines(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
      setMeshType(Mesh.MeshType.Lines);
   }

   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructureList(buf, fmt, getInputs());
      appendXMLStructure(buf, fmt, p);
      appendXMLStructureList(buf, fmt, getExtras());
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Input.xmlTag()))  {   
             Input inp = new Input(getCollada(), tokenizer);         
             getInputs().add(inp);    
             if (inp.getOffset() > getMaxOffset()) setMaxOffset(inp.getOffset()); 
         } else if (tag.equals(Extra.xmlTag()))  {                
             getExtras().add(new Extra(getCollada(), tokenizer));   
         } else if (tag.equals(P.xmlTag()))  {                
             p = new P(getCollada(), tokenizer);
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Lines: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }    
      addColladaNodes(getInputs());
      addColladaNodes(getExtras());
      addColladaNode(p);  
   }
 


   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "lines";
 
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

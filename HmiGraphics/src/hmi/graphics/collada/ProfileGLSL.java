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
 * Opens a block of platform-specific data types and technique declarations.
 * @author Job Zwiers
 */
public class ProfileGLSL extends ProfileCOMMON {
 
   private ArrayList<Code> codeList = new ArrayList<Code>();
 
   public ProfileGLSL() {
      super();
   }
   
   public ProfileGLSL(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer);        
   }

 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      super.appendContent(buf, fmt);
      appendXMLStructureList(buf, fmt, codeList);
//      appendXMLStructureList(buf, fmt, newparamList);
//      appendXMLStructureList(buf, fmt, techniqueList);
//      appendXMLStructureList(buf, fmt, extras);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Code.xmlTag()))  {                
             codeList.add(new Code(getCollada(), tokenizer)); 
             addColladaNodes(getNewparamList());
//         } else if (tag.equals(ColladaImage.xmlTag()))  {
//             imageList.add(new ColladaImage(getCollada(), tokenizer));
         } else {         
            super.decodeElement(tokenizer);
         }
      }      
      super.addElements();
      addColladaNodes(codeList);  
   }
 
 
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "profile_GLSL";
 
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

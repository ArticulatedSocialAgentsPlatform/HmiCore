/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/

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

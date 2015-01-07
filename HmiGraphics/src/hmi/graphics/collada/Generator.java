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
 * Describes a procedural surface generator
 * @author Job Zwiers
 */
public class Generator extends ColladaElement {
 
   // attributes: none
   
   // child elements:
   private ArrayList<Annotate> annotateList = new ArrayList<Annotate>();
   private Code code;
   private ShaderInclude include;
   private Name name;
   private ArrayList<Setparam> setparamList = new ArrayList<Setparam>();
   
   public Generator() {
      super();
   }
   
   public Generator(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);     
      readXML(tokenizer);  
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructureList(buf, fmt, annotateList);
      appendXMLStructure(buf, fmt, code);
      appendXMLStructure(buf, fmt, include);
      appendXMLStructure(buf, fmt, name);
      appendXMLStructureList(buf, fmt, setparamList);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Annotate.xmlTag()))  {                
            annotateList.add(new Annotate(getCollada(), tokenizer));  
         } else if (tag.equals(Code.xmlTag()))  {
            code = new Code(getCollada(), tokenizer);
         } else if (tag.equals(Include.xmlTag()))  {
            include = new ShaderInclude(getCollada(), tokenizer);
         } else if (tag.equals(Name.xmlTag()))  {
            name = new Name(getCollada(), tokenizer);
         } else if (tag.equals(Setparam.xmlTag()))  {                
            setparamList.add(new Setparam(getCollada(), tokenizer)); 
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Generator: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }  
      addColladaNodes(annotateList);
      addColladaNode(code);
      addColladaNode(include);
      addColladaNode(name);
      addColladaNodes(setparamList);
   }


   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "generator";
 
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

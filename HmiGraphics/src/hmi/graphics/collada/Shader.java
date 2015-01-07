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
import java.util.HashMap;

/** 
 * Declares and prepares a shader for execution in the renderering pipeline of a pass
 * @author Job Zwiers
 */
public class Shader extends ColladaElement {
 
   // attributes: 
   private String stage; // optional, platform specific., VERTEXPROGRAM or FRAGMENTPROGRAM
   
   // child elements:
   private ArrayList<Annotate> annotateList = new ArrayList<Annotate>();
   private CompilerTarget compilerTarget;       // optional
   private Name name;                             // required
   private CompilerOptions compilerOptions;     // optional
   private ArrayList<Bind> bindList = new ArrayList<Bind>();
   
   public Shader() {
      super();
   }
   
   public Shader(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer);   
   }
  
   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "stage", stage);
      return buf;
   }

   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {     
      stage = getOptionalAttribute("stage", attrMap);
      super.decodeAttributes(attrMap, tokenizer);
   }

   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructureList(buf, fmt, annotateList);
      appendXMLStructure(buf, fmt, compilerTarget);
      appendXMLStructure(buf, fmt, name);
      appendXMLStructure(buf, fmt, compilerOptions);
      appendXMLStructureList(buf, fmt, bindList);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Annotate.xmlTag()))  {                
                 annotateList.add(new Annotate(getCollada(), tokenizer));  
         } else if (tag.equals(CompilerTarget.xmlTag()))  {                
                 compilerTarget = new CompilerTarget(getCollada(), tokenizer); 
         } else if (tag.equals(Name.xmlTag()))  {                
                 name = new Name(getCollada(), tokenizer); 
         } else if (tag.equals(CompilerOptions.xmlTag()))  {                
                 compilerOptions = new CompilerOptions(getCollada(), tokenizer); 
         } else if (tag.equals(Bind.xmlTag()))  {                
                 bindList.add(new Bind(getCollada(), tokenizer));    
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Shader: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }  
      addColladaNodes(annotateList);
      addColladaNode(compilerTarget);
      addColladaNode(name);
      addColladaNode(compilerOptions);
      addColladaNodes(bindList);
   }

   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "shader";
 
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

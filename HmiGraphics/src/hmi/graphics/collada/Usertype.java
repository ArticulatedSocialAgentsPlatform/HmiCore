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
 * Creates an instance of a structured class.
 * @author Job Zwiers
 */
public class Usertype extends ColladaElement {
   
   // attributes: name inherited from ColladaElement
   //private String source; 
   
   // child elements:
   private ArrayList<ParamArray> arrays = new ArrayList<ParamArray>();
   private ArrayList<ValueType> valueTypes = new ArrayList<ValueType>();
   private ArrayList<ConnectParam> connectParams = new ArrayList<ConnectParam>();
   private ArrayList<Usertype> usertypes = new ArrayList<Usertype>();
   private ArrayList<Setparam> setparams = new ArrayList<Setparam>();
   
   public Usertype() {
      super();
   }
   
   public Usertype(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendNewLine(buf, fmt);
      appendXMLStructureList(buf, fmt, arrays);
      appendXMLStructureList(buf, fmt, valueTypes);
      appendXMLStructureList(buf, fmt, connectParams);
      appendXMLStructureList(buf, fmt, usertypes);
      appendXMLStructureList(buf, fmt, setparams);
      
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(ConnectParam.xmlTag()))  {                
                 connectParams.add(new ConnectParam(getCollada(), tokenizer));     
         } else if (tag.equals(Usertype.xmlTag()))  {                
                 usertypes.add(new Usertype(getCollada(), tokenizer)); 
         } else if (tag.equals(Setparam.xmlTag()))  {                
                 setparams.add(new Setparam(getCollada(), tokenizer));
         } else if (ValueType.hasTag(tag))  {                
                 valueTypes.add(new ValueType(getCollada(), tokenizer));
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Usertype: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }    
      addColladaNodes(connectParams);
      addColladaNodes(usertypes);
      addColladaNodes(setparams); 
      addColladaNodes(valueTypes);
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "usertype";
 
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

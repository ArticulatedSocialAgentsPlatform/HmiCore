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

import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.HashMap;


/** 
 * References a predefined parameter in shader binding declarations.
 * @author Job Zwiers
 */
public class Param extends ColladaElement {
 
   // attributes: name, sid inherited from ColladaElement
  private String semantic;
  private String type;
  private String ref; // seems to be used only when Param is a child of a CommonFloatOrParamType element.
   
   
   public Param() {
      super();
   }
   
   public Param(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer);    
   }
   
   /** Return semantic */
   public String getSemantics() { return semantic; }
      
   /** Return type */         
   public String getType() { return type; }
   
   /** Return ref */
   public String getRef() { return ref; }
  
   private static final int FLOAT4X4SIZE = 16;
  
   public int getSize() {
       if (type == null) return 1;
       if (type.equals("int")) return 1;
       if (type.equals("float")) return 1;
       if (type.equals("double")) return 1;
       if (type.equals("Name")) return 1;
       if (type.equals("float4x4")) return FLOAT4X4SIZE;  
       if (type.equals("IDREF")) return 1;   
       logger.error("Collada Param.getSize: unknown param type: " + type);
       return 1;
         
   }
 
 
   /**
    * returns false, to denote that Params are encoded by means of empty XML elements
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
      appendAttribute(buf, "type", type);
      appendAttribute(buf, "ref", ref);
      return buf;
   }


   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      semantic = getOptionalAttribute("semantic", attrMap);
      type     = getOptionalAttribute("type", attrMap);
      ref      = getOptionalAttribute("ref", attrMap);
      super.decodeAttributes(attrMap, tokenizer); // takes care of possible name attribute
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "param";
 
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

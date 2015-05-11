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

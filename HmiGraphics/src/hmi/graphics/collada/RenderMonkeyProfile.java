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

/** 
 * profile for TechniqueCore profile RenderMonkey
 * @author Job Zwiers
 */
public class RenderMonkeyProfile extends ColladaElement {
 
   //private String profile; 
   
   // profile RenderMonkey:
   //public RenderMonkey_TimeCycle rendermonkey_timecycle;
   
   
 
   public RenderMonkeyProfile() {
      super();
   }
   
    public RenderMonkeyProfile(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      decodeContent(tokenizer); // N.B. non-standard for ColladaElements
   }
  
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      //appendXMLStructure(buf, fmt, rendermonkey_timecycle);
    
     // appendOptionalIntElement(buf, "amount", amount, -1, fmt);
    
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException { 
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
        
         if (tag.equals("RenderMonkey_TimeCycle"))  {        
             tokenizer.skipTag();        
          //       rendermonkey_timecycle = new RenderMonkey_TimeCycle(getCollada(), tokenizer);    
         } else {
            getCollada().warning(tokenizer.getErrorMessage("Technique, RenderMonkey profile, skipping: " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }
      //addColladaNode(rendermonkey_timecycle);
   }
 
 
}

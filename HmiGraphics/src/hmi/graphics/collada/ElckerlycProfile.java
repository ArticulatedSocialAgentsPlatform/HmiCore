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
 * profile for TechniqueCore profile ElckerlycProfile
 * @author Job Zwiers
 */
public class ElckerlycProfile extends ColladaElement {
 
   private String renamingList = "";
   
 
   public ElckerlycProfile() {
      super();
   }
   
    public ElckerlycProfile(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      decodeContent(tokenizer); // N.B. non-standard for ColladaElements
   }
  
   public String getRenamingList() {
      return renamingList;
   }
  
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
  
    
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException { 
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
        
         if (tag.equals("renaming"))  {   
            renamingList = tokenizer.takeTextElement("renaming");
         } else {
            getCollada().warning(tokenizer.getErrorMessage("Technique, Elckerlyc profile, skipping: " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }
    
   }
 
 
}

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
 * Max3D profile for TechniqueCore profile MAX3D
 * @author Job Zwiers
 */
public class FColladaProfile extends ColladaElement {
 
  //private String profile; 
   
   // profile FCOLLADA:
  private FColladaSpecLevel fcolladaSpecLevel;
  private FColladaEmissionLevel fcolladaEmissionLevel;
  private FColladaStartTime fcolladaStartTime;
  private FColladaEndTime fcolladaEndTime;
  private FColladaIntensity fcolladaIntensity;
  private FColladaBump fcolladaBump;
  private FColladaTarget fcolladaTarget;
   
   
   public FColladaProfile() {
      super();
   }
   
    public FColladaProfile(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      decodeContent(tokenizer); // N.B. non-standard for ColladaElements
   }
  
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructure(buf, fmt, fcolladaSpecLevel);
      appendXMLStructure(buf, fmt, fcolladaEmissionLevel);
      appendXMLStructure(buf, fmt, fcolladaStartTime);
      appendXMLStructure(buf, fmt, fcolladaEndTime);
      appendXMLStructure(buf, fmt, fcolladaIntensity);
      appendXMLStructure(buf, fmt, fcolladaBump);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
    
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();      
         if (tag.equals(FColladaSpecLevel.xmlTag()))  {                
                    fcolladaSpecLevel = new FColladaSpecLevel(getCollada(), tokenizer);  
         } else if (tag.equals(FColladaEmissionLevel.xmlTag()))  {                
                    fcolladaEmissionLevel = new FColladaEmissionLevel(getCollada(), tokenizer);  
         } else if (tag.equals(FColladaStartTime.xmlTag()))  {                
                    fcolladaStartTime = new FColladaStartTime(getCollada(), tokenizer);  
         } else if (tag.equals(FColladaEndTime.xmlTag()))  {                
                    fcolladaEndTime = new FColladaEndTime(getCollada(), tokenizer);  
         } else if (tag.equals(FColladaIntensity.xmlTag()))  {                
                    fcolladaIntensity = new FColladaIntensity(getCollada(), tokenizer);  
         } else if (tag.equals(FColladaBump.xmlTag()))  {                
                    fcolladaBump = new FColladaBump(getCollada(), tokenizer);  
         } else if (tag.equals(FColladaTarget.xmlTag()))  {                
                    fcolladaTarget = new FColladaTarget(getCollada(), tokenizer);  
         } else {
            getCollada().warning(tokenizer.getErrorMessage("Technique, FCollada profile, skipping: " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }
      addColladaNode(fcolladaSpecLevel);
      addColladaNode(fcolladaEmissionLevel);
      addColladaNode(fcolladaStartTime);
      addColladaNode(fcolladaEndTime);
      addColladaNode(fcolladaIntensity);
      addColladaNode(fcolladaBump);
      addColladaNode(fcolladaTarget);
   }
 
 
}

     

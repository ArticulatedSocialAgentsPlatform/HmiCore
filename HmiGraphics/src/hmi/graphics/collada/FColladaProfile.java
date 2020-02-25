/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
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

     

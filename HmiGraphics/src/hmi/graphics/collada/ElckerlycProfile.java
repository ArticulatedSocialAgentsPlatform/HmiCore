/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

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

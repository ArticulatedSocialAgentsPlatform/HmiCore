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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** 
 * Describes the asset of some Collada content
 * @author Job Zwiers
 */
public class Asset extends ColladaElement {
   private List<Contributor> contributors = new ArrayList<Contributor>();
   private String created;
   private List<String> keywordsList = new ArrayList<String>();
   private String modified;
   private String revision;
   private String subject;
   private String title;
   private String unitName; // if null, COLLADA default == "meter"
   private Double unitMeter = 1.0; // COLLADA default value (meter)
   private String upAxis = "Y_UP"; // COLLADA default 
   
   public Asset() {
      super();
   }
   
   public Asset(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
   }
 
   /** Returns the conversion factor for representing coordinates in meters */
   public float getUnitMeter() { return unitMeter.floatValue(); }
   
   /** Returns a String like &quot;Y_UP&quot; to denote the up axis */
   public String getUpAxis() { return upAxis; }

   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructureList(buf, fmt, contributors);
      appendTextElement(buf, "created", created, fmt);
      appendXMLTextElementList(buf, fmt, "keywords", keywordsList);
      appendTextElement(buf, "modified", modified, fmt);
      appendTextElement(buf, "revision", revision, fmt);
      appendTextElement(buf, "subject", subject, fmt);
      appendTextElement(buf, "title", title, fmt);
      if (unitName != null) {
         appendEmptyTag(buf, fmt, "unit", "name", unitName, "meter", Double.toString(unitMeter));
      }
      appendTextElement(buf, "up_axis", upAxis, fmt);
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals("contributor"))        contributors.add(new Contributor(getCollada(), tokenizer));
         else if (tag.equals("created"))       created = tokenizer.takeTextElement("created");
         else if (tag.equals("keywords"))      keywordsList.add(tokenizer.takeTextElement("keywords"));
         else if (tag.equals("modified"))      modified = tokenizer.takeTextElement("modified");
         else if (tag.equals("revision"))      revision = tokenizer.takeTextElement("revision");
         else if (tag.equals("title"))         title = tokenizer.takeTextElement("title");            
         else if (tag.equals("subject"))       subject = tokenizer.takeTextElement("subject");
         else if (tag.equals("unit")) {
            HashMap<String, String> attr = tokenizer.takeEmptyElement("unit");               
            unitName = attr.get("name");
            unitMeter = Double.parseDouble(attr.get("meter"));
         } else if (tag.equals("up_axis"))     upAxis = tokenizer.takeTextElement("up_axis");            
         else {         
            getCollada().warning(tokenizer.getErrorMessage("Asset: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }   
      addColladaNodes(contributors);   
   }

 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "asset";
 
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

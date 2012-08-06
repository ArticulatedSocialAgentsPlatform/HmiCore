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

package hmi.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * parlevink.xml.XMLAttributeMap is like java.util.properties, except that
 * it is not limited to String-typed keys and values, and it is based on
 * HashMaps, rather than Hashtables.
 * Keys take the form of Id's or Strings, although internally Id's are used.
 * For storing/retrieving Object-typed values, the prime methods are 
 * setAttribute(id, value) and getAttribute(id).
 * When values are known to be Strings, the preferred methods are setString(id, string)
 * and getString(id). In all cases, "id" can be an Id, or it can be a String.
 * @author Job Zwiers
 */
public class XMLAttributeMap implements XMLStructure {
   
 
   /**
    * creates a new, empty set of attributes.
    */
   public XMLAttributeMap() {
      attr = new LinkedHashMap<String, Object>();  
   }

   /**
    * creates a new, empty set of attributes.
    * The initial size of the internal Hasmap is specified
    * by means of "initialCapacity"
    */
   public XMLAttributeMap(int initialCapacity) {
      attr = new LinkedHashMap<String, Object>(initialCapacity);  
   }

//   /**
//    * creates an XMLAttributeMap that is a clone of some other map..
//    */
//   public XMLAttributeMap(XMLAttributeMap attrmap) {
//      attr = attrmap.attr;  
//   }

   /**
    * sets the value of the attribute identified by "key".
    * Returns the previously bound value for "key", or null,
    * if "key" was not bound. 
    */
   public Object setAttribute(String key, Object value) {
      return attr.put(key, value);      
   }


  
   /**
    * retrieves the (Object-typed) value of the attribute identified by key.
    * Returns the previously bound value for "key", or null,
    * if "key" was not bound. 
    */
   public Object getAttribute(String key) {
      return attr.get(key);
   }

  
   /**
    * sets the String-typed value of the attribute identified by "key".
    * Returns the previously bound value for "key", or null,
    * if "key" was not bound. 
    */
   public Object setString(String key, String value) {
      return  attr.put(key, value);      
   }

   /**
    * retrieves the String-typed value of the attribute identified by key.
    * Returns the previously bound value for "key", or null,
    * if "key" was not bound. 
    */
   public String getString(String key) {
      return (String) (attr.get(key));
   }

  

   /**
    * checks whether an attribute for "key" has been defined.
    */
   public boolean hasAttribute(String key) {
      return attr.get(key) != null;
   }


  /**
   * Returns an Iterator for all the property attribute keys.
   */
  public Iterator<String> attributeNames() {
     return attr.keySet().iterator();
  }

  /**
   * Returns an Iterator for all the attribute values.
   */
  public Iterator<Object> attributeValues() {
     return attr.values().iterator();
  }


   protected Iterator<Map.Entry<String, Object>> iterator() {
      return attr.entrySet().iterator();
   }

//----------------------
 


   private void appendSpace(StringBuilder buf, int nsp) {
      for (int i=0; i<nsp; i++) buf.append(' ');
   }

   private static final int ENCODEBUFSIZE = 100;
   /*
    * encodes the XMLAttributeMap as an XML string
    */
   private void encode(int tab) {
      clearBuf(ENCODEBUFSIZE);
      appendSpace(buf, tab);
      buf.append("<");
      buf.append(getXMLTag());
      buf.append(">");
      Iterator<Map.Entry<String,Object>> iter = iterator(); // iterator for Map Entries
      while (iter.hasNext()) {
         Map.Entry<String,Object> elem = iter.next();
         String idStr =  elem.getKey().toString(); // Id String value
         buf.append('\n');
         appendSpace(buf, tab+TAB);
         buf.append("<Attribute id=\"");
         buf.append(idStr);
         buf.append('"');
         Object attrValue = elem.getValue();        
         if (  attrValue instanceof java.lang.String) {
             buf.append(" String=\"");
             buf.append(attrValue);
             buf.append("\"/>"); 
         } else if ( attrValue instanceof Integer) {
             buf.append(" Integer=\"");
             buf.append(attrValue);
             buf.append("\"/>");             
         } else if ( attrValue instanceof Long) {
             buf.append(" Long=\"");
             buf.append(attrValue);
             buf.append("\"/>");    
         } else if ( attrValue instanceof Float) {
             buf.append(" Float=\"");
             buf.append(attrValue);
             buf.append("\"/>");  
         } else if ( attrValue instanceof Double) {
             buf.append(" Double=\"");
             buf.append(attrValue);
             buf.append("\"/>");  
         } else if ( attrValue instanceof Boolean) {
             buf.append(" Boolean=\"");
             buf.append(attrValue);
             buf.append("\"/>");  
         } else {  
             buf.append(">\n");           
//             XMLStructure xmlVal = XML.wrap(attrValue);                    
//             xmlVal.appendXML(buf, tab+2*TAB);
//             buf.append('\n');
             appendSpace(buf, tab+TAB);
             buf.append("</Attribute>");
         }
      }
      buf.append('\n');
      appendSpace(buf, tab);
      buf.append("</");
      buf.append(getXMLTag());
      buf.append(">");
      encoding = buf.toString();
   }

   /**
    * reconstructs this XMLLIst object by parsing an XML encoded String s
    * This method can throw an (unchecked) XMLScanException in case of incorrectly
    * formatted XML. 
    */
   public XMLStructure readXML(String s) {
      return readXML(new XMLTokenizer(s));
   } 



   /**
    * reconstructs this XMLList object by reading and parsing XML encoded data
    * Data is read until the end of data is reached,
    * or until a '<' character is read. 
    * This method can throw IOExceptions.
    */
   public XMLStructure readXML(Reader in) throws IOException {
      if (in instanceof BufferedReader) {
         return readXML(new XMLTokenizer( (BufferedReader)in));
      } else {
         return readXML(new XMLTokenizer(new BufferedReader(in)));
      }
   } 


   /**
    * reconstructs this XMLList using a XMLTokenizer.
    * This method can throw an (unchecked) XMLScanException in case of incorrectly
    * formatted XML. 
    */
   public XMLStructure readXML(XMLTokenizer tokenizer) {
      attr.clear();
      try {
         tokenizer.takeSTag(getXMLTag());
         while (tokenizer.atSTag()) {
             HashMap<String, String> xmlAttributes = tokenizer.getAttributes();
//            String idStr = (String) xmlAttributes.get("id");
//            if (idStr == null) {
//                  throw new XMLScanException("\"id\" attribute missing in Attribute tag");
//            }
//            Id id = Id.forName(idStr);
            Iterator<Map.Entry<String, String>> iter = xmlAttributes.entrySet().iterator();
            String idStr = null;
            Object value = null;
            while (iter.hasNext() ) {
                Map.Entry<String, String> e = iter.next();  
                String as = e.getKey();
                String stringVal = e.getValue();
                if (as.equals("id")) {
                    idStr = stringVal;                                    
                } else if (as.equals("String") || as.equals("value")) {
                    value = stringVal;
                } else if (as.equals("Integer") || as.equals("int")) {
                    value = Integer.valueOf(stringVal); 
                } else if (as.equals("Long") || as.equals("long")) {
                    value = Long.valueOf(stringVal); 
                } else if (as.equals("Float") || as.equals("float")) {
                    value = Float.valueOf(stringVal); 
                } else if (as.equals("Double") || as.equals("double")) {
                    value = Double.valueOf(stringVal); 
                } else if (as.equals("Boolean") || as.equals("boolean") || as.equals("bool")) {
                    value = Boolean.valueOf(stringVal); 
                }
            }
            tokenizer.takeSTag();
            if (idStr == null) {
                  throw new RuntimeException("\"id\" attribute missing in Attribute tag");
            }
           
//            if (value == null) {
//                XMLStructure xmlAttr = XML.createXMLStructure(tokenizer);
//                if (xmlAttr instanceof XMLWrapper) {
//                   value = ((XMLWrapper)xmlAttr).unwrap();
//                } else {
//                   value = xmlAttr; 
//                }
//            }
            tokenizer.takeETag("Attribute"); 
            attr.put(idStr, value);
         }
         tokenizer.takeETag(getXMLTag()); // </XMLAttributeMap>
      }  catch (IOException e) { throw new RuntimeException("XMLAttributeMap: " + e); }
         return this;
   } 

   
   /**
    * writes the value of this XMLList to out.
    */
   @Override
   public void writeXML(PrintWriter out) {
      encode(0);
      out.write(encoding);
   }

   /**
    * writes the value of this XMLList to out.
    */
   @Override
   public void writeXML(PrintWriter out, int tab) {
      encode(tab);
      out.write(encoding);
   }

   /**
    * writes the value of this XMLList to out.
    */
   @Override
   public void writeXML(PrintWriter out, XMLFormatting fmt) {
      encode(fmt.getTab());
      out.write(encoding);
   }


   /**
    * appends the value of this XMLList to buf.
    */
   @Override
   public StringBuilder  appendXML(StringBuilder buf) {
      encode(0);
      buf.append(encoding);
      return buf;
   }

    /**
    * appends the value of this XMLList to buf.
    */
   @Override
   public StringBuilder  appendXML(StringBuilder buf, XMLFormatting fmt) {
       return appendXML(buf, fmt.getTab());
   }

   /**
    * appends the value of this XMLList to buf.
    */
   @Override
   public StringBuilder  appendXML(StringBuilder buf, int tab) {
      encode(tab);
      buf.append(encoding);
      return buf;
   }


   /**
    * yields an XML encoded String of this XMLIzable object. 
    * The readXML() methods are able to reconstruct this object from 
    * the String delivered by toXMLString().
    */ 
     @Override
   public String toXMLString() {
      encode(0);
      return encoding;
   } 

   /**
    * yields an XML encoded String of this XMLIzable object. 
    * The readXML() methods are able to reconstruct this object from 
    * the String delivered by toXMLString().
    */ 
    @Override
   public String toXMLString(XMLFormatting fmt) {
      return toXMLString(fmt.getTab());
   } 


   /**
    * yields an XML encoded String of this XMLIzable object. 
    * The readXML() methods are able to reconstruct this object from 
    * the String delivered by toXMLString().
    */ 
    @Override
   public String toXMLString(int tab) {
      encode(tab);
      return encoding;
   } 

   @Override
   public String toString() {     
      return toXMLString();
   }

   
   @Override
   public boolean equals(Object attributeMap) 
   {
       if(attributeMap==null)return false;
       if(attributeMap instanceof XMLAttributeMap)
       {
           LinkedHashMap attr2 = ((XMLAttributeMap) attributeMap).attr;
           //Set<String> s1 = attr.keySet();
           //Set s2 = attr2.keySet();
           return attr.equals(attr2);
       }
       return false;
   }


   @Override
   public int hashCode() {
      return attr.hashCode();
   }

   /*
    * allocates a new StringBuilder , if necessary, and else deletes
    * all data in the buffer
    */
   private void clearBuf(int len) {
      if (buf == null) buf = new StringBuilder(len);
      else buf.delete(0, buf.length());
   }

   

  /**
   * returns the XML tag that is used to encode this type of XMLStructure.
   * The default returns null.
   */
  public String getXMLTag() {
     return XMLTAG; 
  }
 
   /**
    * The XML Stag for XML encoding -- use this static method when you want to see if a given String equals
    * the xml tag for this class
    */
   public static String xmlTag() { return XMLTAG; }
 


   private LinkedHashMap<String, Object> attr;
   
   
   /**
    * the (initial) length of the StringBuilder used for readXML(in).
    */
   private static final int BUFLEN = 40;

   private String encoding;
   private StringBuilder buf;
  // public String xmlTag;
   //private int curtab;
   public static final int TAB = 3;
   
   public static final String ATTRIBUTEMAPTAG = "AttributeMap";
   private static final String XMLTAG = ATTRIBUTEMAPTAG;
   public static final String CLASSNAME = "parlevink.xml.XMLAttributeMap";
   public static final String WRAPPEDCLASSNAME = "parlevink.xml.XMLAttributeMap";
   //private static Class<?> wrappedClass;
//   static {
//       try {
//         wrappedClass = Class.forName(WRAPPEDCLASSNAME);
//       } catch (Exception e) {}
//       XML.addClass(ATTRIBUTEMAPTAG, CLASSNAME);     
//   }   

   
}

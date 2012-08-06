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

package hmi.xml.wrap;

import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructure;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * XMLBasicValue is in essence a wrapper around a basic value, like an int, a String etc
 * that turns that value into an  XMLWrapper (and therefore also XMLStructure) object.
 * A basic value must have an XML encoding, both as complete XML in the form
 * <TypeTag val="..."> as well as an attribute value String encoding, i.e just the "..." part
 * from the complete encoding.
 * This implies that the XML encoding cannot recursively contain other XML tags,
 * which limits basic values to simple values like ints, longs, floats, doubles,
 * Strings, tuples of fixed length etc.
 * XMLBasicValue is a "base" class for implementing such classes.
 * It suffices to (re)implement the following methods:
 * public StringBufer appendAttributeString(StringBuilder buf) and
 * public void decodeAttribute(String attrName, String valCode).
 * The appendAttributeString method must append a string to buf, and return the latter;
 * The appended string must have the form: attr1="value1" attr2="value2" ....
 * The decodeAttribute method is called while the XMLStructure is being reconstructed
 * from XML code. A call of the form decodeAttribute(attr-i, value-i) will be made,
 * for each attribute found in the XML encoding. The implementation of decodeAttribute
 * should be able to reconstruct the XMLStructure object from these calls.
 * @param <T> base type being wrapped by the XMLBasicValue class
 * @author Job Zwiers
 */
public class XMLBasicValue<T> implements XMLWrapper<T> {
 
   private String encoding; // if != null, caches the current encoding
   private StringBuilder buffer; // Stringbuffer containing encoding
   
   private int ciPos;
   private int ci;
   private int bufLen;
   private int curtab; // caches the tab of the current encoding
  
   public static final int BUFFERSIZE = 25;
   private static final int EOS  = -1;
   

   /*
    * creates a new XMLBasicValue
    */
   public XMLBasicValue() {
   }

   /**
    * Returns the XML tag that is used to encode this XMLBasicValue
    * The default returns null
    */
   public String getXMLTag() {
      return null;
   }

   /**
    * The default implementation of unwrap simply return this.
    * (Should be overwritten by XMLBasicValues that are "wrappers"
    * for non-XMLStructure Java Objects)
    */
   public T unwrap() {
      return null;
      //return this;
   }

   
   /*
    * allocates a new StringBuilder , if necessary, and else deletes
    * all data in the buffer
    */
   private void clearBuffer(int len) {
      if (buffer == null) {
         buffer = new StringBuilder(len);
      } else {
         buffer.delete(0, buffer.length());
         if (len > buffer.length()) buffer.ensureCapacity(len);
      }
   }

   /*
    * appends nsp spaces to the buffer
    */
   protected void appendSpace(int nsp) {
      for (int i=0; i<nsp; i++) buffer.append(' ');
   }
  

   /*
    * decodes the value present in the buffer.
    * Assume that it has the form <xmlTag attr1="val1" attr2="val2"  ... />,
    * and also assume that an attribute (attr, val) can be decoded 
    * by a call to decodeAttribute(attr, val)
    */
   private void decode() {
      ciPos = 0;
      ci = buffer.charAt(ciPos);
      bufLen = buffer.length();
      if (ci != '<') throw new RuntimeException("Missing < character at start of XMLBasicValue");
      String xmlTag = getXMLTag();
      int tagEnd = xmlTag.length()+1;     
      if(! xmlTag.equals(buffer.substring(1, tagEnd)))
         throw new RuntimeException("Wrong XML tag in XMLBasicValue: " + buffer);
         
      ciPos = tagEnd; // skip tag
      nextChar();
      skipSpaceChars();
      while (ci != '/' && ci != EOS) {
         String attrName = getAttributeName();
         String attrValue = getAttributeValue();       
         decodeAttribute(attrName, attrValue, null);
         skipSpaceChars();
      }
      if (ci == EOS) throw new RuntimeException("Missing / character at end of XMLBasicValue");
      nextChar();
      if (ci != '>') throw new RuntimeException("Missing > character at end of XMLBasicValue");
      nextChar();
      skipSpaceChars();
      if (ci != EOS) throw new RuntimeException("extra characters at end of XMLBasicValue");
      
   }

   /**
    * reconstructs this XMLBasicValue object by reading and parsing XML encoded data
    * Data is read until a '<' character is read or until end-of-data is reached.
    * This method can throw IOExceptions.
    */
   public XMLStructure readXML(Reader in) throws IOException {
      clearBuffer(BUFFERSIZE);   
      int ci = in.read(); //  expected to be the '<' character of the STag.
      buffer.append((char)ci); 
      ci = in.read();
      while (ci != EOS && ci != '<') {
          buffer.append((char)ci);
          ci = in.read();
      }   
      decode();
      return this;
   } 
   
   /**
    * reconstructs this XMLBasicValue object by parsing an XML encoded String s
    * This method can throw an (unchecked) RuntimeException in case of incorrectly
    * formatted XML. 
    */
   public XMLStructure readXML(String s) {
      buffer = new StringBuilder(s);
      decode();
      return this;
   } 
   
   /**
    * reconstructs this XMLBasicValue using a XMLTokenizer.
    * This method can throw an (unchecked) RuntimeException in case of incorrectly
    * formatted XML. 
    */
   public XMLStructure readXML(XMLTokenizer tokenizer) {
      try {
         if (! tokenizer.atSTag(getXMLTag())) {
             throw tokenizer.getXMLScanException
                ("Erroneous XML encoding, expected: " + getXMLTag() 
                  + ", encountered: " + tokenizer.getTagName()
                );
         }
         HashMap<String, String> attrMap = tokenizer.getAttributes(); // will parse remainder of STag tail, if necessary
         Iterator<Map.Entry<String,String>> attrIter = attrMap.entrySet().iterator();
         while (attrIter.hasNext()) {
             Map.Entry<String,String> attr = attrIter.next();
             decodeAttribute(attr.getKey(), attr.getValue(), tokenizer); 
         }
         tokenizer.takeSTag();
         tokenizer.takeETag();
         return this;
      }  catch (IOException e) { throw tokenizer.getXMLScanException("IOException in XMLTokenizer: " + e); }
   } 



   /**
    * writes the value of this XMLBasicValue to out,
    * with no indentation.
    * relies on the String as delivered by toXMLString()
    */
   public void writeXML(PrintWriter out) {
      out.write(toXMLString(0));
   }

   /**
    * writes the value of this XMLBasicValue to out,
    * using "tab" as a hint for indentation.
    * relies on the String as delivered by toXMLString(tab)
    */
   public void writeXML(PrintWriter out, int tab) {
      out.write(toXMLString(tab));
   }

    /**
    * writes the value of this XMLBasicValue to out,
    * using "tab" as a hint for indentation.
    * relies on the String as delivered by toXMLString(tab)
    */
   public void writeXML(PrintWriter out, XMLFormatting fmt) {
      out.write(toXMLString(fmt.getTab()));
   }

   /**
    * appends the value of this XMLBasicValue to buf.
    * relies on the String as delivered by toXMLString(0)
    */
   public StringBuilder  appendXML(StringBuilder buf) {
      buf.append(toXMLString(0));
      return buf;
   }

   /**
    * appends the value of this XMLBasicValue to buf.
    * relies on the String as delivered by toXMLString(tab)
    */
   public StringBuilder  appendXML(StringBuilder buf, int tab) {
      buf.append(toXMLString(tab));
      return buf;
   }

   /**
    * appends the value of this XMLBasicValue to buf.
    * relies on the String as delivered by toXMLString(tab)
    */
   public StringBuilder  appendXML(StringBuilder buf, XMLFormatting fmt) {
      buf.append(toXMLString(fmt.getTab()));
      return buf;
   }

   /**
    * yields an XML encoded String of this XMLIzable object. 
    * The readXML() methods are able to reconstruct this object from 
    * the String delivered by toXMLString().
    */ 
   public String toXMLString() {
      return toXMLString(0);
   } 

   /**
    * yields an XML encoded String of this XMLIzable object. 
    * The readXML() methods are able to reconstruct this object from 
    * the String delivered by toXMLString().
    */ 
   public String toXMLString(int tab) {
      if (encoding != null && curtab == tab) return encoding;
      clearBuffer(BUFFERSIZE);
      curtab = tab;
      appendSpace(tab);
      buffer.append('<');
      buffer.append(getXMLTag());
      buffer.append(' ');
      //buffer.append(" val=\"");
      appendAttributeString(buffer);
      buffer.append("/>");
      encoding = buffer.toString();
      return encoding;
   } 

   /**
    * yields an XML encoded String of this XMLIzable object. 
    * The readXML() methods are able to reconstruct this object from 
    * the String delivered by toXMLString().
    */ 
   public String toXMLString(XMLFormatting fmt) {
      return toXMLString(fmt.getTab());  
   }

   /**
    * returns a  String that encodes the attributes for the XML encoding.
    * Relies on String appended by appendAttributeString()
    */
   public String toAttributeString() {
       return appendAttributeString(new StringBuilder()).toString();
   }

   /**
    * Appends a String to buf that encodes the attributes for the XML encoding.
    * and that encodes this basic value. 
    * MUST BE OVERWRITTEN BY IMPLEMENTATIONS. (The default implementation appends nothing).
    */
   public StringBuilder appendAttributeString(StringBuilder buf) {
      //example from XMLBasicValue: buf.append(" val=\"");buf.append(val); buf.append('"')
      // example: return appendAttribute("val", val);
      return buf;
   }


   public StringBuilder appendAttribute(StringBuilder buf, String attrName, String attrValue) {
       buf.append(attrName);
       buf.append("=\"");  
       buf.append(attrValue);
       buf.append('"');
       return buf;
   }      


   public StringBuilder appendAttribute(StringBuilder buf, String attrName, Object attrValue) {
       buf.append(attrName);
       buf.append("=\"");  
       buf.append(attrValue.toString());
       buf.append('"');
       return buf;
   }      

   /**
    * decodes the value from an attribute value String
    * MUST BE OVERWRITTEN BY IMPLEMENTATIONS.
    */
   public void decodeAttribute(String attrName, String attrValue, XMLTokenizer tokenizer) {
      //example from XMLBasicValue: val = Long.parseLong(attrValue);
   }

   /*
    * for testing puposes only
    */
   public void decodeAttribute(String attrName, String attrValue) {
      //example from XMLBasicValue: val = Long.parseLong(attrValue);
   }


   /** 
    * parse attribute value part of form:  = "value"   or:  = 'value' 
    * and returns the value as a String.
    * The value string is not "parsed" in the sense of XML parsed character data,
    * i.e. XML entities like &lt; or &apos; are not expanded.
    * If necessary, this should be done at a later stage, while decoding the attribute value.
    */
   protected final String getAttributeValue()  {
      skipSpaceChars();
      if (ci != '=') throw new RuntimeException("\'=\' character expected in attribute");
      nextChar();
      if (ci != '"' && ci != '\'') throw new RuntimeException("\" or \' character expected at start of attribute value");
      boolean aposmode = (ci=='\'');
      nextChar(); 
      int valStart = ciPos;
      while ((aposmode || ci != '"') && (! aposmode || ci != '\'') && ci != EOS) {
          nextChar();
      }    
      if (ci == EOS) throw new RuntimeException("\" or \' character expected at end of attribute value");
      int valEnd = ciPos;
      nextChar();
      return buffer.substring(valStart, valEnd);
   }


   /** 
    * parse attribute name part, consisting of a chracter sequence.
    * and returns the value as a String.
    */
   protected final String getAttributeName()  {
      skipSpaceChars();
      if (! isNameChar()) throw new RuntimeException("XMLBasicValue: Attribute name expected");
      int nameStart = ciPos;
      while (isNameChar()) {
          nextChar();
      }    
      if (ci == EOS) throw new RuntimeException("\" or \' character expected at end of attribute value");
      int nameEnd = ciPos;
      return buffer.substring(nameStart, nameEnd);
   }


   /*
    * skips "blank space" characters. i.e skips ' ', '\n', and '\r' characters.
    */
   private void skipSpaceChars() {
      while(ci == ' ' || ci == '\n' || ci == '\t' || ci == '\r') {
         ci =  (++ciPos < bufLen) ? buffer.charAt(ciPos) : EOS;
      } 
   }

    /*
     * defines the characters that can be used as attribute names
     */
   private boolean isNameChar() {
      return ('a'<=ci && ci<='z') ||  ('A'<=ci && ci<='Z') || 
             ('0'<=ci && ci<='9') ||  (ci=='_') || (ci=='-') || (ci=='.') || (ci==':');
   }

   /*
    * reads the  character from the buffer
    * assumes that the buffer has been filled, and bufLen has been set.
    */
   private int nextChar()  {
      ci =  (++ciPos < bufLen) ? buffer.charAt(ciPos) : EOS;
      return ci;
   }

   
  /**
   * example: add a similar static block for each implementation,
   * using the appropriate values for XMLTAG and CLASSNAME.
   *
   public static final String XMLTAG = "XMLBasicValue";
   public static final String CLASSNAME = "parlevink.xml.XMLBasicValue";
   static {
       XML.addClass(XMLTAG, CLASSNAME);     
   }
   */
}

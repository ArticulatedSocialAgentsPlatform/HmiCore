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

package hmi.xml;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

/**
 * XMLStructure is an extension of XMLizable, that makes some extra assumptions, 
 * and also requires a few more methods.
 * The most important assumption is that XMLStructures have an XMLEncoding
 * that consists of proper XML (unlike XMLizable, which does not require this).
 * In particular, it is required that "CharData" contents do not contain
 * forbidden characters, like '<'.
 * Moreover, the XML encoding should start with an opening STag of the
 * form "<ClassSpec", where "ClassSpec is an identifier that identifies the
 * Class of the implementation. This Class specification must be registered
 * with the XMLObject class, by calling one of its addXMLizableClass() method,
 * preferably in a static init block of the XMLStructure class.
 * These assumptions guarantee that an XMLStructure Object can be created by means
 * of the readXML method.
 * The methods required by this interface, including methods required by XMLizable are:
 *
 * public void readXML(Reader) throws IOException
 * public void readXML(String)
 * public void readXML(XMLTokenizer) throws IOException
 * public void writeXML(PrintWriter)
 * public void writeXML(PrintWriter, int tab)
 * public StringBuilder appendXML(StringBuilder buf);
 * public StringBuilder appendXML(StringBuilder buf, int tab);
 * public String toXMLString()
 * public String toXMLString(int tab)
 * public String getXMLTag();
 *
 * The readXML(XMLTokenizer) implementation is optional; the method must be
 * available, but is allowed to throw an UnsupportedOperationException.
 * The other methods must be properly implemented, and must be consistent.
 * That is, XML formatted Strings, as written by writeXML, or produced by toXMLString()
 * must be accepted by the readXML() methods and must lead to reconstruction of
 * the original XMLStructure object.
 * @author Job Zwiers
 */

public interface XMLStructure
{
   /**
    * reconstructs this XMLizable object by reading and parsing XML encoded text from a Reader.
    * This method can throw an (unchecked) ScanException in case of incorrectly
    * formatted XML. 
    */
   XMLStructure readXML(Reader in) throws IOException; 
   
   /**
    * reconstructs this XMLizable object by parsing an XML encoded String s.
    * This method can throw an (unchecked) ScanException in case of incorrectly
    * formatted XML. 
    */
   XMLStructure readXML(String s); 
   
   /**
    * reconstructs this XMLizable object by parsing a stream of XML tokens,
    * that are delivered by a XMLTokenizer.
    * This method need not be supported, in which case the method should throw
    * a java.lang.UnsupportedOperationException.
    * This method can throw an (unchecked) ScanException in case of incorrectly
    * formatted XML. 
    */
   XMLStructure readXML(XMLTokenizer tokenizer) throws IOException; 

 

   /**
    * yields an XML encoded String of this XMLIzable object. 
    * The readXML() methods should be able to reconstruct this object from 
    * the String delivered by toXMLString().
    */ 
   String toXMLString(); 
   

   /**
    * yields an XML encoded String of this XMLizable object. 
    * The readXML() methods should be able to reconstruct this object from 
    * the String delivered by toXMLString().
    */ 
   String toXMLString(int tab); 
   
   /**
    * yields an XML encoded String of this XMLizable object. 
    * The readXML() methods should be able to reconstruct this object from 
    * the String delivered by toXMLString().
    */ 
   String toXMLString(XMLFormatting fmt); 
   
   /**
    * like writeXML(PrintWriter), except that the XML encoding is appended
    * to a StringBuilder.
    * the latter must be returned.
    */
   StringBuilder appendXML(StringBuilder buf);

   /**
    * like writeXML(PrintWriter, int), except that the XML encoding is appended
    * to a StringBuilder.
    * The latter must be returned.
    */   
   StringBuilder appendXML(StringBuilder buf, int tab);
   
   
   /**
    * like writeXML(PrintWriter, XMLFormatting), except that the XML encoding is appended
    * to a StringBuilder.
    * The latter must be returned.
    */   
   StringBuilder appendXML(StringBuilder buf, XMLFormatting fmt);
   
   /**
    * writes an XML encoded String to "out".
    * The int "tab" can be used as a hint for indentation, and
    * denotes the indentation to be applied to the XML code
    * as a whole. 
    * This String should equal the result of toXMLString(tab).
    */
   void writeXML(PrintWriter out, int tab); 
  
   /**
    * writes an XML encoded String to "out".
    * The int "tab" can be used as a hint for indentation, and
    * denotes the indentation to be applied to the XML code
    * as a whole. 
    * This String should equal the result of toXMLString(tab).
    */
   void writeXML(PrintWriter out, XMLFormatting fmt); 
  
   /**
    * writes an XML encoded String to "out".
    * This String should equal the result of toXMLString().
    */
   void writeXML(PrintWriter out); 
  
   /**
    * returns the XML tag that is used to encode this type of XMLStructure.
    */
   String getXMLTag();
  
}

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

/**
 * CharDataConversion has methods to translate Strings (back and forth)
 * into XML style CharData. The latter refers to the character data that occurs between
 * XML tags: <tag ...> CharData </tag>, or as Attribute Value strings of the form : "text"
 * or 'text'.  Such character data and string texts cannot include the '<' and '&,
 * characters, and, in the case of text strings, it cannot contain the 
 * string quote or apostrophe character. Moreover, XML CharData cannot include the pattern "]]>".
 * CharDataConversion takes care of this: selected characters are replaced by XML entity references, 
 * such as "&lt;", representing a "<" character. Unmarshalling carries out the 
 * reverse translation. Marshalling performs the following translations:
 * '<' to "&lt;", '>' to "&gt;", '&' to "&amp;", '"' to "&quot;", '\'' to "&apos;".
 * (Note that the sequence of the form "]]>" is therefore translated to "]]&gt;").
 * @author Job Zwiers
 * adapted from old parlevink packages.
 */
public final class CharDataConversion 
{
    private CharDataConversion(){}
   /**
    * converts the String representation
    * to the encoded XML representation. 
    * The latter uses XML entity references: "&lt"; for '<', "&gt;" for '>', and "&amp;" for '&'
    * "&quot;" for '"', and "&apos;" for '\''.
    * The resulting external String can be safely used as CharData or attribute value
    * in an XML text.
    */
   public static String encode(String value) {
      int len = value.length();
      StringBuffer buf = new StringBuffer(len+10);
      for (int i=0; i<len; i++) {
          char ch = value.charAt(i);
               if (ch == '<') buf.append("&lt;");
          else if (ch == '>') buf.append("&gt;");
          else if (ch == '&') buf.append("&amp;");
          else if (ch == '"') buf.append("&quot;");
          else if (ch == '\'') buf.append("&apos;");
          else buf.append(ch);   
      }
      return buf.toString();
   }

   /**
    * converts the encoded XML representation
    * to the 'normal' String representation. 
    * The former uses "&lt"; for '<', "&gt;" for '>', and "&amp;" for '&';
    * "&quot;" for '"', and "&apos;" for '\''.
    * these XML entity references are translated back to ordinary characters.
    */
   public static String decode(String value) {
      int len = value.length();
      StringBuffer buf = new StringBuffer(len+10);
      int i = 0; 
      char ch;
      while (i < len) {
         // parse char value char i 
         if ( (ch=value.charAt(i++)) != '&') {
             buf.append(ch);
         } else {
            switch ( value.charAt(i++) ) {
               case 'l' : {  
                  if ( value.charAt(i++) != 't' || value.charAt(i++) != ';') throw new XMLScanException("error in \"&lt;\" reference");
                  buf.append('<');
                  break; 
               }           
               case 'g' : {               
                  if (value.charAt(i++) != 't' || value.charAt(i++) != ';') throw new XMLScanException("error in \"&gt;\" reference");
                  buf.append('>');
                  break; 
               }
               case 'q' : {               
                  if (value.charAt(i++) != 'u' ||  value.charAt(i++) != 'o' 
                   || value.charAt(i++) != 't' ||  value.charAt(i++) != ';') throw new XMLScanException("error in \"&quot;\" reference");
                  buf.append('"');
                  break; 
               }
               case 'a' : {  // could be &amp; or &apos;  
                  if (value.charAt(i) == 'm') {
                      i++;
                      if (value.charAt(i++) != 'p' || value.charAt(i++) != ';') 
                           throw new XMLScanException("error in \"&amp;\" reference");
                      else buf.append('&');
                  } else if (value.charAt(i) == 'p') {
                      i++;
                      if (value.charAt(i++) != 'o' || value.charAt(i++) != 's' || value.charAt(i++) != ';') 
                           throw new XMLScanException("error in \"&apos;\" reference");
                      else buf.append('\'');
                  } else {
                      throw new XMLScanException("error in \"$amp;\" or \"&apos;\" reference");
                  }
                  break; 
               }    
               default: { throw new XMLScanException("unexpected character after \'&\' : " + (value.charAt(i-1)) );            
               }
            }
         }
      }
      return buf.toString();
   }

}

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

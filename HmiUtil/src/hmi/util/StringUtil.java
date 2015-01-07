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

package hmi.util;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Collection;


/**
 * Some utils for Strings
 * @author Job Zwiers
 */
public final class StringUtil {
   
  /*
   * Disable StringUtil Object creation
   */
  private StringUtil() {}
  
  /**
   * Add the trimmed elements of a String str seperated by some seperator sep to collection c
   */
  public static void splitToCollection(String str, String sep, Collection<String> c)
  {
      if(!str.equals(""))
      {
          for(String s :str.split(sep))
          {
              c.add(s.trim());            
          }
      }
  }
  
  public static boolean isNumeric(String inputData) 
  {
      NumberFormat formatter = NumberFormat.getInstance();
      ParsePosition pos = new ParsePosition(0);
      formatter.parse(inputData, pos);
      return inputData.length() == pos.getIndex();
  }
  
  public static boolean isInteger(String s) 
  {
      for (int x = 0; x < s.length(); x++) {
        final char c = s.charAt(x);
        if (x == 0 && (c == '-')) continue;  // negative
        if ((c >= '0') && (c <= '9')) continue;  // 0 - 9
        return false; // invalid
      }
      return true; // valid
    }
  
  public static boolean isPostiveInteger(String s) 
  {
      for (int x = 0; x < s.length(); x++) {
          final char c = s.charAt(x);
          if ((c >= '0') && (c <= '9')) continue;  // 0 - 9
          return false; // invalid
        }
        return true; // valid
  }
  /**
   * Returns the index where String s1 and s2 differ for the first time,
   * or returns -1 if they actually equal strings.
   * If the two strings are not of equals size, and one of them
   * is actually a prefix of the other, then the length of the
   * shorter string is returned. 
   */ 
  public static int diff(String s1, String s2) {
      if (s1 == null || s2 == null) return -1;
      int len1 = s1.length();
      int len2 = s2.length();
      
      int len =  (len1<len2) ? len1 : len2;
       
      
      for (int i=0; i<len; i++) {
         if (s1.charAt(i) != s2.charAt(i)) return i;
      }
      
      return (len1==len2) ? -1 : len;
    }
    
  /**
   * Returns the TextPos where the two string differ for the first time.
   * When the two strings are equal, an undefined TextPos is returned.
   */ 
  public static TextPos diffPos(String s1, String s2) {
      int d = diff(s1, s2);
      if (d <0) return new TextPos(); // return undefined TextPos
      if (s1.length() >= s2.length()) {
          return getTextPos(s1, d);
      } else {
           return getTextPos(s2, d);
      }
    }
    
  /**
   * Returns a String showing whether their length are equal and
   * if so, where the first difference between two Strings occurs.
   * If completely equal, or both are null, then the empty String is returned
   */ 
  public static String showDiff(String msg, String s1, String s2) {
      if (s1 == null || s2 == null) return "";
      int len1 = s1.length();
      int len2 = s2.length();
      if (len1 != len2) {
         return (msg + ": len1=" + len1 + ", len2=" + len2);
      }
      for (int i=0; i<len1; i++) {
         if (s1.charAt(i) != s2.charAt(i)) {
            int line = getLineNumber(s1, i);
            int linepos = getLinePos(s1, i);
            return msg + ": At line=" + line + ", pos=" + linepos + " ch1:\'" + s1.charAt(i) + "\'  ch2:\'" + s2.charAt(i) + "\'";
         }
      }
      return "";
    }
    
    
    /**
     * Counts the line number within a specified String,
     * for a specified position inside that String.
     * The first line gets number 0.
     * A line ends at a \n char; this \n char still belongs to that line.
     * The specified position should be less than the string length;
     * if not, line number -1 is returned.
     */   
    public static int getLineNumber(String s, int charPos) {   
       if (s == null || charPos <0 || charPos >= s.length()) return -1;
       int line = 0;
       for (int i=1; i<=charPos; i++) {
          if (s.charAt(i-1) == '\n') line++; 
       }
       return line; 
    }
  
    /**
     * Gets the line position of some character within a String,
     * i.e the position within the line where the character occurs.
     * The first line position is 0, the position of the \n that ends 
     * a line is the length of that line. 
     * The specified position should be less than the string length;
     * if not, line position -1 is returned.
     */   
    public static int getLinePos(String s, int charPos) {   
       if (s == null || charPos <0 || charPos >= s.length()) return -1;
       int linepos = 0;
       for (int i=charPos-1; i>=0; i--) {
          if (s.charAt(i) == '\n') return linepos;
          linepos++;
       }
       return linepos; 
    }
 
 
    public static TextPos getTextPos(String s, int charPos) {
       if (s == null || charPos <0 || charPos >= s.length()) {
         return new TextPos(-1, -1);
       }
       int line = 0;
       int linePos = 0;
       for (int i=1; i<=charPos; i++) {
          linePos++;
          if (s.charAt(i-1) == '\n') {
            line++; 
            linePos = 0;
          }
       }
       TextPos result = new TextPos(line, linePos);
       return result; 
    }
 
    /**
     * Local class, for reposting positions in a String
     * in the form of (line-number, line-pos).
     */
    public static class TextPos {
       private int lineNumber, linePos;
       
       /**
        * Creates a new undefined LinePos
        */
       public TextPos() {
          lineNumber = -1;
          linePos = -1; 
       }
       
       /**
        * Creates a new LinePos
        */
       public TextPos(int lineNumber, int linePos) {
         this.lineNumber = lineNumber;
         this.linePos = linePos;
       }
       
       /**
        * Sets the line number
        */
       public void setLineNumber(int lineNumber) {
          this.lineNumber = lineNumber;
       }
       
       /**
        * Sets the line position
        */
       public void setLinePos(int linePos) {
          this.linePos = linePos;
       }
       
       /**
        * returns the line number
        */
       public int getLineNumber() {
         return lineNumber;
       }
       
       /**
        * returns the line pos
        */
       public int getLinePos() {
         return linePos;
       }
       
       @Override
       public boolean equals(Object textpos){
          if(textpos==null)return false;
          if(textpos instanceof TextPos)
          { 
              TextPos pos = (TextPos) textpos;
              return pos.lineNumber == lineNumber && pos.linePos == linePos;
          }
          return false;
       }
       
       @Override
       public int hashCode() {
          return lineNumber + linePos;
       }
       
       /**
        * return true for a defined text position,
        * i.e. when (at least) the line number is >= 0
        */
       public boolean isDefined() {
         return lineNumber >= 0;
       }
       
       @Override
       /**
        * Returns a String of the form (lineNumber, linePos)
        */
       public String toString() {
          if (lineNumber < 0) {
            return "(-,-)";
          } else if (linePos < 0) {
            return "(" + lineNumber + ", -)";
          } else {
            return "(" + lineNumber + ", " + linePos + ")"; 
          }
       }  
    }
 
}

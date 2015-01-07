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
import java.util.StringTokenizer;

/** 
 * Vertex-Weight bounds.
 * @author Job Zwiers
 */
public class V extends ColladaElement {

   //private static final int NR_OF_INTS_PER_LINE = 16;
      
   private int[] indices;
   //public float[] weights;
   private static final int NR_OF_INDICES_PER_LINE = 20;
   
   public V() {
      super();
   }
   
   public V(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }
 
 
//   /**
//    * returns indices    array
//    */
//   public int[] getIndices() {
//      return indices;
//   }
    
   /**
    * Returns indices[i]
    */   
   public int getIndex(int i) {
      return indices[i];
   }           
   
   /**
    * return number of indices
    */
   public int getNrIndices() {
      return indices.length;
   }           
              
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
    appendNewLine(buf, fmt);
    if (indices == null || indices.length == 0) return buf;
      int tabCounter = NR_OF_INDICES_PER_LINE;
      buf.append(indices[0]);tabCounter--;
      for (int i=1; i<indices.length; i++) {
         if (tabCounter == 0) {
            buf.append('\n'); 
            appendSpaces(buf, fmt);
            tabCounter = NR_OF_INDICES_PER_LINE;   
         }         
         buf.append(' '); buf.append(indices[i]);
         tabCounter--;
      }
      return buf;  
   }

   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      String vertexWeightPairs = tokenizer.takeCharData();
      StringTokenizer strTokenizer = new StringTokenizer(vertexWeightPairs, " \t\n\r\f");
      int nrOfTokens = strTokenizer.countTokens();
      if (nrOfTokens%2 != 0) {
         getCollada().warning("Vertex_weights/V with odd number of elements");
          //throw new RuntimeException("Collada V element with odd number of elements");  
      }
     // int nrOfPairs = nrOfTokens/2;
      indices = new int[nrOfTokens];
      //weights = new float[nrOfPairs];
      for (int i=0; i<nrOfTokens; i++) {
         indices[i] = Integer.parseInt(strTokenizer.nextToken());
      }
//      int pt = 0;
//      while (strTokenizer.hasMoreTokens() ) {
//         indices[pt] = Integer.parseInt(strTokenizer.nextToken());
//         weights[pt++] = Float.parseFloat(strTokenizer.nextToken());
//      }   
   }

   
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "v";
 
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

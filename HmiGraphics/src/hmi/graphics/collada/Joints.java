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
import java.util.ArrayList;

/** 
 * Storage of graphical representation, like rasetr data.
 * @author Job Zwiers
 */
public class Joints extends ColladaElement {
 
   // no attributes
   
   // child elements:
    // at least two Inputs required, one with semantic="JOINT", other? with "INV_BIND_MATRIX"
   private ArrayList<Input> inputs = new ArrayList<Input>();
   private ArrayList<Extra> extras = new ArrayList<Extra>();
   
   
   private String[] jointSIDs = null;
   private float[] invBindMatrices = null;
   private String jointSourceId = null;
   private String invbindmatrixSourceId = null;
   
   public Joints() {
      super();
   }
   
   public Joints(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada);
      readXML(tokenizer); 
   }
   
   /**
    * Tries to resolve the reference for the input with joint semantic 
    */
   public void resolve() {
      for (Input inp : inputs) {
         if (inp.getSemantic().equals("JOINT")) {
            jointSourceId = urlToId(inp.getSource());
         }  
         if (inp.getSemantic().equals("INV_BIND_MATRIX")) {
            invbindmatrixSourceId = urlToId(inp.getSource());
         }  
      }
      if (jointSourceId == null) {
         throw new RuntimeException("<joints> element: no <input> with JOINT semantic specified");
      }
      Source jointSource = getCollada().getSource(jointSourceId);
      jointSIDs = jointSource.getHomogeneousNameData();
      if (invbindmatrixSourceId == null) {
         throw new RuntimeException("<joints> element: no <input> with INV_BIND_MATRIX semantic specified");
      }
      Source invBindMatrixSource = getCollada().getSource(invbindmatrixSourceId);
      invBindMatrices = invBindMatrixSource.getHomogeneousFloatData();
   }
   
   /**
    * Return a String array with joint names
    */
   public String[] getJointSIDs() {
      if (jointSIDs == null) resolve();
      return jointSIDs;
   }
   
   /**
    * Return an array of float array with inverse bind matrices. (16 floats per matrix)
    */
   public float[] getInvBindMatrices() {
      if (invBindMatrices == null) resolve();
      return invBindMatrices;
//      int nrOfMatrices = invBindMatrices.length/16;
//      float[][] matrices = new float[nrOfMatrices][16];
//      for (int m=0; m<nrOfMatrices; m++) {
//         System.arraycopy(invBindMatrices, 16 * m, matrices[m], 0, 16);
//      }     
//      return matrices;
   }
   
   /**
    * appends the XML content
    */
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {  
      appendXMLStructureList(buf, fmt, inputs); 
      appendXMLStructureList(buf, fmt, extras); 
      return buf;
   }

   /**
    * decodes the XML content
    */
   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Input.xmlTag()))  {          
                 inputs.add(new Input(getCollada(), tokenizer));       
         } else if (tag.equals(Extra.xmlTag()))  {                
                 extras.add(new Extra(getCollada(), tokenizer));      
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("Joints: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }
      addColladaNodes(inputs);
      addColladaNodes(extras);
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "joints";
 
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

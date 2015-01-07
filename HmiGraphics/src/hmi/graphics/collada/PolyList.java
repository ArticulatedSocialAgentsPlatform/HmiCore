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

/** 
 * Declares the binding of geometric primitives and vertex attributes for a mesh element.
 * @author Job Zwiers
 */
public class PolyList extends PrimitiveMeshElement {
    
   private P p;
   private VCount vcount;

   /**
    * Default constructor
    */     
   public PolyList() {
      super();
      setMeshType(Mesh.MeshType.Polylist);
   }

   /**
    * Constructor used to create a PolyList Object from XML
    */      
   public PolyList(Collada collada, XMLTokenizer tokenizer) throws IOException {
      super(collada); 
      readXML(tokenizer); 
      setMeshType(Mesh.MeshType.Polylist);
   }
 

   /**
    * Returns the polygon vertex counts, i.e. one integer value for every polygon,
    * denoting the number of vertices for that polygon
    */
   public int[] getVCounts() {
      return vcount.getCounts();
   }
 
   @Override
   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
      appendXMLStructureList(buf, fmt, getInputs());
      appendXMLStructure(buf, fmt, vcount);
      appendXMLStructure(buf, fmt, p);
      appendXMLStructureList(buf, fmt, getExtras());
      return buf;  
   }
   
   @Override
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
      while (tokenizer.atSTag()) {
         String tag = tokenizer.getTagName();
         if (tag.equals(Input.xmlTag()))  {   
             Input inp = new Input(getCollada(), tokenizer);         
             getInputs().add(inp);   
             if (inp.getOffset() > getMaxOffset()) setMaxOffset(inp.getOffset()); 
         } else if (tag.equals(Extra.xmlTag()))  {                
             getExtras().add(new Extra(getCollada(), tokenizer));   
         } else if (tag.equals(VCount.xmlTag()))  {                
             vcount = new VCount(getCollada(), tokenizer);
         } else if (tag.equals(P.xmlTag()))  {                
             p = new P(getCollada(), tokenizer);
         } else {         
            getCollada().warning(tokenizer.getErrorMessage("PolyList: skip : " + tokenizer.getTagName()));
            tokenizer.skipTag();
         }
      }    
      addColladaNodes(getInputs());
      addColladaNodes(getExtras());
      addColladaNode(vcount);
      addColladaNode(p);  
   }
 
   /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "polylist";
 
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
   
   /*
    * creates separate index arrays from the single P index list, dividing up according
    * to the number of offsets used in the inputs
    */
   @Override
   public void createIndexArrays() {
      int[] pindices = p.getIndices();
      setNrOfOffsets(getMaxOffset()+1);
      setIndexArrayLength(pindices.length/getNrOfOffsets());
      if (pindices.length % getNrOfOffsets() != 0 ) {
         getCollada().warning("Warning: PolyList.createIndexArrays: number of P indices (" 
           + pindices.length + ") is not a multiple of the number of offsets (" + getNrOfOffsets() + ")" );  
      }
      allocateIndices(getNrOfOffsets(), getIndexArrayLength());
      int noffsets = getNrOfOffsets();
      for (int indexOffset = 0; indexOffset < noffsets; indexOffset++) {
         int[] indices = getIndices(indexOffset);
         for (int i=0; i<getIndexArrayLength(); i++) {
            indices[i] = pindices[ i*noffsets + indexOffset]; 
         }
      }
   }
   
 
}

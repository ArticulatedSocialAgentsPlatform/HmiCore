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
import hmi.xml.XMLTokenizer;

import java.util.ArrayList;
import java.util.HashMap;


/** 
 * PrimitiveMeshElement is the super class for Lines, LineStrips, Polygons, PolyList, Triangles, TriFans, and TriStrips.
 * @author Job Zwiers
 */
public class PrimitiveMeshElement extends ColladaElement {
    
   private int count;
   private int first = 0; //  index of first triangle           optional and non-standard extension
   private int end;       //  index of last triangle = end-1    optional and non-standard extension
   private String material;
   
 
   private int maxOffset = 0;
   private int nrOfOffsets;
   private int[][] indices;
   private int indexArrayLength;
   private ArrayList<Input> inputs = new ArrayList<Input>();
   private ArrayList<Extra> extras = new ArrayList<Extra>(2);

   private Mesh.MeshType meshType;
    
   public  PrimitiveMeshElement() { 
      super();
   }
    
   public PrimitiveMeshElement(Collada collada)  {
      super(collada);
   } 
                
   /** Return count */            
   public int getCount() { return count; }
   // public void setCount(int count) { this.count = count; }
   
   /** Return first */            
   public int getFirst() { return first; }
   
   /** Return last */            
   public int getEnd() { return end; }
   
   /** Return maxOffset */
   public int getMaxOffset() { return maxOffset; }   
   /** Set maxOffset */    
   public void setMaxOffset(int maxOffset ) { this.maxOffset = maxOffset; }   
   
   /** Return nrOfOffsets */
   public int getNrOfOffsets() { return nrOfOffsets; }   
   /** Set nrOfOffsets */    
   public void setNrOfOffsets(int nrOfOffsets ) { this.nrOfOffsets = nrOfOffsets; }  
   
   
   public ArrayList<Input> getInputs() {  return inputs;   }
   
   /** Return meshType */
   public Mesh.MeshType getMeshType() { return meshType; }   
   /** Set meshType */    
   public void setMeshType(Mesh.MeshType meshType ) { this.meshType = meshType; }   
   
   /** Return indexArrayLength */
   public int getIndexArrayLength() { return indexArrayLength; }   
   /** Set indexArrayLength */    
   public void setIndexArrayLength(int indexArrayLength ) { this.indexArrayLength = indexArrayLength; }  

   /**
    * Returns an array with indices, corresponding to a specified Collada offset value, used
    * for Input elements. The indices themselves are extracted from the Collada P element for this PrimitiveMeshElement.
    */
   public int[] getIndices(int offset) {  return indices[offset];   }
   
   
   /**
    * Allocates a new array of index arrays */
   public void allocateIndices(int nrOfOffsets, int indexArrayLength) {
      indices = new int[nrOfOffsets][indexArrayLength]; 
   }
   
   
   /** Return extras */
   public ArrayList<Extra> getExtras() {  return extras;  }


   /** Return material */
   public String getMaterialId() {   return material;  }


   /**
    * appends a String of attributes to buf.
    */
   @Override
   public StringBuilder appendAttributes(StringBuilder buf) {
      super.appendAttributes(buf);
      appendAttribute(buf, "count", count);      
      if (first > 0) appendAttribute(buf, "first", first);  // non-standard attribute
      if (end > 0) appendAttribute(buf, "end", end);  // non-standard attribute
      appendAttribute(buf, "material", material);
      return buf;
   }


   /**
    * decodes the XML attributes
    */
   @Override
   public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer) {      
      count     = getRequiredIntAttribute("count", attrMap, tokenizer);
      first     = getOptionalIntAttribute("first", attrMap, 0);
      end      = getOptionalIntAttribute("end", attrMap, -1);
      material  = getOptionalAttribute("material", attrMap);
      super.decodeAttributes(attrMap, tokenizer);
   } 
    
   /* overwrite in subclasses */
   public void createIndexArrays() {
   }
 


 

//   public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
//      appendXMLStructureList(buf, fmt, inputs);
////      appendXMLStructure(buf, fmt, vcount);
//      appendXMLStructure(buf, fmt, p);
//      appendXMLStructureList(buf, fmt, plist);
//      appendXMLStructureList(buf, fmt, phlist);
//      appendXMLStructureList(buf, fmt, extras);
//      return buf;  
//   }
//
//   public void decodeContent(XMLTokenizer tokenizer) throws IOException {
//      while (tokenizer.atSTag()) {
//         String tag = tokenizer.getTagName();
//         if (tag.equals(Input.xmlTag()))  {   
//             Input inp = new Input(tokenizer);         
//             inputs.add(inp);   
//             if (inp.offset > maxOffset) maxOffset = inp.offset;
//         } else if (tag.equals(Extra.xmlTag()))  {                
//             extras.add(new Extra(tokenizer));   
//
//         } else if (tag.equals(P.xmlTag()))  {                
//             p = new P(tokenizer);
//         } else {         
//            Collada.warning(getXMLTag() + ": skip : " + tokenizer.getTagName());
//            tokenizer.skipTag();
//         }
//      }    
//      addColladaNodes(inputs);
//      addColladaNodes(extras);
//     // addColladaNode(vcount);
//      addColladaNode(p);  
//   } 
    
//   /** 
//    * Subclass implementations should return a MeshData object, containing the data arrays and index arrays
//    * for a given Collada Primitive element, taking into account inputs like the Position data
//    * from a Vertices element. These inputs are passed in by means of the vertexInputs parameter.
//    */    
//   public GMesh getGMesh(java.util.List<Input> vertexInputs) {
//       return null;  
//   }

}

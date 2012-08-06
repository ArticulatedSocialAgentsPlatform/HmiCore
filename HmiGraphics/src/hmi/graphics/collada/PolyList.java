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

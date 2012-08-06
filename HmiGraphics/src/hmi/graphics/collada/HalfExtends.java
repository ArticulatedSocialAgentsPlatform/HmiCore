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

import hmi.math.Vec3f;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.IOException;

/**
 * Used for the size of a Box
 * @author welberge
 */
public class HalfExtends extends ColladaElement
{
    private float[] xyz = Vec3f.getVec3f();
    
    public HalfExtends() {
        super();
    }
    
    public HalfExtends(Collada collada, XMLTokenizer tokenizer) throws IOException {
        super(collada);   
        readXML(tokenizer); 
    }
    
    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) {
        appendFloats(buf, getXyz(), ' ', fmt, Vec3f.VEC3F_SIZE);
        return buf;  
     }

    @Override
     public void decodeContent(XMLTokenizer tokenizer) throws IOException 
     {
         decodeFloatArray(tokenizer.takeCharData(), getXyz());
     }
   
     /*
    * The XML Stag for XML encoding
    */
   private static final String XMLTAG = "half_extents";
 
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

public void setXyz(float[] xyz)
{
    this.xyz = xyz;
}

public float[] getXyz()
{
    return xyz;
}
}

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
public class Triangles extends PrimitiveMeshElement
{

    private P p;

    /**
     * Default constructor
     */
    public Triangles()
    {
        super();
        setMeshType(Mesh.MeshType.Triangles);
    }

    /**
     * Constructor used to create a PolyList Object from XML
     */
    public Triangles(Collada collada, XMLTokenizer tokenizer) throws IOException
    {
        super(collada);
        readXML(tokenizer);
        setMeshType(Mesh.MeshType.Triangles);
    }

    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        appendXMLStructureList(buf, fmt, getInputs());
        appendXMLStructure(buf, fmt, p);
        appendXMLStructureList(buf, fmt, getExtras());
        return buf;
    }

    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException
    {
        while (tokenizer.atSTag())
        {
            String tag = tokenizer.getTagName();
            if (tag.equals(Input.xmlTag()))
            {
                Input inp = new Input(getCollada(), tokenizer);
                getInputs().add(inp);
                if (inp.getOffset() > getMaxOffset()) setMaxOffset(inp.getOffset());
            }
            else if (tag.equals(Extra.xmlTag()))
            {
                getExtras().add(new Extra(getCollada(), tokenizer));
            }
            else if (tag.equals(P.xmlTag()))
            {
                p = new P(getCollada(), tokenizer);
            }
            else
            {
                getCollada().warning(tokenizer.getErrorMessage("Triangles: skip : " + tokenizer.getTagName()));
                tokenizer.skipTag();
            }
        }
        addColladaNodes(getInputs());
        addColladaNodes(getExtras());
        addColladaNode(p);
    }

    /*
     * The XML Stag for XML encoding
     */
    private static final String XMLTAG = "triangles";

    /**
     * The XML Stag for XML encoding
     */
    public static String xmlTag()
    {
        return XMLTAG;
    }

    /**
     * returns the XML Stag for XML encoding
     */
    @Override
    public String getXMLTag()
    {
        return XMLTAG;
    }

    /*
     * creates separate index arrays from the single P index list, dividing up according
     * to the number of offsets used in the inputs
     */
    @Override
    public void createIndexArrays()
    {
        setNrOfOffsets(getMaxOffset() + 1);
        int count = getCount();
        int first = getFirst();
        int end = getEnd();

        if ((end >= 0 && count >= 0) || (end < 0 && count < 0)) logger
                .error("Triangles.createIndexArrays: inconsistent specification of \"end\" and \"count\" parameters");

        if (count >= 0)
        {
            end = first + count;
        }
        else
        {
            count = end - first;
        }
        // By now we have end = first +count, count = nr Of triangles, end = index of last tri + 1 etc

        int nrOfTris = count;
        int indexLen = 3 * nrOfTris;
        // - getStart(); // normally start == 0 (non-standard attribute)

        setIndexArrayLength(indexLen); // three indices per triangle
        int[] pindices = p.getIndices(); // length should be count * 3 * nrOfOffsets == indexArrayLength * nrOfOffsets
        int expectedLen = getCount() * 3 * getNrOfOffsets();
        if (pindices.length != expectedLen)
        {
            getCollada().warning(
                    "Warning: Collada Triangles.createIndexArrays: number of P indices (" + pindices.length
                            + ") does not match the number of triangles and  offsets (" + getCount() + ", " + getNrOfOffsets() + ")");
        }

        int startIndex = 3 * first; // default ==0

        int endIndex = getIndexArrayLength() - 1;

        // System.out.println("Triangles.createIndexArrays: indexArrayLength = " + indexArrayLength + " count = " + count);
        allocateIndices(getNrOfOffsets(), getIndexArrayLength());
        // indices = new int[getNrOfOffsets()][indexArrayLength];
        for (int indexOffset = 0; indexOffset < getNrOfOffsets(); indexOffset++)
        {
            int[] indices = getIndices(indexOffset);
            for (int i = 0; i < indexLen; i++)
            {
                indices[i] = pindices[(startIndex + i) * getNrOfOffsets() + indexOffset];
            }
        }
    }

}

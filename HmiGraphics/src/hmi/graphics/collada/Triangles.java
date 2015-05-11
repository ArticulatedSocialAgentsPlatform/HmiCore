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

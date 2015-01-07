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
 * Declares the binding of geometric primitives and vertex attributes for a mesh element.
 * 
 * @author Job Zwiers
 */
public class Polygons extends PrimitiveMeshElement
{

    private ArrayList<P> plist = new ArrayList<P>();
    private ArrayList<PH> phlist = new ArrayList<PH>();
    private int[] vcounts;

    /**
     * Default constructor
     */
    public Polygons()
    {
        super();
        setMeshType(Mesh.MeshType.Polygons);
    }

    /**
     * Constructor used to create a Polygons Object from XML
     */
    public Polygons(Collada collada, XMLTokenizer tokenizer) throws IOException
    {
        super(collada);
        readXML(tokenizer);
        setMeshType(Mesh.MeshType.Polygons);
    }

    /**
     * Returns the polygon vertex counts, i.e. one integer value for every polygon, denoting the number of vertices for that polygon This value is
     * defined only after calling the createIndexArrays method.
     */
    public int[] getVCounts()
    {
        return vcounts;
    }

    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        appendXMLStructureList(buf, fmt, getInputs());
        appendXMLStructureList(buf, fmt, plist);
        appendXMLStructureList(buf, fmt, phlist);
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
                if (inp.getOffset() > getMaxOffset())
                    setMaxOffset(inp.getOffset());
            }
            else if (tag.equals(Extra.xmlTag()))
            {
                getExtras().add(new Extra(getCollada(), tokenizer));
            }
            else if (tag.equals(P.xmlTag()))
            {
                plist.add(new P(getCollada(), tokenizer));
            }
            else if (tag.equals(PH.xmlTag()))
            {
                phlist.add(new PH(getCollada(), tokenizer));
            }
            else
            {
                getCollada().warning(tokenizer.getErrorMessage("Polygons: skip : " + tokenizer.getTagName()));
                tokenizer.skipTag();
            }
        }
        addColladaNodes(getInputs());
        addColladaNodes(plist);
        addColladaNodes(phlist);
        addColladaNodes(getExtras());
    }

    /*
     * The XML Stag for XML encoding
     */
    private static final String XMLTAG = "polygons";

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
     * creates separate index arrays from the single P index list, dividing up according to the number of offsets used in the inputs
     */
    @Override
    public void createIndexArrays()
    {
        setNrOfOffsets(getMaxOffset() + 1);
        int indexArrayLength = 0;
        int pcount = 0;
        vcounts = new int[getCount()];
        for (P p : plist)
        {
            int plen = p.getIndices().length;
            if (plen % getNrOfOffsets() != 0)
            {
                getCollada().warning(
                        "Warning: Collada Polygon Mesh, createIndices: number of P indices (" + plen + ") for polygon " + pcount
                                + " is not a multiple of the number of offsets (" + getNrOfOffsets() + ")");
            }
            int nrOfVerts = plen / getNrOfOffsets();
            indexArrayLength += nrOfVerts;
            vcounts[pcount] = nrOfVerts;
            pcount++;
        }
        setIndexArrayLength(indexArrayLength);
        if (getCount() != pcount)
        {
            getCollada().warning(
                    "Warning: Collada Polygon Mesh, specified polygon count (" + getCount() + ") does not match actual number of polygons (" + pcount
                            + ")");
        }
        // System.out.println("Polygons.createIndexArrays: indexArrayLength = " + indexArrayLength + " polygon count = " + pcount);
        // indices = new int[getNrOfOffsets()][indexArrayLength];
        allocateIndices(getNrOfOffsets(), getIndexArrayLength());
        int i = 0; // index in indices arrays
        int pi = 0; // index in vcounts array
        for (P p : plist)
        {
            int[] pindices = p.getIndices();
            int nrOfVerts = vcounts[pi];
            int noffsets = getNrOfOffsets();
            for (int vi = 0; vi < nrOfVerts; vi++)
            {// add indices for vertex vi from polygon number pi
                for (int indexOffset = 0; indexOffset < noffsets; indexOffset++)
                {
                    int[] indices = getIndices(indexOffset);
                    indices[i] = pindices[vi * noffsets + indexOffset];
                }
                i++;
            }
            pi++;
        }
    }

}

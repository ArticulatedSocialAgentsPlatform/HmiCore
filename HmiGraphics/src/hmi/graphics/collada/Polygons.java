/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
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

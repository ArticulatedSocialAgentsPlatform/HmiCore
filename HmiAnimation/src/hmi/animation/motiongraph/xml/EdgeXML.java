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
package hmi.animation.motiongraph.xml;

import hmi.animation.SkeletonInterpolator;
import hmi.animation.motiongraph.Edge;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.HashMap;

import lombok.Getter;

/**
 * XML representation and loader/saver of a motiongraph Edge
 * @author hvanwelbergen
 *
 */
public class EdgeXML extends XMLStructureAdapter
{
    @Getter
    private Edge edge;
    @Getter
    private int id;

    @Getter
    private int startNodeId;
    @Getter
    private int endNodeId;

    public EdgeXML()
    {

    }

    public EdgeXML(Edge e)
    {
        this.edge = e;
    }

    @Override
    public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer)
    {
        id = getRequiredIntAttribute("id", attrMap, tokenizer);
        startNodeId = getRequiredIntAttribute("startNode", attrMap, tokenizer);
        endNodeId = getRequiredIntAttribute("endNode", attrMap, tokenizer);
    }

    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException
    {
        String tag = tokenizer.getTagName();
        if (tokenizer.atSTag())
        {
            if (tag.equals(SkeletonInterpolator.xmlTag()))
            {
                SkeletonInterpolator skel = new SkeletonInterpolator();
                skel.readXML(tokenizer);
                edge = new Edge(skel);
            }
        }

    }

    @Override
    public StringBuilder appendAttributeString(StringBuilder buf, XMLFormatting fmt)
    {
        appendAttribute(buf, "id", edge.getId());
        appendAttribute(buf, "startNode", edge.getStartNode().getId());
        appendAttribute(buf, "endNode", edge.getEndNode().getId());
        return super.appendAttributeString(buf, fmt);
    }

    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        return edge.getMotion().appendXML(buf, fmt);
    }

    private static final String XMLTAG = "edge";

    /**
     * The XML Stag for XML encoding -- use this static method when you want to see if a given
     * String equals the xml tag for this class
     */
    public static String xmlTag()
    {
        return XMLTAG;
    }

    /**
     * The XML Stag for XML encoding -- use this method to find out the run-time xml tag of an
     * object
     */
    @Override
    public String getXMLTag()
    {
        return XMLTAG;
    }

    
}

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
 * Max3D profile for TechniqueCore profile MAX3D
 * @author Job Zwiers
 */
public class Max3DProfile extends ColladaElement
{

    // private String profile;

    // profile Max3D:
    private Max3DFrameRate max3dFrameRate;
    private Max3DHelper max3dHelper;
    private float amount = -1; // -1 denotes: not used
    private int faceted = -1; // -1 denotes: not used
    private int doubleSided = -1; // -1 denotes: not used
    private int wireframe = -1; // -1 denotes: not used
    private int faceMap = -1; // -1 denotes: not used

    public Max3DProfile()
    {
        super();
    }

    public Max3DProfile(Collada collada, XMLTokenizer tokenizer) throws IOException
    {
        super(collada);
        decodeContent(tokenizer); // N.B. non-standard for ColladaElements
    }

    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        appendXMLStructure(buf, fmt, max3dFrameRate);
        appendXMLStructure(buf, fmt, max3dHelper);

        appendOptionalFloatElement(buf, "amount", amount, -1, fmt);

        appendOptionalIntElement(buf, "faceted", faceted, -1, fmt);
        appendOptionalIntElement(buf, "double_sided", doubleSided, -1, fmt);
        appendOptionalIntElement(buf, "wireframe", wireframe, -1, fmt);
        appendOptionalIntElement(buf, "face_map", faceMap, -1, fmt);
        return buf;
    }

    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException
    {

        while (tokenizer.atSTag())
        {
            String tag = tokenizer.getTagName();

            if (tag.equals(Max3DFrameRate.xmlTag()))
            {
                max3dFrameRate = new Max3DFrameRate(getCollada(), tokenizer);
            }
            else if (tag.equals(Max3DHelper.xmlTag()))
            {
                max3dHelper = new Max3DHelper(getCollada(), tokenizer);
            }
            else if (tag.equals("amount"))
            {
                amount = tokenizer.takeFloatElement("amount");
            }
            else if (tag.equals("faceted"))
            {
                faceted = tokenizer.takeIntElement("faceted");
            }
            else if (tag.equals("double_sided"))
            {
                doubleSided = tokenizer.takeIntElement("double_sided");
            }
            else if (tag.equals("wireframe"))
            {
                wireframe = tokenizer.takeIntElement("wireframe");
            }
            else if (tag.equals("face_map"))
            {
                faceMap = tokenizer.takeIntElement("face_map");
            }
            else if (tag.equals("skylight"))
            {
                tokenizer.skipTag();

            }
            else
            {
                getCollada().warning(tokenizer.getErrorMessage("Technique, MAX3D profile, skipping: " + tokenizer.getTagName()));
                tokenizer.skipTag();
            }
        }
        addColladaNode(max3dFrameRate);
        addColladaNode(max3dHelper);
    }

}

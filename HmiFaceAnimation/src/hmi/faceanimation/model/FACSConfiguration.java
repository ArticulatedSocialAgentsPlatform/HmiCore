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
package hmi.faceanimation.model;

import hmi.faceanimation.model.ActionUnit.Symmetry;
import hmi.faceanimation.model.FACS.Side;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.HashMap;

/**
 * A FACS Configuration (set of values for AU's, both for the left and right side of the face). The float array of values first contains all Left
 * values for all AUs, then all Right values. Values for Symmetrical AUs are stored in the first half of the array, i.e. among the Left values.
 * 
 * @author PaulRC
 */
public class FACSConfiguration extends XMLStructureAdapter implements Configuration
{
    private Float[] values;
    private int numAus;

    /**
     * Constructor
     */
    public FACSConfiguration()
    {
        numAus = FACS.getActionUnits().size();
        values = new Float[numAus * 2];
    }

    public void setValue(Side side, int index, float value)
    {
        if (side == Side.RIGHT)
            index += numAus;
        values[index] = value;
    }

    public Float getValue(Side side, int index)
    {
        if (side == Side.RIGHT)
            index += numAus;
        return values[index];
    }

    /**
     * Sets the values. This must be a float[] with the right length.
     * 
     * @param values
     * @throws Exception
     */
    public void setValues(Float[] values) throws Exception
    {
        if (values.length == numAus * 2)
        {
            this.values = values;
        }
        else
        {
            throw new Exception("wrong number of values");
        }
    }

    public Float[] getValues()
    {
        return values;
    }

    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        for (int i = 0; i < numAus; i++)
        {
            if (values[i] != null)
            {
                ActionUnit au = FACS.getActionUnitsByIndex().get(Integer.valueOf(i));
                if (au.getSymmetry() != Symmetry.ASYMMETRIC)
                {
                    appendAU(au.getNumber(), Side.NONE, values[i], buf, fmt);
                }
                else
                {
                    appendAU(au.getNumber(), Side.LEFT, values[i], buf, fmt);
                }
            }
        }
        for (int i = numAus; i < 2 * numAus - 1; i++)
        {
            if (values[i] != null)
            {
                ActionUnit au = FACS.getActionUnitsByIndex().get(Integer.valueOf(i - numAus));
                if (au.getSymmetry() == Symmetry.ASYMMETRIC)
                {
                    appendAU(au.getNumber(), Side.RIGHT, values[i], buf, fmt);
                }
            }
        }
        return buf;
    }

    public StringBuilder appendAU(int number, Side s, Float value, StringBuilder buf, XMLFormatting fmt)
    {
        appendEmptyTag(buf, fmt, "AU", "number", "" + number, "side", "" + s, "value", value.toString());
        return buf;
    }

    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException
    {
        while (tokenizer.atSTag("AU"))
        {
            HashMap<String, String> attrMap = tokenizer.getAttributes();
            int number = getRequiredIntAttribute("number", attrMap, tokenizer);
            String s = getRequiredAttribute("side", attrMap, tokenizer);
            float value = getRequiredFloatAttribute("value", attrMap, tokenizer);
            int index = FACS.getActionUnit(number).getIndex();
            if (s.equals("RIGHT"))
            {
                index += numAus;
            }
            values[index] = value;
            tokenizer.takeSTag("AU");
            tokenizer.takeETag("AU");
        }
    }

    /*
     * The XML Stag for XML encoding
     */
    private static final String XMLTAG = "FACSConfiguration";

    /**
     * The XML Stag for XML encoding -- use this static method when you want to see if a given String equals the xml tag for this class
     */
    public static String xmlTag()
    {
        return XMLTAG;
    }

    /**
     * The XML Stag for XML encoding -- use this method to find out the run-time xml tag of an object
     */
    @Override
    public String getXMLTag()
    {
        return XMLTAG;
    }

}

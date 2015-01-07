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

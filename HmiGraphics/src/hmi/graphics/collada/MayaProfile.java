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
 * Maya profile for TechniqueCore profile MAYA
 * 
 * @author Job Zwiers
 */
public class MayaProfile extends ColladaElement
{

    // private String profile;

    // <mirrorU>0</mirrorU>
    // <mirrorV>0</mirrorV>
    // <wrapU>1</wrapU>
    // <wrapV>1</wrapV>
    // <repeatU>1</repeatU>
    // <repeatV>1</repeatV>
    // <offsetU>0</offsetU>
    // <offsetV>0</offsetV>
    // <rotateUV>0</rotateUV>

    // profile Max3D:
    private int mirrorU = 0; // Contains one boolean value that represents whether the texture should mirror within the U or V dimensions.
                             // Mirroring can only occur if the corresponding wrap flag is also set.
    private int mirrorV = 0;
    private int wrapU = 1; // Contains one boolean value that represents whether the texture should wrap within the U or V dimensions.
    private int wrapV = 1;
    private float repeatU = 1f; // Contains one floating-point Contains one floating-point value that represents a multiplier to apply on the texture
                                // coordinates before sampling.
    private float repeatV = 1f;
    private float offsetU = 0; // Contains one floating-point value that represents an offset to add on the texture coordinates before sampling.
    private float offsetV = 0;
    private float rotateUV = 0; // Contains one floating-point value that represents an angle of rotation to apply to the texture coordinates before
                                // sampling
    private String preInfinity, postInfinity;

    public MayaProfile()
    {
        super();
    }

    public MayaProfile(Collada collada, XMLTokenizer tokenizer) throws IOException
    {
        super(collada);
        decodeContent(tokenizer); // N.B. non-standard for ColladaElements
    }

    public float getMirrorU()
    {
        return mirrorU;
    }

    public float getMirrorV()
    {
        return mirrorV;
    }

    public float getWrapU()
    {
        return wrapU;
    }

    public float getWrapV()
    {
        return wrapV;
    }

    public float getRepeatU()
    {
        return repeatU;
    }

    public float getRepeatV()
    {
        return repeatV;
    }

    public float getOffsetU()
    {
        return offsetU;
    }

    public float getOffsetV()
    {
        return offsetV;
    }

    public float getRotateUV()
    {
        return rotateUV;
    }

    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        appendOptionalIntElement(buf, "mirrorU", mirrorU, 0, fmt);
        appendOptionalIntElement(buf, "mirrorV", mirrorV, 0, fmt);
        appendOptionalIntElement(buf, "wrapU", wrapU, 0, fmt);
        appendOptionalIntElement(buf, "wrapV", wrapV, 0, fmt);
        appendOptionalFloatElement(buf, "repeatU", repeatU, 0, fmt);
        appendOptionalFloatElement(buf, "repeatV", repeatV, 0, fmt);
        appendOptionalFloatElement(buf, "offsetU", offsetU, 0, fmt);
        appendOptionalFloatElement(buf, "offsetV", offsetV, 0, fmt);
        appendOptionalFloatElement(buf, "rotateUV", rotateUV, 0, fmt);
        appendTextElement(buf, "pre_infinity", preInfinity, fmt);
        appendTextElement(buf, "post_infinity", postInfinity, fmt);
        return buf;
    }

    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException
    {

        while (tokenizer.atSTag())
        {
            String tag = tokenizer.getTagName();

            if (tag.equals("mirrorU"))
            {
                mirrorU = tokenizer.takeIntElement("mirrorU");
            }
            else if (tag.equals("mirrorV"))
            {
                mirrorV = tokenizer.takeIntElement("mirrorV");
            }
            else if (tag.equals("wrapU"))
            {
                wrapU = tokenizer.takeIntElement("wrapU");
            }
            else if (tag.equals("wrapV"))
            {
                wrapV = tokenizer.takeIntElement("wrapV");
            }
            else if (tag.equals("repeatU"))
            {
                repeatU = tokenizer.takeFloatElement("repeatU");
            }
            else if (tag.equals("repeatV"))
            {
                repeatV = tokenizer.takeFloatElement("repeatV");
            }
            else if (tag.equals("offsetU"))
            {
                offsetU = tokenizer.takeFloatElement("offsetU");
            }
            else if (tag.equals("offsetV"))
            {
                offsetV = tokenizer.takeFloatElement("offsetV");
            }
            else if (tag.equals("rotateUV"))
            {
                rotateUV = tokenizer.takeFloatElement("rotateUV");
            }
            else if (tag.equals("pre_infinity"))
            {
                preInfinity = tokenizer.takeTextElement("pre_infinity");
            }
            else if (tag.equals("post_infinity"))
            {
                postInfinity = tokenizer.takeTextElement("post_infinity");
            }
            else
            {
                getCollada().warning(tokenizer.getErrorMessage("Technique, MAYA profile, skipping: " + tokenizer.getTagName()));
                tokenizer.skipTag();
            }
        }
    }

}

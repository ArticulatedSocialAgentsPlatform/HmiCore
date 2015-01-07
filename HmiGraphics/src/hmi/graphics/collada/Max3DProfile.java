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

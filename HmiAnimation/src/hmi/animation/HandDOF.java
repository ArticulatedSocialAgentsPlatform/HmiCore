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
package hmi.animation;

import java.util.HashMap;

import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

/**
 * DOF for the hand as used in the HandAnimator
 * @author Herwin
 * 
 */
public class HandDOF extends XMLStructureAdapter
{
    public double PIPPinkyFlexion;
    public double PIPRingFlexion;
    public double PIPMiddleFlexion;
    public double PIPIndexFlexion;
    public double IPThumbFlexion;

    public double MCPPinkyFlexion;
    public double MCPRingFlexion;
    public double MCPMiddleFlexion;
    public double MCPIndexFlexion;
    public double MCPThumbFlexion;

    public double MCPPinkyAbduction;
    public double MCPRingAbduction;
    public double MCPMiddleAbduction;
    public double MCPIndexAbduction;

    public double TMCFlexion;
    public double TMCAbduction;

    public double getPIPFlexion(String joint)
    {
        switch (joint)
        {
        case "r_pinky":
        case "l_pinky":
            return PIPPinkyFlexion;
        case "r_ring":
        case "l_ring":
            return PIPRingFlexion;
        case "r_middle":
        case "l_middle":
            return PIPMiddleFlexion;
        case "r_index":
        case "l_index":
            return PIPIndexFlexion;
        default:
            throw new IllegalArgumentException(joint + " is not a valid PIP joint");
        }
    }

    public double getMCPFlexion(String joint)
    {
        switch (joint)
        {
        case "r_pinky":
        case "l_pinky":
            return MCPPinkyFlexion;
        case "r_ring":
        case "l_ring":
            return MCPRingFlexion;
        case "r_middle":
        case "l_middle":
            return MCPMiddleFlexion;
        case "r_index":
        case "l_index":
            return MCPIndexFlexion;
        default:
            throw new IllegalArgumentException(joint + " is not a valid MCP joint");
        }
    }

    public double getMCPAbduction(String joint)
    {
        switch (joint)
        {
        case "r_pinky":
        case "l_pinky":
            return MCPPinkyAbduction;
        case "r_ring":
        case "l_ring":
            return MCPRingAbduction;
        case "r_middle":
        case "l_middle":
            return MCPMiddleAbduction;
        case "r_index":
        case "l_index":
            return MCPIndexAbduction;
        default:
            throw new IllegalArgumentException(joint + " is not a valid MCP joint");
        }
    }

    @Override
    public StringBuilder appendAttributeString(StringBuilder buf, XMLFormatting fmt)
    {
        appendAttribute(buf, "PIPPinkyFlexion", PIPPinkyFlexion);
        appendAttribute(buf, "PIPRingFlexion", PIPRingFlexion);
        appendAttribute(buf, "PIPMiddleFlexion", PIPMiddleFlexion);
        appendAttribute(buf, "PIPIndexFlexion", PIPIndexFlexion);
        appendAttribute(buf, "IPThumbFlexion", IPThumbFlexion);
        appendAttribute(buf, "MCPPinkyFlexion", MCPPinkyFlexion);
        appendAttribute(buf, "MCPRingFlexion", MCPRingFlexion);
        appendAttribute(buf, "MCPMiddleFlexion", MCPMiddleFlexion);
        appendAttribute(buf, "MCPIndexFlexion", MCPIndexFlexion);
        appendAttribute(buf, "MCPThumbFlexion", MCPThumbFlexion);
        appendAttribute(buf, "MCPPinkyAbduction", MCPPinkyAbduction);
        appendAttribute(buf, "MCPRingAbduction", MCPRingAbduction);
        appendAttribute(buf, "MCPMiddleAbduction", MCPMiddleAbduction);
        appendAttribute(buf, "MCPIndexAbduction", MCPIndexAbduction);
        appendAttribute(buf, "TMCFlexion", TMCFlexion);
        appendAttribute(buf, "TMCAbduction", TMCAbduction);        
        return super.appendAttributeString(buf, fmt);
    }

    @Override
    public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer)
    {
        PIPPinkyFlexion = getRequiredFloatAttribute("PIPPinkyFlexion", attrMap, tokenizer);
        PIPRingFlexion = getRequiredFloatAttribute("PIPRingFlexion", attrMap, tokenizer);
        PIPMiddleFlexion = getRequiredFloatAttribute("PIPMiddleFlexion", attrMap, tokenizer);
        PIPIndexFlexion = getRequiredFloatAttribute("PIPIndexFlexion", attrMap, tokenizer);
        IPThumbFlexion = getRequiredFloatAttribute("IPThumbFlexion", attrMap, tokenizer);
        MCPPinkyFlexion = getRequiredFloatAttribute("MCPPinkyFlexion", attrMap, tokenizer);
        MCPRingFlexion = getRequiredFloatAttribute("MCPRingFlexion", attrMap, tokenizer);
        MCPMiddleFlexion = getRequiredFloatAttribute("MCPMiddleFlexion", attrMap, tokenizer);
        MCPIndexFlexion = getRequiredFloatAttribute("MCPIndexFlexion", attrMap, tokenizer);
        MCPThumbFlexion = getRequiredFloatAttribute("MCPThumbFlexion", attrMap, tokenizer);
        MCPPinkyAbduction = getRequiredFloatAttribute("MCPPinkyAbduction", attrMap, tokenizer);
        MCPRingAbduction = getRequiredFloatAttribute("MCPRingAbduction", attrMap, tokenizer);
        MCPMiddleAbduction = getRequiredFloatAttribute("MCPMiddleAbduction", attrMap, tokenizer);
        MCPIndexAbduction = getRequiredFloatAttribute("MCPIndexAbduction", attrMap, tokenizer);
        TMCFlexion = getRequiredFloatAttribute("TMCFlexion", attrMap, tokenizer);
        TMCAbduction = getRequiredFloatAttribute("TMCAbduction", attrMap, tokenizer);
        super.decodeAttributes(attrMap, tokenizer);
    }

    private static final String XMLTAG = "handdof";

    public static String xmlTag()
    {
        return XMLTAG;
    }

    @Override
    public String getXMLTag()
    {
        return XMLTAG;
    }
}

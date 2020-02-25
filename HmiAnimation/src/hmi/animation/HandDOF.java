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

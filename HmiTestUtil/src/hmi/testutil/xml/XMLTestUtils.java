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
package hmi.testutil.xml;

import java.io.IOException;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.SAXException;

import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;

/**
 * XMLStructure adapter testing utilities
 * @author welberge
 *
 */
public final class XMLTestUtils
{
    private XMLTestUtils(){}
    
    /**
     * Does adapter.readXML(input), then compares the input with adapter.writeXML
     */
    public static void testAppendXML(XMLStructureAdapter adapter, String input, String nsPrefix, String nameSpace) throws SAXException, IOException
    {
        adapter.readXML(input);        
        StringBuilder buf = new StringBuilder();
        adapter.appendXML(buf, new XMLFormatting(), nsPrefix,nameSpace);        
        XMLTestCase xmlTester = new XMLTestCase(""){};
        XMLUnit.setIgnoreWhitespace(true);
        xmlTester.assertXMLEqual(input, buf.toString()); 
    }
}

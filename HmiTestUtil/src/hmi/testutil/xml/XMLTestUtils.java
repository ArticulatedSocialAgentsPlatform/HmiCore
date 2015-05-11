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

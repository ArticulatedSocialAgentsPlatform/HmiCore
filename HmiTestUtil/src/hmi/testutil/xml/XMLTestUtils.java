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

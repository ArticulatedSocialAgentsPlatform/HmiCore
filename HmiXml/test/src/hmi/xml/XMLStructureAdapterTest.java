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
/*
 * XMLStructureAdapter JUnit test
 */

package hmi.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.primitives.Ints;

//import lombok.Getter;

/**
 * JUnit test for hmi.xml.XMLStructureAdapter
 */
public class XMLStructureAdapterTest
{

    public XMLStructureAdapterTest()
    {
    }

    @Test
    public void basics()
    {
        XMLStructureAdapter xa = new XMLStructureAdapter();
        String xmlEncoding = xa.toXMLString();
        String expectedCoding = "<null>\n" + "</null>";
        // System.out.println("xa0: \n" + xmlEncoding + "\n expected: \n" + expectedCoding);
        assertTrue(xmlEncoding.equals(expectedCoding));

    }

    @Test
    public void writeTag()
    {
        XMLStructureAdapter xa = new XMLStructureAdapter()
        {
            public String getXMLTag()
            {
                return "test";
            }
        };
        // xa.setNamespacelabel(null);
        String xmlEncoding = xa.toXMLString();
        String expectedCoding = "<test>\n" + "</test>";
        // System.out.println("xa1: \n" + xmlEncoding + "\n expected: \n" + expectedCoding);
        assertTrue(xmlEncoding.equals(expectedCoding));

    }

    @Test(expected=XMLScanException.class)
    public void testQuoteInTag()
    {
        XMLStructureAdapter xa = new XMLStructureAdapter()
        {
            public String getXMLTag()
            {
                return "gaze";
            }
        };
        xa.readXML("<gaze id=\"gaze1\" start=\"0\" ready=\"1\" target=\"redbox\" \"influence=\"HEAD\" />");
    }
    
    @Test
    public void decodeIntArray1()
    {
        int ints[] = XMLStructureAdapter.decodeIntArray("1 2 3", " ");
        assertThat(Ints.asList(ints), IsIterableContainingInOrder.contains(1, 2, 3));
    }

    @Test
    public void decodeIntArray2()
    {
        int ints[] = XMLStructureAdapter.decodeIntArray("1 2\n 3", " \n");
        assertThat(Ints.asList(ints), IsIterableContainingInOrder.contains(1, 2, 3));
    }

    @Test
    public void decodeIntArray3()
    {
        int ints[] = XMLStructureAdapter.decodeIntArray("1 2 \n 3", " \n");
        assertThat(Ints.asList(ints), IsIterableContainingInOrder.contains(1, 2, 3));
    }

    class TestXML extends XMLStructureAdapter
    {
        @Getter
        String id;
        
        @Getter
        String test;
        
        @Override
        public String getXMLTag()
        {
            return "test";
        }
        
        @Override
        public String getNamespace()
        {
            return "testxml";
        }
        
        @Override
        public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer)
        {
            id = getRequiredAttribute("id", attrMap, tokenizer);
            test = getRequiredAttribute("externalns:testname", attrMap, tokenizer);
        }
    }
    
    @Test
    public void testReadNameSpacedAttribute()
    {
        TestXML xa = new TestXML();
        String xmlString = "<test test:testname=\"testval\" id=\"idx\" xmlns=\"testxml\" xmlns:test=\"externalns\"/>";
        xa.readXML(xmlString);
        assertEquals("idx", xa.getId());
        assertEquals("testval", xa.getTest());        
    }
    
    @Test
    public void testReadNameSpacedAttribute2()
    {
        TestXML xa = new TestXML();
        String xmlString = "<test id=\"idx\" test:testname=\"testval\" xmlns=\"testxml\" xmlns:test=\"externalns\"/>";
        xa.readXML(xmlString);
        assertEquals("idx", xa.getId());
        assertEquals("testval", xa.getTest());
    }
    
    @Test
    public void writeNamespaceTag()
    {
        XMLStructureAdapter xa = new XMLStructureAdapter()
        {
            public String getXMLTag()
            {
                return "test";
            }

            public String getNamespace()
            {
                return "http://hmi.ns.test";
            }
        };
        StringBuilder buf = new StringBuilder();
        XMLFormatting fmt = new XMLFormatting();
        xa.appendXML(buf, fmt, "ns", "http://hmi.ns.test");
        String xmlEncoding = buf.toString();

        String expectedCoding = "<ns:test xmlns:ns=\"http://hmi.ns.test\">\n" + "</ns:test>";
        // System.out.println("xa2: \n" + xmlEncoding + "\n expected: \n" + expectedCoding);
        assertTrue(xmlEncoding.equals(expectedCoding));
    }

    @Test
    public void writeNamespaceTag2()
    {
        XMLStructureAdapter xa = new XMLStructureAdapter()
        {
            public String getXMLTag()
            {
                return "test";
            }

            public String getNamespace()
            {
                return "http://hmi.ns.test";
            }
        };
        String xmlEncoding = xa.toXMLString("ns", "http://hmi.ns.test");

        String expectedCoding = "<ns:test xmlns:ns=\"http://hmi.ns.test\">\n" + "</ns:test>";
        // System.out.println("xa3: \n" + xmlEncoding + "\n expected: \n" + expectedCoding);
        assertTrue(xmlEncoding.equals(expectedCoding));
    }

    @Test
    public void writeNamespaceTag3()
    {
        XMLStructureAdapter xa = new XMLStructureAdapter()
        {
            public String getXMLTag()
            {
                return "test";
            }

            public String getNamespace()
            {
                return "http://hmi.ns.test";
            }
        };
        String xmlEncoding = xa.toXMLString("ns", "http://hmi.ns.test");

        String expectedCoding = "<ns:test xmlns:ns=\"http://hmi.ns.test\">\n" + "</ns:test>";
        // System.out.println("xa3: \n" + xmlEncoding + "\n expected: \n" + expectedCoding);
        assertTrue(xmlEncoding.equals(expectedCoding));
    }

    @Test
    public void testDefaultNS()
    {
        XMLStructureAdapter test = new XMLStructureAdapter()
        {
            public String getXMLTag()
            {
                return "test";
            }

            XMLStructureAdapter test1 = new XMLStructureAdapter()
            {
                public String getXMLTag()
                {
                    return "test1";
                }

                public String getNamespace()
                {
                    return "http://hmi.ns1.test";
                }
            };
            XMLStructureAdapter test2 = new XMLStructureAdapter()
            {
                public String getXMLTag()
                {
                    return "test2";
                }
            };

            @Override
            public void decodeContent(XMLTokenizer tokenizer) throws IOException
            {

                while (tokenizer.atSTag())
                {
                    String tag = tokenizer.getTagName();
                    if (tag.equals("test1"))
                    {
                        test1.readXML(tokenizer);
                    }
                    else if (tag.equals("test2"))
                    {
                        test2.readXML(tokenizer);
                    }
                }
            }
        };
        test.readXML("<test>" + "<test1  xmlns=\"http://hmi.ns1.test\">" + "</test1>" + "<test2/>" + "</test>");
    }

    @Test
    public void testAmpInAttribute()
    {
        class TestAmpXML extends XMLStructureAdapter
        {
            public String attr;

            @Override
            public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer)
            {
                attr = getRequiredAttribute("attr", attrMap, tokenizer);
            }

            @Override
            public String getXMLTag()
            {
                return XMLTAG;
            }

            public static final String XMLTAG = "TestXMLAmp";
        }
        TestAmpXML test = new TestAmpXML();

        test.readXML("<TestXMLAmp attr=\"&gt;&amp;&amp;&lt;&quot;\"/>");
        assertEquals(">&&<\"", test.attr);
    }

    @Test
    public void testAppendXML1()
    {
        XMLStructureAdapter test = new XMLStructureAdapter()
        {
            public String getXMLTag()
            {
                return "xmlTag";
            }

            // public String getNamespace() { return "http://hmi.ns2.test"; }
            public boolean hasContent()
            {
                return false;
            }
        };
        StringBuilder buf = new StringBuilder();
        XMLFormatting fmt = new XMLFormatting(0);
        List<XMLNameSpace> namespaceList = new ArrayList<XMLNameSpace>();
        test.appendXML(buf, fmt, namespaceList);
        String encoded1 = buf.toString();
        // System.out.println("encoded1=" + encoded1);
        // System.out.println(hmi.util.StringUtil.showDiff("encoded1", encoded1, "<xmlTag/>"));
        assertTrue(encoded1.equals("<xmlTag/>"));

        buf = new StringBuilder();
        namespaceList.add(new XMLNameSpace("pref1", "http://hmi.ns1.test"));
        test.appendXML(buf, fmt, namespaceList);
        String encoded2 = buf.toString();
        // System.out.println("encoded2=" + encoded2);
        assertTrue(encoded2.equals("<xmlTag xmlns:pref1=\"http://hmi.ns1.test\"/>"));

        buf = new StringBuilder();
        namespaceList.add(new XMLNameSpace("pref2", "http://hmi.ns2.test"));
        test.appendXML(buf, fmt, namespaceList);
        String encoded3 = buf.toString();
        // System.out.println("encoded3=" + encoded3);
        assertTrue(encoded3.equals("<xmlTag xmlns:pref1=\"http://hmi.ns1.test\" xmlns:pref2=\"http://hmi.ns2.test\"/>"));

    }

    @Test
    public void testAppendXML2()
    {
        XMLStructureAdapter test = new XMLStructureAdapter()
        {
            public String getXMLTag()
            {
                return "xmlTag";
            }

            public String getNamespace()
            {
                return "http://hmi.ns2.test";
            }

            public boolean hasContent()
            {
                return false;
            }
        };
        StringBuilder buf = new StringBuilder();
        XMLFormatting fmt = new XMLFormatting(0);
        List<XMLNameSpace> namespaceList = new ArrayList<XMLNameSpace>();
        test.appendXML(buf, fmt, namespaceList);
        String encoded1 = buf.toString();
        // System.out.println("encoded1=" + encoded1);
        // System.out.println(hmi.util.StringUtil.showDiff("encoded1", encoded1, "<xmlTag/>"));
        assertTrue(encoded1.equals("<xmlTag xmlns=\"http://hmi.ns2.test\"/>"));

        buf = new StringBuilder();
        namespaceList.add(new XMLNameSpace("pref1", "http://hmi.ns1.test"));
        test.appendXML(buf, fmt, namespaceList);
        String encoded2 = buf.toString();
        // System.out.println("encoded2=" + encoded2);
        assertTrue(encoded2.equals("<xmlTag xmlns=\"http://hmi.ns2.test\" xmlns:pref1=\"http://hmi.ns1.test\"/>"));

        buf = new StringBuilder();
        namespaceList.add(new XMLNameSpace("pref2", "http://hmi.ns2.test"));
        test.appendXML(buf, fmt, namespaceList);
        String encoded3 = buf.toString();
        // System.out.println("encoded3=" + encoded3);
        assertTrue(encoded3.equals("<pref2:xmlTag xmlns:pref1=\"http://hmi.ns1.test\" xmlns:pref2=\"http://hmi.ns2.test\"/>"));

    }

    @Test
    public void testAppendXML3()
    {

        StringBuilder buf = new StringBuilder();
        XMLFormatting fmt = new XMLFormatting(0);

        fmt.pushXMLNameSpace(new XMLNameSpace("bmlt", "http://hmi.ns1.test"));
        XMLStructureAdapter.appendNamespacedAttribute(buf, fmt, "http://hmi.ns1.test", "bmltattr", "bmltval");

        String encoded = buf.toString();
        // System.out.println("encoded=" + encoded);
        // System.out.println(hmi.util.StringUtil.showDiff("encoded", encoded, " bmlt:bmltattr=\"bmltval\""));
        assertEquals(" bmlt:bmltattr=\"bmltval\"", encoded);

        buf = new StringBuilder();
    }

    @Test
    public void testWriteDefaultNS()
    {
        final class StubSA extends XMLStructureAdapter
        {
            @Override
            public String getNamespace()
            {
                return "http://stubns";
            }

            @Override
            public String getXMLTag()
            {
                return "stub";
            }
        }

        StubSA ssaSource = new StubSA();
        StringBuilder buf = new StringBuilder();
        // This should automatically insert the default namespace in buf
        ssaSource.appendXML(buf);

        StubSA ssaTarget = new StubSA();
        ssaTarget.readXML(buf.toString());
    }

    final class TestA extends XMLStructureAdapter
    {

        String section;

        public String getSection()
        {
            return section;
        }

        @Override
        public String getXMLTag()
        {
            return "TestA";
        }

        @Override
        public void decodeContent(XMLTokenizer tokenizer) throws IOException
        {
            section = tokenizer.getXMLSection();
        }
    }

    @Test
    public void testGetEmptyXMLSection()
    {
        TestA testA = new TestA();
        testA.readXML("<TestA><test/></TestA>");
        assertEquals("<test/>", testA.getSection());
    }

    @Test(expected = XMLScanException.class)
    public void testMissingPrefix()
    {
        final class TestB extends XMLStructureAdapter
        {
            @Override
            public String getXMLTag()
            {
                return "TestB";
            }
            
            @Override
            public boolean decodeAttribute(String attrName, String attrValue, XMLTokenizer tokenizer)
            {
                throw new XMLScanException("Attribute "+attrName+" not supported.");
            }
        }
        TestB testB = new TestB();
        testB.readXML("<TestB bmla:appendAfter=\"bml1\"/>");        
    }
    
    @Test
    public void testGetEmptyXMLSectionWithClosingTag()
    {
        TestA testA = new TestA();
        testA.readXML("<TestA><test></test></TestA>");
        assertEquals("<test></test>", testA.getSection());
    }

    @Test
    public void testBOM() throws IOException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write(0xEF);
        os.write(0xBB);
        os.write(0xBF);
        os.write("<TestA><test></test></TestA>".getBytes(Charsets.UTF_8));
        TestA testA = new TestA();
        testA.readXML(os.toString(Charsets.UTF_8.toString()));
        assertEquals("<test></test>", testA.getSection());
    }

    @Test(expected = XMLScanException.class)
    public void testNonBOMAtStart()
    {
        TestA testA = new TestA();
        testA.readXML("xyz<TestA><test></test></TestA>");
        assertEquals("<test></test>", testA.getSection());
    }

    class TestXMLAdapter extends XMLStructureAdapter
    {
        @Getter
        private String testVal = "val";

        public StringBuilder appendAttributeString(StringBuilder buf, XMLFormatting fmt)
        {
            appendNamespacedAttribute(buf, fmt, "http://test.com", "testatr", "testval");
            return buf;
        }

        public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer)
        {
            testVal = getRequiredAttribute("http://test.com:testatr", attrMap, tokenizer);
        }

        @Override
        public String getXMLTag()
        {
            return "Test";
        }
    }

    @Test
    public void testAppendWithAutomaticNamespace()
    {
        StringBuilder buf = new StringBuilder();
        TestXMLAdapter sIn = new TestXMLAdapter();
        sIn.appendXML(buf);

        TestXMLAdapter sOut = new TestXMLAdapter();
        sOut.readXML(buf.toString());
        assertEquals("testval", sOut.getTestVal());
    }

    @Test(expected = Exception.class)
    public void testAppend()
    {
        TestXMLAdapter adapter = new TestXMLAdapter();
        XMLFormatting fmt = new XMLFormatting();
        StringBuilder buf = new StringBuilder();
        adapter.appendXML(buf, fmt);
        fmt.popXMLNameSpace();// namespacestack should be empty
    }
    
    
}

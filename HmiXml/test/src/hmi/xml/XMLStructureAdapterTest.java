/*
 * XMLStructureAdapter JUnit test
 */

package hmi.xml;

import static org.junit.Assert.*;
import org.junit.*;

import java.io.IOException;
import java.util.*;

import lombok.Getter;

import org.hamcrest.collection.IsIterableContainingInOrder;

import com.google.common.primitives.Ints;

/**
 * JUnit test for hmi.xml.XMLStructureAdapter
 */
public class XMLStructureAdapterTest
{

    public XMLStructureAdapterTest()
    {
    }

    @Before
    public void setUp()
    { // common initialization, executed for every test.
    }

    @After
    public void tearDown()
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
        List<XMLNameSpace> namespaceList = new ArrayList<XMLNameSpace>();

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
        @Getter
        String content;

        @Override
        public String getXMLTag()
        {
            return "TestA";
        }

        @Override
        public void decodeContent(XMLTokenizer tokenizer) throws IOException
        {
            content = tokenizer.getXMLSection();
        }
    }
    
    @Test
    public void testGetEmptyXMLSection()
    {
        TestA testA = new TestA();
        testA.readXML("<TestA><test/></TestA>");
        assertEquals("<test/>", testA.getContent());
    }
    
    @Test
    public void testGetEmptyXMLSectionWithClosingTag()
    {
        TestA testA = new TestA();
        testA.readXML("<TestA><test></test></TestA>");
        assertEquals("<test></test>", testA.getContent());
    }
}

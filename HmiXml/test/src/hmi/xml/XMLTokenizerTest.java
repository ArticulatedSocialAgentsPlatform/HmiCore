/*
 * XMLTokenizer JUnit test
 */

package hmi.xml;

import static org.junit.Assert.*;
import org.junit.*;
import hmi.util.*;
import java.io.*;
import java.net.*;

/**
 * JUnit test for hmi.xml.XMLTokenizer
 */
public class XMLTokenizerTest
{

    public XMLTokenizerTest()
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

    /**
     * a few basic tests
     */
    @Test
    public void basics()
    {

        new XMLTokenizer();

    }

    /**
     * test XMLTokenizer for a minimal xml file.
     */
    @Test
    public void basics2()
    {
        Resources testRes = new Resources("XmlTokenizerTests");
        Reader basictest1 = testRes.getReader("basictest1.xml");
        assertTrue(basictest1 != null);
        XMLTokenizer tokenizer = new XMLTokenizer(basictest1);
        try
        {
            assertTrue(tokenizer.atSTag("test"));
            tokenizer.takeSTag("test");
            assertTrue(tokenizer.atSTag("tg"));
            tokenizer.takeSTag("tg");
            assertTrue(tokenizer.atCharData());
            tokenizer.takeCharData();
            assertTrue(tokenizer.atETag("tg"));
            tokenizer.takeETag();
            assertTrue(tokenizer.atCharData());
            tokenizer.takeCharData();
            assertTrue(tokenizer.atETag("test"));
            tokenizer.takeETag();
            assertTrue(tokenizer.atEndOfDocument());
        }
        catch (Exception e)
        {
            System.out.println("XMLTokenizerTest: " + e);
            assertTrue(false); // Exceptions should not happen
        }
    }

    /**
     * test XMLTokenizer for a minimal xml file.
     * 
     * @throws IOException
     */
    @Test
    public void testFileConstructor() throws IOException
    {
        // String udir = System.getProperty("user.dir");
        String sharedprojectdir = System.getProperty("shared.project.root");
        String udir = sharedprojectdir + "/HmiCore/HmiXml";

        // System.out.println("user.dir = " + udir);
        File testFile = new File(udir + "/test/resource/XmlTokenizerTests/basictest1.xml"); // requires a copy of basictest1.xml inside the
                                                                                            // test/src/hmi/xml directory
        // File testFile = new File("../../../resource/XmlTokenizerTests/basictest1.xml"); // does work only from the right user dir

        XMLTokenizer tokenizer = new XMLTokenizer(testFile);
        assertTrue(tokenizer.atSTag("test"));
        tokenizer.takeSTag("test");
        assertTrue(tokenizer.atSTag("tg"));
        tokenizer.takeSTag("tg");
        assertTrue(tokenizer.atCharData());
        tokenizer.takeCharData();
        assertTrue(tokenizer.atETag("tg"));
        tokenizer.takeETag();
        assertTrue(tokenizer.atCharData());
        tokenizer.takeCharData();
        assertTrue(tokenizer.atETag("test"));
        tokenizer.takeETag();
        assertTrue(tokenizer.atEndOfDocument());
    }

    /**
     * test XMLTokenizer for a minimal xml file, accessed via an file URL
     * 
     * @throws IOException
     */
    @Test
    public void testURLConstructor() throws IOException
    {

        String sharedprojectdir = System.getProperty("shared.project.root");
        String udir = sharedprojectdir + "/HmiCore/HmiXml";

        // System.out.println("user.dir = " + udir);
        URL testFileURL = new URL("file:///" + udir + "/test/resource/XmlTokenizerTests/basictest1.xml");

        XMLTokenizer tokenizer = new XMLTokenizer(testFileURL);
        assertTrue(tokenizer.atSTag("test"));
        tokenizer.takeSTag("test");
        assertTrue(tokenizer.atSTag("tg"));
        tokenizer.takeSTag("tg");
        assertTrue(tokenizer.atCharData());
        tokenizer.takeCharData();
        assertTrue(tokenizer.atETag("tg"));
        tokenizer.takeETag();
        assertTrue(tokenizer.atCharData());
        tokenizer.takeCharData();
        assertTrue(tokenizer.atETag("test"));
        tokenizer.takeETag();
        assertTrue(tokenizer.atEndOfDocument());
    }

    /**
     * test basic usage of namespaces when namespace recognition is turned off
     */
    @Test
    public void namespace1a()
    {
        Resources testRes = new Resources("XmlTokenizerTests");
        Reader namespacetest1 = testRes.getReader("namespacetest1.xml");
        XMLTokenizer tokenizer = new XMLTokenizer(namespacetest1);
        tokenizer.setRecognizeNamespaces(false);
        try
        {
            assertTrue(tokenizer.atSTag("ns:test"));
            tokenizer.takeSTag("ns:test");
            assertTrue(tokenizer.atSTag("ns:innertag"));
            tokenizer.takeSTag("ns:innertag");
            assertTrue(tokenizer.atETag("ns:innertag"));
            tokenizer.takeETag("ns:innertag");
            assertTrue(tokenizer.atETag("ns:test"));
            tokenizer.takeETag();
            assertTrue(tokenizer.atEndOfDocument());
        }
        catch (Exception e)
        {
            System.out.println("XMLTokenizerTest: " + e);
            assertTrue(false); // Exceptions should not happen
        }
    }

    /**
     * test basic usage of namespaces: explicit declaration of ns, tags with ns labels
     */
    @Test
    public void namespace1b()
    {
        Resources testRes = new Resources("XmlTokenizerTests");
        Reader namespacetest1 = testRes.getReader("namespacetest1.xml");
        XMLTokenizer tokenizer = new XMLTokenizer(namespacetest1);
        tokenizer.setRecognizeNamespaces(true);
        String expectedNamespace = "http://hmi.ns.test";
        try
        {
            assertTrue(tokenizer.atSTag("test"));
            // critical: even this tag, where the xmlns is added already takes the new ns namespace into account
            assertTrue(tokenizer.getNamespace() == expectedNamespace);
            tokenizer.takeSTag("test");
            assertTrue(tokenizer.atSTag("innertag"));
            assertTrue(tokenizer.getNamespace() == expectedNamespace);
            tokenizer.takeSTag("innertag");
            assertTrue(tokenizer.atETag("innertag"));
            assertTrue(tokenizer.getNamespace() == expectedNamespace);
            tokenizer.takeETag("innertag");
            assertTrue(tokenizer.atETag("test"));
            assertTrue(tokenizer.getNamespace() == expectedNamespace);
            tokenizer.takeETag();
            assertTrue(tokenizer.atEndOfDocument());
        }
        catch (Exception e)
        {
            System.out.println("XMLTokenizerTest: " + e);
            assertTrue(false); // Exceptions should not happen
        }
    }

    /**
     * test basic usage of default namespaces.
     */
    @Test
    public void namespace2()
    {
        Resources testRes = new Resources("XmlTokenizerTests");
        Reader namespacetest2 = testRes.getReader("namespacetest2.xml");
        XMLTokenizer tokenizer = new XMLTokenizer(namespacetest2);
        tokenizer.setRecognizeNamespaces(true);
        String expectedNamespace = "http://hmi.ns.test"; // this is declared to be the default namespace
        try
        {
            assertTrue(tokenizer.atSTag("test"));
            // critical: even this tag, where the xmlns is added already takes the new ns namespace into account
            assertTrue(tokenizer.getNamespace() == expectedNamespace);
            tokenizer.takeSTag("test");
            assertTrue(tokenizer.atSTag("innertag"));
            assertTrue(tokenizer.getNamespace() == expectedNamespace);
            tokenizer.takeSTag("innertag");
            assertTrue(tokenizer.atETag("innertag"));
            assertTrue(tokenizer.getNamespace() == expectedNamespace);
            tokenizer.takeETag("innertag");
            assertTrue(tokenizer.atETag("test"));
            assertTrue(tokenizer.getNamespace() == expectedNamespace);
            tokenizer.takeETag();
            assertTrue(tokenizer.atEndOfDocument());
        }
        catch (Exception e)
        {
            System.out.println("XMLTokenizerTest: " + e);
            assertTrue(false); // Exceptions should not happen
        }
    }

    /**
     * combined named namespace and default namespace
     */
    @Test
    public void namespace3()
    {
        Resources testRes = new Resources("XmlTokenizerTests");
        Reader namespacetest3 = testRes.getReader("namespacetest3.xml");
        XMLTokenizer tokenizer = new XMLTokenizer(namespacetest3);
        tokenizer.setRecognizeNamespaces(true);
        String expectedDefaultNamespace = "http://hmi.ns.test"; // this is declared to be the default namespace
        String expectedNSNamespace = "ns-namespace"; // this is declared to be the default namespace
        String expectedEXNamespace = "ex-namespace"; // this is declared to be the default namespace
        try
        {
            assertTrue(tokenizer.atSTag("test"));
            // critical: even this tag, where the xmlns is added already takes the new ns namespace into account
            assertTrue(tokenizer.getNamespace() == expectedDefaultNamespace);
            // System.out.println("log:" + tokenizer.getLog());
            tokenizer.takeSTag("test");
            assertTrue(tokenizer.atSTag("innertag"));
            assertTrue(tokenizer.getNamespace() == expectedNSNamespace);
            // System.out.println("log:" + tokenizer.getLog());
            tokenizer.takeSTag("innertag");
            assertTrue(tokenizer.atETag("innertag"));
            assertTrue(tokenizer.getNamespace() == expectedNSNamespace);
            tokenizer.takeETag("innertag");

            assertTrue(tokenizer.atSTag("innertag"));
            assertTrue(tokenizer.getNamespace() == expectedDefaultNamespace);
            // System.out.println("log:" + tokenizer.getLog());
            tokenizer.takeSTag("innertag");
            assertTrue(tokenizer.atETag("innertag"));
            assertTrue(tokenizer.getNamespace() == expectedDefaultNamespace);
            tokenizer.takeETag("innertag");

            assertTrue(tokenizer.atSTag("sometag"));
            // System.out.println("log:" + tokenizer.getLog());
            // System.out.println("Sometag-namespace:" + tokenizer.getNamespace());
            assertTrue(tokenizer.getNamespace() == expectedDefaultNamespace);
            tokenizer.takeSTag("sometag");
            assertTrue(tokenizer.atETag("sometag"));
            assertTrue(tokenizer.getNamespace() == expectedDefaultNamespace);
            tokenizer.takeETag();

            assertTrue(tokenizer.atSTag("extratag"));
            assertTrue(tokenizer.getNamespace() == expectedEXNamespace);
            tokenizer.takeSTag("extratag");
            assertTrue(tokenizer.atETag("extratag"));
            assertTrue(tokenizer.getNamespace() == expectedEXNamespace);
            tokenizer.takeETag();

            assertTrue(tokenizer.atETag("test"));
            // System.out.println("ETAG-namespace:" + tokenizer.getNamespace());
            assertTrue(tokenizer.getNamespace() == expectedDefaultNamespace);
            tokenizer.takeETag();
            assertTrue(tokenizer.atEndOfDocument());
        }
        catch (Exception e)
        {
            System.out.println("XMLTokenizerTest: " + e);
            assertTrue(false); // Exceptions should not happen
        }
    }

    /**
     * combined named namespace and default namespace
     */
    @Test
    public void namespace5()
    {
        Resources testRes = new Resources("XmlTokenizerTests");
        Reader namespacetest5 = testRes.getReader("namespacetest5.xml");
        XMLTokenizer tokenizer = new XMLTokenizer(namespacetest5);
        tokenizer.setRecognizeNamespaces(true);
        String expectedDefaultNamespace = "default-namespace"; // this is declared to be the default namespace
        // String expectedNSNamespace = "ns-namespace"; // this is declared to be the default namespace
        // String expectedEXNamespace = "ex-namespace"; // this is declared to be the default namespace
        try
        {
            assertTrue(tokenizer.atSTag("test"));
            // critical: even this tag, where the xmlns is added already takes the new ns namespace into account
            assertTrue(tokenizer.getNamespace() == null);
            // System.out.println("log:" + tokenizer.getLog());
            tokenizer.takeSTag("test");
            assertTrue(tokenizer.atSTag("innertag"));
            // System.out.println("innertag ns: " + tokenizer.getNamespace());
            assertTrue(tokenizer.getNamespace() == expectedDefaultNamespace);
            // System.out.println("log:" + tokenizer.getLog());
            tokenizer.takeSTag("innertag");
            assertTrue(tokenizer.atSTag("sometag"));
            assertTrue(tokenizer.getNamespace() == expectedDefaultNamespace);
            tokenizer.takeSTag("sometag");
            tokenizer.takeETag("sometag");

            assertTrue(tokenizer.atETag("innertag"));
            // System.out.println("/innertag ns: " + tokenizer.getNamespace());
            assertTrue(tokenizer.getNamespace() == expectedDefaultNamespace);
            tokenizer.takeETag("innertag");

            assertTrue(tokenizer.atSTag("nonamespace"));
            // System.out.println("nonamespace ns:" + tokenizer.getNamespace() );
            assertTrue(tokenizer.getNamespace() == null);
            tokenizer.takeSTag("nonamespace");

            assertTrue(tokenizer.atSTag("sometag"));
            assertTrue(tokenizer.getNamespace() == null);
            tokenizer.takeSTag("sometag");
            tokenizer.takeETag("sometag");

            assertTrue(tokenizer.atETag("nonamespace"));
            assertTrue(tokenizer.getNamespace() == null);
            tokenizer.takeETag("nonamespace");

            assertTrue(tokenizer.atETag("test"));
            // System.out.println("ETAG-namespace:" + tokenizer.getNamespace());
            assertTrue(tokenizer.getNamespace() == null);
            tokenizer.takeETag();
            assertTrue(tokenizer.atEndOfDocument());
        }
        catch (Exception e)
        {
            System.out.println("XMLTokenizerTest: " + e);
            assertTrue(false); // Exceptions should not happen
        }
    }

    /**
     * test attribute namepaces
     */
    @Test
    public void attributeNamespace()
    {
        Resources testRes = new Resources("XmlTokenizerTests");
        Reader attributeNamespacetest = testRes.getReader("attributeNamespacetest.xml");
        XMLTokenizer tokenizer = new XMLTokenizer(attributeNamespacetest);
        tokenizer.setRecognizeNamespaces(true);
        String expectedDefaultNamespace = "http://hmi.ns.test"; // this is declared to be the default namespace
        String expectedNSNamespace = "ns-namespace"; // this is declared to be the default namespace
        String expectedEXNamespace = "ex-namespace"; // this is declared to be the default namespace
        String expectedAttr0 = "value0";
        String expectedAttr1 = "value1";
        String expectedAttr2NS = "value2";

        try
        {
            assertTrue(tokenizer.atSTag("test"));
            // critical: even this tag, where the xmlns is added already takes the new ns namespace into account
            assertTrue(tokenizer.getNamespace() == expectedDefaultNamespace);
            // System.out.println("log:" + tokenizer.getLog());
            tokenizer.takeSTag("test");
            assertTrue(tokenizer.atSTag("innertag"));
            assertTrue(tokenizer.getNamespace() == expectedNSNamespace);
            String val0 = tokenizer.getAttribute(expectedNSNamespace + ":atr0");
            // //System.out.println("val0=" + val0);
            assertTrue(val0 != null);
            assertTrue(val0.equals(expectedAttr0));
            String val1 = tokenizer.getAttribute("atr1");
            assertTrue(val1 != null);
            assertTrue(val1.equals(expectedAttr1));
            String val2 = tokenizer.getAttribute(expectedNSNamespace + ":atr2");
            assertTrue(val2 != null);
            assertTrue(val2.equals(expectedAttr2NS));
            // String ns2 = tokenizer.getAttributeNamespace("atr2");
            // assertTrue(ns2 != null);
            // assertTrue(ns2.equals(expectedNS));

            // System.out.println("log:" + tokenizer.getLog());
            tokenizer.takeSTag("innertag");
            assertTrue(tokenizer.atETag("innertag"));
            assertTrue(tokenizer.getNamespace() == expectedNSNamespace);
            tokenizer.takeETag("innertag");

            assertTrue(tokenizer.atSTag("innertag"));
            assertTrue(tokenizer.getNamespace() == expectedDefaultNamespace);
            // System.out.println("log:" + tokenizer.getLog());
            tokenizer.takeSTag("innertag");
            assertTrue(tokenizer.atETag("innertag"));
            assertTrue(tokenizer.getNamespace() == expectedDefaultNamespace);
            tokenizer.takeETag("innertag");

            assertTrue(tokenizer.atSTag("sometag"));
            // System.out.println("log:" + tokenizer.getLog());
            // System.out.println("Sometag-namespace:" + tokenizer.getNamespace());
            assertTrue(tokenizer.getNamespace() == expectedDefaultNamespace);
            tokenizer.takeSTag("sometag");
            assertTrue(tokenizer.atETag("sometag"));
            assertTrue(tokenizer.getNamespace() == expectedDefaultNamespace);
            tokenizer.takeETag();

            assertTrue(tokenizer.atSTag("extratag"));
            assertTrue(tokenizer.getNamespace() == expectedEXNamespace);
            tokenizer.takeSTag("extratag");
            assertTrue(tokenizer.atETag("extratag"));
            assertTrue(tokenizer.getNamespace() == expectedEXNamespace);
            tokenizer.takeETag();

            assertTrue(tokenizer.atETag("test"));
            // System.out.println("ETAG-namespace:" + tokenizer.getNamespace());
            assertTrue(tokenizer.getNamespace() == expectedDefaultNamespace);
            tokenizer.takeETag();
            assertTrue(tokenizer.atEndOfDocument());
        }
        catch (Exception e)
        {
            System.out.println("XMLTokenizerTest: " + e);
            assertTrue(false); // Exceptions should not happen
        }
    }

    /**
     * skipTag test
     * @throws IOException
     */
    @Test
    public void skipTagTest() throws IOException
    {
        Resources testRes = new Resources("XmlTokenizerTests");
        Reader xmlsection = testRes.getReader("getXMLSectionTest.xml");
        XMLTokenizer tokenizer = new XMLTokenizer(xmlsection);
        tokenizer.setRecognizeNamespaces(true);
        String expectedDefaultNamespace = "http://hmi.ns.test"; // this is declared to be the default namespace

        assertTrue(tokenizer.atSTag("test"));
        // critical: even this tag, where the xmlns is added already takes the new ns namespace into account
        assertTrue(tokenizer.getNamespace() == expectedDefaultNamespace);
        // System.out.println("log:" + tokenizer.getLog());
        tokenizer.takeSTag("test");
        assertTrue(tokenizer.atSTag("preamble"));
        tokenizer.takeSTag("preamble");
        assertTrue(tokenizer.atCharData());
        tokenizer.takeCharData();
        tokenizer.takeSTag("nested");
        tokenizer.takeETag("nested");
        tokenizer.takeETag("preamble");
        // Now the main section that we want to skip starts:
        assertTrue(tokenizer.atSTag("skippedtag"));
        tokenizer.skipTag(); // skip it ...
        // Some extras afterwards:
        assertTrue(tokenizer.atSTag("moretags"));
        tokenizer.takeSTag();
        tokenizer.takeCharData();
        tokenizer.takeETag("moretags");
        assertTrue(tokenizer.atETag("test"));
        assertTrue(tokenizer.getNamespace() == expectedDefaultNamespace);
        tokenizer.takeETag();
        assertTrue(tokenizer.atEndOfDocument());
    }

    /**
     * getXMLSection test
     * 
     * @throws IOException
     */
    @Test
    public void getXMLSectionTest() throws IOException
    {
        Resources testRes = new Resources("XmlTokenizerTests");
        Reader xmlsection = testRes.getReader("getXMLSectionTest.xml");
        XMLTokenizer tokenizer = new XMLTokenizer(xmlsection);
        tokenizer.setRecognizeNamespaces(true);
        String expectedDefaultNamespace = "http://hmi.ns.test"; // this is declared to be the default namespace

        String expectedSection = "<ns:skippedtag attr=\"val\" xmlns:ns=\"http://ns\">" + OS.getNewline() + "      <innertag> chardata "
                + OS.getNewline() + "         <nestedtag> chardata" + OS.getNewline() + "             <ns:skippedtag>" + OS.getNewline()
                + "                 more data" + OS.getNewline() + "             </ns:skippedtag>" + OS.getNewline()
                + "         </nestedtag>" + OS.getNewline() + "      </innertag>" + OS.getNewline()
                + "      <sometag xmlns:some=\"some-namespace\"/>" + OS.getNewline() + "      <ns:extratag > moredata </ns:extratag>"
                + OS.getNewline() + "   </ns:skippedtag>";

        assertTrue(tokenizer.atSTag("test"));
        // critical: even this tag, where the xmlns is added already takes the new ns namespace into account
        assertTrue(tokenizer.getNamespace() == expectedDefaultNamespace);
        // System.out.println("log:" + tokenizer.getLog());
        tokenizer.takeSTag("test");
        assertTrue(tokenizer.atSTag("preamble"));
        tokenizer.takeSTag("preamble");
        assertTrue(tokenizer.atCharData());
        tokenizer.takeCharData();
        tokenizer.takeSTag("nested");
        tokenizer.takeETag("nested");
        tokenizer.takeETag("preamble");
        // Now the main section that we want starts:
        assertTrue(tokenizer.atSTag("skippedtag"));
        String skipped = tokenizer.getXMLSection(); // get the section ...
        // System.out.println("skipped:\n" + skipped);
        // System.out.println("expected:\n" + expectedSection);
        // System.out.println("Diff: " + StringUtil.showDiff(skipped, expectedSection));
        // System.out.println("DiffPos: " + StringUtil.diffPos(skipped, expectedSection));
        assertEquals(skipped, expectedSection); // test the section we extracted
        // Some extras afterwards:
        assertTrue(tokenizer.atSTag("moretags"));
        tokenizer.takeSTag();
        tokenizer.takeCharData();
        tokenizer.takeETag("moretags");

        assertTrue(tokenizer.atETag("test"));

        assertTrue(tokenizer.getNamespace() == expectedDefaultNamespace);
        tokenizer.takeETag();

        assertTrue(tokenizer.atEndOfDocument());

    }

    @Test
    public void getEmptyXMLSectionTest() throws IOException
    {
        XMLTokenizer tokenizer = new XMLTokenizer("<test/>");
        assertTrue(tokenizer.atSTag("test"));
        String skipped = tokenizer.getXMLSection();
        assertEquals(skipped, "<test/>");
    }
    
    @Test
    public void getEmptyXMLSectionTestWithAttribute() throws IOException
    {
        XMLTokenizer tokenizer = new XMLTokenizer("<test id=\"ident\"/>");
        assertTrue(tokenizer.atSTag("test"));
        String skipped = tokenizer.getXMLSection();
        assertEquals(skipped, "<test id=\"ident\"/>");
    }
    
    @Test
    public void getEmptyXMLSectionTestWithDangerousAttribute() throws IOException
    {
        XMLTokenizer tokenizer = new XMLTokenizer("<test id=\"&gt;ident\"/>"); // escaped > char (unescaped > chars are not allowed)
        assertTrue(tokenizer.atSTag("test"));
        String skipped = tokenizer.getXMLSection();
        assertEquals(skipped, "<test id=\"&gt;ident\"/>");
    }
    
    @Test
    public void getEmptyXMLSectionTestWithClosingTag() throws IOException
    {
        XMLTokenizer tokenizer = new XMLTokenizer("<test></test>");
        assertEquals("<test></test>", tokenizer.getXMLSection());
    }
    
    @Test
    public void getXMLSectionContentTest2() throws IOException
    {
        XMLTokenizer tok = new XMLTokenizer("<tag>blah</tag>");
        tok.takeSTag("tag");
        assertEquals("blah", tok.getXMLSectionContent());
    }

    /**
     * getXMLSectionContent test, where we just want the contents, without the surrounding <tag> </tag> of the XML section
     * 
     * @throws IOException
     */
    @Test
    public void getXMLSectionContentTest() throws IOException
    {
        Resources testRes = new Resources("XmlTokenizerTests");
        Reader xmlsection = testRes.getReader("getXMLSectionTest.xml");
        XMLTokenizer tokenizer = new XMLTokenizer(xmlsection);
        tokenizer.setRecognizeNamespaces(true);
        String expectedDefaultNamespace = "http://hmi.ns.test"; // this is declared to be the default namespace

        String expectedSection = OS.getNewline() + "      <innertag> chardata " + OS.getNewline() + "         <nestedtag> chardata"
                + OS.getNewline() + "             <ns:skippedtag>" + OS.getNewline() + "                 more data" + OS.getNewline()
                + "             </ns:skippedtag>" + OS.getNewline() + "         </nestedtag>" + OS.getNewline() + "      </innertag>"
                + OS.getNewline() + "      <sometag xmlns:some=\"some-namespace\"/>" + OS.getNewline()
                + "      <ns:extratag > moredata </ns:extratag>" + OS.getNewline() + "   ";

        assertTrue(tokenizer.atSTag("test"));
        // critical: even this tag, where the xmlns is added already takes the new ns namespace into account
        assertTrue(tokenizer.getNamespace() == expectedDefaultNamespace);
        // System.out.println("log:" + tokenizer.getLog());
        tokenizer.takeSTag("test");
        assertTrue(tokenizer.atSTag("preamble"));
        tokenizer.takeSTag("preamble");
        assertTrue(tokenizer.atCharData());
        tokenizer.takeCharData();
        tokenizer.takeSTag("nested");
        tokenizer.takeETag("nested");
        tokenizer.takeETag("preamble");
        // Now the main section that we want starts:
        assertTrue(tokenizer.atSTag("skippedtag"));
        String skipped = tokenizer.getXMLSectionContent(); // get the section ...
        // System.out.println("skipped:\n[" + skipped + "]");
        // System.out.println("expected:\n[" + expectedSection + "]");
        // System.out.println("Diff: " + StringUtil.showDiff(skipped, expectedSection));
        // System.out.println("DiffPos: " + StringUtil.diffPos(skipped, expectedSection));
        assertEquals(skipped, expectedSection); // test the section we extracted
        tokenizer.takeETag("skippedtag");
        // Some extras afterwards:
        assertTrue(tokenizer.atSTag("moretags"));
        tokenizer.takeSTag();
        tokenizer.takeCharData();
        tokenizer.takeETag("moretags");

        assertTrue(tokenizer.atETag("test"));

        assertTrue(tokenizer.getNamespace() == expectedDefaultNamespace);
        tokenizer.takeETag();

        assertTrue(tokenizer.atEndOfDocument());
    }

    @Test
    public void getXMLSectionEmptyContentTest() throws IOException
    {
        XMLTokenizer tokenizer = new XMLTokenizer("<test/>");
        tokenizer.takeSTag("test");
        assertEquals("", tokenizer.getXMLSectionContent());
    }
}

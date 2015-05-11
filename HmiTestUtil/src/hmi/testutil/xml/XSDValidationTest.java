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

import hmi.testutil.LabelledParameterized;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import junit.framework.AssertionFailedError;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;

/**
 * Recursively validates all .xml files in a directory against an .xsd
 * See ProcAnimationXSDValidation for a usage example
 * @author welberge
 */
@RunWith(LabelledParameterized.class)
public abstract class XSDValidationTest
{
    protected Reader xsdReader;
    private File currentFile;
    private static final Logger LOGGER = LoggerFactory.getLogger(XSDValidationTest.class.getName());
    
    private static void getFilesRecursive(File dir, List<File> fileList)
    {
        for (File f : dir.listFiles())
        {
            if (f.isFile() && f.getName().endsWith(".xml"))
            {
                fileList.add(f);
            }
            else if (f.isDirectory())
            {
                getFilesRecursive(f, fileList);
            }
        }
    }

    public static Collection<Object[]> configs(String xmlDirectories[])
    {
        List<File> fileList = new ArrayList<File>();
        for (String xmlDirectory : xmlDirectories)
        {
            File dir = new File(xmlDirectory);
            getFilesRecursive(dir, fileList);
        }

        Collection<Object[]> objs = new ArrayList<Object[]>();
        for (File f : fileList)
        {
            Object obj[] = new Object[2];
            obj[0] = f.getAbsolutePath();
            obj[1] = f;
            objs.add(obj);
        }
        return objs;
    }

    public static Collection<Object[]> configs(String xmlDirectory)
    {
        File dir = new File(xmlDirectory);
        List<File> fileList = new ArrayList<File>();
        getFilesRecursive(dir, fileList);

        Collection<Object[]> objs = new ArrayList<Object[]>();
        for (File f : fileList)
        {
            Object obj[] = new Object[2];
            obj[0] = f.getAbsolutePath();
            obj[1] = f;
            objs.add(obj);
        }
        return objs;
    }

    public XSDValidationTest(String label, File f)
    {
        currentFile = f;
    }
    
    protected InputStream getXSDStream(String fileName)
    {
        return null;
    }
    
    private final class XSDPathResourceResolver implements LSResourceResolver
    {
        private DOMImplementationLS domImplementationLS;

        private XSDPathResourceResolver() throws ClassNotFoundException, IllegalAccessException, InstantiationException
        {
            //System.setProperty(DOMImplementationRegistry.PROPERTY, "org.apache.xerces.dom.DOMImplementationSourceImpl");
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            domImplementationLS = (DOMImplementationLS) registry.getDOMImplementation("LS");
        }

        public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI)
        {
            LOGGER.debug("==== Resolving '" + type + "' '" + namespaceURI + "' '" + publicId + "' '" + systemId + "' '" +
                    baseURI + "'");
            LSInput lsInput = domImplementationLS.createLSInput();
            InputStream is = getXSDStream(systemId);
            if(is==null)return null;                
            lsInput.setByteStream(is);
            lsInput.setSystemId(systemId);
            lsInput.setBaseURI(baseURI);
            lsInput.setPublicId(publicId);
            LOGGER.debug("==== Resolved ====");
            return lsInput;            
        }
    }
    
    @Test
    public void validateXML()
    {
        try
        {
            LOGGER.debug("validateXML for "+currentFile);
            // Parse an XML document into a DOM tree.
            DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
            df.setNamespaceAware(true);
            DocumentBuilder parser = df.newDocumentBuilder();
            
            Document document = parser.parse(currentFile);

            // Create a SchemaFactory capable of understanding WXS schemas.
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            factory.setResourceResolver(new XSDPathResourceResolver());
            
            // Load a WXS schema, represented by a Schema instance.
            Source schemaFile = new StreamSource(xsdReader);
            Schema schema = factory.newSchema(schemaFile);
            
            // Create a Validator object, which can be used to validate
            // an instance document.
            Validator validator = schema.newValidator();
            
            // Validate the DOM tree.
            validator.validate(new DOMSource(document));
        }
        catch (Exception ex)
        {
            AssertionFailedError afe = new AssertionFailedError("ParserConfigurationException in file " + currentFile.getAbsolutePath());
            afe.initCause(ex);
            throw afe;
        }
    }
}

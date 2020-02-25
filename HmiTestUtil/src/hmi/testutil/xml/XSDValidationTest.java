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

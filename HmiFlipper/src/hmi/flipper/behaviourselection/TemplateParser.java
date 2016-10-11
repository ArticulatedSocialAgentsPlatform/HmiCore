/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.behaviourselection;

import hmi.flipper.behaviourselection.template.Template;
import hmi.flipper.exceptions.TemplateParseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * The TemplateParser is given a String filename, parses the XML-file, and returns a list of Templates found in that File.
 * 
 * @author Mark ter Maat
 * @version 0.1
 */

public class TemplateParser
{
	private static Logger logger = LoggerFactory.getLogger(TemplateParser.class.getName());

    /* The current filename */
    protected String filename;
    protected HashMap<String,Integer> errors = new HashMap<String,Integer>();

    /**
     * Creates a new TemplateParser
     */
    public TemplateParser()
    {

    }

    public HashMap<String,Integer> getErrors()
    {
        return errors;
    }

    public ArrayList<Template> parseFile( String filename ) throws ParserConfigurationException,SAXException,IOException
    {
        errors.clear();
        return parseFile("templateDennis.xsd", filename);
    }

    /**
     *  Given the Filename, find and parse the file, and return a list of found Templates
     * 
     * @param xsdFileName - the path of the XSD-file
     * @param filename - the file to parse
     * @return a list of Templates
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public ArrayList<Template> parseFile( String xsdFileName, String filename ) throws ParserConfigurationException,SAXException,IOException
    {
        /* Find the file */
        this.filename = filename;
        errors.clear();
        InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(filename);
        if( inStream == null ) {
            throw new IOException("File '"+filename+"' does not exists");
        }

        URL xsdFile = this.getClass().getClassLoader().getResource(xsdFileName);
        if( xsdFile == null ) {
            throw new IOException("File '"+xsdFileName+"' does not exists");
        }

        /* Create a new Document-builder, based on the specified XSD */
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(true);
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", ""+xsdFile);
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        docBuilder.setErrorHandler( new XSDErrorHandler(this) );
        Document doc = docBuilder.parse(inStream);

        return parseDocument( doc );
    }

    public ArrayList<Template> parseString( String file ) throws ParserConfigurationException,SAXException,IOException
    {
        errors.clear();
        return parseString("template.xsd", file);
    }

    public ArrayList<Template> parseString( String xsdFileName, String file ) throws ParserConfigurationException,SAXException,IOException
    {
        errors.clear();
        URL xsdFile = this.getClass().getClassLoader().getResource(xsdFileName);
        if( xsdFile == null ) {
            throw new IOException("File '"+xsdFileName+"' does not exists");
        }

        /* Create a new Document-builder, based on the specified XSD */
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(true);
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
        factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", ""+xsdFile);
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        docBuilder.setErrorHandler( new XSDErrorHandler(this) );
        InputStream inStream = null;
        try {
            inStream = new ByteArrayInputStream(file.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        try {
            Document doc = docBuilder.parse(inStream);
            return parseDocument( doc );
        }catch( SAXParseException e ) {
            logger.error("XSD Error in file '{}' (r. {}): {}", new Object[]{filename, e.getLineNumber(), e.getMessage()});
            errors.put(e.getMessage(),e.getLineNumber());
            return null;

        }
    }

    public ArrayList<Template> parseDocument( Document doc )
    {
        ArrayList<Template> templates = new ArrayList<Template>();

        /* Find all <template>-Elements, and parse those */
        NodeList templateNodeList = doc.getElementsByTagName("template");
        boolean succes = true;
        for( int i=0; i<templateNodeList.getLength(); i++ ) {
            Element templateElement = (Element)templateNodeList.item(i);
            try {
                Template template = Template.parseTemplateElement( templateElement );
                templates.add(template);
            }catch( TemplateParseException e ) {
                String templateId, functionId;
                if( e.templateName != null ) {
                    templateId = " in template " + e.templateName;
                } else {
                    templateId = "";
                }
                if( e.functionName != null ) {
                    functionId = " in " + e.functionName;
                } else {
                    functionId = "";
                }
                logger.error("Template Parse Error in '{}' {}: {}" , new Object[]{filename, templateId+functionId, e.getMessage()});
                errors.put("Error" + templateId + ": " + e.getMessage(), -1 );
                succes = false;
            }
        }
        if( succes ) return templates;
        else return null;
    }

    /**
     * Inner Class to handle XSD-Errors
     * @author mark
     * @version 0.1
     */
    public class XSDErrorHandler implements ErrorHandler
    {
        private TemplateParser parser;
        public XSDErrorHandler(TemplateParser p) {
            parser = p;
        }

        public void error(SAXParseException exception) {
            logger.error("XSD Error in file '{}' (r. {}): {}", new Object[]{filename, exception.getLineNumber(), exception.getMessage()});
            parser.errors.put(exception.getMessage(),exception.getLineNumber());
        }

        public void fatalError(SAXParseException exception) {
            logger.error("XSD Error in file '{}' (r. {}): {}", new Object[]{filename, exception.getLineNumber(), exception.getMessage()});
            parser.errors.put(exception.getMessage(),exception.getLineNumber());
        }

        public void warning(SAXParseException exception) {
            logger.error("XSD Error in file '{}' (r. {}): {}", new Object[]{filename, exception.getLineNumber(), exception.getMessage()});
            parser.errors.put(exception.getMessage(),exception.getLineNumber());
        }
    }
}

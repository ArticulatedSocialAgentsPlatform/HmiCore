/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package hmi.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

/**
 * XMLStructureAdapter is an implementation of XMLStructure that is intended
 * to be a base class that must be extended. There are two different approaches:
 * 
 * The FIRST APPROACH is to re-implement the methods
 * <ul>
 * <li>readXML(XMLTokenizer)</li>
 * <li>appendXML(StringBuilder buf, XMLFormatting fmt)</li>
 * <li>getXMLTag()</li>
 * </ul>
 * The remaining XMLStructure methods in XMLStructureAdapter are already implemented, in XMLStructureAdapter,
 * and are expressed in terms of these three methods. </br>
 * The SECOND APPROACH uses the fact that even readXML and appendXML already have
 * default implementations in XMLStructureAdapter, that build on top of a few even simpler methods.
 * So, rather than re-implementing readXML and appendXML, it is often easier to reimplement
 * those more basic methods. (getXMLTag must still be implemented);
 * With this second approach one should (re-)implement the following five methods:
 * <ul>
 * <li>public String getXMLTag()</li>
 * <li>public StringBuilder appendAttributeString(StringBuilder buf)</li>
 * <li>public boolean decodeAttribute(String attrName, String valCode)</li>
 * <li>public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) or public boolean hasContent()</li>
 * <li>public void decodeContent(XMLTokenizer tok)</li>
 * </ul>
 * The XML tag is some String that identifies (uniquely) the class that you are implementing,
 * and that extends XMLStructureAdapter. The deafult choice would be to take the full qualified classname,
 * that is, the class name prefixed with the package name. The alternative is to use a short string, like
 * the class name without the package, and ensure that your class is truly unique.
 * The XMLStructureAdapter assume that your XML tag can be obtained by calling the getXMLTag method, so
 * you should reimplement this method.
 * The standard way to do this is the following piece of code:<br>
 * private static final String XMLTAG = "your-tag";<br>
 * public String getXMLTag() { return XMLTAG; }<br>
 * public static String xmlTag() { return XMLTAG; }<br/>
 * 
 * Unfortunately, you have to copy the code for getXMLTag again for every class, because XMLTag is a static field,
 * and so, the <em>default</em> getXMLTag() always returns "XMLStructureAdapter", since it ignores your
 * XMLTAG, and uses the default XMLTAG instead.
 * Description of the remaining methods:
 * <ul>
 * <li>The appendAttributeString method must append a string to buf, and return the latter; The appended string must have the form: attr1="value1" attr2="value2" ....</li>
 * <li>The appendContent method is similar, but is called to produce the "contents" in between the start en end tag of this structure. If you don't have any contents, you can
 * skip this method, but you should re-implement the hasContent method instead: public boolean hasContent() { return false: } In this case, appendContent is not used, and the
 * XML code consists only of a so called empty tag, of the form <tag attributes />.</li>
 * <li>The decodeAttribute method is called while the XMLStructure is being reconstructed from XML code. A call of the form decodeAttribute(attr-i, value-i) will be made, for
 * <em>each</em> individual attribute found in the XML encoding. The implementation of decodeAttribute should be able to reconstruct the XMLStructure object from a series of
 * these calls.</li>
 * <li>Finally, the decodeContent method must be able to handle the XML produced by appendContent.</li>
 * </ul>
 * Note: Although not required at all by the XMLStructure interface, it is often desirable
 * to register an XMLStructureAdapter class with the global XML class. This guarantees that instances
 * can be recreated from XML code, even when the precise type is not known on forehand, provided that
 * the XML tag is enough to determine the corresponding Java Class.
 * This is done conveniently in a static code block, as follows:<br>
 * static { XML.addClass(XMLTAG, fullclassname.class); },<br>
 * where &quot;fullclassname&quot; is the full Class name, including the package prefix.
 * @author Job Zwiers
 */

public class XMLStructureAdapter implements XMLStructure
{

    /**
     * The default-default recovery mode.
     */
    public static final boolean DEFAULT_RECOVER_MODE = false;
    private static boolean recoverMode = DEFAULT_RECOVER_MODE;
    private static final String XMLTAG = null;
    private static final String NAMESPACE = null;
    private int tagLine = -1; // -1 signals an invalid position
    private boolean recover = recoverMode;
    private static Logger logger = LoggerFactory.getLogger("hmi.xml.XMLStructure");

    /**
     * returns the XML tag that is used to encode this type of XMLStructure.
     * The default returns null.
     */
    public String getXMLTag()
    {
        return XMLTAG;
    }

    /**
     * The XML tagname for XML encoding for this Class -- use this static method when you want to see if a given String equals
     * the xml tag for this class. Default null.
     */
    public static String xmlTag()
    {
        return XMLTAG;
    }

    /**
     * Returns the (full, unabbreviated) namespace string for this XMLStructure Class
     */
    public String getNamespace()
    {
        return NAMESPACE;
    }

    /**
     * returns whether the XML encoding should have an contents part, or should be an empty element tag.
     * This method should be overwritten if necessary in classes that inherit from XMLStructureAdapter.
     * The default implementation returns always true.
     */
    public boolean hasContent()
    {
        return true;
    }

    /**
     * returns line number of the start tag, if this Object has been decoded by
     * using an XMLTokenizer.
     * line counting starts at 1.
     */
    public final int getTagLine()
    {
        return tagLine;
    }

    /**
     * reconstructs this XMLStructure object by reading and parsing XML
     * encoded text from a File.
     * This method can throw an (unchecked) ScanException in case of incorrectly
     * formatted XML. The implementation relies on readXML(XMLTokenizer) for actual reading.
     */
    public XMLStructure readXML(File inFile) throws IOException
    {
        return readXML(new XMLTokenizer(new BufferedReader(new FileReader(inFile))));
    }

    /**
     * Like readXML(File), except that the input file is specified by name within
     * a specified directory.
     */
    public XMLStructure readXML(File dir, String fileName) throws IOException
    {
        return readXML(new XMLTokenizer(new BufferedReader(new FileReader(new File(dir, fileName)))));
    }

    /**
     * reconstructs this XMLStructure object by reading and parsing XML
     * encoded text from a Reader. A BufferedReader will be inserted when the specified Reader
     * is not a BufferedReader already.
     * This method can throw an (unchecked) ScanException in case of incorrectly
     * formatted XML.
     * The default implementation relies on readXML(XMLTokenizer()) for actual reading.
     */
    public XMLStructure readXML(Reader in) throws IOException
    {
        if (in instanceof BufferedReader)
        {
            return readXML(new XMLTokenizer((BufferedReader) in));
        }
        else
        {
            return readXML(new XMLTokenizer(new BufferedReader(in)));
        }
    }

    /**
     * reconstructs this XMLStructure object by parsing an XML encoded String s
     * This method can throw an (unchecked) ScanException in case of incorrectly
     * formatted XML.
     * Although this default implementation exploits the readXML(Tokenizer) method
     * to implement readXML(String), it might be more efficient to reimplement
     * the latter, as it is heavily used while decoding Messages.
     */
    public XMLStructure readXML(String s)
    {
        try
        {
            return readXML(new StringReader(s));
        }
        catch (IOException e)
        {
            // The assumption is that IOExceptions are not possible for StringReaders.
            System.err.println("Unexpected IOException while reading a String: " + e);
            return null;
        }
    }

    /**
     * decodes the value from an attribute value String
     * returns true if successful, returns false for attribute names
     * that are not recognized. Might throw a RuntimeException when
     * an attribute has been recognized, but is ill formatted.
     * MUST BE OVERWRITTEN BY IMPLEMENTATIONS.
     */
    public boolean decodeAttribute(String attrName, String attrValue)
    {
        return decodeAttribute(attrName, attrValue, (XMLTokenizer) null);
    }

    /**
     * decodes the value from an attribute value String
     * returns true if successful, returns false for attribute names
     * that are not recognized. Might throw a RuntimeException when
     * an attribute has been recognized, but is ill formatted.
     * Moreover, an XMLTokenizer reference is available which can be queried
     * for attributes, like getTokenLine() or getTokenCharPos(), which might be helpful
     * to produce error messages referring to lines/positions within the XML document
     * The default implementation simply calls decodeAttribute(attrName, attrValue)
     * SHOUL BE OVERWRITTEN BY IMPLEMENTATIONS.
     */
    public boolean decodeAttribute(String attrName, String attrValue, XMLTokenizer tokenizer)
    {
        String tokenLine = (tokenizer == null) ? "?" : "" + tokenizer.getTokenLine();
        if (consoleAttributeEnabled && attrName.equals("console"))
        {
            if (attrValue.equals(""))
            {
                logger.info("XML at line " + tokenLine);
            }
            else
            {
                logger.info("XML at line " + tokenLine + ": " + attrValue);
            }
            return true;
        }
        else
        {
            logger.warn(getXMLTag() + ": unknown XML attribute at line: " + tokenLine + ", attribute: " + attrName);
            return false;
        }
    }

    /**
     * decodes the XML contents, i.e. the XML between the STag and ETag
     * of the encoding.
     * MUST BE OVERWRITTEN BY IMPLEMENTATIONS.
     */
    public void decodeContent(XMLTokenizer tokenizer) throws IOException
    {
        // super.decodeContent(tokenizer);
        // decode own content
    }

    /**
     * Sets the default mode for recovery. That is, for all XMLStructureAdapter instances
     * where no explicit call to setRecoverMode is made, the recovery mode will be set to
     * the specified default recover mode. The "default" default is DEFAULT_RECOVER_MODE
     */
    public static void setDefaultRecoverMode(boolean defaultMode)
    {
        recoverMode = defaultMode;
    }

    /**
     * Sets the mode for recovery when the ETag is not found.
     * When set to "true", a recovery action tries to skip tokens until
     * the ETag is found, if possible. When this does not succeed, or when recovery mode
     * is set to "false", an XMLScanException is thrown.
     */
    public void setRecoverMode(boolean mode)
    {
        recover = mode;
    }

    /**
     * reconstructs this XMLStructureAdapter using an XMLTokenizer.
     * This method can throw an (unchecked) XMLScanException in case of incorrectly
     * formatted XML.
     */
    public XMLStructure readXML(XMLTokenizer tokenizer) throws IOException
    {
        String tag = getXMLTag();
        try
        {
            // ignore UTF-8 byte order marks
            if (tokenizer.atCharData())
            {
                String data = tokenizer.takeCharData();
                byte[] bytes = data.getBytes(Charsets.UTF_8);
                if (bytes.length != 3 || (bytes[0] & 0xFF) != 0xEF || (bytes[1] & 0xFF) != 0xBB || (bytes[2] & 0xFF) != 0xBF)
                {
                    StringBuffer hex = new StringBuffer();
                    for (int h : bytes)
                    {
                        hex.append(Integer.toHexString(h & 0xff));
                    }
                    throw tokenizer.getXMLScanException("Erroneous XML encoding, expected: " + getXMLTag()
                            + " or Byte Order Mark, encountered: " + tokenizer.currentTokenString() + data + " hex: " + hex);
                }
            }
            if (!tokenizer.atSTag(tag))
            {
                if (!tokenizer.atSTag())
                { // not at an STag at all
                    String extraText = "";
                    if (tokenizer.atCharData())
                    {
                        extraText = ": " + tokenizer.getCharData();
                        if (extraText.length() > 50)
                        {
                            extraText = extraText.substring(0, 50) + " ...";
                        }
                    }
                    throw tokenizer.getXMLScanException("Erroneous XML encoding, expected: " + getXMLTag() + ", encountered: "
                            + tokenizer.currentTokenString() + extraText);
                }
                else
                { // at STag, but wrong tagName

                    throw tokenizer.getXMLScanException("Erroneous XML encoding, expected: " + getXMLTag() + ", encountered: "
                            + tokenizer.getTagName());
                }

            }

            if (tokenizer.getNamespace() != (getNamespace()))
            {
                throw tokenizer.getXMLScanException("Erroneous XML encoding for \"" + tag + "\", expected namespaced: " + getNamespace()
                        + ", encountered: " + tokenizer.getNamespace());
            }
            // String ns = namespace();
            // if (ns != null && tokenizer.getNamespace() != ns) {
            // throw tokenizer.getXMLScanException
            // ("Erroneous XML encoding, expected namespaced: " + ns
            // + ", encountered: " + tokenizer.getNamespace()
            // );
            // }

            preProcess(tokenizer);
            HashMap<String, String> attrMap = tokenizer.getAttributes(); // will parse remainder of STag tail, if necessary
            decodeAttributes(attrMap, tokenizer);
            tokenizer.takeSTag();
            decodeContent(tokenizer);
            if (tokenizer.atETag(tag))
            {
                tokenizer.takeETag();
            }
            else
            {
                if (recover)
                {
                    logger.error(tokenizer.getErrorMessage("Expected: </" + tag + ">, skipping tokens..."));
                    boolean recovered = tokenizer.recoverAfterETag(tag);
                    if (recovered)
                    {
                        logger.error(tokenizer.getErrorMessage(" found </" + tag + ">"));
                    }
                    else
                    {
                        throw tokenizer.getXMLScanException("Unable to recover, could not find </" + tag + ">");
                    }
                }
                else
                {
                    // logger.severe(tokenizer.getErrorMessage("Expected: </" + tag + ">"));
                    throw new XMLScanException(tokenizer.getErrorMessage("Expected: </" + tag + ">"));
                }
            }
        }
        catch (IOException e)
        {
            throw tokenizer.getXMLScanException("XMLTokenizer IOException: " + e);
        }
        postProcess(tokenizer);
        return this;
    }

    /**
     * decodes all attributes. The default implementation calls decodeAttribute for every attribute in turn.
     * The decodeAttributes method can be reimplemented when attributes must be processed in some particular order.
     */
    public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer)
    {
        for (Map.Entry<String, String> attr : attrMap.entrySet())
        {
            decodeAttribute(attr.getKey(), attr.getValue(), tokenizer);
        }
    }

    /**
     * Returns true iff attrMap contains an attribute with the specified attribute name
     */
    public boolean hasAttribute(String attrName, HashMap<String, String> attrMap)
    {
        return attrMap.containsKey(attrName);
    }

    /**
     * checkes whether an attribute with specified name is present, and returns its value.
     * The attribute is removed from the Map.
     * If the attribute is missing, a XMLScanException is thrown, using the error message
     * method from the XMLTokenizer. If the tokenizer itself is null, a generic RuntimeException
     * is thrown, rather than an XMLScanException.
     */
    public String getRequiredAttribute(String attrName, HashMap<String, String> attrMap, XMLTokenizer tokenizer)
    {
        String attrValue = attrMap.get(attrName);
        if (attrValue != null)
        {
            attrMap.remove(attrName);
            return attrValue;
        }
        else
        {
            if (tokenizer != null)
            {
                throw tokenizer.getXMLScanException(getXMLTag() + " - required attribute missing: " + attrName);
            }
            else
            {
                throw new RuntimeException("XMLElement " + getXMLTag() + " - required attribute missing: " + attrName);
            }
        }
    }

    /**
     * checkes whether an attribute with specified name is present, and returns its value.
     * The attribute is removed from the Map.
     * The attribute is not required, so a null value can be returned
     */
    public String getOptionalAttribute(String attrName, HashMap<String, String> attrMap)
    {
        String attrValue = attrMap.get(attrName);
        if (attrValue != null) attrMap.remove(attrName);
        return attrValue;
    }

    /**
     * checkes whether an attribute with specified name is present, and returns its value.
     * In this case the attribute is also removed from the Map.
     * The attribute is not required, and if it is not present, the specfied defaultValue is returned.
     */
    public String getOptionalAttribute(String attrName, HashMap<String, String> attrMap, String defaultValue)
    {
        String attrValue = attrMap.get(attrName);
        if (attrValue != null)
        {
            attrMap.remove(attrName);
            return attrValue;
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Like getRequiredAttribute, but with the result converted to an int.
     */
    public int getRequiredIntAttribute(String attrName, HashMap<String, String> attrMap, XMLTokenizer tokenizer)
    {
        return Integer.parseInt(getRequiredAttribute(attrName, attrMap, tokenizer));
    }

    /**
     * Like getOptionalAttribute, but with the result converted to an int.
     */
    public int getOptionalIntAttribute(String attrName, HashMap<String, String> attrMap, int defaultValue)
    {
        String attrValue = attrMap.get(attrName);
        if (attrValue != null)
        {
            attrMap.remove(attrName);
            return Integer.parseInt(attrValue);
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Like getRequiredAttribute, but with the result converted to an long.
     */
    public long getRequiredLongAttribute(String attrName, HashMap<String, String> attrMap, XMLTokenizer tokenizer)
    {
        return Long.parseLong(getRequiredAttribute(attrName, attrMap, tokenizer));
    }

    /**
     * Like getOptionalAttribute, but with the result converted to an long.
     */
    public long getOptionalLongAttribute(String attrName, HashMap<String, String> attrMap, long defaultValue)
    {
        String attrValue = attrMap.get(attrName);
        if (attrValue != null)
        {
            attrMap.remove(attrName);
            return Long.parseLong(attrValue);
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Like getRequiredAttribute, but with the result converted to a float.
     */
    public float getRequiredFloatAttribute(String attrName, HashMap<String, String> attrMap, XMLTokenizer tokenizer)
    {
        return Float.parseFloat(getRequiredAttribute(attrName, attrMap, tokenizer));
    }

    /**
     * Like getOptionalAttribute, but with the result converted to a float.
     */
    public float getOptionalFloatAttribute(String attrName, HashMap<String, String> attrMap, float defaultValue)
    {
        String attrValue = attrMap.get(attrName);
        if (attrValue != null)
        {
            attrMap.remove(attrName);
            return Float.parseFloat(attrValue);
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Like getRequiredAttribute, but with the result converted to an double.
     */
    public double getRequiredDoubleAttribute(String attrName, HashMap<String, String> attrMap, XMLTokenizer tokenizer)
    {
        return Double.parseDouble(getRequiredAttribute(attrName, attrMap, tokenizer));
    }

    /**
     * Like getOptionalAttribute, but with the result converted to an double.
     */
    public double getOptionalDoubleAttribute(String attrName, HashMap<String, String> attrMap, double defaultValue)
    {
        String attrValue = attrMap.get(attrName);
        if (attrValue != null)
        {
            attrMap.remove(attrName);
            return Double.parseDouble(attrValue);
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Like getRequiredAttribute, but with the result converted to an boolean.
     */
    public boolean getRequiredBooleanAttribute(String attrName, HashMap<String, String> attrMap, XMLTokenizer tokenizer)
    {
        return Boolean.valueOf(getRequiredAttribute(attrName, attrMap, tokenizer));
    }

    /**
     * Like getOptionalAttribute, but with the result converted to a boolean.
     */
    public boolean getOptionalBooleanAttribute(String attrName, HashMap<String, String> attrMap, boolean defaultValue)
    {
        String attrValue = attrMap.get(attrName);
        if (attrValue != null)
        {
            attrMap.remove(attrName);
            return Boolean.valueOf(attrValue);
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * The preProcess method is called, afetr the XML tag has been recognized,
     * but before attributes or XML content has been parsed.
     * The default implementation just records the tagLine;
     */
    public void preProcess(XMLTokenizer tokenizer)
    {
        tagLine = tokenizer.getTokenLine();
    }

    /**
     * The postProcess method is called after the XML content has been parsed, and the ETag
     * has been checked. This is the point to do some ``post processing'', after all
     * relevant information has been collected.
     * The default implementation does nothing.
     */
    public void postProcess(XMLTokenizer tokenizer)
    {
    }

    /**
     * Opens the specified File, writes the XML encoding followed by a newline,
     * and finally flushes and closes the file.
     * So basically this is equivalent to the writeXML(PrintWriter) method, except
     * that it also opens and flushes/closes the a File for the PrintWriter.
     */
    public final void writeXML(File outFile)
    {
        try
        {
            PrintWriter out = new PrintWriter(outFile);
            StringBuilder buf = new StringBuilder();
            appendXML(buf);
            out.print(buf);
            out.println();
            out.close();
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException("writeXML: " + e);
        }
    }

    /**
     * The default implementation relies on appendXML()
     */
    public final void writeXML(PrintWriter out)
    {
        StringBuilder buf = new StringBuilder();
        appendXML(buf);
        out.print(buf);
    }

    /**
     * writes an XML encoded String to "out".
     * The int "tab" can be used as a hint for indentation, and
     * denotes the indentation to be applied to the XML code
     * as a whole.
     * This String should equal the result of toXMLString(tab).
     * The default implementation relies on appendXML(.. tab).
     */
    public final void writeXML(PrintWriter out, int tab)
    {
        writeXML(out, new XMLFormatting(tab));
    }

    /**
     * writes an XML encoded String to "out".
     * The int "tab" can be used as a hint for indentation, and
     * denotes the indentation to be applied to the XML code
     * as a whole.
     * This String should equal the result of toXMLString(tab).
     * The default implementation relies on appendXML(.. tab).
     */
    public final void writeXML(PrintWriter out, XMLFormatting fmt)
    {
        StringBuilder buf = new StringBuilder();
        appendXML(buf, fmt);
        out.print(buf);
    }

    /**
     * writes an XML encoded String to "out".
     * The int "tab" can be used as a hint for indentation, and
     * denotes the indentation to be applied to the XML code
     * as a whole.
     * This String should equal the result of toXMLString(tab).
     * The default implementation relies on appendXML(.. tab).
     */
    public final void writeXML(PrintWriter out, XMLFormatting fmt, XMLNameSpace xmlNamespace)
    {
        StringBuilder buf = new StringBuilder();
        appendXML(buf, fmt, xmlNamespace);
        out.print(buf);
    }

    /**
     * yields an XML encoded String of this XMLStructure object.
     * The default implementation relies on appendXML()
     */
    public final String toXMLString()
    {
        StringBuilder buf = new StringBuilder();
        appendXML(buf);
        return buf.toString();
    }

    /**
     * yields an XML encoded String of this XMLStructure object.
     * The readXML() methods should be able to reconstruct this object from
     * the String delivered by toXMLString().
     * The default implementation relies on appendXML( .., fmt)
     */
    public final String toXMLString(int tab)
    {
        return toXMLString(new XMLFormatting(tab));
    }

    /**
     * yields an XML encoded String of this XMLStructure object.
     * The readXML() methods should be able to reconstruct this object from
     * the String delivered by toXMLString().
     * The default implementation relies on appendXML( .., fmt)
     */
    public final String toXMLString(XMLFormatting fmt)
    {
        StringBuilder buf = new StringBuilder();
        appendXML(buf, fmt);
        return buf.toString();
    }

    /**
     * yields an XML encoded String of this XMLStructure object.
     * The readXML() methods should be able to reconstruct this object from
     * the String delivered by toXMLString().
     * The default implementation relies on appendXML( .., fmt)
     */
    public final String toXMLString(XMLFormatting fmt, XMLNameSpace xmlNamespace)
    {
        StringBuilder buf = new StringBuilder();
        appendXML(buf, fmt, xmlNamespace);
        return buf.toString();
    }

    /**
     * yields an XML encoded String of this XMLStructure object.
     * The readXML() methods should be able to reconstruct this object from
     * the String delivered by toXMLString().
     * The default implementation relies on appendXML( .., fmt)
     */
    public final String toXMLString(XMLFormatting fmt, String prefix, String namespace)
    {
        return toXMLString(fmt, new XMLNameSpace(prefix, namespace));
    }

    /**
     * yields an XML encoded String of this XMLStructure object.
     * The readXML() methods should be able to reconstruct this object from
     * the String delivered by toXMLString().
     * The default implementation relies on appendXML( .., fmt)
     */
    public final String toXMLString(String prefix, String namespace)
    {
        return toXMLString(new XMLFormatting(), new XMLNameSpace(prefix, namespace));
    }

    /**
     * the default toString() method returns the result of toXMLSTring()
     */
    @Override
    public String toString()
    {
        return toXMLString();
    }

    /**
     * like writeXML(PrintWriter ,int), except that the XML encoding is appended
     * to a StringBuilder.
     * The latter must be returned.
     * The default implementation appends nothing, and must be overwrritten
     * by extension classes.
     */
    public final StringBuilder appendXML(StringBuilder buf, int tab)
    {
        return appendXML(buf, new XMLFormatting(tab));
    }

    /**
     * like writeXML(PrintWriter ,int), except that the XML encoding is appended
     * to a StringBuilder.
     * The latter must be returned.
     * The default implementation appends nothing, and must be overwrritten
     * by extension classes.
     */
    public final StringBuilder appendXML(StringBuilder buf, XMLFormatting fmt, String prefix, String namespace)
    {
        XMLNameSpace xmlNamespace = new XMLNameSpace(prefix, namespace);
        return appendXML(buf, fmt, xmlNamespace);
    }

    /**
     * like writeXML(PrintWriter ,int), except that the XML encoding is appended
     * to a StringBuilder.
     * The latter must be returned.
     * The default implementation appends nothing, and must be overwrritten
     * by extension classes.
     */
    public final StringBuilder appendXML(StringBuilder buf, XMLFormatting fmt)
    {
        return appendXML(buf, fmt, (XMLNameSpace) null);
    }

    /**
     * like writeXML(PrintWriter ,int), except that the XML encoding is appended
     * to a StringBuilder.
     * The latter must be returned.
     * The default implementation appends nothing, and must be overwrritten
     * by extension classes.
     */
    public StringBuilder appendXML(StringBuilder buf, XMLFormatting fmt, XMLNameSpace xmlNamespace)
    {
        if (xmlNamespace == null)
        {
            return appendXML(buf, fmt, new ArrayList<XMLNameSpace>());
        }
        else
        {
            return appendXML(buf, fmt, java.util.Arrays.asList(new XMLNameSpace[] { xmlNamespace }));
        }
    }

    /**
     * like writeXML(PrintWriter ,int), except that the XML encoding is appended
     * to a StringBuilder.
     * The latter must be returned.
     * The default implementation appends nothing, and must be overwrritten
     * by extension classes.
     */
    public StringBuilder appendXML(StringBuilder buf, XMLFormatting fmt, List<XMLNameSpace> xmlNamespaceList)
    {
        if (xmlNamespaceList != null)
        {
            for (XMLNameSpace ns : xmlNamespaceList)
            {
                fmt.pushXMLNameSpace(ns);
            }
        }
        int tab = fmt.getTab();
        if (tab > 0) appendSpaces(buf, tab);
        buf.append('<');
        // String nsLabel = namespaceLabel();
        // if (nsLabel != null) {
        // buf.append(nsLabel);
        // buf.append(':');
        // }
        String thisNamespace = getNamespace();
        String prefix = null;
        if (thisNamespace != null)
        {
            prefix = fmt.getNamespacePrefix(thisNamespace);
            if (prefix != null)
            {
                buf.append(prefix);
                buf.append(':');
            }
            else
            // introduce default namespace declaration
            {
                fmt.pushXMLNameSpace(new XMLNameSpace(thisNamespace));

            }
        }

        String tag = getXMLTag();
        buf.append(tag);
        // buf.append(' ');
        appendAttributeString(buf, fmt);
        if (xmlNamespaceList != null)
        {
            if (thisNamespace != null && prefix == null)
            {
                buf.append(' ');
                buf.append("xmlns=\"");
                buf.append(thisNamespace);
                buf.append('"');
            }
            for (XMLNameSpace ns : xmlNamespaceList)
            {
                buf.append(' ');
                buf.append("xmlns:");
                buf.append(ns.getPrefix());
                buf.append("=\"");
                buf.append(ns.getNamespace());
                buf.append('"');
            }
        }
        if (hasContent())
        {
            buf.append('>');
            appendContent(buf, fmt.indent());
            fmt.unIndent();
            buf.append('\n');
            if (tab > 0) appendSpaces(buf, tab);
            buf.append("</");
            if (prefix != null)
            {
                buf.append(prefix);
                buf.append(':');
            }
            buf.append(tag);
            buf.append('>');
        }
        else
        {
            buf.append("/>");
        }
        if (xmlNamespaceList != null)
        {
            for (XMLNameSpace ns : xmlNamespaceList)
            {
                fmt.popXMLNameSpace();
            }
        }
        return buf;
    }

    /**
     * like writeXML(PrintWriter), except that the XML encoding is appended
     * to a StringBuilder.
     * the latter must be returned.
     * The default implementation relies on appendXML(..., 0)
     */
    public final StringBuilder appendXML(StringBuilder buf)
    {
        return appendXML(buf, 0);
    }

    /**
     * Appends a String to buf that encodes the contents for the XML encoding.
     * MUST BE OVERWRITTEN BY IMPLEMENTATIONS. (The default implementation appends nothing).
     * The encoding should start on a new line, using indentation equal to tab.
     * There should be no newline after the encoding.
     */
    public StringBuilder appendContent(StringBuilder buf)
    {
        // super.addContent(buf, tab);
        return buf;
    }

    /**
     * Appends a String to buf that encodes the contents for the XML encoding.
     * MUST BE OVERWRITTEN BY IMPLEMENTATIONS. (The default implementation appends nothing).
     * The encoding should start on a new line, using indentation equal to tab.
     * There should be no newline after the encoding.
     */
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        return buf;
    }

    /**
     * A special case of appendAttributeString that also takes a fmt parameter.
     * This method need not be re-implemted, unless it is desirable to layout the attributes
     * spanning several lines. The X3D standard is an example where XML attributes are abused
     * to store complete arrays of data; in such cases, inserting newlines between data elements
     * is highly desirable. Of course, the new lines then should start with a proper indentation,
     * as denoted by the fmt parameter.
     * The default implementation simply ignores this fmt, and calls appendAttributeString(buf),
     * i.e. without the fmt parameter, which is fine for all cases where the attributes
     * are on the same line as the XML tag.
     * 
     */
    public StringBuilder appendAttributeString(StringBuilder buf, XMLFormatting fmt)
    {
        return appendAttributeString(buf);
    }

    /**
     * Appends a String to buf that encodes the attributes for the XML encoding.
     * When non empty, the attribute string should start with a space character.
     * Hint: call the appendAttribute(StringBuilder buf, String attrName, String attrValue)
     * for every relevant attribute; this takes care of the leading space as well as spaces
     * in between the attributes)
     * MUST BE OVERWRITTEN BY IMPLEMENTATIONS. (The default implementation appends nothing).
     * The encoding should preferably not add newline characters.
     */
    public StringBuilder appendAttributeString(StringBuilder buf)
    {
        appendAttributes(buf);
        // super.appendAttributeString(buf);
        // appendAttribute(buf, attr-1, value-1);
        // .....
        // appendAttribute(buf, attr-n, value-n);
        return buf;
    }

    public StringBuilder appendAttributes(StringBuilder buf)
    {
        return buf;
    }

    /**
     * If xmlStruct != null, appends the XML encoding for xmlStruct, starting
     * on a new line
     * If xmlStruct == null, nothing is appended.
     */
    public StringBuilder appendXMLStructure(StringBuilder buf, XMLFormatting fmt, XMLStructure xmlStruct)
    {
        if (xmlStruct != null)
        {
            buf.append('\n');
            xmlStruct.appendXML(buf, fmt);
        }
        return buf;
    }

    /**
     * returns a String consisting of exactly "tab" spaces.
     */
    public final static String spaces(int tab)
    {
        while (lotsOfSpaces.length() < tab)
        {
            lotsOfSpaces.append("         ");
        }
        return lotsOfSpaces.substring(0, tab);
    }

    /**
     * appends a String consisting of exactly "tab" spaces.
     */
    public final static StringBuilder appendSpaces(StringBuilder buf, XMLFormatting fmt)
    {
        return appendSpaces(buf, fmt.getTab());
    }

    /**
     * appends a String consisting of exactly "tab" spaces.
     */
    public final static StringBuilder appendSpaces(StringBuilder buf, int tab)
    {
        if (tab <= 0) return buf;
        while (lotsOfSpaces.length() < tab)
        {
            lotsOfSpaces.append("         ");
        }
        return buf.append(lotsOfSpaces.substring(0, tab));
    }

    /**
     * appends a String consisting of the amount of spaces for a "Tab".
     * This amount is equal to XMLStructureAdapter.TAB;
     */
    public final static StringBuilder appendTab(StringBuilder buf)
    {
        return buf.append(TAB_STRING);
    }

    /**
     * appends an XML comment string to buf,
     * The string appended is of the form:
     * &lt;!-- comment --&gt;
     */
    public final static StringBuilder appendComment(StringBuilder buf, String comment)
    {
        buf.append("<!-- ");
        buf.append(comment);
        buf.append(" -->");
        return buf;
    }

    /**
     * This method appends an XML open-STag with specified tag name to buf.
     * The string appended is of the form &lt;tagName;
     */
    public final static StringBuilder appendOpenSTag(StringBuilder buf, String tagName)
    {
        buf.append('<');
        buf.append(tagName);
        return buf;
    }

    /**
     * This method appends an XML open- STag with specified tag name to buf.
     * The string appended is of the form &lt;tagName;
     * No attributes are included The tag is on a new line, preceded by spaces specified by fmt.
     */
    public final static StringBuilder appendOpenSTag(StringBuilder buf, String tagName, XMLFormatting fmt)
    {
        buf.append('\n');
        appendSpaces(buf, fmt);
        buf.append('<');
        buf.append(tagName);
        return buf;
    }

    /**
     * Appends &gt; (Closing an STag)
     */
    public final static StringBuilder appendCloseSTag(StringBuilder buf)
    {
        buf.append('>');
        return buf;
    }

    /**
     * Appends /&gt;
     */
    public final static StringBuilder appendCloseEmptyTag(StringBuilder buf)
    {
        buf.append("/>");
        return buf;
    }

    /**
     * This method appends an XML STag with specified tag name to buf.
     * The string appended is of the form &lt;tagName&gt;
     * No attributes are included
     */
    public final static StringBuilder appendSTag(StringBuilder buf, String tagName)
    {
        buf.append('<');
        buf.append(tagName);
        buf.append('>');
        return buf;
    }

    /**
     * This method appends an XML STag with specified tag name to buf.
     * The string appended is of the form &lt;tagName&gt;
     * No attributes are included The tag is on a new line, preceded by tab spaces.
     */
    public final static StringBuilder appendSTag(StringBuilder buf, String tagName, XMLFormatting fmt)
    {
        buf.append('\n');
        appendSpaces(buf, fmt);
        buf.append('<');
        buf.append(tagName);
        buf.append('>');
        return buf;
    }

    /**
     * This method appends an XML STag with specified tag name and attributes to buf.
     * It is assumed that keys and values are String typed,
     * and that values do not contain " characters.
     * The string appended is of the form &lt;tagName attr_0="value_0" ... attr_n="value_n">
     */
    public final static StringBuilder appendSTag(StringBuilder buf, String tagName, HashMap attributes)
    {
        buf.append('<');
        buf.append(tagName);
        if (attributes != null) appendAttributes(buf, attributes);
        buf.append('>');
        return buf;
    }

    /**
     * This method appends an "empty element" XML STag with specified tag name to buf.
     * The string appended is of the form &lt;tagName/&gt;
     * No attributes are included
     */
    public final static StringBuilder appendEmptyTag(StringBuilder buf, String tagName)
    {
        buf.append('<');
        buf.append(tagName);
        buf.append("/>");
        return buf;
    }

    /**
     * This method appends an "empty element" XML STag with specified tag name to buf.
     * The string appended is of the form &lt;tagName/&gt; and is indented according to the specified XMLFormatting.
     * No attributes are included
     */
    public final static StringBuilder appendEmptyTag(StringBuilder buf, XMLFormatting fmt, String tagName)
    {
        buf.append('\n');
        appendSpaces(buf, fmt);
        buf.append('<');
        buf.append(tagName);
        buf.append("/>");
        return buf;
    }

    /**
     * This method appends an "empty element" XML STag with specified tag name to buf.
     * The string appended is of the form &lt;tagName attr=value/&gt;
     * A single attribute-value pair is included
     */
    public final static StringBuilder appendEmptyTag(StringBuilder buf, XMLFormatting fmt, String tagName, String attrName, String attrValue)
    {
        buf.append('\n');
        appendSpaces(buf, fmt);
        buf.append('<');
        buf.append(tagName);
        if (attrValue != null)
        {
            buf.append(' ');
            buf.append(attrName);
            buf.append("=\"");
            buf.append(attrValue);
            buf.append('"');
        }
        buf.append("/>");
        return buf;
    }

    /**
     * This method appends an "empty element" XML STag with specified tag name to buf.
     * The string appended is of the form &lt;tagName attr=value/&gt;
     * A single attribute-value pair is included
     */
    public final static StringBuilder appendEmptyTag(StringBuilder buf, XMLFormatting fmt, String tagName, String attrName1,
            String attrValue1, String attrName2, String attrValue2)
    {
        buf.append('\n');
        appendSpaces(buf, fmt);
        buf.append('<');
        buf.append(tagName);
        if (attrValue1 != null)
        {
            buf.append(' ');
            buf.append(attrName1);
            buf.append("=\"");
            buf.append(attrValue1);
            buf.append('"');
        }
        if (attrValue2 != null)
        {
            buf.append(' ');
            buf.append(attrName2);
            buf.append("=\"");
            buf.append(attrValue2);
            buf.append('"');
        }
        buf.append("/>");
        return buf;
    }

    /**
     * This method appends an "empty element" XML STag with specified tag name to buf.
     * The string appended is of the form &lt;tagName attr=value/&gt;
     * A single attribute-value pair is included
     */
    public final static StringBuilder appendEmptyTag(StringBuilder buf, XMLFormatting fmt, String tagName, String attrName1,
            String attrValue1, String attrName2, String attrValue2, String attrName3, String attrValue3)
    {
        buf.append('\n');
        appendSpaces(buf, fmt);
        buf.append('<');
        buf.append(tagName);
        if (attrValue1 != null)
        {
            buf.append(' ');
            buf.append(attrName1);
            buf.append("=\"");
            buf.append(attrValue1);
            buf.append('"');
        }
        if (attrValue2 != null)
        {
            buf.append(' ');
            buf.append(attrName2);
            buf.append("=\"");
            buf.append(attrValue2);
            buf.append('"');
        }
        if (attrValue3 != null)
        {
            buf.append(' ');
            buf.append(attrName3);
            buf.append("=\"");
            buf.append(attrValue3);
            buf.append('"');
        }
        buf.append("/>");
        return buf;
    }

    /**
     * This method appends an "empty element" XML STag with specified tag name to buf.
     * The string appended is of the form &lt;tagName attr_0="value_0" ... attr_n="value_n"/&gt;
     */
    public final static StringBuilder appendEmptyTag(StringBuilder buf, String tagName, HashMap attributes)
    {
        buf.append('<');
        buf.append(tagName);
        if (attributes != null) appendAttributes(buf, attributes);
        buf.append("/>");
        return buf;
    }

    /**
     * This method appends an "empty element" XML STag with specified tag name to buf.
     * The string appended is of the form &lt;tagName attr_0="value_0" ... attr_n="value_n"/&gt;
     */
    public final static StringBuilder appendEmptyTag(StringBuilder buf, XMLFormatting fmt, String tagName, HashMap attributes)
    {
        buf.append('\n');
        appendSpaces(buf, fmt);
        buf.append('<');
        buf.append(tagName);
        if (attributes != null) appendAttributes(buf, attributes);
        buf.append("/>");
        return buf;
    }

    /**
     * appends a list of XMLStructures.
     * Each List element must be an XMLStructure, and is appended on a new line, with proper indentation.
     * There are no enclosing brackets or tags.
     * The list is allowed to be null, and in this case, nothing is added.
     */
    public final static <T extends XMLStructure> StringBuilder appendXMLStructureList(StringBuilder buf, XMLFormatting fmt, List<T> elements)
    {
        if (elements == null) return buf;
        for (XMLStructure elem : elements)
        {
            appendNewLine(buf);
            elem.appendXML(buf, fmt);

        }
        return buf;
    }

    /**
     * decodes a list of XMLStructures, and returns a Java List.
     * All XMLStructures are supposed to be of the same class, and with the same XML tag.
     */
    public final static <T extends XMLStructure> List<T> decodeXMLStructureList(XMLTokenizer tokenizer, String tagName,
            Class<T> structureClass) throws IOException
    {
        // if (! tokenizer.atSTag(tagName)) return null;
        List<T> result = new ArrayList<T>();
        try
        {
            while (tokenizer.atSTag(tagName))
            {
                T elem = structureClass.newInstance();
                elem.readXML(tokenizer);
                result.add(elem);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Decoding XMLStructure List: " + e);
        }
        return result;
    }

    /**
     * appends a list of uniform XMLTextElements.
     * Each List element must be a String, and is appended on a new line, as an XMLTextElement
     * with specified tag name.
     * The list is allowed to be null, and in this case, nothing is added.
     */
    public final static StringBuilder appendXMLTextElementList(StringBuilder buf, XMLFormatting fmt, String tagName, List<String> elements)
    {
        if (elements == null) return buf;
        for (String elem : elements)
            appendTextElement(buf, tagName, elem, fmt);
        return buf;
    }

    /**
     * This method appends a single XML style attribute to buf.
     * The string has the form : " attrName=\"attrValue\"".
     * It is assumed that attrName is a legal XML attribute name,
     * and that attrValue is a legal XML value, not containing " characters.
     * The attrValue is allowed to be null, and in this case nothing is appended at all.
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, String attrValue)
    {
        if (attrValue == null) return buf;
        buf.append(' ');
        buf.append(attrName);
        buf.append("=\"");
        buf.append(attrValue);
        buf.append('"');
        return buf;
    }

    /**
     * Constructs a unique namespace prefix for ns when none is there yet
     */
    public static void constructNSPrefix(String ns, XMLFormatting fmt, StringBuilder buf)
    {
        if (fmt.getNamespacePrefix(ns.intern()) == null)
        {
            String prefix = "ns" + UUID.randomUUID().toString();
            XMLNameSpace n = new XMLNameSpace(prefix, ns);
            fmt.pushXMLNameSpace(n);
            buf.append(" xmlns:" + prefix + "=\"" + ns + "\" ");
        }
    }

    public static StringBuilder appendNamespacedAttribute(StringBuilder buf, XMLFormatting fmt, String nameSpace, String attrName,
            String attrValue)
    {
        if (attrValue == null) return buf;
        buf.append(' ');

        constructNSPrefix(nameSpace, fmt, buf);
        String pref = fmt.getNamespacePrefix(nameSpace.intern());
        if (pref == null) throw new RuntimeException("appendnamespacedAttribute: no prefix found for " + nameSpace + " (Context:\n " + buf);

        buf.append(pref);
        buf.append(":");
        buf.append(attrName);
        buf.append("=\"");
        buf.append(attrValue);
        buf.append('"');
        return buf;
    }

    /**
     * This method appends a single XML style attribute to buf.
     * The string has the form : " attrName=\"attrValue1 attrValue2\"".
     * It is assumed that attrName is a legal XML attribute name,
     * and that attrValue is a legal XML value, not containing " characters.
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, String attrValue1, String attrValue2)
    {
        buf.append(' ');
        buf.append(attrName);
        buf.append("=\"");
        buf.append(attrValue1 == null ? "NULL" : attrValue1);
        buf.append(' ');
        buf.append(attrValue2 == null ? "NULL" : attrValue2);
        buf.append('"');
        return buf;
    }

    /**
     * This method appends a single XML style attribute to buf.
     * The string has the form : " attrName=\"attrValue1 attrValue2 attrValue3\"".
     * It is assumed that attrName is a legal XML attribute name,
     * and that attrValue is a legal XML value, not containing " characters.
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, String attrValue1, String attrValue2,
            String attrValue3)
    {
        buf.append(' ');
        buf.append(attrName);
        buf.append("=\"");
        buf.append(attrValue1 == null ? "NULL" : attrValue1);
        buf.append(' ');
        buf.append(attrValue2 == null ? "NULL" : attrValue2);
        buf.append(' ');
        buf.append(attrValue3 == null ? "NULL" : attrValue3);
        buf.append('"');
        return buf;
    }

    /**
     * This method appends a single XML style attribute to buf.
     * The string has the form : " attrName=\"attrValue1 attrValue2 attrValue3 attrValue4\"".
     * It is assumed that attrName is a legal XML attribute name,
     * and that attrValue is a legal XML value, not containing " characters.
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, String attrValue1, String attrValue2,
            String attrValue3, String attrValue4)
    {
        buf.append(' ');
        buf.append(attrName);
        buf.append("=\"");
        buf.append(attrValue1 == null ? "NULL" : attrValue1);
        buf.append(' ');
        buf.append(attrValue2 == null ? "NULL" : attrValue2);
        buf.append(' ');
        buf.append(attrValue3 == null ? "NULL" : attrValue3);
        buf.append(' ');
        buf.append(attrValue4 == null ? "NULL" : attrValue4);
        buf.append('"');
        return buf;
    }

    /**
     * appends an attribute of the form name="value".
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, int intValue)
    {
        return appendAttribute(buf, attrName, Integer.toString(intValue));
    }

    /**
     * appends an attribute of the form name="value1 value2"
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, int intValue1, int intValue2)
    {
        return appendAttribute(buf, attrName, Integer.toString(intValue1), Integer.toString(intValue2));
    }

    /**
     * appends an attribute of the form name="value1 value2 value3"
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, int intValue1, int intValue2, int intValue3)
    {
        return appendAttribute(buf, attrName, Integer.toString(intValue1), Integer.toString(intValue2), Integer.toString(intValue3));
    }

    /**
     * appends an attribute of the form name="value1 value2 value3 value4"
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, int intValue1, int intValue2, int intValue3,
            int intValue4)
    {
        return appendAttribute(buf, attrName, Integer.toString(intValue1), Integer.toString(intValue2), Integer.toString(intValue3),
                Integer.toString(intValue4));
    }

    /**
     * appends an attribute of the form name="value".
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, long longValue)
    {
        return appendAttribute(buf, attrName, Long.toString(longValue));
    }

    /**
     * appends an attribute of the form name="value1 value2".
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, long longValue1, long longValue2)
    {
        return appendAttribute(buf, attrName, Long.toString(longValue1), Long.toString(longValue2));
    }

    /**
     * appends an attribute of the form name="value1 value2 value3".
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, long longValue1, long longValue2, long longValue3)
    {
        return appendAttribute(buf, attrName, Long.toString(longValue1), Long.toString(longValue2), Long.toString(longValue3));
    }

    /**
     * appends an attribute of the form name="value1 value2 value3 value4".
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, long longValue1, long longValue2,
            long longValue3, long longValue4)
    {
        return appendAttribute(buf, attrName, Long.toString(longValue1), Long.toString(longValue2), Long.toString(longValue3),
                Long.toString(longValue4));
    }

    /**
     * appends an attribute of the form name="value"
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, float floatValue)
    {
        return appendAttribute(buf, attrName, Float.toString(floatValue));
    }

    /**
     * appends an attribute of the form name="value1 value2"
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, float floatValue1, float floatValue2)
    {
        return appendAttribute(buf, attrName, Float.toString(floatValue1), Float.toString(floatValue2));
    }

    /**
     * appends an attribute of the form name="value1 value2 value3"
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, float floatValue1, float floatValue2,
            float floatValue3)
    {
        return appendAttribute(buf, attrName, Float.toString(floatValue1), Float.toString(floatValue2), Float.toString(floatValue3));
    }

    /**
     * appends an attribute of the form name="value1 value2 value3 value4"
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, float floatValue1, float floatValue2,
            float floatValue3, float floatValue4)
    {
        return appendAttribute(buf, attrName, Float.toString(floatValue1), Float.toString(floatValue2), Float.toString(floatValue3),
                Float.toString(floatValue4));
    }

    /**
     * appends an attribute of the form name="value"
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, double doubleValue)
    {
        return appendAttribute(buf, attrName, Double.toString(doubleValue));
    }

    /**
     * appends an attribute of the form name="value1 value2"
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, double doubleValue1, double doubleValue2)
    {
        return appendAttribute(buf, attrName, Double.toString(doubleValue1), Double.toString(doubleValue2));
    }

    /**
     * appends an attribute of the form name="value1 value2 value3"
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, double doubleValue1, double doubleValue2,
            double doubleValue3)
    {
        return appendAttribute(buf, attrName, Double.toString(doubleValue1), Double.toString(doubleValue2), Double.toString(doubleValue3));
    }

    /**
     * appends an attribute of the form name="value1 value2 value3 value4"
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, double doubleValue1, double doubleValue2,
            double doubleValue3, double doubleValue4)
    {
        return appendAttribute(buf, attrName, Double.toString(doubleValue1), Double.toString(doubleValue2), Double.toString(doubleValue3),
                Double.toString(doubleValue4));
    }

    /**
     * appends an attribute of the form name="value"
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, boolean boolValue)
    {
        return appendAttribute(buf, attrName, Boolean.toString(boolValue));
    }

    /**
     * appends an attribute of the form name="value1 value2"
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, boolean boolValue1, boolean boolValue2)
    {
        return appendAttribute(buf, attrName, Boolean.toString(boolValue1), Boolean.toString(boolValue2));
    }

    /**
     * appends an attribute of the form name="value1 value2 value3"
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, boolean boolValue1, boolean boolValue2,
            boolean boolValue3)
    {
        return appendAttribute(buf, attrName, Boolean.toString(boolValue1), Boolean.toString(boolValue2), Boolean.toString(boolValue3));
    }

    /**
     * appends an attribute of the form name="value1 value2 value3 value4"
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, boolean boolValue1, boolean boolValue2,
            boolean boolValue3, boolean boolValue4)
    {
        return appendAttribute(buf, attrName, Boolean.toString(boolValue1), Boolean.toString(boolValue2), Boolean.toString(boolValue3),
                Boolean.toString(boolValue4));
    }

    /**
     * appends an attribute of the form name="valu0 value1 ....."
     * The ints array is allowed to be null, and in this case, nothing is appended at all.
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, int[] ints, char separator, XMLFormatting fmt,
            int nrElementsPerLine)
    {
        if (ints == null) return buf;
        buf.append(' ');
        buf.append(attrName);
        buf.append("=\"");
        appendInts(buf, ints, separator, fmt, nrElementsPerLine);
        buf.append('"');
        return buf;
    }

    /**
     * appends an array of ints, with maxNrOfElements ints at one line.
     * The ints array is allowed to be null or empty, and in this case, nothing is appended at all.
     */
    public final static StringBuilder appendInts(StringBuilder buf, int[] ints, char separator, XMLFormatting fmt, int nrElementsPerLine)
    {
        if (ints == null || ints.length == 0) return buf;
        int tabCounter = nrElementsPerLine;
        buf.append(ints[0]);
        tabCounter--;
        for (int i = 1; i < ints.length; i++)
        {
            if (tabCounter == 0)
            {
                buf.append('\n');
                appendSpaces(buf, fmt);
                tabCounter = nrElementsPerLine;
            }
            else
            {
                buf.append(separator);
            }
            buf.append(ints[i]);
            tabCounter--;
        }
        return buf;
    }

    /**
     * appends an attribute of the form name="value0 value1 ....."
     * The floats array is allowed to be null, and in this case, nothing is appended at all.
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, float[] floats, char separator)
    {
        if (floats == null) return buf;
        buf.append(' ');
        buf.append(attrName);
        buf.append("=\"");
        if (floats != null && floats.length > 0)
        {
            buf.append(floats[0]);
            for (int i = 1; i < floats.length; i++)
            {
                buf.append(separator);
                buf.append(floats[i]);
            }
        }
        buf.append('"');
        return buf;
    }

    /**
     * appends an attribute of the form name="value0 value1 .....", with maxNrOfElements float at one line.
     * The floats array is allowed to be null and in this case, nothing is appended at all.
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, float[] floats, char separator,
            XMLFormatting fmt, int nrElementsPerLine)
    {
        if (floats == null) return buf;
        buf.append(' ');
        buf.append(attrName);
        buf.append("=\"");
        appendFloats(buf, floats, separator, fmt, nrElementsPerLine);
        buf.append('"');
        return buf;
    }

    /**
     * appends an array of floats, with maxNrOfElements floats at one line.
     * The floats array is allowed to be null or empty, and in this case, nothing is appended at all.
     */
    public final static StringBuilder appendFloats(StringBuilder buf, float[] floats, char separator, XMLFormatting fmt,
            int nrElementsPerLine)
    {
        if (floats == null || floats.length == 0) return buf;
        int tabCounter = nrElementsPerLine;
        buf.append(floats[0]);
        tabCounter--;
        for (int i = 1; i < floats.length; i++)
        {
            if (tabCounter == 0)
            {
                buf.append('\n');
                appendSpaces(buf, fmt);
                tabCounter = nrElementsPerLine;
            }
            else
            {
                buf.append(separator);
            }
            buf.append(floats[i]);
            tabCounter--;
        }
        return buf;
    }

    /**
     * appends an attribute of the form name="value0 value1 .....", with maxNrOfElements Strings at one line.
     * The strings array is allowed to be null and in this case, nothing is appended at all.
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, String[] strings, char separator,
            XMLFormatting fmt, int nrElementsPerLine)
    {
        if (strings == null) return buf;
        buf.append(' ');
        buf.append(attrName);
        buf.append("=\"");
        appendStrings(buf, strings, separator, fmt, nrElementsPerLine);
        buf.append('"');
        return buf;
    }

    /**
     * appends an array of strings, with maxNrOfElements floats at one line.
     * The strings array is allowed to be null or empty, and in this case, nothing is appended at all.
     */
    public final static StringBuilder appendStrings(StringBuilder buf, String[] strings, char separator, XMLFormatting fmt,
            int nrElementsPerLine)
    {
        if (strings == null || strings.length == 0) return buf;
        int tabCounter = nrElementsPerLine;
        buf.append(strings[0]);
        tabCounter--;
        for (int i = 1; i < strings.length; i++)
        {
            if (tabCounter == 0)
            {
                buf.append('\n');
                appendSpaces(buf, fmt);
                tabCounter = nrElementsPerLine;
            }
            else
            {
                buf.append(separator);
            }
            buf.append(strings[i]);
            tabCounter--;
        }
        return buf;
    }

    /**
     * appends an attribute of the form name="value0 value1 .....", with maxNrOfElements Strings at one line.
     * The strings array is allowed to be null and in this case, nothing is appended at all.
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, boolean[] bools, char separator,
            XMLFormatting fmt, int nrElementsPerLine)
    {
        if (bools == null) return buf;
        buf.append(' ');
        buf.append(attrName);
        buf.append("=\"");
        appendBooleans(buf, bools, separator, fmt, nrElementsPerLine);
        buf.append('"');
        return buf;
    }

    /**
     * appends an array of floats, with maxNrOfElements booleans at one line.
     * The bools array is allowed to be null or empty, and in this case, nothing is appended at all.
     */
    public final static StringBuilder appendBooleans(StringBuilder buf, boolean[] bools, char separator, XMLFormatting fmt,
            int nrElementsPerLine)
    {
        if (bools == null || bools.length == 0) return buf;
        int tabCounter = nrElementsPerLine;
        buf.append(bools[0]);
        tabCounter--;
        for (int i = 1; i < bools.length; i++)
        {
            if (tabCounter == 0)
            {
                buf.append('\n');
                appendSpaces(buf, fmt);
                tabCounter = nrElementsPerLine;
            }
            else
            {
                buf.append(separator);
            }
            buf.append(bools[i]);
            tabCounter--;
        }
        return buf;
    }

    /**
     * appends an attribute of the form name="valu0 value1 ....."
     * The doubles array is allowed to be null, and in this case, nothing is appended at all.
     */
    public final static StringBuilder appendAttribute(StringBuilder buf, String attrName, double[] doubles, char separator)
    {
        if (doubles == null) return buf;
        buf.append(' ');
        buf.append(attrName);
        buf.append("=\"");
        if (doubles != null && doubles.length > 0)
        {
            buf.append(doubles[0]);
            for (int i = 1; i < doubles.length; i++)
            {
                buf.append(separator);
                buf.append(doubles[i]);
            }
        }
        buf.append('"');
        return buf;
    }

    /**
     * This method appends XML style tag attributes to buf.
     * It is assumed that the keys and values of the attributes hashMap
     * are String typed, and that values do not contain " characters.
     * The attributes map is allowed to be null, and in this case, nothing is appended at all.
     */
    public final static StringBuilder appendAttributes(StringBuilder buf, HashMap attributes)
    {
        if (attributes == null) return buf;
        Iterator iter = attributes.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            buf.append(' ');
            buf.append(key);
            buf.append("=\"");
            buf.append(value == null ? "NULL" : value);
            buf.append('"');
        }
        return buf;
    }

    /**
     * appends an XML ETag to buf
     */
    public final static StringBuilder appendETag(StringBuilder buf, String tagName)
    {
        buf.append("</");
        buf.append(tagName);
        buf.append('>');
        return buf;
    }

    /**
     * appends an XML ETag to buf, on a new line, preceded by tab spaces.
     */
    public final static StringBuilder appendETag(StringBuilder buf, String tagName, XMLFormatting fmt)
    {
        buf.append('\n');
        appendSpaces(buf, fmt);
        buf.append("</");
        buf.append(tagName);
        buf.append('>');
        return buf;
    }

    /*
     * append a platform dependent NEWLINE character String to buf
     */
    public final static StringBuilder appendSystemNewLine(StringBuilder buf)
    {
        buf.append(SYSTEMNEWLINE);
        return buf;
    }

    /*
     * append a platform indepenent '\n' character to buf
     */
    public final static StringBuilder appendNewLine(StringBuilder buf)
    {
        buf.append('\n');
        return buf;
    }

    /*
     * append a platform indepenent '\n' character to buf, followed by tab spaces
     */
    public final static StringBuilder appendNewLine(StringBuilder buf, int tab)
    {
        buf.append('\n');
        while (lotsOfSpaces.length() < tab)
        {
            lotsOfSpaces.append("         ");
        }
        return buf.append(lotsOfSpaces.substring(0, tab));
    }

    /*
     * append a platform indepenent '\n' character to buf, followed by tab spaces
     */
    public final static StringBuilder appendNewLine(StringBuilder buf, XMLFormatting fmt)
    {
        return appendNewLine(buf, fmt.getTab());
    }

    /**
     * appends an XML element of the form &quot;tagName&quot;value&quot;/tagName&quot;
     * The value parameter is allowed to be null, and in this case, nothing is appended at all.
     */
    public final static StringBuilder appendTextElement(StringBuilder buf, String tagName, String value, XMLFormatting fmt)
    {
        if (value == null) return buf;
        appendSTag(buf, tagName, fmt);
        buf.append(value);
        appendETag(buf, tagName);
        return buf;
    }

    /**
     * appends an XML element of the form &quot;tagName&quot;value&quot;/tagName&quot;
     */
    public final static StringBuilder appendIntElement(StringBuilder buf, String tagName, int value, XMLFormatting fmt)
    {
        return appendTextElement(buf, tagName, Integer.toString(value), fmt);
    }

    /**
     * appends an XML element of the form &quot;tagName&quot;value&quot;/tagName&quot;,
     * provided that value is not the special &quot;unspecified&quot; value.
     */
    public final static StringBuilder appendOptionalIntElement(StringBuilder buf, String tagName, int value, int unspecified,
            XMLFormatting fmt)
    {
        return (value == unspecified) ? buf : appendTextElement(buf, tagName, Integer.toString(value), fmt);
    }

    /**
     * appends an XML element of the form &quot;tagName&quot;value&quot;/tagName&quot;
     */
    public final static StringBuilder appendLongElement(StringBuilder buf, String tagName, long value, XMLFormatting fmt)
    {
        return appendTextElement(buf, tagName, Long.toString(value), fmt);
    }

    /**
     * appends an XML element of the form &quot;tagName&quot;value&quot;/tagName&quot;,
     * provided that value is not the special &quot;unspecified&quot; value.
     */
    public final static StringBuilder appendOptionalLongElement(StringBuilder buf, String tagName, long value, long unspecified,
            XMLFormatting fmt)
    {
        return (value == unspecified) ? buf : appendTextElement(buf, tagName, Long.toString(value), fmt);
    }

    /**
     * appends an XML element of the form &quot;tagName&quot;value&quot;/tagName&quot;
     */
    public final static StringBuilder appendFloatElement(StringBuilder buf, String tagName, float value, XMLFormatting fmt)
    {
        return appendTextElement(buf, tagName, Float.toString(value), fmt);
    }

    /**
     * appends an XML element of the form &quot;tagName&quot;value&quot;/tagName&quot;,
     * provided that value is not the special &quot;unspecified&quot; value.
     */
    public final static StringBuilder appendOptionalFloatElement(StringBuilder buf, String tagName, float value, float unspecified,
            XMLFormatting fmt)
    {
        return (value == unspecified) ? buf : appendTextElement(buf, tagName, Float.toString(value), fmt);
    }

    /**
     * appends an XML element of the form &quot;tagName&quot;value&quot;/tagName&quot;
     */
    public final static StringBuilder appendDoubleElement(StringBuilder buf, String tagName, double value, XMLFormatting fmt)
    {
        return appendTextElement(buf, tagName, Double.toString(value), fmt);
    }

    /**
     * appends an XML element of the form &quot;tagName&quot;value&quot;/tagName&quot;,
     * provided that value is not the special &quot;unspecified&quot; value.
     */
    public final static StringBuilder appendOptionalDoubleElement(StringBuilder buf, String tagName, double value, double unspecified,
            XMLFormatting fmt)
    {
        return (value == unspecified) ? buf : appendTextElement(buf, tagName, Double.toString(value), fmt);
    }

    /**
     * appends an XML element of the form &quot;tagName&quot;value&quot;/tagName&quot;
     */
    public final static StringBuilder appendIntArrayElement(StringBuilder buf, String tagName, int[] values, char separator,
            XMLFormatting fmt, int nrElementsPerLine)
    {
        if (values == null) return buf;
        appendOpenSTag(buf, tagName, fmt);
        appendAttribute(buf, "count", values.length);
        appendCloseSTag(buf);
        buf.append('\n');
        appendSpaces(buf, fmt.indent());
        appendInts(buf, values, separator, fmt, nrElementsPerLine);
        appendETag(buf, tagName, fmt.unIndent());
        return buf;
    }

    /**
     * appends an XML element of the form &quot;tagName&quot;value&quot;/tagName&quot;
     */
    public final static StringBuilder appendFloatArrayElement(StringBuilder buf, String tagName, float[] values, char separator,
            XMLFormatting fmt, int nrElementsPerLine)
    {
        if (values == null) return buf;
        appendOpenSTag(buf, tagName, fmt);
        appendAttribute(buf, "count", values.length);
        appendCloseSTag(buf);
        buf.append('\n');
        appendSpaces(buf, fmt.indent());
        appendFloats(buf, values, separator, fmt, nrElementsPerLine);
        appendETag(buf, tagName, fmt.unIndent());
        return buf;
    }

    /**
     * Encodes an array of Strings in a single String, where the array ellements are separated by the a comma and space char.
     */
    public final static String encodeStringArray(String[] str)
    {
        return encodeStringArray(str, ", ");
    }

    /**
     * Encodes an array of Strings in a single String, where the array ellements are separated by the specified separator String.
     * When the specified array str is null, the result is null.
     * Unlike appendStrings, there is to formatting involved by means of newlines.
     */
    public final static String encodeStringArray(String[] str, String separator)
    {
        if (str == null) return null;
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < str.length - 1; i++)
        {
            buf.append(str[i]);
            buf.append(separator);
        }
        if (str.length > 0) buf.append(str[str.length - 1]);
        return buf.toString();
    }

    /**
     * Encodes a List of Strings in a single String, where the array ellements are separated by the a comma and space char.
     */
    public final static String encodeStringList(java.util.List<String> str)
    {
        return encodeStringList(str, ", ");
    }

    /**
     * Encodes a List of Strings in a single String, where the array ellements are separated by the specified separator String.
     * When the specified List str is null, the result is null.
     * Unlike appendStrings, there is to formatting involved by means of newlines.
     */
    public final static String encodeStringList(java.util.List<String> str, String separator)
    {
        if (str == null) return null;
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < str.size() - 1; i++)
        {
            buf.append(str.get(i));
            buf.append(separator);
        }
        if (str.size() > 0) buf.append(str.get(str.size() - 1));
        return buf.toString();
    }

    /**
     * appends an XML element of the form &quot;tagName&quot;value&quot;/tagName&quot;
     */
    public final static StringBuilder appendStringArrayElement(StringBuilder buf, String tagName, String[] values, char separator,
            XMLFormatting fmt, int nrElementsPerLine)
    {
        if (values == null) return buf;
        appendOpenSTag(buf, tagName, fmt);
        appendAttribute(buf, "count", values.length);
        appendCloseSTag(buf);
        buf.append('\n');
        appendSpaces(buf, fmt.indent());
        appendStrings(buf, values, separator, fmt, nrElementsPerLine);
        appendETag(buf, tagName, fmt.unIndent());
        return buf;
    }

    /**
    *
    */
    public static final String decodeXMLValueElement(String tagName, XMLTokenizer tokenizer) throws IOException
    {
        return tokenizer.takeTextElement(tagName);
    }

    /**
     * decodes an int
     */
    public final static int decodeInt(String encoding)
    {
        return Integer.parseInt(encoding);
    }

    /**
     * decodes a long
     */
    public final static long decodeLong(String encoding)
    {
        return Long.parseLong(encoding);
    }

    /**
     * decodes a float
     */
    public final static float decodeFloat(String encoding)
    {
        return Float.parseFloat(encoding);
    }

    /**
     * decodes a double
     */
    public final static double decodeDouble(String encoding)
    {
        return Double.parseDouble(encoding);
    }

    /**
     * decodes a boolean value (true or false)
     */
    public final static boolean decodeBoolean(String encoding)
    {
        return encoding.trim().toLowerCase().equals("true");
    }

    /**
     * counts the number of tokens, separated by specified delimiter characters,
     * within a given encoding.
     */
    public final static int countTokens(String encoding, String delimiters)
    {
        StringTokenizer tokenizer = new StringTokenizer(encoding, delimiters);
        return tokenizer.countTokens();
    }

    /**
     * counts the number of tokens, separated by ATTRIBUTE_TOKEN_DELIMITERS characters,
     * within a given encoding.
     */
    public final static int countTokens(String encoding)
    {
        StringTokenizer tokenizer = new StringTokenizer(encoding, ATTRIBUTE_TOKEN_DELIMITERS);
        return tokenizer.countTokens();
    }

    /**
     * decodes a int array, encoded as String of the form value0 value1 ..... value-n,
     */
    public final static boolean[] decodeBooleanArray(String encoding)
    {
        return decodeBooleanArray(encoding, null, ATTRIBUTE_TOKEN_DELIMITERS);
    }

    /**
     * decodes a int array, encoded as String of the form value0 value1 ..... value-n,
     * where the values are separated by characters specified by means of the delimiter String.
     */
    public final static boolean[] decodeBooleanArray(String encoding, String delimiters)
    {
        return decodeBooleanArray(encoding, null, delimiters);
    }

    /**
     * equivalent to decodeBooleanArray(encoding, ints, ATTRIBUTE_TOKEN_DELIMITERS);
     */
    public final static boolean[] decodeBooleanArray(String encoding, boolean[] bools)
    {
        return decodeBooleanArray(encoding, bools, ATTRIBUTE_TOKEN_DELIMITERS);
    }

    /**
     * decodes a int array, encoded as String of the form value0 value1 ..... value-n,
     * where the values are separated by characters specified by means of the delimiter String.
     * The ints array should be sufficiently large so that all tokens can be allocated,
     * or else it should be null. In the latter case, a new int array
     * is allocated with length equal to the number of tokens.
     */
    public final static boolean[] decodeBooleanArray(String encoding, boolean[] bools, String delimiters)
    {
        int decode = 0;
        StringTokenizer tokenizer = new StringTokenizer(encoding, delimiters);
        if (bools == null) bools = new boolean[tokenizer.countTokens()];
        while (tokenizer.hasMoreTokens())
        {
            if (decode >= bools.length)
            {
                throw new RuntimeException("More booleans than expected: " + decode);
            }
            bools[decode++] = Boolean.parseBoolean(tokenizer.nextToken());
        }
        return bools;
    }

    /**
     * decodes a int array, encoded as String of the form value0 value1 ..... value-n,
     */
    public final static int[] decodeIntArray(String encoding)
    {
        return decodeIntArray(encoding, null, ATTRIBUTE_TOKEN_DELIMITERS);
    }

    /**
     * decodes a int array, encoded as String of the form value0 value1 ..... value-n,
     * where the values are separated by characters specified by means of the delimiter String.
     */
    public final static int[] decodeIntArray(String encoding, String delimiters)
    {
        return decodeIntArray(encoding, null, delimiters);
    }

    /**
     * equivalent to decodeIntArray(encoding, ints, ATTRIBUTE_TOKEN_DELIMITERS);
     */
    public final static int[] decodeIntArray(String encoding, int[] ints)
    {
        return decodeIntArray(encoding, ints, ATTRIBUTE_TOKEN_DELIMITERS);
    }

    /**
     * decodes a int array, encoded as String of the form value0 value1 ..... value-n,
     * where the values are separated by characters specified by means of the delimiter String.
     * The ints array should be sufficiently large so that all tokens can be allocated,
     * or else it should be null. In the latter case, a new int array
     * is allocated with length equal to the number of tokens.
     */
    public final static int[] decodeIntArray(String encoding, int[] ints, String delimiters)
    {
        int decode = 0;
        StringTokenizer tokenizer = new StringTokenizer(encoding, delimiters);
        if (ints == null) ints = new int[tokenizer.countTokens()];
        while (tokenizer.hasMoreTokens())
        {
            if (decode >= ints.length)
            {
                throw new RuntimeException("More ints than expected: " + decode);
            }
            ints[decode++] = Integer.parseInt(tokenizer.nextToken());
        }
        return ints;
    }

    /**
     * decodes a float array, encoded as String of the form value0 value1 ..... value-n,
     * where the values are separated by characters specified by means of the delimiter String.
     */
    public final static float[] decodeFloatArray(String encoding)
    {
        return decodeFloatArray(encoding, null, ATTRIBUTE_TOKEN_DELIMITERS);
    }

    /**
     * decodes a float array, encoded as String of the form value0 value1 ..... value-n,
     * where the values are separated by characters specified by means of the delimiter String.
     */
    public final static float[] decodeFloatArray(String encoding, String delimiters)
    {
        return decodeFloatArray(encoding, null, delimiters);
    }

    /**
     * equivalent to decodeFloatArray(encoding, floats, ATTRIBUTE_TOKEN_DELIMITERS);
     * (i.e. attribute tokens have to be separated by white space)
     */
    public final static float[] decodeFloatArray(String encoding, float[] floats)
    {
        return decodeFloatArray(encoding, floats, ATTRIBUTE_TOKEN_DELIMITERS);
    }

    /**
     * decodes a float array, encoded as String of the form value0 value1 ..... value-n,
     * where the values are separated by characters specified by means of the delimiter String.
     * The floats array should be sufficiently large so that all tokens can be allocated,
     * or else it should be null. In the latter case, a new float array
     * is allocated with length equal to the number of tokens.
     */
    public final static float[] decodeFloatArray(String encoding, float[] floats, String delimiters)
    {
        int decode = 0;
        StringTokenizer tokenizer = new StringTokenizer(encoding, delimiters);
        if (floats == null) floats = new float[tokenizer.countTokens()];
        while (tokenizer.hasMoreTokens())
        {
            if (decode >= floats.length)
            {
                throw new RuntimeException("More floats than expected: " + decode);
            }
            floats[decode++] = Float.parseFloat(tokenizer.nextToken());
        }
        return floats;
    }

    /**
     * decodes a double array, encoded as String of the form value0 value1 ..... value-n
     */
    public final static double[] decodeDoubleArray(String encoding)
    {
        return decodeDoubleArray(encoding, null, ATTRIBUTE_TOKEN_DELIMITERS);
    }

    /**
     * decodes a double array, encoded as String of the form value0 value1 ..... value-n
     */
    public final static double[] decodeDoubleArray(String encoding, String delimiters)
    {
        return decodeDoubleArray(encoding, null, delimiters);
    }

    /**
     * decodes a double array, encoded as String of the form value0 value1 ..... value-n
     * The doubles array should be sufficiently large so that all tokens can be allocated,
     * or else it should be null. In the latter case, a new double array
     * is allocated with length equal to the number of tokens.
     */
    public final static double[] decodeDoubleArray(String encoding, double[] doubles)
    {
        return decodeDoubleArray(encoding, doubles, ATTRIBUTE_TOKEN_DELIMITERS);
    }

    /**
     * decodes a double array, encoded as String of the form value0 value1 ..... value-n,
     * where the values are separated by characters specified by means of the delimiter String.
     * The doubles array should be sufficiently large so that all tokens can be allocated,
     * or else it should be null. In the latter case, a new double array
     * is allocated with length equal to the number of tokens.
     */
    public final static double[] decodeDoubleArray(String encoding, double[] doubles, String delimiters)
    {
        int decode = 0;
        StringTokenizer tokenizer = new StringTokenizer(encoding, delimiters);
        if (doubles == null) doubles = new double[tokenizer.countTokens()];
        while (tokenizer.hasMoreTokens())
        {
            if (decode >= doubles.length)
            {
                throw new RuntimeException("More doubles than expected: " + decode);
            }
            doubles[decode++] = Double.parseDouble(tokenizer.nextToken());
        }
        return doubles;
    }

    /**
     * decodes a string array, encoded as String of the form value0 value1 ..... value-n
     */
    public final static String[] decodeStringArray(String encoding)
    {
        return decodeStringArray(encoding, null, ATTRIBUTE_TOKEN_DELIMITERS);
    }

    /**
     * decodes a String array, encoded as String of the form value0 value1 ..... value-n
     */
    public final static String[] decodeStringArray(String encoding, String delimiters)
    {
        return decodeStringArray(encoding, null, delimiters);
    }

    /**
     * decodes a String array, encoded as String of the form value0 value1 ..... value-n,
     * where the values are separated by characters specified by means of the delimiter String.
     * The Strings array should be sufficiently large so that all tokens can be allocated,
     * or else it should be null. In the latter case, a new String array
     * is allocated with length equal to the number of tokens.
     */
    public final static String[] decodeStringArray(String encoding, String[] strings)
    {
        return decodeStringArray(encoding, strings, ATTRIBUTE_TOKEN_DELIMITERS);
    }

    /**
     * decodes a sequence of Strings, separated by specified delimiters
     */
    public final static String[] decodeStringArray(String encoding, String[] strings, String delimiters)
    {
        int decode = 0;
        StringTokenizer tokenizer = new StringTokenizer(encoding, delimiters);
        if (strings == null) strings = new String[tokenizer.countTokens()];
        while (tokenizer.hasMoreTokens())
        {
            if (decode >= strings.length)
            {
                throw new RuntimeException("More Strings than expected: " + decode);
            }
            strings[decode++] = tokenizer.nextToken();
        }
        return strings;
    }

    /**
     * decodes a sequence of Strings, separated by specified delimiters
     */
    public final static java.util.List<String> decodeStringList(String encoding)
    {
        return decodeStringList(encoding, COMMA_SEPARATOR);
    }

    /**
     * decodes a sequence of Strings, separated by specified delimiters
     */
    public final static java.util.List<String> decodeStringList(String encoding, String delimiters)
    {
        if (encoding == null) return null;
        StringTokenizer tokenizer = new StringTokenizer(encoding, delimiters);
        ArrayList<String> strings = new ArrayList<String>();
        while (tokenizer.hasMoreTokens())
        {
            strings.add(tokenizer.nextToken());
        }
        return strings;
    }

    public final static String[] decodeStringArrayElement(String tagName, XMLTokenizer tokenizer) throws IOException
    {
        String attrValue = tokenizer.getAttribute("count");
        if (attrValue == null)
        {
            throw tokenizer.getXMLScanException(tagName + " - count attribute missing");
        }
        int count = Integer.parseInt(attrValue);
        String[] result = new String[count];
        tokenizer.takeSTag(tagName);
        decodeStringArray(tokenizer.takeOptionalCharData(), result);
        tokenizer.takeETag(tagName);
        return result;
    }

    public final static int[] decodeIntArrayElement(String tagName, XMLTokenizer tokenizer) throws IOException
    {
        String attrValue = tokenizer.getAttribute("count");
        if (attrValue == null)
        {
            throw tokenizer.getXMLScanException(tagName + " - count attribute missing");
        }
        int count = Integer.parseInt(attrValue);
        int[] result = new int[count];
        tokenizer.takeSTag(tagName);
        decodeIntArray(tokenizer.takeOptionalCharData(), result);
        tokenizer.takeETag(tagName);
        return result;
    }

    public final static float[] decodeFloatArrayElement(String tagName, XMLTokenizer tokenizer) throws IOException
    {
        String attrValue = tokenizer.getAttribute("count");
        if (attrValue == null)
        {
            throw tokenizer.getXMLScanException(tagName + " - count attribute missing");
        }
        int count = Integer.parseInt(attrValue);
        float[] result = new float[count];
        tokenizer.takeSTag(tagName);
        decodeFloatArray(tokenizer.takeOptionalCharData(), result);
        tokenizer.takeETag(tagName);
        return result;
    }

    public static void setConsoleAttributeEnabled(boolean b)
    {
        consoleAttributeEnabled = b;
    }

    public static <T extends XMLStructureAdapter> T parseXMLElement(Class<T> tClass, XMLTokenizer tokenizer)
    {
        try
        {
            T result = tClass.newInstance();
            result.readXML(tokenizer);
            return result;
        }
        catch (InstantiationException ie)
        {
            throw new RuntimeException("InstantiationException while recreating XMLStructure: \n" + ie);
        }
        catch (IllegalAccessException iae)
        {
            throw new RuntimeException("IllegalAccessException while recreating XMLStructure: \n" + iae);
        }
        catch (IOException e)
        {
            throw new RuntimeException("IOException while recreating XMLStructure: \n" + e);
        }
    }

    private static boolean consoleAttributeEnabled = true;
    private static StringBuilder lotsOfSpaces = new StringBuilder("               ");
    public static final int TAB = 3;
    public static final String TAB_STRING = "   ";
    public static final String NEWLINE = "\n";
    public static final String SYSTEMNEWLINE = System.getProperty("line.separator");
    public static final String ATTRIBUTE_TOKEN_DELIMITERS = " \t\n\r\f";
    public static final String LINE_DELIMITERS = "\n\r\f";
    public static final String COMMA_SEPARATOR = ", \t\n\r\f";
    public static final int DECODEDARRAYSIZE = 4;

    // private static String getNamespaceLabel(String namespace) {
    // for (int index=namespaceStack.size()-1; index >= 0; index--) {
    //
    // if (namespace == namespaceStack.get(index)) return
    // }
    // return null;
    // }
    //
    // private static void pushXMLNameSpace(XMLNameSpace nsp) {
    // namespaceStack.add(nsp);
    // }
    //
    // private static XMLNameSpace popXMLNameSpace() {
    // int topindex = namespaceStack.size()-1;
    // XMLNameSpace top = namespaceStack.get(topindex);
    // namespaceStack.remove(topindex);
    // return top;
    // }
    //
    // private static ArrayList<XMLNameSpace> namespaceStack = new ArrayList<XMLNameSpace>();

}

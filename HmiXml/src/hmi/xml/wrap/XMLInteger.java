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

package hmi.xml.wrap;

import hmi.xml.XMLStructure;
import hmi.xml.XMLTokenizer;

/**
 * XMLInteger is in essence a wrapper around int or Integer values, that turns
 * them effectively into an XMLStructure object.
 * @author Job Zwiers
 */
public class XMLInteger extends XMLBasicValue<Integer> implements XMLStructure
{

    private int val;

    /**
     * creates a new XMLInteger with value 0.
     */
    public XMLInteger()
    {
        val = 0;
    }

    /**
     * creates a new XMLInteger with specified value.
     */
    public XMLInteger(int value)
    {
        val = value;
    }

    /**
     * creates a new XMLInteger with specified value.
     */
    public XMLInteger(Integer value)
    {
        val = value;
    }

    /**
     * creates a new XMLInteger with specified value.
     */
    public XMLInteger(XMLInteger xmlValue)
    {
        val = xmlValue.val;
    }

    /**
     * returns the value as an int.
     */
    public int intValue()
    {
        return val;
    }

    /**
     * returns the value as an Integer
     */
    public Integer integerValue()
    {
        return Integer.valueOf(val);
    }

    /**
     * returns the normal int value as String
     */
    @Override
    public String toString()
    {
        return Integer.toString(val);
    }

    /**
     * equality, based on String equality for the value fields;
     */
    @Override
    public boolean equals(Object xmlInteger)
    {
        if (xmlInteger == null)
            return false;
        if (xmlInteger instanceof XMLInteger)
        {
            return (((XMLInteger) xmlInteger).val == this.val);
        }
        return false;
    }

    /**
     * calculates the hash code consistent with "equals". i.e "equal" objects
     * get the same hash code.
     */
    @Override
    public int hashCode()
    {
        return val;
    }

    /**
     * returns a String that can be used as XML attribute value.
     */
    @Override
    public StringBuilder appendAttributeString(StringBuilder buf)
    {
        return appendAttribute(buf, "val", Integer.toString(val));
    }

    /**
     * decodes the value from an attribute value String
     */
    @Override
    public void decodeAttribute(String attrName, String valCode,
            XMLTokenizer tokenizer)
    {
        val = Integer.parseInt(valCode);
    }

    /**
     * returns the value as an Integer Object
     */
    @Override
    public Integer unwrap()
    {
        return Integer.valueOf(val);
    }

 
   /**
    * The XML Stag for XML encoding -- use this static method when you want to see if a given String equals
    * the xml tag for this class
    */
   public static String xmlTag() { return XMLTAG; }
 
   /**
    * The XML Stag for XML encoding -- use this method to find out the run-time xml tag of an object
    */
   @Override
   public String getXMLTag() {
      return XMLTAG;
   }

    private static final String XMLTAG = "XMLInteger";

    public static final String CLASSNAME = "parlevink.xml.XMLInteger";

    public static final String WRAPPEDCLASSNAME = "java.lang.Integer";

}

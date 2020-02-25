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

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

package hmi.graphics.collada;

import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Declares a physical shape
 * Ignored tags: hollow, mass, density, instance_physics_material, physics_material, instance_geometry,
 * plane, cylinder, tapered_cylinder, tapered_capsule
 * 
 * Assumes that the shape contains only one rotation and one translation, does not check or enforce only
 * one of box, plane, sphere, cylinder, tapered_cylinder, capsule, tapered_capsule
 * @author Herwin van Welbergen
 */
public class Shape extends ColladaElement
{
    private Translate translate = null;
    private Rotate rotate = null;
    private Box box = null;
    private Sphere sphere = null;
    private Capsule capsule = null;
    private ArrayList<Extra> extras = new ArrayList<Extra>();

    public Shape()
    {
        super();
    }

    public Shape(Collada collada, XMLTokenizer tokenizer) throws IOException
    {
        super(collada);
        readXML(tokenizer);
    }

    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        appendXMLStructure(buf, fmt, getBox());
        appendXMLStructure(buf, fmt, getSphere());
        appendXMLStructure(buf, fmt, getCapsule());
        appendXMLStructure(buf, fmt, getTranslate());
        appendXMLStructure(buf, fmt, getRotate());

        appendXMLStructureList(buf, fmt, extras);
        return buf;
    }

    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException
    {
        while (tokenizer.atSTag())
        {
            String tag = tokenizer.getTagName();
            if (tag.equals(Translate.xmlTag()))
            {
                setTranslate(new Translate(getCollada(), tokenizer));
            }
            else if (tag.equals(Rotate.xmlTag()))
            {
                setRotate(new Rotate(getCollada(), tokenizer));
            }
            else if (tag.equals(Box.xmlTag()))
            {
                setBox(new Box(getCollada(), tokenizer));
            }
            else if (tag.equals(Sphere.xmlTag()))
            {
                setSphere(new Sphere(getCollada(), tokenizer));
            }
            else if (tag.equals(Capsule.xmlTag()))
            {
                setCapsule(new Capsule(getCollada(), tokenizer));
            }
            else
            {
                getCollada().warning(tokenizer.getErrorMessage("Shape: skip : " + tokenizer.getTagName()));
                tokenizer.skipTag();
            }
        }
        addColladaNode(getTranslate());
        addColladaNode(getRotate());
        addColladaNode(getBox());
        addColladaNode(getSphere());
        addColladaNode(getCapsule());
    }

    /*
     * The XML Stag for XML encoding
     */
    private static final String XMLTAG = "shape";

    /**
     * The XML Stag for XML encoding
     */
    public static String xmlTag()
    {
        return XMLTAG;
    }

    /**
     * returns the XML Stag for XML encoding
     */
    @Override
    public String getXMLTag()
    {
        return XMLTAG;
    }

    public void setCapsule(Capsule capsule)
    {
        this.capsule = capsule;
    }

    public Capsule getCapsule()
    {
        return capsule;
    }

    public void setSphere(Sphere sphere)
    {
        this.sphere = sphere;
    }

    public Sphere getSphere()
    {
        return sphere;
    }

    public void setBox(Box box)
    {
        this.box = box;
    }

    public Box getBox()
    {
        return box;
    }

    public void setTranslate(Translate translate)
    {
        this.translate = translate;
    }

    public Translate getTranslate()
    {
        return translate;
    }

    public void setRotate(Rotate rotate)
    {
        this.rotate = rotate;
    }

    public Rotate getRotate()
    {
        return rotate;
    }
}

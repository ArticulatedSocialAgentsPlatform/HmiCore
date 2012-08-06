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

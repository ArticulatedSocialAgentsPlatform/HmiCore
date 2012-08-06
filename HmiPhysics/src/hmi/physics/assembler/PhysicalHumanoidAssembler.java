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
package hmi.physics.assembler;

import hmi.animation.VJoint;
import hmi.graphics.scenegraph.GNode;
import hmi.math.Vec3f;
import hmi.physics.JointType;
import hmi.physics.PhysicalHumanoid;
import hmi.physics.PhysicalSegment;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.util.ArrayList;

/**
 * Sets up PhysicalHumanoid and its connection to the VObject that is steered by
 * the PhysicalHumanoid
 * 
 * @author welberge
 */
public class PhysicalHumanoidAssembler extends XMLStructureAdapter
{
    private VJoint human;
    private PhysicalHumanoid ph;

    public PhysicalSegmentAssembler rootAssembler;
    public ArrayList<PhysicalSegmentAssembler> segmentAssemblers = new ArrayList<PhysicalSegmentAssembler>();

    /**
     * Constructor
     * 
     * @param h
     *            the visualization humanoid
     * @param p
     *            the PhysicalHumanoid
     */
    public PhysicalHumanoidAssembler(VJoint h, PhysicalHumanoid p)
    {
        human = h;
        ph = p;
    }

    public void createFromGNode(GNode node, String[] segments, String endSegments[], String rootSegment, JointType[] types, float axes[],
            float limits1[], float limits2[], float limits3[])
    {
        PhysicalSegment ps = ph.createSegment(ph.getId() + "_" + rootSegment, rootSegment);
        PhysicalSegmentAssembler psa = new PhysicalSegmentAssembler(human, ph, ps);
        psa.setRoot(true);

        psa.createFromGNode(ph.getId(), node, rootSegment, segments, endSegments);

        setRootSegmentAssembler(psa);
        for (int i = 0; i < segments.length; i++)
        {
            ps = ph.createSegment(ph.getId() + "_" + segments[i], segments[i]);
            psa = new PhysicalSegmentAssembler(human, ph, ps);

            psa.createFromGNode(ph.getId(), node, segments[i], segments, endSegments);
            psa.segment.jointType = types[i];

            Vec3f.set(psa.segment.axis1, 0, axes, i * 6);
            Vec3f.set(psa.segment.axis2, 0, axes, i * 6 + 3);
            psa.segment.loStop[0] = limits1[i * 2];
            psa.segment.hiStop[0] = limits1[i * 2 + 1];
            psa.segment.loStop[1] = limits2[i * 2];
            psa.segment.hiStop[1] = limits2[i * 2 + 1];
            psa.segment.loStop[2] = limits3[i * 2];
            psa.segment.hiStop[2] = limits3[i * 2 + 1];

            addPhysicalSegmentAssembler(psa);
        }
    }

    public void setupStartJointOffset(PhysicalSegmentAssembler psa)
    {
        float[] trans = new float[3];
        psa.startJoint.getPathTranslation(null, trans);
        float pos[] = new float[3];
        psa.segment.box.getTranslation(pos);
        Vec3f.sub(trans, pos);
        Vec3f.set(psa.segment.startJointOffset, trans);
    }

    /**
     * Create the PhysicalSegments and PhysicalJoints
     */
    public void setupJoints(String rootJoint)
    {
        setupStartJointOffset(rootAssembler);
        ph.getRootSegment().box.setRotation(1, 0, 0, 0);
        VJoint rootJ = human.getPart(rootJoint);
        ph.setRootRotationBuffer(rootJ.getRotationBuffer());
        ph.setRootTranslationBuffer(rootJ.getTranslationBuffer());
        setJoints();
    }

    private void setJoints()
    {
        ArrayList<VJoint> assignedJoints = new ArrayList<VJoint>();
        for (PhysicalSegmentAssembler ps1 : segmentAssemblers)
        {
            for (VJoint rootEndJoint : rootAssembler.endJoints)
            {
                if (ps1.startJoint == rootEndJoint)
                {
                    float[] center = new float[3];
                    rootEndJoint.getPathTranslation(null, center);
                    setupStartJointOffset(ps1);
                    ps1.segment.startJoint = ph.setupJoint(ps1.startJoint.getSid(), ph.getRootSegment(), ps1.segment, center);
                    ps1.segment.startJoint.setRotationBuffer(rootEndJoint.getRotationBuffer());
                    assignedJoints.add(rootEndJoint);
                }
            }
            for (PhysicalSegmentAssembler ps2 : segmentAssemblers)
            {
                for (VJoint endJoint : ps2.endJoints)
                {
                    if (ps1.startJoint == endJoint)
                    {
                        float[] center = new float[3];
                        endJoint.getPathTranslation(null, center);
                        setupStartJointOffset(ps1);
                        ps1.segment.startJoint = ph.setupJoint(ps1.startJoint.getSid(), ps2.segment, ps1.segment, center);
                        ps1.segment.startJoint.setRotationBuffer(endJoint.getRotationBuffer());
                        assignedJoints.add(endJoint);
                    }
                }
            }
        }
    }

    /**
     * Set to 'empty' state, remove all segments etc
     */
    public void clear()
    {
        segmentAssemblers.clear();
        ph.clear();
    }

    /**
     * Adds a new PhysicalSegmentAssembler, assumes psa has a valid segment
     * attached
     */
    public void addPhysicalSegmentAssembler(PhysicalSegmentAssembler psa)
    {
        ph.addSegment(psa.segment);
        segmentAssemblers.add(psa);
    }

    public void removePhysicalSegmentAssembler(PhysicalSegmentAssembler psa)
    {
        ph.removeSegment(psa.segment);
        if (ph.getRootSegment() == psa.segment)
            ph.setRootSegment(null);
        ph.removeFromHashMap(psa.segment);
        segmentAssemblers.remove(psa);
    }

    /**
     * Sets the root assembler
     */
    public void setRootSegmentAssembler(PhysicalSegmentAssembler psa)
    {
        rootAssembler = psa;
        ph.setRootSegment(psa.segment);
        ph.setupHashMaps(ph.getRootSegment());
    }

    /*
     * --------------------------------------------------------------------------
     * ---------------------- XML parsing part
     * ----------------------------------
     * --------------------------------------------------------------
     */
    /**
     * decodes the value from an attribute value String returns true if
     * succesful, returns false for attribute names that are not recognized.
     * Might throw a RuntimeException when an attribute has been recognized, but
     * is ill formatted.
     */
    @Override
    public boolean decodeAttribute(String attrName, String attrValue)
    {
        // no attributes
        return false;
    }

    /**
     * decodes the value from an attribute value String returns true if
     * succesful, returns false for attribute names that are not recognized.
     * Might throw a RuntimeException when an attribute has been recognized, but
     * is ill formatted. Moreover, an XMLTokenizer reference is available which
     * can be queried for attributes, like getTokenLine() or getTokenCharPos(),
     * which might be helpful to produce error messages referring to
     * lines/positions within the XML document
     */
    @Override
    public boolean decodeAttribute(String attrName, String attrValue, XMLTokenizer tokenizer)
    {
        return decodeAttribute(attrName, attrValue);
    }

    /**
     * decodes the XML contents, i.e. the XML between the STag and ETag of the
     * encoding.
     */
    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws java.io.IOException
    {
        while (tokenizer.atSTag())
        {
            if (tokenizer.atSTag("PhysicalSegment"))
            {
                PhysicalSegment ps = ph.createSegment(ph.getId() + "_", "");
                PhysicalSegmentAssembler psa = new PhysicalSegmentAssembler(human, ph, ps);
                psa.readXML(tokenizer);
                addPhysicalSegmentAssembler(psa);
            }
            else if (tokenizer.atSTag("PhysicalRootSegment"))
            {
                PhysicalSegment ps = ph.createSegment(ph.getId() + "_", "");
                PhysicalSegmentAssembler psa = new PhysicalSegmentAssembler(human, ph, ps);
                psa.setRoot(true);
                psa.readXML(tokenizer);
                setRootSegmentAssembler(psa);
            }
        }
    }

    /**
     * Appends a String to buf that encodes the contents for the XML encoding.
     * The encoding should start on a new line, using indentation equal to tab.
     * There should be no newline after the encoding.
     */
    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        appendNewLine(buf);
        if (rootAssembler != null)
        {
            rootAssembler.appendXML(buf, fmt);
        }
        for (PhysicalSegmentAssembler ps : segmentAssemblers)
        {
            // appendNewLine(buf, tab);
            appendNewLine(buf);
            ps.appendXML(buf, fmt);
        }
        return buf;
    }

    /**
     * Appends a String to buf that encodes the attributes for the XML encoding.
     * When non empty, the attribute string should start with a space character.
     * Hint: call the appendAttribute(StringBuffer buf, String attrName, String
     * attrValue) for every relevant attribute; this takes care of the leading
     * space as well as spaces in between the attributes) The encoding should
     * preferably not add newline characters.
     */
    @Override
    public StringBuilder appendAttributeString(StringBuilder buf)
    {
        // no attributes
        return buf;
    }

    /**
     * returns the XML tag that is used to encode this type of XMLStructure.
     */
    @Override
    public String getXMLTag()
    {
        return "PhysicalHumanoid";
    }

    public PhysicalHumanoid getPhysicalHumanoid()
    {
        return ph;
    }
}

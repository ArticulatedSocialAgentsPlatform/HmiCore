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
package hmi.physics.assembler;

import hmi.animation.VJoint;
import hmi.graphics.scenegraph.GNode;
import hmi.graphics.scenegraph.GShape;
import hmi.math.Quat4f;
import hmi.math.Vec3f;
import hmi.physics.AABoxFitter;
import hmi.physics.JointType;
import hmi.physics.Mass;
import hmi.physics.PhysicalHumanoid;
import hmi.physics.PhysicalSegment;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sets up PhysicalSegment and the connection to the VObject that is steered by the PhysicalSegment
 * 
 * @author welberge
 */
public class PhysicalSegmentAssembler extends XMLStructureAdapter
{
    public VJoint startJoint;
    private VJoint human;
    public PhysicalSegment segment;
    public List<VJoint> endJoints = new ArrayList<VJoint>();
    private boolean isRoot = false;
    private PhysicalHumanoid pHuman;
    private Logger logger = LoggerFactory.getLogger(PhysicalSegmentAssembler.class.getName());

    /**
     * Create a PhysicalSegment
     * 
     * @param h humanoid
     * @param seg segment
     */
    public PhysicalSegmentAssembler(VJoint h, PhysicalHumanoid ph, PhysicalSegment seg)
    {
        human = h;
        segment = seg;
        pHuman = ph;
    }

    @Override
    public String toString()
    {
        return segment.getSid();
    }

    private void findEndJoints(VJoint start, String[] joints, String[] ej)
    {
        for (VJoint j : start.getChildren())
        {
            boolean isEndJoint = false;
            for (int i = 0; i < ej.length; i++)
            {
                if (ej[i].equals(j.getSid()))
                {
                    endJoints.add(j);
                    isEndJoint = true;
                    break;
                }
            }
            if (!isEndJoint)
            {
                if (j.getChildren().size() == 0)
                {
                    // always add the end of a chain
                    endJoints.add(j);
                }
                else
                {
                    boolean found = false;
                    for (int i = 0; i < joints.length; i++)
                    {
                        if (joints[i].equals(j.getSid()))
                        {
                            endJoints.add(j);
                            found = true;
                            break;
                        }
                    }
                    if (!found)
                    {
                        findEndJoints(j, joints, ej);
                    }
                }
            }
        }
    }

    private void findJoints(VJoint start, String jointNames[], String endJoints[], ArrayList<VJoint> joints)
    {
        for (VJoint j : start.getChildren())
        {
            boolean found = false;
            for (int i = 0; i < jointNames.length; i++)
            {
                if (jointNames[i].equals(j.getSid()))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                found = false;
                for (int i = 0; i < endJoints.length; i++)
                {
                    if (endJoints[i].equals(j.getSid()))
                    {
                        found = true;
                        break;
                    }
                }
                if (!found)
                {
                    joints.add(j);
                    findJoints(j, jointNames, endJoints, joints);
                }
            }
        }
    }

    private void findNodes(GNode rootNode, ArrayList<GNode> nodes, ArrayList<VJoint> segmentJoints)
    {
        for (VJoint vj : segmentJoints)
        {
            if (vj.getSid().equals(rootNode.getSid()))
            {
                nodes.add(rootNode);
            }
        }
        for (GNode gn : rootNode.getChildNodes())
        {
            findNodes(gn, nodes, segmentJoints);
        }
    }

    public void createFromGNode(String phID, GNode rootNode, String jointName, String joints[], String endJ[])
    {
        // System.out.println("Creating segment "+jointName);
        startJoint = human.getPart(jointName);
        if (startJoint == null)
        {
            throw new RuntimeException("Start joint not found in human: " + jointName);
        }
        segment.setId(startJoint.getSid() + "_segment");

        findEndJoints(startJoint, joints, endJ);
        ArrayList<VJoint> segmentJoints = new ArrayList<VJoint>();
        segmentJoints.add(startJoint);
        findJoints(startJoint, joints, endJ, segmentJoints);
        segment.setSid(startJoint.getSid());
        segment.setId(phID + "_" + startJoint.getSid());

        ArrayList<GNode> nodes = new ArrayList<GNode>();
        findNodes(rootNode, nodes, segmentJoints);

        float totalMass = 0;

        Mass totalM = null;
        float cTotal[] = new float[3];
        float c2[] = new float[3];
        float c[] = new float[3];
        float I[] = new float[9];
        AABoxFitter fitter = new AABoxFitter();
        float jTrans[] = new float[3];

        int i = 0;
        for (GNode node : nodes)
        {
            VJoint vj = segmentJoints.get(i);
            float density = BodyDensities.bodyDensityMap.get(vj.getSid());

            i++;
            vj.getPathTranslation(startJoint, jTrans);
            // System.out.println("Connected node "+node.getName());
            List<GShape> shapes = node.getGShapes();
            for (GShape s : shapes)
            {
                Mass m = segment.createMass();
                if (s.getGMesh().getVCountData() != null)
                {
                    s.getGMesh().triangulate();
                }
                m.setFromGMesh(s.getGMesh(), density);
                m.getCOM(c);
                m.translate(-c[0], -c[1], -c[2]);
                Vec3f.add(c, jTrans);

                if (totalM == null)
                {
                    totalM = m;
                    Vec3f.set(cTotal, c);
                }
                else
                {
                    // System.out.println("Adding mass for "+node.getName());
                    totalM.addMass(m, cTotal, c, c2);
                    Vec3f.set(cTotal, c2);
                }
                totalMass += m.getMass();
            }
        }

        i = 0;
        for (GNode node : nodes)
        {
            // System.out.println("Connected node "+node.getName());
            List<GShape> shapes = node.getGShapes();
            VJoint vj = segmentJoints.get(i);
            i++;
            vj.getPathTranslation(startJoint, jTrans);

            for (GShape s : shapes)
            {
                float vertices[] = s.getGMesh().getVertexData("mcPosition");
                fitter.fit(vertices);
                float q[] = new float[4];
                float tr[] = new float[3];
                Vec3f.set(tr, fitter.center);
                Vec3f.sub(tr, cTotal);
                Vec3f.add(tr, jTrans);
                Quat4f.setIdentity(q);
                segment.box.addBox(q, tr, fitter.half_extends);
            }
        }

        segment.box.setTranslation(cTotal);
        segment.box.adjustMass(totalMass);
        totalM.getInertiaTensor(I);
        segment.box.setInertiaTensor(I);
        float trans[] = new float[3];
        startJoint.getPathTranslation(null, trans);
        Vec3f.add(trans, cTotal);
        segment.box.setTranslation(trans);

        // System.out.println("Mass: "+segment.box.getMass());
        segment.box.getInertiaTensor(I);
        // System.out.println("I: "+Mat3f.toString(I));
        // System.out.println("Pos "+Vec3f.toString(trans));
    }

    /*
     * ------------------------------------------------------------------------------------------------ XML parsing part
     * ------------------------------------------------------------------------------------------------
     */
    /**
     * decodes the value from an attribute value String returns true if successful, returns false for attribute names that are not recognized. Might
     * throw a RuntimeException when an attribute has been recognized, but is ill formatted.
     */
    @Override
    public boolean decodeAttribute(String attrName, String attrValue)
    {

        if (attrName.equals("startJoint"))
        {
            startJoint = human.getPart(attrValue);
            if (startJoint == null)
            {
                throw new RuntimeException("NULL STARTJOINT: " + attrValue + " on human " + human.getName());
            }
            segment.setId(pHuman.getId() + "_" + startJoint.getSid() + "_segment");
            segment.setSid(startJoint.getSid());
            return true;
        }
        else if (attrName.equals("endJoint"))
        {
            String[] joint = attrValue.split(" ");
            for (int i = 0; i < joint.length; i++)
            {
                VJoint endJoint = human.getPart(joint[i]);
                if (endJoint == null)
                {
                    logger.warn("NULL ENDJOINT: {} on human {}", joint[i], human.getName());
                }
                endJoints.add(endJoint);
            }
            return true;
        }
        else if (attrName.equals("mass"))
        {
            segment.box.adjustMass(Float.parseFloat(attrValue));
            return true;
        }
        else if (attrName.equals("type"))
        {
            if (attrValue.equals("fixed"))
            {
                segment.jointType = JointType.FIXED;
            }
            else if (attrValue.equals("hinge"))
            {
                segment.jointType = JointType.HINGE;
            }
            else if (attrValue.equals("universal"))
            {
                segment.jointType = JointType.UNIVERSAL;
            }
            else if (attrValue.equals("ball"))
            {
                segment.jointType = JointType.BALL;
            }
            else
            {
                return false;
            }
            return true;
        }
        else if (attrName.equals("axis1"))
        {
            String[] vals = attrValue.split(" ");
            segment.axis1[0] = Float.parseFloat(vals[0]);
            segment.axis1[1] = Float.parseFloat(vals[1]);
            segment.axis1[2] = Float.parseFloat(vals[2]);
            return true;
        }
        else if (attrName.equals("axis2"))
        {
            String[] vals = attrValue.split(" ");
            segment.axis2[0] = Float.parseFloat(vals[0]);
            segment.axis2[1] = Float.parseFloat(vals[1]);
            segment.axis2[2] = Float.parseFloat(vals[2]);
            return true;
        }
        else if (attrName.equals("hiStop1"))
        {
            segment.hiStop[0] = Float.parseFloat(attrValue);
            return true;
        }
        else if (attrName.equals("hiStop2"))
        {
            segment.hiStop[1] = Float.parseFloat(attrValue);
            return true;
        }
        else if (attrName.equals("hiStop3"))
        {
            segment.hiStop[2] = Float.parseFloat(attrValue);
            return true;
        }
        else if (attrName.equals("loStop1"))
        {
            segment.loStop[0] = Float.parseFloat(attrValue);
            return true;
        }
        else if (attrName.equals("loStop2"))
        {
            segment.loStop[1] = Float.parseFloat(attrValue);
            return true;
        }
        else if (attrName.equals("loStop3"))
        {
            segment.loStop[2] = Float.parseFloat(attrValue);
            return true;
        }
        else if (attrName.equals("position"))
        {
            String[] vals = attrValue.split(" ");
            float pos[] = new float[3];
            pos[0] = Float.parseFloat(vals[0]);
            pos[1] = Float.parseFloat(vals[1]);
            pos[2] = Float.parseFloat(vals[2]);
            segment.box.setTranslation(pos);
        }
        else if (attrName.equals("I"))
        {
            String[] vals = attrValue.split(" ");
            float I[] = new float[9];
            for (int i = 0; i < 9; i++)
            {
                I[i] = Float.parseFloat(vals[i]);
            }
            segment.box.setInertiaTensor(I);
        }
        return false;
    }

    /**
     * decodes the value from an attribute value String returns true if succesful, returns false for attribute names that are not recognized. Might
     * throw a RuntimeException when an attribute has been recognized, but is ill formatted. Moreover, an XMLTokenizer reference is available which
     * can be queried for attributes, like getTokenLine() or getTokenCharPos(), which might be helpful to produce error messages referring to
     * lines/positions within the XML document
     */
    @Override
    public boolean decodeAttribute(String attrName, String attrValue, XMLTokenizer tokenizer)
    {
        return decodeAttribute(attrName, attrValue);
    }

    /**
     * decodes the XML contents, i.e. the XML between the STag and ETag of the encoding.
     */
    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws java.io.IOException
    {

        while (tokenizer.atSTag())
        {
            if (tokenizer.atSTag("CollisionModel"))
            {
                CollisionModel cm = new CollisionModel(segment.box);
                cm.readXML(tokenizer);
            }
        }
    }

    /**
     * Appends a String to buf that encodes the contents for the XML encoding. The encoding should start on a new line, using indentation equal to
     * tab. There should be no newline after the encoding.
     */
    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        appendNewLine(buf);
        CollisionModel cm = new CollisionModel(segment.box);
        cm.appendXML(buf, fmt);
        return buf;
    }

    /**
     * Appends a String to buf that encodes the attributes for the XML encoding. When non empty, the attribute string should start with a space
     * character. Hint: call the appendAttribute(StringBuffer buf, String attrName, String attrValue) for every relevant attribute; this takes care of
     * the leading space as well as spaces in between the attributes) The encoding should preferably not add newline characters.
     */
    @Override
    public StringBuilder appendAttributeString(StringBuilder buf)
    {
        appendAttribute(buf, "startJoint", startJoint.getSid());
        StringBuffer str = new StringBuffer();
        for (VJoint j : endJoints)
        {
            str.append(" " + j.getSid());
        }
        appendAttribute(buf, "endJoint", str.substring(1)); // cut first ","
        appendAttribute(buf, "mass", segment.box.getMass());
        float pos[] = new float[3];
        segment.box.getTranslation(pos);
        appendAttribute(buf, "position", pos[0] + " " + pos[1] + " " + pos[2]);
        float I[] = new float[9];
        segment.box.getInertiaTensor(I);
        str = new StringBuffer("" + I[0]);
        for (int i = 1; i < 9; i++)
        {
            str.append(" " + I[i]);
        }
        appendAttribute(buf, "I", str.toString());

        if (!isRoot())
        {
            appendAttribute(buf, "type", segment.jointType.getTypeAsString());
            appendAttribute(buf, "axis1", segment.axis1[0] + " " + segment.axis1[1] + " " + segment.axis1[2]);
            appendAttribute(buf, "axis2", segment.axis2[0] + " " + segment.axis2[1] + " " + segment.axis2[2]);

            for (int i = 0; i < 3; i++)
            {
                if (segment.loStop[i] > -Math.PI)
                {
                    appendAttribute(buf, "loStop" + (i + 1), segment.loStop[i]);
                }
                if (segment.hiStop[i] < Math.PI)
                {
                    appendAttribute(buf, "hiStop" + (i + 1), segment.hiStop[i]);
                }
            }
        }
        return buf;
    }

    /**
     * returns the XML tag that is used to encode this type of XMLStructure.
     */
    @Override
    public String getXMLTag()
    {
        if (isRoot)
        {
            return "PhysicalRootSegment";
        }
        return "PhysicalSegment";
    }

    public boolean isRoot()
    {
        return isRoot;
    }

    public void setRoot(boolean isRoot)
    {
        this.isRoot = isRoot;
    }
}

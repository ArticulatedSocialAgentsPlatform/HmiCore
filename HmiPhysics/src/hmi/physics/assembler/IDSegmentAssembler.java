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
import hmi.math.Mat3f;
import hmi.math.Vec3f;
import hmi.physics.Mass;
import hmi.physics.PhysicalSegment;
import hmi.physics.inversedynamics.IDSegment;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Constructs an IDSegment
 * @author Herwin
 */
public class IDSegmentAssembler extends XMLStructureAdapter 
{
    public VJoint startJoint;
    private VJoint human;
    public IDSegment segment;
    public List<VJoint> endJoints = new ArrayList<VJoint>();
    private boolean isRoot = false;
    public float pos[]=new float[3];
    private Logger logger = LoggerFactory.getLogger(IDSegmentAssembler.class.getName());
    
    /**
     * Create a IDSegment
     * @param h humanoid
     * @param seg segment
     */
    public IDSegmentAssembler(VJoint h, IDSegment seg)
    {
        human = h;
        segment = seg;
    }
    
    private GNode findStartNode(GNode rootNode)
    {
        GNode n = null;
        if(startJoint.getSid().equals(rootNode.getSid()))
        {
            return rootNode;
        }
        else
        {
            for(GNode gn:rootNode.getChildNodes ())
            {
                n = findStartNode(gn);
                if(n!=null)
                {
                    return n;
                }
            }
        }
        return null;
    }
    
    public void createFromGNode(GNode rootNode,String name)
    {
        startJoint = human.getPart(name);
        segment.name = name;
        //System.out.println("Creating IDSegment with name "+name);
        for(VJoint vj : startJoint.getChildren())
        {
            endJoints.add(vj);
        }
        
        GNode node = findStartNode(rootNode);
        
        float totalMass = 0;
        Mass totalM = null;
        float cTotal[]=new float[3];
        float c2[] = new float[3];
        float c[]=new float[3];
        float I[]=new float[9];
        float jTrans[] = new float[3];
        
        float density = BodyDensities.bodyDensityMap.get(name);
        List<GShape> shapes = node.getGShapes();
        for (GShape s:shapes)
        {
            Mass m = segment.createMass();
            if(s.getGMesh().getVCountData()!=null)
            {
                s.getGMesh().triangulate();
            }                
            m.setFromGMesh(s.getGMesh(), density);
            m.getCOM(c);            
            m.translate(-c[0], -c[1], -c[2]);
            Vec3f.add(c,jTrans);
                
            if(totalM == null)
            {
                totalM = m;
                Vec3f.set(cTotal, c);
            }
            else
            {
                //System.out.println("Adding mass for "+node.getName());
                totalM.addMass(m, cTotal, c, c2);
                Vec3f.set(cTotal, c2);                    
            }
            totalMass+=m.getMass();            
        }
        segment.mass = totalMass;
        //segment.box.adjustMass(totalMass);
        totalM.getInertiaTensor(I);
        Mat3f.set(segment.I,I);
        startJoint.getPathTranslation(null, pos);
        Vec3f.add(pos, cTotal);
        //System.out.println("segment "+ name +" pos: "+Vec3f.toString(pos));        
    }
    
    public void createFromPhysicalSegment(PhysicalSegment ps, List<VJoint>endJoints)
    {
        //startJoint
        startJoint = human.getPartBySid(ps.getSid());
        segment.name = startJoint.getSid();
        
        //no endJoints set        
        this.endJoints = endJoints;
        
        //mass
        segment.mass = ps.box.getMass();
        
        //I
        ps.box.getInertiaTensor(segment.I);
        
        //position
        ps.box.getCOM(segment.com);
        
        
    }
    /*
     * ------------------------------------------------------------------------------------------------
     *                                  XML parsing part
     * ------------------------------------------------------------------------------------------------
     */
    @Override
    public boolean decodeAttribute(String attrName, String attrValue) 
    {
        
        if(attrName.equals("startJoint"))
        {
            startJoint = human.getPart(attrValue);
            if (startJoint == null) 
            {
                throw new RuntimeException("Startjoint not found in human: " + attrValue);                
            }
            String n = startJoint.getSid();
            if (n==null)n = startJoint.getName();
            segment.name = n;
            return true;
        }
        else if (attrName.equals("endJoint"))
        {
            String[] joint = attrValue.split(" ");
            for(int i=0;i<joint.length;i++)
            {
                VJoint endJoint = human.getPart(joint[i]);
                if (endJoint == null) 
                {
                    logger.warn("Null endjoint: {}",joint[i]);
                }
                else
                {
                    endJoints.add(endJoint);                
                }
            }           
            return true;
        }
        else if(attrName.equals("mass"))
        {
            segment.mass = Float.parseFloat(attrValue);
            return true;
        }
        /* disabled for now, all segments are balljoints
        else if(attrName.equals("type"))
        {
            if(attrValue.equals("fixed"))
            {
                segment.jointType=JointType.FIXED;
            }
            else if(attrValue.equals("hinge"))
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
        else if(attrName.equals("axis1"))
        {
            String[] vals = attrValue.split(" ");
            segment.axis1[0] = Float.parseFloat(vals[0]);
            segment.axis1[1] = Float.parseFloat(vals[1]);
            segment.axis1[2] = Float.parseFloat(vals[2]);           
            return true;
        }
        else if(attrName.equals("axis2"))
        {
            String[] vals = attrValue.split(" ");
            segment.axis2[0] = Float.parseFloat(vals[0]);
            segment.axis2[1] = Float.parseFloat(vals[1]);
            segment.axis2[2] = Float.parseFloat(vals[2]);
            return true;
        }
        */                
        else if(attrName.equals("position"))
        {
            //position of the CoM in world coordinates
            
            String[] vals = attrValue.split(" ");
            //float pos[]=new float[3];
            pos[0] = Float.parseFloat(vals[0]);
            pos[1] = Float.parseFloat(vals[1]);
            pos[2] = Float.parseFloat(vals[2]);
            //segment.box.setPosition(pos);
        }
        else if(attrName.equals("I"))
        {
            String[] vals = attrValue.split(" ");
            for(int i=0;i<9;i++)
            {
                segment.I[i] = Float.parseFloat(vals[i]);
            }            
        }
        return false;
    }
    
    
    @Override
    public boolean decodeAttribute(String attrName, String attrValue, XMLTokenizer tokenizer) 
    { 
        return decodeAttribute(attrName, attrValue);
    }

    @Override
    public StringBuilder appendAttributeString(StringBuilder buf) 
    {
        appendAttribute(buf, "startJoint", startJoint.getSid());
        StringBuffer str = new StringBuffer("");
        for(VJoint j:endJoints)
        {
            str.append(" "+j.getName());
        }
        if(!str.toString().equals(""))
        {
            appendAttribute(buf, "endJoint", str.substring(1)); //cut first ","     
        }
        
        
        appendAttribute(buf, "mass", segment.mass);
        appendAttribute(buf, "position", pos[0]+" "+pos[1]+" "+pos[2]);
        float I[]=new float[9];            
        str = new StringBuffer(""+I[0]);            
        for(int i=1;i<9;i++)
        {
            str.append(" " + segment.I[i]);
        }
        appendAttribute(buf, "I",str.toString());
        /* disabled for now, all IDJoints are ball joints
        appendAttribute(buf, "type", segment.jointType.getTypeAsString());
        appendAttribute(buf, "axis1", segment.axis1[0]+" "+segment.axis1[1]+" "+segment.axis1[2]);
        appendAttribute(buf, "axis2", segment.axis2[0]+" "+segment.axis2[1]+" "+segment.axis2[2]);
        */
        return buf;
    }   
    
    @Override
    public String getXMLTag()
    {
        if(isRoot)
        {
            return "IDRootSegment";
        }
        return "IDSegment";               
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }
}

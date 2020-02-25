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
import hmi.math.Vec3f;
import hmi.physics.inversedynamics.IDBranch;
import hmi.physics.inversedynamics.IDSegment;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.util.ArrayList;

/**
 * Constructs a branch of ID joints
 * @author Herwin
 */
public class IDBranchAssembler extends XMLStructureAdapter
{
    private VJoint human;
    public IDBranch branch;
    
    public IDSegmentAssembler rootAssembler;
    public ArrayList<IDSegmentAssembler> segmentAssemblers = new ArrayList<IDSegmentAssembler>(); 
    
    /**
     * Constructor
     * @param h human to get link sizes from
     * @param b branch to parse
     */
    public IDBranchAssembler(VJoint h,IDBranch b)
    {
        human = h;
        branch = b;        
    }
    
    /**
     * Adds a new PhysicalSegmentAssembler, assumes psa has a valid segment attached
     */
    public void addPhysicalSegmentAssembler(IDSegmentAssembler psa)
    {
        segmentAssemblers.add(psa);
    }
    
    /**
     * Sets the root assembler
     */
    public void setRootSegmentAssembler(IDSegmentAssembler psa)
    {
        rootAssembler = psa;
        branch.setRoot(rootAssembler.segment);        
    }
    
    private void setCoM(IDSegmentAssembler psa)
    {
        float jPos[] = new float[3];
        psa.startJoint.getPathTranslation(null, jPos);
        Vec3f.sub(psa.segment.com, psa.pos,jPos);        
    }
    
    private void setupSegments(IDSegmentAssembler psa, float[] jPos)
    {
        for(VJoint vo:psa.endJoints)
        {
            for(IDSegmentAssembler sa:segmentAssemblers)
            {
                if(vo==sa.startJoint)
                {
                    setCoM(sa);
                    float jNewPos[] = new float[3];
                    sa.startJoint.getPathTranslation(null, jNewPos);
                    Vec3f.sub(sa.segment.translation,jNewPos,jPos);                    
                    psa.segment.addChild(sa.segment);
                    setupSegments(sa,jNewPos);
                }
            }
        }
    }
    
    /**
     * Set CoM and translation of the segments
     */
    public void setup()
    {
        Vec3f.set(rootAssembler.segment.translation,0,0,0);
        setCoM(rootAssembler);
        float jPos[] = new float[3];
        rootAssembler.startJoint.getPathTranslation(null, jPos);
        branch.setRoot(rootAssembler.segment);        
        setupSegments(rootAssembler,jPos);        
    }
    
    
    /*
     * ------------------------------------------------------------------------------------------------
     *                                  XML parsing part
     * ------------------------------------------------------------------------------------------------
     */
    @Override
    public boolean decodeAttribute(String attrName, String attrValue) 
    {
        //no attributes
        return false;
    }
        
    @Override
    public boolean decodeAttribute(String attrName, String attrValue, XMLTokenizer tokenizer) 
    { 
        return decodeAttribute(attrName, attrValue);
    }

    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws java.io.IOException  
    {   
        while (tokenizer.atSTag())
        {
            if (tokenizer.atSTag("IDSegment"))
            {
                IDSegment ps = new IDSegment();
                IDSegmentAssembler psa = new IDSegmentAssembler(human,ps);                
                psa.readXML(tokenizer);
                addPhysicalSegmentAssembler(psa);                   
            }        
            else if (tokenizer.atSTag("IDRootSegment"))
            {
                IDSegment ps = new IDSegment();
                IDSegmentAssembler psa = new IDSegmentAssembler(human,ps);
                psa.setRoot(true);
                psa.readXML(tokenizer);
                setRootSegmentAssembler(psa);                    
            }
        }       
    }  
          
    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt) 
    {
        appendNewLine(buf);
        if(rootAssembler!=null)
        {
            rootAssembler.appendXML(buf,fmt);
        }
        for(IDSegmentAssembler ps:segmentAssemblers)
        {
            appendNewLine(buf);
            ps.appendXML(buf,fmt);
        }
        return buf;
    }

    @Override
    public StringBuilder appendAttributeString(StringBuilder buf) 
    {
        //no attributes
        return buf;
    }   
        
    @Override
    public String getXMLTag()
    {
        return "IDBranch";
    }
}

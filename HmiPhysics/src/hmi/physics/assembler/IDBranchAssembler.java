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

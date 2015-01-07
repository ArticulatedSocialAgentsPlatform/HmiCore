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

import hmi.animation.Hanim;
import hmi.animation.VJoint;
import hmi.graphics.scenegraph.GNode;
import hmi.physics.JointType;
import hmi.physics.PhysicalHumanoid;
import hmi.physics.PhysicalSegment;
import hmi.physics.inversedynamics.IDBranch;
import hmi.physics.inversedynamics.IDSegment;
import hmi.physics.mixed.Connector;
import hmi.physics.mixed.MixedSystem;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * Utilities to construct a MixedSystem from XML, from a GNode or by hand.
 * @author Herwin
 */
@Slf4j
public class MixedSystemAssembler extends XMLStructureAdapter
{
    public PhysicalHumanoidAssembler pha;    
    private List<IDBranchAssembler> idbas = new ArrayList<IDBranchAssembler>();    
    private VJoint human;
    private MixedSystem ms;
    /**
     * Constructor
     * @param h the visualization humanoid
     * @param p the PhysicalHumanoid
     * @param m the system
     */
    public MixedSystemAssembler(VJoint h,PhysicalHumanoid p, MixedSystem m)
    {
        human = h;
        ms = m;
        pha = new PhysicalHumanoidAssembler(h,p);
    }
    
    public void addBranchAssembler(IDBranchAssembler assembler)
    {
        idbas.add(assembler);
    }
    
    private void addIDSegments(VJoint startJoint, GNode rootNode,IDBranchAssembler ba)
    {
        for(VJoint vj: startJoint.getChildren())
        {
            IDSegment is = ms.createIDSegment();
            IDSegmentAssembler psa = new IDSegmentAssembler(human,is);
            
            psa.createFromGNode(rootNode, vj.getSid());
            ba.addPhysicalSegmentAssembler(psa);
            addIDSegments(vj, rootNode, ba);
        }
    }
    
    public IDBranchAssembler createBranchFromGNode(GNode rootNode, String idStartSegment)
    {
        IDBranch b = new IDBranch();
        IDSegment ps = ms.createIDSegment();
        IDSegmentAssembler psa = new IDSegmentAssembler(human,ps);
        psa.setRoot(true);
        
        psa.createFromGNode(rootNode, idStartSegment);
        IDBranchAssembler idba = new IDBranchAssembler(human, b);
        idba.setRootSegmentAssembler(psa);
        
        VJoint vj = human.getPart(idStartSegment);
        addIDSegments(vj,rootNode, idba);
        
        return idba;
    }
    
    public void createFromGNode(GNode node, String[] fdSegments, String rootSegment,
            JointType[] types, float axes[], float limits1[], float limits2[], float limits3[],
            String[] idSegments)
    {
        pha.createFromGNode(node, fdSegments, idSegments, rootSegment, types, axes, limits1, limits2, limits3);
        for(int i=0;i<idSegments.length;i++)
        {
            idbas.add(createBranchFromGNode(node, idSegments[i]));
        }        
    }
    /**
     * Constructs the parsed system
     */
    public void setup()
    {
        pha.setupJoints(Hanim.HumanoidRoot);
        
        float p[]=new float[3];
        for(IDBranchAssembler idba:idbas)
        {
            idba.setup();
            idba.branch.setupSolver();
            
            idba.rootAssembler.startJoint.getPathTranslation(null, p);
            PhysicalSegment ps = null;
            if(pha.rootAssembler.endJoints.contains(idba.rootAssembler.startJoint))
            {
                ps = pha.rootAssembler.segment;
            }
            else
            {
                for(PhysicalSegmentAssembler psa:pha.segmentAssemblers)
                {
                    if(psa.endJoints.contains(idba.rootAssembler.startJoint))
                    {
                        ps = psa.segment;
                    }
                }
            }    
            if(ps!=null)
            {
                Connector c = new Connector(ps, p, 1);                
                ms.addBranch(idba.branch,c);
            }
            else
            {
                log.warn("Can't find PhysicalSegment {}",idba.rootAssembler.startJoint);            
            }
        }
        ms.setup();
    }
    
    /*
     * ------------------------------------------------------------------------------------------------
     *                                  XML parsing part
     * ------------------------------------------------------------------------------------------------
     */
    /**
     * decodes the value from an attribute value String
     * returns true if succesful, returns false for attribute names
     * that are not recognized. Might throw a RuntimeException when
     * an attribute has been recognized, but is ill formatted.
     */
    @Override
    public boolean decodeAttribute(String attrName, String attrValue) 
    {
        //no attributes
        return false;
    }
        
    /**
     * decodes the value from an attribute value String
     * returns true if succesful, returns false for attribute names
     * that are not recognized. Might throw a RuntimeException when
     * an attribute has been recognized, but is ill formatted.
     * Moreover, an  XMLTokenizer reference is available which can be queried
     * for attributes, like getTokenLine() or getTokenCharPos(), which might be helpful
     * to produce error messages referring to lines/positions within the XML document
     */
    @Override
    public boolean decodeAttribute(String attrName, String attrValue, XMLTokenizer tokenizer) 
    { 
        return decodeAttribute(attrName, attrValue);
    }

        
    
    /**
     * decodes the XML contents, i.e. the XML between the STag and ETag
     * of the encoding.
     */
    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws java.io.IOException  
    {   
        while (tokenizer.atSTag())
        {
            if (tokenizer.atSTag("PhysicalHumanoid"))
            {
                pha.readXML(tokenizer);
                log.debug("Finished parsing physical humanoid");
            }        
            else if (tokenizer.atSTag("IDBranch"))
            {
                IDBranch b = new IDBranch();
                IDBranchAssembler idba = new IDBranchAssembler(human, b);
                idba.readXML(tokenizer);
                idbas.add(idba);
                log.debug("Finished parsing physical branch with root {}",b.getRoot().name);
                
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
        pha.appendXML(buf,fmt);
        appendNewLine(buf);
        for(IDBranchAssembler idba:idbas)
        {
            idba.appendXML(buf, fmt);
            appendNewLine(buf);
        }        
        return buf;
    }

    /**
     * Appends a String to buf that encodes the attributes for the XML encoding.
     * When non empty, the attribute string should start with a space character.
     * Hint: call the appendAttribute(StringBuffer buf, String attrName, String attrValue)
     * for every relevant attribute; this takes care of the leading space as well as spaces 
     * in between the attributes)
     * The encoding should preferably not add newline characters.
     */
    @Override
    public StringBuilder appendAttributeString(StringBuilder buf) 
    {
        //no attributes
        return buf;
    }   
        
    /**
     * returns the XML tag that is used to encode this type of XMLStructure.
     */
    @Override
    public String getXMLTag()
    {
        return "MixedSystem";
    }           
}

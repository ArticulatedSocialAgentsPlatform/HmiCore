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

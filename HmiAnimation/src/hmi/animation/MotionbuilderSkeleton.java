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
package hmi.animation;

import hmi.math.Vec3f;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;

/**
 * XML representation of a VJoint MotionbuilderSkeleton.
 * Captures only its translations and sid-s. 
 * @author Herwin
 *
 */
public class MotionbuilderSkeleton extends XMLStructureAdapter
{
    private final VJoint rootJoint;
    
    private static class VJointXML extends XMLStructureAdapter
    {
        private final VJoint joint;
        public VJointXML(VJoint v)
        {
            joint = v;
        }
        
        @Override
        public StringBuilder appendAttributeString(StringBuilder buf) 
        {     
           appendAttribute(buf, "sid", joint.getSid());
           float trans[]=Vec3f.getVec3f();
           joint.getTranslation(trans);
           appendAttribute(buf, "translation", trans[0]+" "+trans[1]+" "+trans[2]);
           return buf;
        }
        
        public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
        {
            for(VJoint vj:joint.getChildren())
            {
                VJointXML vjXML = new VJointXML(vj);
                vjXML.appendXML(buf,fmt);
            }
            return buf;
        }    
        
        
        private static final String XMLTAG = "Joint";
        
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
    }
    
    public MotionbuilderSkeleton(VJoint root)
    {
        rootJoint = root;
    }
    
    public StringBuilder appendJoint(VJoint v,StringBuilder buf)
    {
        
        return buf;
    }
    
    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        VJointXML vjXML = new VJointXML(rootJoint);
        return vjXML.appendXML(buf, fmt);        
    }
    
    private static final String XMLTAG = "MotionbuilderSkeleton";
    
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
}

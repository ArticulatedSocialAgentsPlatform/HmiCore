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

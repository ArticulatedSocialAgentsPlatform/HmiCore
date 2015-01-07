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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * a convertor for renaming the sids of all joints in an avatar to Hanim standard, from sources such as 3ds max joint names
 * @author dennisr
 */
public final class JointSidConverter
{
    private JointSidConverter(){}
    private static Logger logger = LoggerFactory.getLogger(JointSidConverter.class.getName());
    
    /**
    The joints in a default collada export of 3dsmax have SIDs "Bone<nr>", but I am not sure whether these numbers mean anything.
    Therefore I rely on the names, which have a format of <prefix><jointname> -- I remove the prefix, take the remainder, and look it up in a list;
    take the found hanim name and assign it to the joint's sid
    
    NOTE: method not yet complete, not all conversions have been added yet
    */
    public static void rename3DSMaxIdsToHanimSids(VJoint theJoint, String prefix)
    {
        String jointName = theJoint.getName();
        String name = "";
        String newSid = "";
        if (jointName.startsWith(prefix)) {
            name = jointName.substring(prefix.length()).toLowerCase();
            newSid = theJoint.getSid(); //don't change sid if it is not in the list...
            
            if (name.equals("pelvis"))      newSid = Hanim.sacroiliac;
            if (name.equals("spine"))       newSid = Hanim.vl5;
            if (name.equals("head"))        newSid = Hanim.skullbase;

            if (name.equals("l_thigh"))     newSid = Hanim.l_hip;
            if (name.equals("l_calf"))      newSid = Hanim.l_knee;
            if (name.equals("l_foot"))      newSid = Hanim.l_ankle;
            if (name.equals("l_toe0"))      newSid = Hanim.l_metatarsal;
            if (name.equals("r_thigh"))     newSid = Hanim.r_hip;
            if (name.equals("r_calf"))      newSid = Hanim.r_knee;
            if (name.equals("r_foot"))      newSid = Hanim.r_ankle;
            if (name.equals("r_toe0"))      newSid = Hanim.r_metatarsal;

            if (name.equals("l_clavicle"))  newSid = Hanim.l_sternoclavicular;
            if (name.equals("l_scapula"))   newSid = Hanim.l_acromioclavicular;
            if (name.equals("l_upperarm"))  newSid = Hanim.l_shoulder;
            if (name.equals("l_forearm"))   newSid = Hanim.l_elbow;
            if (name.equals("l_hand"))      newSid = Hanim.l_wrist;
            //if (name.equals("l_finger0"))   newSid = Hanim.l_thumb0;
            if (name.equals("l_finger01"))  newSid = Hanim.l_thumb1;
            if (name.equals("l_finger02"))  newSid = Hanim.l_thumb2;
            if (name.equals("l_finger0nub"))newSid = Hanim.l_thumb3;
            if (name.equals("l_finger1"))   newSid = Hanim.l_index0;
            if (name.equals("l_finger11"))  newSid = Hanim.l_index1;
            if (name.equals("l_finger12"))  newSid = Hanim.l_index2;
            if (name.equals("l_finger1nub"))newSid = Hanim.l_index3;
            if (name.equals("l_finger2"))   newSid = Hanim.l_middle0;
            if (name.equals("l_finger21"))  newSid = Hanim.l_middle1;
            if (name.equals("l_finger22"))  newSid = Hanim.l_middle2;
            if (name.equals("l_finger2nub"))newSid = Hanim.l_middle3;
            if (name.equals("l_finger3"))   newSid = Hanim.l_ring0;
            if (name.equals("l_finger31"))  newSid = Hanim.l_ring1;
            if (name.equals("l_finger32"))  newSid = Hanim.l_ring2;
            if (name.equals("l_finger3nub"))newSid = Hanim.l_ring3;
            if (name.equals("l_finger4"))   newSid = Hanim.l_pinky0;
            if (name.equals("l_finger41"))  newSid = Hanim.l_pinky1;
            if (name.equals("l_finger42"))  newSid = Hanim.l_pinky2;
            if (name.equals("l_finger4nub"))newSid = Hanim.l_pinky3;

            if (name.equals("r_clavicle"))  newSid = Hanim.r_sternoclavicular;
            if (name.equals("r_scapula"))   newSid = Hanim.r_acromioclavicular;
            if (name.equals("r_upperarm"))  newSid = Hanim.r_shoulder;
            if (name.equals("r_forearm"))   newSid = Hanim.r_elbow;
            if (name.equals("r_hand"))      newSid = Hanim.r_wrist;
            //if (name.equals("r_finger0"))   newSid = Hanim.r_thumb0;
            if (name.equals("r_finger01"))  newSid = Hanim.r_thumb1;
            if (name.equals("r_finger02"))  newSid = Hanim.r_thumb2;
            if (name.equals("r_finger0nub"))newSid = Hanim.r_thumb3;
            if (name.equals("r_finger1"))   newSid = Hanim.r_index0;
            if (name.equals("r_finger11"))  newSid = Hanim.r_index1;
            if (name.equals("r_finger12"))  newSid = Hanim.r_index2;
            if (name.equals("r_finger1nub"))newSid = Hanim.r_index3;
            if (name.equals("r_finger2"))   newSid = Hanim.r_middle0;
            if (name.equals("r_finger21"))  newSid = Hanim.r_middle1;
            if (name.equals("r_finger22"))  newSid = Hanim.r_middle2;
            if (name.equals("r_finger2nub"))newSid = Hanim.r_middle3;
            if (name.equals("r_finger3"))   newSid = Hanim.r_ring0;
            if (name.equals("r_finger31"))  newSid = Hanim.r_ring1;
            if (name.equals("r_finger32"))  newSid = Hanim.r_ring2;
            if (name.equals("r_finger3nub"))newSid = Hanim.r_ring3;
            if (name.equals("r_finger4"))   newSid = Hanim.r_pinky0;
            if (name.equals("r_finger41"))  newSid = Hanim.r_pinky1;
            if (name.equals("r_finger42"))  newSid = Hanim.r_pinky2;
            if (name.equals("r_finger4nub"))newSid = Hanim.r_pinky3;
            
            //if (name.equals(""))  newSid = Hanim.;
            //if (name.equals(""))  newSid = Hanim.;
            //if (name.equals(""))  newSid = Hanim.;
            //if (name.equals(""))  newSid = Hanim.;
            //if (name.equals(""))  newSid = Hanim.;
            
            theJoint.setSid(newSid);
            logger.debug("{} becomes {} becomes {}",new Object[]{jointName,name,newSid});
        }
        //recurse...
        for (VJoint child: theJoint.getChildren())
        {
            rename3DSMaxIdsToHanimSids(child, prefix);
        }
    }
          


}
    

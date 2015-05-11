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

import java.util.HashMap;
/**
 * Human joint limits (average values from US air force personal), taken from:<br>
 * Wesley E. Woodson, Barry Tillman, and Peggy Tillman. Human Factors Design Handbook. McGraw-Hill, 1992.
 * @author Herwin
 */
public final class JointLimits
{
    private JointLimits(){}
    public final static class JointAxisMap  extends HashMap<String,String> 
    {   
        private static final long serialVersionUID = -6072973192095638022L;
        public JointAxisMap()
        {
            put( "l_wrist","XZY");
            put( "r_wrist","XZY");
            put( "l_elbow","XYZ");
            put( "r_elbow","XYZ");
            put( "l_shoulder","XYZ");
            put( "r_shoulder","XYZ");
            put( "vc4","XYZ");
            put( "skullbase","XYZ");
            put( "l_ankle", "XZY");
            put( "r_ankle", "XZY");
            put( "l_knee", "XYZ");
            put( "r_knee", "XYZ");
            put( "l_hip", "XZY");
            put( "r_hip", "XZY");                                    
        }
    }
    
    public final static class JointLimitMinXMale  extends HashMap<String,Float> 
    {   
        private static final long serialVersionUID = -7869849989299889772L;
        public JointLimitMinXMale()
        {
            put( "l_wrist",-27f);
            put( "r_wrist",-27f);
            put( "l_elbow",-142f);
            put( "r_elbow",-142f);
            put( "l_shoulder",-188f);
            put( "r_shoulder",-188f);
            put( "vc4",-60f);
            put( "skullbase",-60f);
            put( "l_ankle", -38f);
            put( "r_ankle", -38f);
            put( "l_knee", 0f);
            put( "r_knee", 0f);
            put( "l_hip", -113f);
            put( "r_hip", -113f);                                    
        }
    }
    
    public final static class JointLimitMinXFemale  extends HashMap<String,Float> 
    {   
        private static final long serialVersionUID = -7869849989299889772L;
        public JointLimitMinXFemale()
        {
            put( "l_wrist",-38f);
            put( "r_wrist",-38f);
            put( "l_elbow",-150f);
            put( "r_elbow",-150f);
            put( "l_shoulder",-188f);
            put( "r_shoulder",-188f);
            put( "vc4",-60f);
            put( "skullbase",-60f);
            put( "l_ankle", -42f);
            put( "r_ankle", -42f);
            put( "l_knee", 0f);
            put( "r_knee", 0f);
            put( "l_hip", -116f);
            put( "r_hip", -116f);                                    
        }
    }
    
    public final static class JointLimitMaxXMale  extends HashMap<String,Float> 
    {   
        private static final long serialVersionUID = -7869849989299889772L;
        public JointLimitMaxXMale()
        {
            put( "l_wrist",47f);
            put( "r_wrist",-47f);
            put( "l_elbow",0f);
            put( "r_elbow",0f);
            put( "l_shoulder",61f);
            put( "r_shoulder",61f);
            put( "vc4",61f);
            put( "skullbase",61f);
            put( "l_ankle", 35f);
            put( "r_ankle", 35f);
            put( "l_knee", 113f);
            put( "r_knee", 113f);
            put( "l_hip", 0f);
            put( "r_hip", 0f);                                    
        }
    }
    
    public final static class JointLimitMaxXFemale  extends HashMap<String,Float> 
    {   
        private static final long serialVersionUID = -7869849989299889772L;
        public JointLimitMaxXFemale()
        {
            put( "l_wrist",58f);
            put( "r_wrist",58f);
            put( "l_elbow",0f);
            put( "r_elbow",0f);
            put( "l_shoulder",61f);
            put( "r_shoulder",61f);
            put( "vc4",61f);
            put( "skullbase",61f);
            put( "l_ankle", 39f);
            put( "r_ankle", 39f);
            put( "l_knee", 113f);
            put( "r_knee", 113f);
            put( "l_hip", 0f);
            put( "r_hip", 0f);                                    
        }
    }
    
    public final static class JointLimitMinYMale  extends HashMap<String,Float> 
    {   
        private static final long serialVersionUID = -7869849989299889772L;
        public JointLimitMinYMale()
        {
            put( "l_wrist",0f);
            put( "r_wrist",0f);
            put( "l_elbow",0f);
            put( "r_elbow",0f);
            put( "l_shoulder",-80f);
            put( "r_shoulder",-80f);
            put( "vc4",-79f);
            put( "skullbase",-79f);
            put( "l_ankle", 0f);
            put( "r_ankle", 0f);
            put( "l_knee", 0f);
            put( "r_knee", 0f);
            put( "l_hip", -31f);
            put( "r_hip", -30f);                                    
        }
    }
    
    public final static class JointLimitMinYFemale  extends HashMap<String,Float> 
    {   
        private static final long serialVersionUID = -7869849989299889772L;
        public JointLimitMinYFemale()
        {
            put( "l_wrist",0f);
            put( "r_wrist",0f);
            put( "l_elbow",0f);
            put( "r_elbow",0f);
            put( "l_shoulder",80f);
            put( "r_shoulder",80f);
            put( "vc4",-79f);
            put( "skullbase",-79f);
            put( "l_ankle", 0f);
            put( "r_ankle", 0f);
            put( "l_knee", 0f);
            put( "r_knee", 0f);
            put( "l_hip", -31f);
            put( "r_hip", -30f);                                    
        }
    }
    
    public final static class JointLimitMaxYMale  extends HashMap<String,Float> 
    {   
        private static final long serialVersionUID = -7869849989299889772L;
        public JointLimitMaxYMale()
        {
            put( "l_wrist",0f);
            put( "r_wrist",0f);
            put( "l_elbow",0f);
            put( "r_elbow",0f);
            put( "l_shoulder",80f);
            put( "r_shoulder",80f);
            put( "vc4",79f);
            put( "skullbase",79f);
            put( "l_ankle", 0f);
            put( "r_ankle", 0f);
            put( "l_knee", 0f);
            put( "r_knee", 0f);
            put( "l_hip", 30f);
            put( "r_hip", 31f);                                    
        }
    }
    
    public final static class JointLimitMaxYFemale  extends HashMap<String,Float> 
    {   
        private static final long serialVersionUID = -7869849989299889772L;
        public JointLimitMaxYFemale()
        {
            put( "l_wrist",0f);
            put( "r_wrist",0f);
            put( "l_elbow",0f);
            put( "r_elbow",0f);
            put( "l_shoulder",80f);
            put( "r_shoulder",80f);
            put( "vc4",79f);
            put( "skullbase",79f);
            put( "l_ankle", 0f);
            put( "r_ankle", 0f);
            put( "l_knee", 0f);
            put( "r_knee", 0f);
            put( "l_hip", 30f);
            put( "r_hip", 31f);                                    
        }
    }
    
    public final static class JointLimitMinZMale  extends HashMap<String,Float> 
    {   
        private static final long serialVersionUID = -7869849989299889772L;
        public JointLimitMinZMale()
        {
            put( "l_wrist",-90f);
            put( "r_wrist",-81f);
            put( "l_elbow",0f);
            put( "r_elbow",0f);
            put( "l_shoulder",-48f);
            put( "r_shoulder",-134f);
            put( "vc4",-41f);
            put( "skullbase",-41f);
            put( "l_ankle", -24f);
            put( "r_ankle", -23f);
            put( "l_knee", 0f);
            put( "r_knee", 0f);
            put( "l_hip", -31f);
            put( "r_hip", -53f);                                    
        }
    }
    
    public final static class JointLimitMinZFemale  extends HashMap<String,Float> 
    {   
        private static final long serialVersionUID = -7869849989299889772L;
        public JointLimitMinZFemale()
        {
            put( "l_wrist",-104f);
            put( "r_wrist",-95f);
            put( "l_elbow",0f);
            put( "r_elbow",0f);
            put( "l_shoulder",-50f);
            put( "r_shoulder",-134f);
            put( "vc4",-41f);
            put( "skullbase",-41f);
            put( "l_ankle", -24f);
            put( "r_ankle", -23f);
            put( "l_knee", 0f);
            put( "r_knee", 0f);
            put( "l_hip", -31f);
            put( "r_hip", -53f);                                    
        }
    }
    
    public final static class JointLimitMaxZMale  extends HashMap<String,Float> 
    {   
        private static final long serialVersionUID = -7869849989299889772L;
        public JointLimitMaxZMale()
        {
            put( "l_wrist",81f);
            put( "r_wrist",90f);
            put( "l_elbow",0f);
            put( "r_elbow",0f);
            put( "l_shoulder",134f);
            put( "r_shoulder",48f);
            put( "vc4",41f);
            put( "skullbase",41f);
            put( "l_ankle", 23f);
            put( "r_ankle", 24f);
            put( "l_knee", 0f);
            put( "r_knee", 0f);
            put( "l_hip", 53f);
            put( "r_hip", 31f);                                    
        }
    }
    
    public final static class JointLimitMaxZFemale  extends HashMap<String,Float> 
    {   
        private static final long serialVersionUID = -7869849989299889772L;
        public JointLimitMaxZFemale()
        {
            put( "l_wrist",95f);
            put( "r_wrist",104f);
            put( "l_elbow",0f);
            put( "r_elbow",0f);
            put( "l_shoulder",134f);
            put( "r_shoulder",50f);
            put( "vc4",41f);
            put( "skullbase",41f);
            put( "l_ankle", 23f);
            put( "r_ankle", 24f);
            put( "l_knee", 0f);
            put( "r_knee", 0f);
            put( "l_hip", 53f);
            put( "r_hip", 31f);                                    
        }
    }
    
    public final static JointAxisMap jointAxisMap = new JointAxisMap();
    
    public final static JointLimitMinXMale jointLimitMinXMale = new JointLimitMinXMale();
    public final static JointLimitMinXFemale jointLimitMinXFemale = new JointLimitMinXFemale();
    public final static JointLimitMaxXMale jointLimitMaxXMale = new JointLimitMaxXMale();
    public final static JointLimitMaxXFemale jointLimitMaxXFemale = new JointLimitMaxXFemale();
    
    public final static JointLimitMinYMale jointLimitMinYMale = new JointLimitMinYMale();
    public final static JointLimitMinYFemale jointLimitMinYFemale = new JointLimitMinYFemale();
    public final static JointLimitMaxYMale jointLimitMaxYMale = new JointLimitMaxYMale();
    public final static JointLimitMaxYFemale jointLimitMaxYFemale = new JointLimitMaxYFemale();
    
    public final static JointLimitMinZMale jointLimitMinZMale = new JointLimitMinZMale();
    public final static JointLimitMinZFemale jointLimitMinZFemale = new JointLimitMinZFemale();
    public final static JointLimitMaxZMale jointLimitMaxZMale = new JointLimitMaxZMale();
    public final static JointLimitMaxZFemale jointLimitMaxZFemale = new JointLimitMaxZFemale();
}

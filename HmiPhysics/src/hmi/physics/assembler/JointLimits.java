/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
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

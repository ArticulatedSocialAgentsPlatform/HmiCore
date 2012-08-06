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
package hmi.animation;

/**
 * String's for all H-anim (www.h-anim.org) joints
 * 
 * @author welberge
 */
public final class Hanim
{
    private Hanim(){}
    // Note all .intern()s removed, since constant Strings are interned already.
    // root, legs, feet
    public static final String HumanoidRoot = "HumanoidRoot";
    public static final String sacroiliac = "sacroiliac";
    public static final String l_hip = "l_hip";
    public static final String l_knee = "l_knee";
    public static final String l_ankle = "l_ankle";
    public static final String l_subtalar = "l_subtalar";
    public static final String l_midtarsal = "l_midtarsal";
    public static final String l_metatarsal = "l_metatarsal";
    public static final String r_hip = "r_hip";
    public static final String r_knee = "r_knee";
    public static final String r_ankle = "r_ankle";
    public static final String r_subtalar = "r_subtalar";
    public static final String r_midtarsal = "r_midtarsal";
    public static final String r_metatarsal = "r_metatarsal";
    // spine
    public static final String vl5 = "vl5";
    public static final String vl4 = "vl4";
    public static final String vl3 = "vl3";
    public static final String vl2 = "vl2";
    public static final String vl1 = "vl1";
    public static final String vt12 = "vt12";
    public static final String vt11 = "vt11";
    public static final String vt10 = "vt10";
    public static final String vt9 = "vt9";
    public static final String vt8 = "vt8";
    public static final String vt7 = "vt7";
    public static final String vt6 = "vt6";
    public static final String vt5 = "vt5";
    public static final String vt4 = "vt4";
    public static final String vt3 = "vt3";
    public static final String vt2 = "vt2";
    public static final String vt1 = "vt1";
    public static final String vc7 = "vc7";
    public static final String vc6 = "vc6";
    public static final String vc5 = "vc5";
    public static final String vc4 = "vc4";
    public static final String vc3 = "vc3";
    public static final String vc2 = "vc2";
    public static final String vc1 = "vc1";
    public static final String skullbase = "skullbase";

    // face joints
    public static final String l_eyelid_joint = "l_eyelid_joint";
    public static final String r_eyelid_joint = "r_eyelid_joint";
    public static final String l_eyeball_joint = "l_eyeball_joint";
    public static final String r_eyeball_joint = "r_eyeball_joint";
    public static final String l_eyebrow_joint = "l_eyebrow_joint";
    public static final String r_eyebrow_joint = "r_eyebrow_joint";
    public static final String temporomandibular = "temporomandibular";

    // shoulder, arm, hand, fingers
    public static final String l_sternoclavicular = "l_sternoclavicular";
    public static final String l_acromioclavicular = "l_acromioclavicular";
    public static final String l_shoulder = "l_shoulder";
    public static final String l_elbow = "l_elbow";
    public static final String l_wrist = "l_wrist";
    public static final String l_thumb1 = "l_thumb1";
    public static final String l_thumb2 = "l_thumb2";
    public static final String l_thumb3 = "l_thumb3";
    public static final String l_thumb_distal_tip = "l_thumb_distal_tip";
    public static final String l_index0 = "l_index0";
    public static final String l_index1 = "l_index1";
    public static final String l_index2 = "l_index2";
    public static final String l_index3 = "l_index3";
    public static final String l_index_distal_tip = "l_index_distal_tip";
    public static final String l_middle0 = "l_middle0";
    public static final String l_middle1 = "l_middle1";
    public static final String l_middle2 = "l_middle2";
    public static final String l_middle3 = "l_middle3";
    public static final String l_middle_distal_tip = "l_middle_distal_tip";
    public static final String l_ring0 = "l_ring0";
    public static final String l_ring1 = "l_ring1";
    public static final String l_ring2 = "l_ring2";
    public static final String l_ring3 = "l_ring3";
    public static final String l_ring_distal_tip = "l_ring_distal_tip";
    public static final String l_pinky0 = "l_pinky0";
    public static final String l_pinky1 = "l_pinky1";
    public static final String l_pinky2 = "l_pinky2";
    public static final String l_pinky3 = "l_pinky3";
    public static final String l_pinky_distal_tip = "l_pinky_distal_tip";

    public static final String r_sternoclavicular = "r_sternoclavicular";
    public static final String r_acromioclavicular = "r_acromioclavicular";
    public static final String r_shoulder = "r_shoulder";
    public static final String r_elbow = "r_elbow";
    public static final String r_wrist = "r_wrist";
    public static final String r_thumb1 = "r_thumb1";
    public static final String r_thumb2 = "r_thumb2";
    public static final String r_thumb3 = "r_thumb3";
    public static final String r_thumb_distal_tip = "r_thumb_distal_tip";
    public static final String r_index0 = "r_index0";
    public static final String r_index1 = "r_index1";
    public static final String r_index2 = "r_index2";
    public static final String r_index3 = "r_index3";
    public static final String r_index_distal_tip = "r_index_distal_tip";
    public static final String r_middle0 = "r_middle0";
    public static final String r_middle1 = "r_middle1";
    public static final String r_middle2 = "r_middle2";
    public static final String r_middle3 = "r_middle3";
    public static final String r_middle_distal_tip = "r_middle_distal_tip";
    public static final String r_ring0 = "r_ring0";
    public static final String r_ring1 = "r_ring1";
    public static final String r_ring2 = "r_ring2";
    public static final String r_ring3 = "r_ring3";
    public static final String r_ring_distal_tip = "r_ring_distal_tip";
    public static final String r_pinky0 = "r_pinky0";
    public static final String r_pinky1 = "r_pinky1";
    public static final String r_pinky2 = "r_pinky2";
    public static final String r_pinky3 = "r_pinky3";
    public static final String r_pinky_distal_tip = "r_pinky_distal_tip";

    public static final String[] all_body_joints = new String[] { HumanoidRoot,
            sacroiliac, l_hip, l_knee, l_ankle, l_subtalar, l_midtarsal,
            l_metatarsal, r_hip, r_knee, r_ankle, r_subtalar, r_midtarsal,
            r_metatarsal, vl5, vl4, vl3, vl2, vl1, vt12, vt11, vt10, vt9, vt8,
            vt7, vt6, vt5, vt4, vt3, vt2, vt1, vc7, vc6, vc5, vc4, vc3, vc2,
            vc1, skullbase, l_sternoclavicular, l_acromioclavicular,
            l_shoulder, l_elbow, l_wrist, l_thumb1, l_thumb2, l_thumb3,
            l_index0, l_index1, l_index2, l_index3, l_middle0, l_middle1,
            l_middle2, l_middle3, l_ring0, l_ring1, l_ring2, l_ring3, l_pinky0,
            l_pinky1, l_pinky2, r_sternoclavicular, r_acromioclavicular,
            r_shoulder, r_elbow, r_wrist, r_thumb1, r_thumb2, r_thumb3,
            r_index0, r_index1, r_index2, r_index3, r_middle0, r_middle1,
            r_middle2, r_middle3, r_ring0, r_ring1, r_ring2, r_ring3, r_pinky0,
            r_pinky1, r_pinky2, r_pinky3 };

    // HAnim Segments (incomplete
    public static final String sacrum = "sacrum";
    public static final String pelvis = "pelvis";
    public static final String l_thigh = "l_thigh";
    public static final String l_calf = "l_calf";
    public static final String l_hindfoot = "l_hindfoot";
    public static final String l_midproximal = "l_midproximal";
    public static final String l_middistal = "l_middistal";
    public static final String l_forefoot = "l_forefoot";
    public static final String l_forefoot_tip = "l_forefoot_tip";
    public static final String r_thigh = "r_thigh";
    public static final String r_calf = "r_calf";
    public static final String r_hindfoot = "r_hindfoot";
    public static final String r_midproximal = "r_midproximal";
    public static final String r_middistal = "r_middistal";
    public static final String r_forefoot = "r_forefoot";
    public static final String r_forefoot_tip = "r_forefoot_tip";

    public static final String l5 = "l5";
    public static final String l4 = "l4";
    public static final String l3 = "l3";
    public static final String l2 = "l2";
    public static final String l1 = "l1";
    public static final String t12 = "t12";
    public static final String t1 = "t1";
    public static final String c4 = "c4";
    public static final String c1 = "c1";
    public static final String skull = "skull";
    public static final String skull_tip = "skull_tip";

    public static final String l_eyelid = "l_eyelid";
    public static final String r_eyelid = "r_eyelid";
    public static final String l_eyeball = "l_eyeball";
    public static final String r_eyeball = "r_eyeball";
    public static final String l_eyebrow = "l_eyebrow";
    public static final String r_eyebrow = "r_eyebrow";
}

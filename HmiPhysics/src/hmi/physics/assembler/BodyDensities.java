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
import com.google.common.collect.ImmutableMap;

/**
 * Body densities for segments directly connected to Hanim joints
 * Densities obtained from:<br>
 * David A. Winter. 
 * Biomechanics and Motor Control of Human Movement. 
 * Wiley, 2004.
 * 
 * @author welberge
 */
public final class BodyDensities 
{
    private BodyDensities(){}
    public final static ImmutableMap<String,Float> bodyDensityMap =
        new ImmutableMap.Builder<String,Float>()
            .put( "HumanoidRoot", 1030f)
            .put( "sacroiliac", 1030f)
            .put( "l_hip", 1050f)
            .put( "l_thigh_roll", 1050f)
            .put( "l_knee", 1090f)
            .put( "l_calf_roll", 1090f)
            .put( "l_ankle", 1100f)
            .put( "l_subtalar", 1100f)
            .put( "l_midtarsal", 1100f)
            .put( "l_metatarsal", 1100f)
            .put( "r_hip", 1050f)
            .put( "r_thigh_roll", 1050f)
            .put( "r_knee", 1090f)
            .put( "r_calf_roll", 1090f)
            .put( "r_ankle", 1100f)
            .put( "r_subtalar", 1100f)
            .put( "r_midtarsal", 1100f)
            .put( "r_metatarsal", 1100f)       
            .put( "vl5",1070f)
            .put( "vl4",1070f)
            .put( "vl3",1070f)
            .put( "vl2",1070f)
            .put( "vl1",1070f)
            .put( "vt12",1070f)
            .put( "vt11",1070f)
            .put( "vt10",1070f)
            .put( "vt9",1070f)
            .put( "vt8",1070f)
            .put( "vt7",1070f)
            .put( "vt6",1070f)
            .put( "vt5",1070f)
            .put( "vt4",1070f)
            .put( "vt3",1070f)
            .put( "vt2",1070f)
            .put( "vt1",1070f)
            .put( "vc7",1110f)
            .put( "vc6",1110f)
            .put( "vc5",1110f)
            .put( "vc4",1110f)
            .put( "vc3",1110f)
            .put( "vc2",1110f)
            .put( "vc1",1110f)
            .put( "skullbase",1110f)
            .put( "temporomandibular",1110f)
            .put( "l_sternoclavicular",1070f)
            .put( "l_acromioclavicular",1070f)
            .put( "l_shoulder",1070f)
            .put( "l_upperarm_roll",1070f)
            .put( "l_elbow",1130f)
            .put( "l_forearm_roll",1030f)
            .put( "l_wrist",1160f)
            .put( "l_thumb1",1160f)
            .put( "l_thumb2",1160f)
            .put( "l_thumb3",1160f)
            .put( "l_index0",1160f)
            .put( "l_index1",1160f)
            .put( "l_index2",1160f)
            .put( "l_index3",1160f)
            .put( "l_middle0",1160f)
            .put( "l_middle1",1160f)
            .put( "l_middle2",1160f)
            .put( "l_middle3",1160f)
            .put( "l_ring0",1160f)
            .put( "l_ring1",1160f)
            .put( "l_ring2",1160f)
            .put( "l_ring3",1160f)
            .put( "l_pinky0",1160f)
            .put( "l_pinky1",1160f)
            .put( "l_pinky2",1160f)
            .put( "l_pinky3",1160f)
            .put( "r_sternoclavicular",1070f)
            .put( "r_acromioclavicular",1070f)
            .put( "r_shoulder",1070f)
            .put( "r_upperarm_roll",1070f)
            .put( "r_elbow",1130f)
            .put( "r_forearm_roll",1030f)
            .put( "r_wrist",1160f)
            .put( "r_thumb1",1160f)
            .put( "r_thumb2",1160f)
            .put( "r_thumb3",1160f)
            .put( "r_index0",1160f)
            .put( "r_index1",1160f)
            .put( "r_index2",1160f)
            .put( "r_index3",1160f)    
            .put( "r_middle0",1160f)
            .put( "r_middle1",1160f)
            .put( "r_middle2",1160f)
            .put( "r_middle3",1160f)
            .put( "r_ring0",1160f)
            .put( "r_ring1",1160f)
            .put( "r_ring2",1160f)
            .put( "r_ring3",1160f)
            .put( "r_pinky0",1160f)
            .put( "r_pinky1",1160f)
            .put( "r_pinky2",1160f)
            .put( "r_pinky3",1160f)
            .put( "sacrum",1030f)
            .put( "pelvis",1030f)
            .put( "l_thigh",1050f)
            .put( "l_calf",1090f)
            .put( "l_hindfoot",1100f)
            .put( "l_midproximal",1100f)
            .put( "l_middistal",1100f)
            .put( "l_forefoot",1100f)
            .put( "l_forefoot_tip",1100f)
            .put( "r_thigh",1050f)
            .put( "r_calf",1090f)
            .put( "r_hindfoot",1100f)
            .put( "r_midproximal",1100f)
            .put( "r_middistal",1100f)
            .put( "r_forefoot",1100f)
            .put( "l5", 1030f)
            .put( "l4", 1030f)
            .put( "l3", 1030f)
            .put( "l2", 1030f)
            .put( "l1", 1030f)
            .put( "t12", 1030f)
            .put( "t1", 1030f)
            .put( "c4", 1110f)
            .put( "c1", 1110f)
            .put( "skull",1110f)
            .build();            
}

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
package hmi.animationui;

import com.google.common.collect.ImmutableList;

import lombok.Getter;
import hmi.animation.AnalyticalIKSolver;
import hmi.animation.Hanim;
import hmi.animation.AnalyticalIKSolver.LimbPosition;
import hmi.animation.VJoint;
import hmi.math.Quat4f;
import hmi.math.Vec3f;

/**
 * A controller handles arm/foot positioning input from the viewer and updates the arm/leg accordingly
 * @author hvanwelbergen
 */
public class AnalyticalIKController
{
    private AnalyticalIKSolver solver;
    private final VJoint shoulder, elbow;
    
    @Getter
    private final float startSwivel;
    
    @Getter
    private final float[] startPos = Vec3f.getVec3f();
    
    
    
    @Getter
    private final String id;
    
    public AnalyticalIKController(String id, VJoint shoulder, VJoint elbow, VJoint wrist, LimbPosition lp)
    {
        this.id = id;
        this.shoulder = shoulder;
        this.elbow = elbow;
        float tv[] = new float[3];
        float sv[] = new float[3];
        elbow.getPathTranslation(shoulder, tv);
        wrist.getPathTranslation(elbow, sv);
        solver = new AnalyticalIKSolver(sv,tv,lp, (Vec3f.length(sv) + Vec3f.length(tv)) * 0.999f);
        
        wrist.getPathTranslation(shoulder, startPos);
        startSwivel = 0;
    }

    public void setJointRotations(float goal[], float swivel)
    {
        float qSho[] = Quat4f.getQuat4f();
        float qElb[] = Quat4f.getQuat4f();
        solver.setProject(true);
        solver.setSwivel(swivel);        
        solver.solve(goal, qSho, qElb);
        shoulder.setRotation(qSho);
        elbow.setRotation(qElb);
    }
    
    public static IKView constructIKView(VJoint humanRoot)
    {
        AnalyticalIKController leftHand = new AnalyticalIKController("left hand:", 
                humanRoot.getPart(Hanim.l_shoulder),humanRoot.getPart(Hanim.l_elbow), humanRoot.getPart(Hanim.l_wrist), LimbPosition.ARM);
        
        AnalyticalIKController rightHand = new AnalyticalIKController("right hand:", 
                humanRoot.getPart(Hanim.r_shoulder),humanRoot.getPart(Hanim.r_elbow), humanRoot.getPart(Hanim.r_wrist), LimbPosition.ARM);
        return new IKView(ImmutableList.of(leftHand,rightHand));
    }
}

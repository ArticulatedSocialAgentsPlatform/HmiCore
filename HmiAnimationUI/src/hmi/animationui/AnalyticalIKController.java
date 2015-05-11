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

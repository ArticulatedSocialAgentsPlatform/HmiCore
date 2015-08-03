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
package hmi.neurophysics;

import hmi.math.Quat4f;
import hmi.math.Vec3f;

/**
 * Biomechanical spine rotation model, based on
 * 
 * B Boulic, B Ulicny and Daniel Thalmann, Versatile Walk Engine (2004), in: Journal of Game Development, 1:1(29-- 52)
 * 
 * secondary ref:
 * Augustus A. White and Manohar M. Panjabi, Clinical Biomechanics of the Spine, Lippincott Williams & Wilkins, 1990
 * 
 * 
 * @author Herwin
 */
public final class Spine
{
    private Spine(){}
    
    public static double getUniform(int n)
    {
        return 1.0 / (double) n;
    }

    public static double getLinearIncrease(int i, int n)
    {
        return (double) i * (2.0 / ((double) n * ((double) n + 1.0)));
    }

    public static double getLinearDecrease(int i, int n)
    {
        return ((double) n + 1.0 - (double) i) * (2.0 / ((double) n * ((double) n + 1.0)));
    }
    
    public static void setTorsoRollPitchYawDegrees(float qRes[], float roll, float pitch, float yaw, int nrOfLumbarJoint, int nrOfThoracicJoints)
    {
        setTorsoRollPitchYaw(qRes, (float)Math.toRadians(roll), (float)Math.toRadians(pitch), (float)Math.toRadians(yaw), nrOfLumbarJoint, nrOfThoracicJoints);
    }
    
    public static void setCervicalRotationRollPitchYawDegrees(float[] qRes, float roll, float pitch, float yaw, int nrOfNeckJoints)
    {
        setCervicalRotationRollPitchYaw(qRes, (float)Math.toRadians(roll), (float)Math.toRadians(pitch), (float)Math.toRadians(yaw), nrOfNeckJoints);
    }
    
    public static void setTorsoRotation(float[] qRes, float[] qRot, int nrOfLumbarJoint, int nrOfThoracicJoints)
    {
        float []rpy = Vec3f.getVec3f();
        Quat4f.getRollPitchYaw(qRot, rpy);
        setTorsoRollPitchYaw(qRes, rpy[0], rpy[1], rpy[2], nrOfLumbarJoint, nrOfThoracicJoints);
    }
    
    public static void setCervicalRotation(float[] qRes, float[] qRot, int nrOfNeckJoints)
    {
        float []rpy = Vec3f.getVec3f();
        Quat4f.getRollPitchYaw(qRot, rpy);
        setCervicalRotationRollPitchYaw(qRes, rpy[0], rpy[1], rpy[2], nrOfNeckJoints);
    }
    
   /**
    * Distribute the rotation specified in roll, pitch, yaw (in radians) over the neck joints
    * @param qRes output: the rotation will be set in this, order from vc7 to skullbase
    * @param roll
    * @param pitch
    * @param yaw
    */   
    public static void setCervicalRotationRollPitchYaw(float[] qRes, float roll, float pitch, float yaw, int nrOfNeckJoints)
    {
        for(int i=0;i<nrOfNeckJoints;i++)
        {
            float contribution = (float)getLinearIncrease(i,nrOfNeckJoints);
            Quat4f.setFromRollPitchYaw(qRes,i*4, contribution*roll, contribution*pitch, contribution*yaw);
        }
    }
    
    /**
     * Distribute the rotation specified in roll, pitch, yaw (in radians) over the torso joints
     * @param qRes output: the rotation will be set in this
     * @param roll
     * @param pitch
     * @param yaw
     * @param nrOfLumbarJoint
     * @param nrOfThoracicJoints
     */
    public static void setTorsoRollPitchYaw(float qRes[], float roll, float pitch, float yaw, int nrOfLumbarJoint, int nrOfThoracicJoints)
    {
        float currentYaw;
        int n = nrOfLumbarJoint+nrOfThoracicJoints;
        int m = nrOfThoracicJoints;        
        
        for(int i=1;i<=n;i++)
        {
            if(i<=nrOfLumbarJoint)
            {
                currentYaw = 0;
            }
            else
            {
                currentYaw = yaw * (float) getLinearIncrease(i-nrOfLumbarJoint, m);
            }
            Quat4f.setFromRollPitchYaw(qRes, (i-1)*4,roll * (float) getUniform(n), pitch * (float) getLinearDecrease(i, n), currentYaw);            
        }
    }
}

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
package hmi.physics.controller;

import hmi.animation.Hanim;
import hmi.math.Quat4f;
import hmi.math.Vec3f;
import hmi.physics.PhysicalHumanoid;
import hmi.physics.PhysicalJoint;
import hmi.physics.PhysicalSegment;
import hmi.util.StringUtil;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

/**
 * Balance controller Entry condition: both feet are flat on the ground Uses the rotation of both feet to determine the local facing direction of the
 * physical humanoid.
 * 
 * @author welberge
 */
public class BalanceController implements PhysicalController
{
    private PhysicalHumanoid ph;
    private PhysicalSegment leftFoot;
    private PhysicalSegment rightFoot;

    private PhysicalJoint leftAnkle;
    private PhysicalJoint rightAnkle;
    private PhysicalJoint rightHip;
    private PhysicalJoint leftHip;
    private PhysicalJoint rightKnee;
    private PhysicalJoint leftKnee;

    private float[] qLeftFoot = new float[4];
    private float[] qRightFoot = new float[4];
    private float[] qBody = new float[4];
    private float[] relCoM = new float[3];
    private float[] relCoMDiff = new float[3];

    private float[] left = new float[3];
    private float[] right = new float[3];
    private float[] center = new float[3];
    private float[] offset = new float[3];
    // private float[] balanceOffset = {0.05f,0,-0.02f};
    // private float[] balanceOffset = {0.0f,0,-0.02f};
    // private float[] balanceOffset = {0.0f,0,0f};
    private final float[] balanceOffset = { 0.0f, 0, -0.01f };

    private float leftFw[] = new float[3];
    private float rightFw[] = new float[3];

    /*
     * g=-5 private float kpx = 140f; private float kvx = 14f;
     * 
     * private float kpz = 140f; private float kvz = 14f;
     */
    private float kpx = 280f;
    private float kvx = 28f;

    private float kpz = 280f;
    private float kvz = 28f;

    private float kpKnee = 150f;
    private float kvKnee = 15f;

    private float rotXLFoot = 0;
    private float rotXRFoot = 0;

    private float rotZLFoot = 0;
    private float rotZRFoot = 0;

    private float lKneeC = 0;
    private float rKneeC = 0;

    private float lHipC = 0;
    private float rHipC = 0;

    private float lHipCZ = 0;
    private float rHipCZ = 0;

    private float ul, ll, pelH = 0.855f;
    // private float ul,ll,pelH=0.7f;

    // temp vars
    private float[] v1 = new float[3];
    private boolean groundCheck = false;
    private float s = 1;

    private final Set<String> jointIDs = ImmutableSet.of(Hanim.l_ankle, Hanim.r_ankle, Hanim.r_hip, Hanim.l_hip, Hanim.r_knee, Hanim.l_knee);
    private final Set<String> desjointIDs = ImmutableSet.of();
    private boolean firstRun = true;
    private boolean dummyRun = true;
    
    private Logger logger = LoggerFactory.getLogger(BalanceController.class.getName());
    
    public BalanceController()
    {
        reset();
    }

    public BalanceController(PhysicalHumanoid p)
    {
        super();
        setPhysicalHumanoid(p);
    }

    @Override
    public void reset()
    {
        rotXLFoot = 0;
        rotXRFoot = 0;
        rotZLFoot = 0;
        rotZRFoot = 0;
        lKneeC = 0;
        rKneeC = 0;
        lHipC = 0;
        rHipC = 0;
        lHipCZ = 0;
        rHipCZ = 0;
        firstRun = true;
        dummyRun = true;
    }

    public void setKMultiplier(float km)
    {
        s = km;
    }

    public BalanceController(PhysicalHumanoid p, boolean gcheck)
    {
        this(p);
        groundCheck = gcheck;
    }

    @Override
    public void setPhysicalHumanoid(PhysicalHumanoid p)
    {
        ph = p;
        leftFoot = p.getSegment(Hanim.l_ankle);
        rightFoot = p.getSegment(Hanim.r_ankle);
        rightHip = p.getJoint(Hanim.r_hip);
        leftHip = p.getJoint(Hanim.l_hip);
        rightKnee = p.getJoint(Hanim.r_knee);
        leftKnee = p.getJoint(Hanim.l_knee);
        rightAnkle = p.getJoint(Hanim.r_ankle);
        leftAnkle = p.getJoint(Hanim.l_ankle);

        if (leftHip != null && leftKnee != null && leftAnkle != null)
        {
            float hip[] = Vec3f.getVec3f();
            float knee[] = Vec3f.getVec3f();
            float ankle[] = Vec3f.getVec3f();
            leftHip.getAnchor(hip);
            leftKnee.getAnchor(knee);
            leftAnkle.getAnchor(ankle);
            Vec3f.sub(hip, knee);
            ul = Vec3f.length(hip);
            Vec3f.sub(knee, ankle);
            ll = Vec3f.length(knee);
            //logger.debug("setPhysicalHumanoid {} ul={} ll={}", new Object[]{p.getId(),ul, ll});            
        }
    }

    @Override
    public Set<String >getRequiredJointIDs()
    {
        return jointIDs;
    }

    @Override
    public void update(double timeDiff)
    {
        if(dummyRun)
        {
            dummyRun = false;   //XXX Ode specific hack, first run is a dummy run, since getAngles doesn't work then
            return;
        }
        
        if (groundCheck)
        {
            if (!leftFoot.onGround || !rightFoot.onGround)
            {
                // System.out.println("no foot on ground");
                return;
            }
        }
        leftFoot.box.getRotation(qLeftFoot);
        rightFoot.box.getRotation(qRightFoot);
        Quat4f.interpolate(qBody, qLeftFoot, qRightFoot, 0.5f);
        // Quat4f.setFromAxisAngle4f(qRoot, 0,1,0, (float)Math.PI*0.5f);
        // Quat4f.setIdentity(qRoot);
        Quat4f.inverse(qBody);

        Vec3f.set(relCoM, ph.getCOM());
        Vec3f.set(relCoMDiff, ph.getCOMDiff());
        Quat4f.transformVec3f(qBody, relCoM);
        Quat4f.transformVec3f(qBody, relCoMDiff);

        leftFoot.box.getTranslation(left);
        rightFoot.box.getTranslation(right);

        Vec3f.set(center, left);
        Vec3f.add(center, right);
        Vec3f.scale(0.5f, center);
        Vec3f.set(v1, balanceOffset);
        // Vec3f.set(v1,0,0,-0.02f);
        // Vec3f.set(v1,0,0,-0.03f);
        // Vec3f.set(v1,0,0,-0.0f);
        // Vec3f.set(v1,0,0,-0.01f);
        // Vec3f.set(v1,0,0,0.01f);
        Vec3f.add(center, v1);

        Quat4f.transformVec3f(qBody, center);
        /*
         * System.out.println("zerror:"+(center.z-ph.getCOM().z)); System.out.println("zd:"+ph.getCOMDiff().z);
         */

        float offsetX = s * kpx * (center[0] - relCoM[0]) - kvx * relCoMDiff[0];
        float offsetZ = s * kpz * (center[2] - relCoM[2]) - kvz * relCoMDiff[2];

        /*
         * double offsetX = kpx*(center.x-ph.getCOMNoContacts().x)-kvx*ph.getCOMNoContactsDiff ().x; double offsetZ =
         * kpz*(center.z-ph.getCOMNoContacts().z)-kvz*ph.getCOMNoContactsDiff ().z;
         */

        /*
         * System.out.println("zoffset:"+offsetZ);
         */

        Vec3f.set(offset, offsetZ, 0f, offsetX);

        // MathUtils.rotateToLocalSystem(null, leftHip.joint, new
        // Point3d(offset[0],offset[1],offset[2]), offsetL);
        // MathUtils.rotateToLocalSystem(null, rightHip.joint, new
        // Point3d(offset[0],offset[1],offset[2]), offsetR);

        // keep balance
        leftHip.addTorque(offsetZ, 0f, offsetX);
        rightHip.addTorque(offsetZ, 0f, offsetX);

        float hipD;
        // System.out.println("pelvis height "+pelH+" "+(ul+ll));
        if (pelH >= ul + ll)
        {
            hipD = 0;
        }
        else
        {
            logger.debug("ul={} ll={} pelH={}", new Object[]{ul, ll,pelH});
            hipD = (float) Math.acos((ul * ul - ll * ll + pelH * pelH) / (2 * ul * pelH));
        }
        // System.out.println("hipD "+hipD);

        // System.out.println("hipD:"+hipD);
        float dHipR = 0;
        float dHipL = 0;
        if (!firstRun)
        {
            float rHipOld = rHipC;
            float lHipOld = lHipC;
            lHipC = leftHip.getAngle(0);
            rHipC = rightHip.getAngle(0);
            dHipR = (rHipC - rHipOld) / (float) timeDiff;
            dHipL = (lHipC - lHipOld) / (float) timeDiff;
        }
        else
        {
            lHipC = leftHip.getAngle(0);
            rHipC = rightHip.getAngle(0);
        }

        // System.out.println("hipC "+rHipC);
        // stay upright
        /*
         * leftHip.addTorque((hipD-lHipC)*150-dHipL*200f,0,0); rightHip.addTorque((hipD-rHipC)*150-dHipR*200f,0,0);
         */
        //logger.debug("hipD {} lHipC{} dHipL {}", new Object[]{hipD, lHipC,dHipL});
        leftHip.addTorque((hipD - lHipC) * s * 150 - dHipL * s * 15f, 0, 0);
        rightHip.addTorque((hipD - rHipC) * s * 150 - dHipR * s * 15f, 0, 0);

        float dHipRZ = 0;
        float dHipLZ = 0;
        if (!firstRun)
        {
            float rHipOldZ = rHipCZ;
            float lHipOldZ = lHipCZ;
            lHipCZ = leftHip.getAngle(2);
            rHipCZ = rightHip.getAngle(2);
            dHipRZ = (rHipCZ - rHipOldZ) / (float) timeDiff;
            dHipLZ = (lHipCZ - lHipOldZ) / (float) timeDiff;
        }
        else
        {
            lHipCZ = leftHip.getAngle(2);
            rHipCZ = rightHip.getAngle(2);
        }

        float hipDZ = 0;
        // stay upright
        /*
         * leftHip.addTorque(0,0,(hipDZ-lHipCZ)*150-dHipLZ*200f); rightHip.addTorque(0,0,(hipDZ-rHipCZ)*150-dHipRZ*200f);
         */
        leftHip.addTorque(0, 0, (hipDZ - lHipCZ) * s * 150 - dHipLZ * s * 15f);
        rightHip.addTorque(0, 0, (hipDZ - rHipCZ) * s * 150 - dHipRZ * s * 15f);

        // offsetLvec.set(offsetL);
        // offsetRvec.set(offsetR);
        // leftHip.physicalMotor.addTorque(offsetLvec);
        // rightHip.physicalMotor.addTorque(offsetRvec);
        float kneeD;
        if (pelH >= ul + ll)
        {
            kneeD = 0;
        }

        else
        {
            kneeD = -((float) Math.PI - (float) Math.acos((ul * ul + ll * ll - pelH * pelH) / (2 * ul * ll)));
        }

        // System.out.println("ul "+ul);
        // System.out.println("ll "+ll);
        // kneeD = -kneeD;
        float dKneeR = 0;
        float dKneeL = 0;
        if (!firstRun)
        {
            float rKneeOld = rKneeC;
            float lKneeOld = lKneeC;
            lKneeC = leftKnee.getAngle(0);
            rKneeC = rightKnee.getAngle(0);
            dKneeR = (rKneeC - rKneeOld) / (float) timeDiff;
            dKneeL = (lKneeC - lKneeOld) / (float) timeDiff;
        }
        else
        {
            lKneeC = leftKnee.getAngle(0);
            rKneeC = rightKnee.getAngle(0);
        }
        /*
         * leftKnee.physicalMotor.addTorque((kneeD-lKneeC)*75-dKneeL*5f,0,0); rightKnee.physicalMotor.addTorque((kneeD-rKneeC)*75-dKneeR*5f,0,0);
         */
        float lKneeT = (kneeD - lKneeC);
        float rKneeT = (kneeD - rKneeC);
        /*
         * leftKnee.addTorque(lKneeT*160-dKneeL*200f,0,0); rightKnee.addTorque(rKneeT*160-dKneeR*200f,0,0);
         */

        leftKnee.addTorque(lKneeT * s * kpKnee - dKneeL * s * kvKnee, 0, 0);
        rightKnee.addTorque(rKneeT * s * kpKnee - dKneeR * s * kvKnee, 0, 0);

        /*
         * System.out.println("kneeD:"+kneeD); System.out.println("lkneeC:"+lKneeC); System.out.println("kneeD-lkneeC:"+(kneeD-lKneeC));
         */
        // System.out.println(leftAnkleU.getAngle1Rate());
        /*
         * leftAnkle.physicalMotor.addTorque((float)leftAnkleOffsetX,0f,(float) leftAnkleOffsetZ);
         * rightAnkle.physicalMotor.addTorque((float)rightAnkleOffsetX ,0f,(float)rightAnkleOffsetZ);
         */

        Vec3f.set(leftFw, 0, 0, 1);
        Vec3f.set(rightFw, 0, 0, 1);
        Quat4f.transformVec3f(qLeftFoot, leftFw);
        Quat4f.transformVec3f(qRightFoot, rightFw);
        Quat4f.transformVec3f(qBody, leftFw);
        Quat4f.transformVec3f(qBody, rightFw);

        // System.out.println("rightFw: " + rightFw);

        float dRotLFoot = 0;
        float dRotRFoot = 0;
        Vec3f.set(v1, 0, 0, 1);

        // System.out.println("leftFw "+Vec3f.toString(leftFw));
        // System.out.println("leftFw.v1 "+Vec3f.dot(leftFw,v1));
        double lDot = Vec3f.dot(leftFw, v1);
        double rDot = Vec3f.dot(rightFw, v1);
        if (lDot < -1)
            lDot = -1;
        if (lDot > 1)
            lDot = 1;
        if (rDot < -1)
            rDot = -1;
        if (rDot > 1)
            rDot = 1;
        float oldRotRFoot;
        float oldRotLFoot;
        if (!firstRun)
        {
            oldRotRFoot = rotXRFoot;
            oldRotLFoot = rotXLFoot;

            rotXLFoot = (float) Math.acos(lDot);
            rotXRFoot = (float) Math.acos(rDot);
            dRotLFoot = (rotXLFoot - oldRotRFoot) / (float) timeDiff;
            dRotRFoot = (rotXRFoot - oldRotLFoot) / (float) timeDiff;
        }
        else
        {
            rotXLFoot = (float) Math.acos(lDot);
            rotXRFoot = (float) Math.acos(rDot);
        }
        // keep the feet flat
        // System.out.println("rotLFoot "+rotLFoot);
        // System.out.println("dRotLFoot "+dRotLFoot);
        // System.out.println("Adding torque to ankle: "+(rotLFoot*30-dRotLFoot*3f)+","+0f+","+0f);

        leftAnkle.addTorque(rotXLFoot * s * 160 - dRotLFoot * s * 1.6f, 0f, 0f);
        rightAnkle.addTorque(rotXRFoot * s * 160 - dRotRFoot * s * 1.6f, 0f, 0f);

        Vec3f.set(v1, 1, 0, 0);
        Vec3f.set(leftFw, 1, 0, 0);
        Quat4f.transformVec3f(qLeftFoot, leftFw);
        Quat4f.transformVec3f(qRightFoot, rightFw);
        Vec3f.set(rightFw, 1, 0, 0);
        lDot = Vec3f.dot(leftFw, v1);
        rDot = Vec3f.dot(rightFw, v1);
        if (lDot < -1)
            lDot = -1;
        if (lDot > 1)
            lDot = 1;
        if (rDot < -1)
            rDot = -1;
        if (rDot > 1)
            rDot = 1;

        dRotLFoot = 0;
        dRotRFoot = 0;
        if (!firstRun)
        {
            oldRotRFoot = rotZRFoot;
            oldRotLFoot = rotZLFoot;
            rotZLFoot = (float) Math.acos(lDot);
            rotZRFoot = (float) Math.acos(rDot);
            dRotLFoot = (rotZLFoot - oldRotLFoot) / (float) timeDiff;
            dRotRFoot = (rotZRFoot - oldRotRFoot) / (float) timeDiff;
        }
        else
        {
            rotZLFoot = (float) Math.acos(lDot);
            rotZRFoot = (float) Math.acos(rDot);
        }

        /*
         * leftAnkle.addTorque( 0f,0f,rotZLFoot*160-dRotLFoot*200f); rightAnkle.addTorque( 0f,0f,rotZRFoot*160-dRotRFoot*200f);
         */
        /*
         * leftAnkle.addTorque( 0f,0f,rotZLFoot*160-dRotLFoot*1.6f); rightAnkle.addTorque( 0f,0f,rotZRFoot*160-dRotRFoot*1.6f);
         */

        // retain balance
        // System.out.println("Adding torque to ankle(2): "+offsetZ*2+","+0f+","+offsetX*2);
        leftAnkle.addTorque(offsetZ * s * 3, 0f, -offsetX * s * 3);
        rightAnkle.addTorque(offsetZ * s * 3, 0f, -offsetX * s * 3);

        // sticky feet
        /*
         * leftFoot.box.addForceAtRelPos(0, -200, 0,0,0,-0.02f); rightFoot.box.addForceAtRelPos(0, -200, 0,0,0,-0.02f);
         * leftFoot.box.addForceAtRelPos(0, -200, 0,0,0,0.02f); rightFoot.box.addForceAtRelPos(0, -200, 0,0,0,0.02f);
         */
        firstRun = false;
    }

    /**
     * @param pelH the pelH to set
     */
    public void setPelH(float pelH)
    {
        // System.out.println("Setting pelvis height "+pelH);
        this.pelH = pelH;
    }

    @Override
    public PhysicalController copy(PhysicalHumanoid ph)
    {
        BalanceController copy = new BalanceController(ph);
        copy.pelH = pelH;
        copy.groundCheck = groundCheck;
        copy.s = s;
        return copy;
    }

    @Override
    public float getFloatParameterValue(String name) throws ControllerParameterNotFoundException
    {
        if (name.equals("pelvisheight"))
            return pelH;
        throw new ControllerParameterNotFoundException(name);
    }

    @Override
    public String getParameterValue(String name)throws ControllerParameterNotFoundException
    {
        if (name.equals("pelvisheight"))
        {
            return "" + pelH;
        }
        throw new ControllerParameterNotFoundException(name);
    }

    @Override
    public void setParameterValue(String name, String value)throws ControllerParameterException
    {
        if(StringUtil.isNumeric(value))
        {
            setParameterValue(name, Float.parseFloat(value));
        }
        else
        {
            throw new ControllerParameterException("Invalid parameter setting"+name+"="+value);
        }
    }

    @Override
    public void setParameterValue(String name, float value)throws ControllerParameterException
    {
        if (name.equals("pelvisheight"))
            setPelH(value);
    }

    @Override
    public Set<String> getDesiredJointIDs()
    {
        return desjointIDs;
    }

    @Override
    public Set<String> getJoints()
    {
        return getRequiredJointIDs();
    }
}

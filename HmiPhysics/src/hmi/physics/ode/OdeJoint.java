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
package hmi.physics.ode;

import hmi.math.Quat4f;
import hmi.math.Vec3f;
import hmi.physics.JointType;
import hmi.physics.PhysicalJoint;
import hmi.physics.RigidBody;

import org.odejava.Body;
import org.odejava.JointAMotor;
import org.odejava.JointBall;
import org.odejava.JointFixed;
import org.odejava.JointGroup;
import org.odejava.JointHinge;
import org.odejava.JointUniversal;
import org.odejava.World;
import org.odejava.ode.OdeConstants;

/**
 * OdeJoint Note: axis2 (default: y axis) must always be limited between -0.5pi..0.5pi
 * 
 * @author Herwin
 */
public class OdeJoint extends PhysicalJoint
{
    private org.odejava.Joint physicalJoint;
    private org.odejava.JointAMotor physicalMotor;
    private JointGroup jointGroup;
    private World world;
    private Body box1;
    private Body box2;

    // temp vars
    private float w1[] = new float[3];
    private float w2[] = new float[3];
    private float q[] = new float[4];

    public OdeJoint(JointType t, String name, World w, JointGroup jg)
    {
        super(t, name);
        world = w;
        jointGroup = jg;
        createJoint();
    }

    private void createJoint()
    {
        switch (type)
        {
        default:
        case FIXED:
            physicalJoint = new JointFixed(name, world, jointGroup);
            break;
        case HINGE:
            physicalJoint = new JointHinge(name, world, jointGroup);
            break;
        case UNIVERSAL:
            physicalJoint = new JointUniversal(name, world, jointGroup);
            break;
        case BALL:
            physicalJoint = new JointBall(name, world, jointGroup);
            break;
        }
        physicalMotor = new JointAMotor(name + "_motor", world, jointGroup);
        physicalMotor.enableFeedbackTracking();
    }

    @Override
    public void getTorque1(float torque[])
    {
        torque[0] = physicalMotor.getFeedback().getTorque1().getX();
        torque[1] = physicalMotor.getFeedback().getTorque1().getY();
        torque[2] = physicalMotor.getFeedback().getTorque1().getZ();
    }

    @Override
    public void getTorque2(float torque[])
    {
        torque[0] = physicalMotor.getFeedback().getTorque2().getX();
        torque[1] = physicalMotor.getFeedback().getTorque2().getY();
        torque[2] = physicalMotor.getFeedback().getTorque2().getZ();
    }

    @Override
    public void getForce2(float force[])
    {
        force[0] = physicalMotor.getFeedback().getForce2().getX();
        force[1] = physicalMotor.getFeedback().getForce2().getY();
        force[2] = physicalMotor.getFeedback().getForce2().getZ();
    }

    @Override
    public void getForce1(float force[])
    {
        force[0] = physicalMotor.getFeedback().getForce1().getX();
        force[1] = physicalMotor.getFeedback().getForce1().getY();
        force[2] = physicalMotor.getFeedback().getForce1().getZ();
    }

    @Override
    public void addTorque(float x, float y, float z)
    {
        if (Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z))
        {
            throw new RuntimeException("Adding torque componenent with NaN force (" + x + "," + y + "," + z + ")");
        }
        if (Float.isInfinite(x) || Float.isInfinite(y) || Float.isInfinite(z))
        {
            throw new RuntimeException("Adding torque componenent with infinite force (" + x + "," + y + "," + z + ")");
        }
        physicalMotor.addTorque(x, y, z);
    }

    @Override
    public float getAngle(int axis)
    {
        return physicalMotor.getAngle(axis);
    }

    @Override
    public void setDesiredVelocity(int axis, float value)
    {
        switch (axis)
        {
        case 0:
            physicalMotor.setParam(OdeConstants.dParamVel, value);
            return;
        case 1:
            physicalMotor.setParam(OdeConstants.dParamVel2, value);
            return;
        case 2:
            physicalMotor.setParam(OdeConstants.dParamVel3, value);
            return;
        default:
            break;
        }
    }

    @Override
    public void setMaximumForce(int axis, float value)
    {
        switch (axis)
        {
        case 0:
            physicalMotor.setParam(OdeConstants.dParamFMax, value);
            return;
        case 1:
            physicalMotor.setParam(OdeConstants.dParamFMax2, value);
            return;
        case 2:
            physicalMotor.setParam(OdeConstants.dParamFMax3, value);
            return;
        default:
            break;
        }
    }

    @Override
    public void setJointMin(int axis, float min)
    {
        super.setJointMin(axis, min);
        switch (axis)
        {
        case 0:
            physicalMotor.setParam(OdeConstants.dParamLoStop, min);
            return;
        case 1:
            physicalMotor.setParam(OdeConstants.dParamLoStop2, min);
            return;
        case 2:
            physicalMotor.setParam(OdeConstants.dParamLoStop3, min);
            return;
        default:
            break;
        }
    }

    @Override
    public void setJointMax(int axis, float max)
    {
        super.setJointMax(axis, max);
        switch (axis)
        {
        case 0:
            physicalMotor.setParam(OdeConstants.dParamHiStop, max);
            return;
        case 1:
            physicalMotor.setParam(OdeConstants.dParamHiStop2, max);
            return;
        case 2:
            physicalMotor.setParam(OdeConstants.dParamHiStop3, max);
            return;
        default:
            break;
        }
    }

    @Override
    public void setAxis(int axis, float x, float y, float z)
    {
        switch (axis)
        {
        case 0:
            physicalJoint.setAxis1(x, y, z);
            return;
        case 1:
            physicalJoint.setAxis2(x, y, z);
            return;
        }
    }

    @Override
    public float[] getAnchor(float[] src)
    {
        if (src == null)
        {
            src = new float[3];
        }

        switch (type)
        {
        default:
        case FIXED:
            src = null;
            break;
        case HINGE:
            ((JointHinge) physicalJoint).getAnchor(src);
            break;
        case UNIVERSAL:
            ((JointUniversal) physicalJoint).getAnchor(src);
            break;
        case BALL:
            ((JointBall) physicalJoint).getAnchor(src);
            break;
        }
        return src;
    }

    @Override
    public void setAnchor(float x, float y, float z)
    {
        // super.setAnchor(x, y, z);
        switch (type)
        {
        default:
        case FIXED:
            ((JointFixed) physicalJoint).setFixed();
            break;
        case HINGE:
            ((JointHinge) physicalJoint).setAnchor(x, y, z);
            break;
        case UNIVERSAL:
            ((JointUniversal) physicalJoint).setAnchor(x, y, z);
            break;
        case BALL:
            ((JointBall) physicalJoint).setAnchor(x, y, z);
            break;
        }
        physicalMotor.setMode(OdeConstants.dAMotorEuler);
        physicalMotor.setNumAxes(3);
        physicalMotor.setAxis(0, 1, 1, 0, 0);
        physicalMotor.setAxis(1, 1, 0, 1, 0);
        physicalMotor.setAxis(2, 1, 0, 0, 1);

        // XXX: make these changeable, for example using the XML
        physicalMotor.setParam(OdeConstants.dParamFudgeFactor, 0.8f);
        physicalMotor.setParam(OdeConstants.dParamFudgeFactor2, 0.8f);
        physicalMotor.setParam(OdeConstants.dParamFudgeFactor3, 0.8f);

        physicalMotor.setParam(OdeConstants.dParamStopCFM, 0.2f);
        physicalMotor.setParam(OdeConstants.dParamStopCFM2, 0.2f);
        physicalMotor.setParam(OdeConstants.dParamStopCFM3, 0.2f);
    }

    public void attach(RigidBody b1, RigidBody b2)
    {
        if (b1 != null)
        {
            box1 = ((OdeRigidBody) b1).getBody();
        }
        else
        {
            box2 = null;
        }
        if (b2 != null)
        {
            box2 = ((OdeRigidBody) b2).getBody();
        }
        else
        {
            box2 = null;
        }
        physicalJoint.attach(box1, box2);
        physicalMotor.attach(box1, box2);
        /*
         * physicalJoint.attach(box2, box1); physicalMotor.attach(box2, box1);
         */
    }

    @Override
    public void getAngularVelocity(float[] w)
    {

        box1.getAngularVel(w1);
        if (box2 != null)
        {
            box2.getAngularVel(w2);
            box2.getQuaternion(q);
        }
        else
        {
            Vec3f.setZero(w2);
            Quat4f.setIdentity(q);
        }
        Vec3f.sub(w, w2, w1);
        Quat4f.transformVec3f(q, w);
    }

    @Override
    public void getAngularVelocity(float[] w, int i)
    {
        box1.getAngularVel(w1);
        box2.getAngularVel(w2);
        Vec3f.sub(w, i, w2, 0, w1, 0);
        box2.getQuaternion(q);
        Quat4f.transformVec3f(q, 0, w, i);
    }
}

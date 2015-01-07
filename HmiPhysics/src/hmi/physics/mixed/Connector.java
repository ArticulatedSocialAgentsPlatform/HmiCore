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
package hmi.physics.mixed;

import hmi.math.Mat4f;
import hmi.math.Quat4f;
import hmi.math.SpatialVec;
import hmi.math.Vec3f;
import hmi.physics.PhysicalSegment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connection point for a @see{KinematicChain} to a @see{PhysicalSegment}
 * @author welberge
 */
public class Connector
{
    private Logger logger = LoggerFactory.getLogger(Connector.class.getName());

    private PhysicalSegment segment;
    private float[] connectionPoint = new float[3];
    private float vel[] = new float[3];
    private float aVel[] = new float[3];
    private float aAcc[] = new float[3];
    private float acc[] = new float[3];
    private float prevVel[] = new float[3];
    private float prevAVel[] = new float[3];
    private float rTorque[] = new float[3];
    private float bodyRotation[] = new float[4];
    private float tempS[] = new float[6];
    private float tempRot[] = new float[4];
    private float tempPos[] = new float[3];
    private float mTemp[] = new float[16];
    private boolean isFirst = false;

    private float k = 1;
    private static final float MAX_REACTIVE_TORQUE = 1000;

    /**
     * Sets up the connector, assumes the segment is initialized and has a valid world position
     * @param seg physical segment
     * @param p the connection point, in world coordinates
     * @param kmul the reactive torque multiplier (typically 0 &lt kmul &lt 1), 0.6 works well
     */
    public Connector(PhysicalSegment seg, float[] p, float kmul)
    {
        segment = seg;
        float pos[] = new float[3];
        seg.box.getTranslation(pos);
        Vec3f.set(connectionPoint, p);
        Vec3f.sub(connectionPoint, pos);
        k = kmul;
        reset();
    }

    /**
     * Sets the ratio of torques that is applied to the physical body
     * @param kmul the new feedback ratio
     */
    public void setFeedbackRatio(float kmul)
    {
        k = kmul;
    }

    /**
     * Resets the state of the connector, newly calculated acceleration values will assume 0 velocity at
     * the previous frame.
     */
    public void reset()
    {
        Vec3f.set(prevVel, 0, 0, 0);
        Vec3f.set(prevAVel, 0, 0, 0);
        isFirst = true;
    }

    /**
     * Sets the prevVel and prevAvel from connector c
     * @param c
     */
    public void setVel(Connector c)
    {
        Vec3f.set(prevVel, c.prevVel);
        Vec3f.set(prevAVel, c.prevAVel);
    }

    /**
     * Get the 4x4 world transform of the connection point
     * Not thread safe, the caller of this function needs to guard for thread safety for the physical segment
     * @param m output: the world position of the connection point
     */
    public void getWorldTransform(float m[])
    {
        Mat4f.setIdentity(m);
        segment.box.getRotation(tempRot);
        Mat4f.setRotation(m, tempRot);
        segment.box.getTranslation(tempPos);
        Mat4f.setTranslation(m, tempPos);
        Mat4f.setIdentity(mTemp);
        Mat4f.setTranslation(mTemp, connectionPoint);
        Mat4f.mul(m, mTemp);
    }

    /**
     * Get the world position of the connection point
     * Not thread safe, the caller of this function needs to guard for thread safety for the physical segment
     * @param dst output: the world position of the connection point
     */
    public void getWorldPosition(float dst[])
    {
        segment.box.getPointRelPosition(dst, connectionPoint);
    }

    public void getRelVelocity(float[] velocity)
    {
        segment.box.getRelativePointVelocity(velocity, connectionPoint);
    }

    public void getVelocity(float[] velocity)
    {
        segment.box.getPointVelocity(velocity, connectionPoint);
    }

    public void getAvelocity(float[] aVelocity)
    {
        segment.box.getAngularVelocity(aVelocity);
    }

    /**
     * Calculates the spatial velocity and acceleration of the connector from
     * the velocity of the physical segment and the velocity at the previous call to this function
     * Not thread safe, the caller of this function needs to guard for thread safety for the physical segment
     * Depends on previous velocity values, call reset before using this function if drastic changes happened
     * @param timeDiff time since previous call
     * @param spatialV output: the current spatial velocity
     * @param spatialA output: the current spatial acceleration
     * @param spatialG spatial acceleration gravity in world coordinates
     */
    public void getSpatialVelocityAndAcceleration(float timeDiff, float spatialV[], float spatialA[], float spatialG[])
    {
        segment.box.getRotation(bodyRotation);
        Quat4f.inverse(bodyRotation);
        segment.box.getAngularVelocity(aVel);
        Quat4f.transformVec3f(bodyRotation, aVel);
        segment.box.getRelativePointVelocity(vel, connectionPoint);
        SpatialVec.set(spatialV, aVel, vel);

        if (timeDiff == 0)
        {
            SpatialVec.set(spatialA, 0, 0, 0, 0, 0, 0);
            // return;
        }
        else
        {
            if (isFirst)
            {
                Vec3f.set(prevVel, vel);
                Vec3f.set(prevAVel, aVel);
                isFirst = false;
            }

            Vec3f.set(acc, vel);
            Vec3f.sub(acc, prevVel);
            Vec3f.scale(1f / timeDiff, acc);

            Vec3f.set(aAcc, aVel);
            Vec3f.sub(aAcc, prevAVel);
            Vec3f.scale(1f / timeDiff, aAcc);

            SpatialVec.setAcc(spatialA, aVel, vel, aAcc, acc);
        }

        SpatialVec.set(tempS, spatialG);
        Quat4f.transformVec3f(bodyRotation, 0, tempS, 3);

        SpatialVec.sub(spatialA, tempS);
        Vec3f.set(prevVel, vel);
        Vec3f.set(prevAVel, aVel);

        /*
         * segment.box.getAngularVelocity(aVel);
         * segment.box.getRelativePointVelocity(vel, connectionPoint);
         * SpatialVec.set(spatialV,aVel,vel);
         * 
         * 
         * if(timeDiff == 0)
         * {
         * SpatialVec.set(spatialA, 0,0,0,0,0,0);
         * //return;
         * }
         * else
         * {
         * if(isFirst)
         * {
         * Vec3f.set(prevVel,vel);
         * Vec3f.set(prevAVel,aVel);
         * isFirst = false;
         * }
         * 
         * Vec3f.set(acc, vel);
         * Vec3f.sub(acc,prevVel);
         * Vec3f.scale(1f/timeDiff, acc);
         * 
         * Vec3f.set(aAcc,aVel);
         * Vec3f.sub(aAcc,prevAVel);
         * Vec3f.scale(1f/timeDiff,aAcc);
         * 
         * SpatialVec.setAcc(spatialA, aVel, vel, aAcc, acc);
         * }
         * segment.box.getRotation(bodyRotation);
         * Quat4f.inverse(bodyRotation);
         * SpatialVec.set(tempS, spatialG);
         * Quat4f.transformVec3f(bodyRotation, 0,tempS,3);
         * 
         * SpatialVec.sub(spatialA, tempS);
         * Vec3f.set(prevVel,vel);
         * Vec3f.set(prevAVel,aVel);
         */
    }

    /**
     * Calculates the spatial velocity and acceleration of the connector from
     * the velocity of the physical segment and the velocity at the previous call to this function
     * Not thread safe, the caller of this function needs to guard for thread safety for the physical segment
     * @param timeDiff time since previous call
     * @param spatialV output: the current spatial velocity
     * @param spatialA output: the current spatial acceleration
     */
    public void getSpatialVelocityAndAcceleration(float timeDiff, float spatialV[], float spatialA[])
    {
        segment.box.getRotation(bodyRotation);
        Quat4f.inverse(bodyRotation);
        segment.box.getAngularVelocity(aVel);
        Quat4f.transformVec3f(bodyRotation, aVel);
        segment.box.getRelativePointVelocity(vel, connectionPoint);
        SpatialVec.set(spatialV, aVel, vel);

        if (timeDiff == 0)
        {
            SpatialVec.set(spatialA, 0, 0, 0, 0, 0, 0);
            return;
        }
        Vec3f.set(acc, vel);
        Vec3f.sub(acc, prevVel);
        Vec3f.scale(1f / timeDiff, acc);

        Vec3f.set(aAcc, aVel);
        Vec3f.sub(aAcc, prevAVel);
        Vec3f.scale(1f / timeDiff, aAcc);

        SpatialVec.setAcc(spatialA, aVel, vel, aAcc, acc);
        Vec3f.set(prevVel, vel);
        Vec3f.set(prevAVel, aVel);
    }

    /**
     * Applies a reactive torque of -k*f to the connectors body
     * @param f the reactive torque
     */
    public void applyReactiveTorque(float q[], float f[])
    {
        Vec3f.set(rTorque, f);
        float q2[] = new float[4];
        // Quat4f.inverse(q2,q);
        Quat4f.set(q2, q);

        float[] force = new float[3];
        Vec3f.set(force, 0, f, 3);
        Quat4f.transformVec3f(q2, force);
        Quat4f.transformVec3f(q2, rTorque);

        /*
         * float tTemp[] = new float[3];
         * float r[] = new float[3];
         * //TODO: set real dist
         * segment.box.getTranslation(r);
         * Vec3f.sub(r,0,5,0);
         * Vec3f.scale(-1, r);
         * Vec3f.add(r,0.5f,0,0);
         * 
         * Vec3f.cross(tTemp, r, force);
         * Vec3f.add(rTorque, tTemp);
         * Vec3f.scale(-k,rTorque);
         */

        Vec3f.scale(-k, force);
        Vec3f.scale(-k, rTorque);

        if (Vec3f.lengthSq(rTorque) < MAX_REACTIVE_TORQUE * MAX_REACTIVE_TORQUE)
        {
            segment.box.addRelTorque(rTorque[0], rTorque[1], rTorque[2]);

            /*
             * segment.box.addRelForce(force);
             * float[]r=new float[3];
             * Vec3f.set(r,0.5f,-5,0);
             * float[]r2=new float[3];
             * Vec3f.cross(r2, r, force);
             * segment.box.addRelTorque(r2);
             */
            segment.box.addRelForceAtRelPos(force, connectionPoint);
            /*
             * System.out.println("reactive torque: "+Vec3f.toString(rTorque));
             * System.out.println("reactive force: "+Vec3f.toString(force));
             */
            float[] t = new float[3];
            float[] fo = new float[3];
            segment.box.getForce(fo);
            segment.box.getTorque(t);

            // System.out.println("torque on body: "+Vec3f.toString(t));
            // System.out.println("force on body: "+Vec3f.toString(fo));

        }
        else
        {
            logger.warn("Max reactive torque exceeded: {} on {}, {}", new Object[] { Vec3f.length(rTorque), getStartSegment().getSid(),
                    Vec3f.toString(connectionPoint) });
        }
    }

    public PhysicalSegment getStartSegment()
    {
        return segment;
    }
}

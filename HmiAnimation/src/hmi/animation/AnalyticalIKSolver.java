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

import hmi.math.Mat3f;
import hmi.math.Mat4f;
import hmi.math.Quat4f;
import hmi.math.Vec3f;

/**
 * IK calculations for a 7 DOF Joint construction (e.g. arm/leg)
 * 
 * Loosely based on the IKAN toolkit: Tolani, Deepak, Goswami, Ambarish and
 * Badler, Norman I., Real-time inverse kinematics techniques for
 * anthropomorphic limbs (2000), in: Graphical Models and Image Processing,
 * 62:5(353--388)
 * 
 * 
 * Given matrices G, S, T solves the equation
 * 
 * G = S*Rx*T*R1 for R1,Rx where R1 represent general rotation matrices Ry
 * represents a rotation about the y axis and G is the desired goal matrix S, T
 * are constant matrices In the case of the arm: S : Wrist to Elbow
 * transformation Rx : Elbow joint T : Elbow to Shoulder transformation R1 :
 * Shoulder joints
 * 
 * This method assumes a close to HAnim resting pose
 * 
 * @author welberge
 */
public class AnalyticalIKSolver
{

    private float[] Sv = new float[3];
    private float[] Tv = new float[3];
    private float[] WH = new float[3];
    private float[] Rx = new float[16];
    private float[] R1 = new float[16];
    public final static int LEFT = 0;
    public final static int RIGHT = 1;
    private double swivel = Math.PI * 0.5;
    private float projectionLength = 0;
    private boolean project = false;
    private float[] sewT = new float[16];

    private float R = 1;
    private float elbowStartRotation = 0;
    private float[] C = new float[3];
    private float[] goal = new float[3];
    private float[] a = new float[3];
    private float[] elbowRotAxis = new float[3];

    /**
     * Defines the type of Limb 
     * @author Herwin
     */
    public enum LimbPosition
    {
        ARM, LEG
    }

    private LimbPosition limbPosition = LimbPosition.ARM;
    private final static double ROT_MARGIN = 0.05; // 0.3 degrees
    private final static double DOT_MARGIN = 0.05; // 0.3 degrees

    float l1, l2;

    /**
     * Constructor
     * 
     * @param sv
     *            wrist to elbow vector in the shoulder coordinate system
     * @param tv
     *            elbow to shoulder vector in the shoulder coordinate system
     * @param lp
     *            arm or leg
     */
    public AnalyticalIKSolver(float[] sv, float[] tv, LimbPosition lp)
    {
        limbPosition = lp;

        Vec3f.set(this.Sv, sv);
        Vec3f.set(this.Tv, tv);
        swivel = 0;
        project = false;

        Vec3f.set(WH, tv);
        Vec3f.add(WH, sv);

        l1 = Vec3f.length(tv);
        l2 = Vec3f.length(sv);
        double l3 = Vec3f.length(WH);

        Vec3f.set(e, tv);

        if (l1 + l2 > l3)
        {
            elbowStartRotation = (float) Math
                    .acos((l1 * l1 + l2 * l2 - l3 * l3) / (2 * l1 * l2));
        }
        else
        {
            elbowStartRotation = (float) Math.PI;
        }

        if (Math.abs(elbowStartRotation - Math.PI) <= ROT_MARGIN)
        {
            if (limbPosition == LimbPosition.ARM)
            {
                Vec3f.set(elbowRotAxis, 1, 0, 0);
            }
            else if (limbPosition == LimbPosition.LEG)
            {
                Vec3f.set(elbowRotAxis, -1, 0, 0);
            }
        }
        else
        {
            Vec3f.cross(elbowRotAxis, sv, tv);
            Vec3f.normalize(elbowRotAxis);
        }

        // x = e/||e||
        Vec3f.set(x, e);
        Vec3f.scale(-1, x);
        Vec3f.normalize(x);

        // y=w-(w.x)x/||w-(w.x)x||
        Vec3f.set(w, WH);
        Vec3f.normalize(w);
        Vec3f.set(y, w);
        float dot = Vec3f.dot(w, x);

        if (Math.abs(Math.acos(dot) - Math.PI) > DOT_MARGIN)
        {
            Vec3f.set(tempv, x);
            Vec3f.scale(dot, tempv);
            Vec3f.sub(y, tempv);
            Vec3f.normalize(y);
            Vec3f.cross(z, x, y);

            Mat4f.set(sewT, x[0], y[0], z[0], 0, x[1], y[1], z[1], 0, x[2],
                    y[2], z[2], 0, 0, 0, 0, 1);
        }
        else
        {
            // at least this part is HAnim specific
            if (limbPosition == LimbPosition.ARM)
            {
                Mat4f.set(sewT, 0, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1);
            }
            else if (limbPosition == LimbPosition.LEG)
            {
                Mat4f.set(sewT, 0, 0, -1, 0, 1, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0,
                        1);
            }
        }
        Mat4f.transpose(sewT);
    }

    /**
     * Constructor, creates a IK system with projection enabled
     * 
     * @param sv
     *            wrist to elbow vector
     * @param tv
     *            elbow to shoulder vector
     * @param limbPosition
     *            arm or leg
     * @param projectionLength
     */
    public AnalyticalIKSolver(float[] sv, float[] tv,
            LimbPosition limbPosition, float projectionLength)
    {
        this(sv, tv, limbPosition);
        project = true;
        this.projectionLength = projectionLength;
    }

    /**
     * set the swivel angle
     * 
     * @param swivel
     *            the new swivel angle
     */
    public void setSwivel(double swivel)
    {
        this.swivel = swivel;
    }

    /**
     * Solves the joint configurations for a goal and the current end effector
     * 
     * @param goal
     *            goal position
     * @param qSho
     *            quaternion rotation around shoulder(-like) joint
     * @param qElb
     *            quaternion rotation around elbow(-like) joint
     */
    public void solve(float[] goal, float[] qSho, float[] qElb)
    {
        solveIt(goal);
        Quat4f.setFromMat4f(qSho, getR1());
        Quat4f.setFromMat4f(qElb, getRx());
    }

    /**
     * Calculate the swivel angle, given the shoulder rotation and the goal in shoulder coordinates.
     * This is more accurate than the getTwist based on elbow position when the arm is fully stretched
     */
    public double getSwivelFromShoulderAndGoal(float qSho[], float goal[])
    {
        float n[]=Vec3f.getVec3f(goal);
        Vec3f.normalize(n);        
        return Quat4f.getTwist(qSho,n);
    }
    
    /**
     * Calculate the swivel angle, given an elbow/wrist position and a goal
     * 
     * @param e
     *            elbow/wrist position in shoulder/hip coordinates
     * @param g
     *            the goal position
     * @return the swivel angle
     */
    public double getSwivel(float[] e, float[] g)
    {
        double sw = 0;
        Vec3f.set(goal, g);

        Vec3f.normalize(n, goal);

        double rem;
        if (limbPosition == LimbPosition.ARM)
        {
            Vec3f.set(forward, g[0], 0, g[2]);
            rem = g[1];
            Vec3f.set(axis, 0, -1, 0);
            Vec3f.set(backAxis, 0, 0, -1);
        }
        else
        {
            Vec3f.set(forward, g[0], g[1], 0);
            rem = g[2];
            Vec3f.set(axis, 0, 0, 1);
            Vec3f.set(backAxis, 0, 1, 0);
        }

        if (rem * rem < 0.0000001)
        {
            Vec3f.set(a, axis);
        }
        else if (Vec3f.length(forward) < 0.00001)
        {
            Vec3f.set(a, backAxis);
        }
        else
        {
            Vec3f.cross(planeNormal, g, forward);
            Vec3f.normalize(planeNormal);

            double rotAngle;
            if (limbPosition == LimbPosition.ARM)
            {
                if (g[1] < 0)
                    rotAngle = -Math.PI * 0.5;
                else
                    rotAngle = Math.PI * 0.5;
            }
            else
            {
                if (g[2] < 0)
                    rotAngle = Math.PI * 0.5;
                else
                    rotAngle = -Math.PI * 0.5;
            }
            Mat3f.setFromAxisAngleScale(aRot, planeNormal, (float) rotAngle, 1);
            Vec3f.set(a, n);
            Mat3f.transformVec3f(aRot, a);
            // Vec3f.normalize(a);
        }

        float l3 = Vec3f.length(goal);

        if (project)
        {
            if (l3 > projectionLength)
            {
                l3 = projectionLength;
                Vec3f.set(goal, n);
                Vec3f.scale(l3, goal);
                // System.out.println("Projecting!");
            }
        }
        float cosAlpha = (l3 * l3 + l1 * l1 - l2 * l2) / (2 * l3 * l1);

        // C = cos(alpha)L1n, circle center
        Vec3f.set(C, n);
        Vec3f.scale(cosAlpha * l1, C);

        Vec3f.set(u, a);

        Vec3f.set(eNorm, e);
        Vec3f.sub(eNorm, C);
        Vec3f.normalize(eNorm);
        sw = Math.acos(Vec3f.dot(eNorm, u));

        return sw;
    }

    /**
     * Solve the elbow and shoulder rotations for a certain rotation goal
     * 
     * @param g
     *            the goal wrist position
     * @return true is solvable, false otherwise
     */
    public boolean solveIt(float[] g)
    {
        double swiv = swivel;
        // System.out.println("Goal "+Vec3f.toString(goal));
        Vec3f.set(goal, g);
        Vec3f.normalize(n, goal);

        double rem;
        if (limbPosition == LimbPosition.ARM)
        {
            Vec3f.set(forward, g[0], 0, g[2]);
            rem = g[1];
            Vec3f.set(axis, 0, -1, 0);
            Vec3f.set(backAxis, 0, 0, -1);
        }
        else
        {
            Vec3f.set(forward, g[0], g[1], 0);
            rem = g[2];
            Vec3f.set(axis, 0, 0, 1);
            Vec3f.set(backAxis, 0, 1, 0);
        }

        // if((rem*rem)<0.00001)
        if (rem * rem < 0.0000001)
        {
            Vec3f.set(a, axis);
        }
        else if (Vec3f.length(forward) < 0.00001)
        {
            Vec3f.set(a, backAxis);
            // System.out.println("a = backAxis");
        }
        else
        {
            Vec3f.cross(planeNormal, g, forward);
            Vec3f.normalize(planeNormal);
            double rotAngle;
            if (limbPosition == LimbPosition.ARM)
            {
                if (g[1] < 0)
                    rotAngle = -Math.PI * 0.5;
                else
                    rotAngle = Math.PI * 0.5;
            }
            else
            {
                if (g[2] < 0)
                    rotAngle = Math.PI * 0.5;
                else
                    rotAngle = -Math.PI * 0.5;
            }
            Mat3f.setFromAxisAngleScale(aRot, planeNormal, (float) rotAngle, 1);
            Vec3f.set(a, n);
            Mat3f.transformVec3f(aRot, a);
            // System.out.println("a = complicated formula "+
            // Vec3f.toString(g));
            // Vec3f.normalize(a);
        }
        // System.out.println("a = "+Vec3f.toString(a) +
        // "g = "+Vec3f.toString(g));

        float l3 = Vec3f.length(goal);
        if (project)
        {
            if (l3 > projectionLength)
            {
                l3 = projectionLength;
                Vec3f.set(goal, n);
                Vec3f.scale(l3, goal);
            }
        }

        if (solveRy(l1, l2, l3))
        {
            float cosAlpha = (l3 * l3 + l1 * l1 - l2 * l2) / (2 * l3 * l1);
            float sinAlpha = (float) Math.sqrt(1 - cosAlpha * cosAlpha);

            // C = cos(alpha)L1n, circle center
            Vec3f.set(C, n);
            Vec3f.scale(cosAlpha * l1, C);

            Vec3f.set(u, a);

            // v=uxn
            Vec3f.cross(v, u, n);

            // circle radius
            R = l1 * sinAlpha;

            // e(sw)=C+R(cos(sw)u+sin(sw)v)
            Vec3f.set(e, u);
            Vec3f.scale((float) Math.cos(swiv), e);
            Vec3f.set(tempv, v);
            Vec3f.scale((float) Math.sin(swiv), tempv);
            Vec3f.add(e, tempv);
            Vec3f.normalize(e);
            Vec3f.scale(R, e);
            Vec3f.add(e, C);

            // x = e/||e||
            Vec3f.set(x, e);
            Vec3f.scale(-1, x);
            Vec3f.normalize(x);

            // y=w-(w.x)x/||w-(w.x)x||
            Vec3f.set(w, goal);
            Vec3f.normalize(w);
            Vec3f.set(y, w);
            float dot = Vec3f.dot(w, x);
            Vec3f.set(tempv, x);
            Vec3f.scale(dot, tempv);
            Vec3f.sub(y, tempv);
            Vec3f.normalize(y);

            // z=x cross y
            Vec3f.cross(z, x, y);

            Mat4f.set(R1, x[0], y[0], z[0], 0, x[1], y[1], z[1], 0, x[2], y[2],
                    z[2], 0, 0, 0, 0, 1);
            Mat4f.mul(R1, R1, sewT);
            return true;
        }

        // aim
        Mat4f.setIdentity(Rx);

        // y = -e/||e||
        Vec3f.set(y, n);
        Vec3f.scale(-1, y);

        // x=w-(w.y)y/||w-(w.y)y||
        Vec3f.set(w, 1, 0, 0);
        Vec3f.set(x, 1, 0, 0);
        float dot = Vec3f.dot(w, y);
        Vec3f.set(tempv, y);
        Vec3f.scale(dot, tempv);
        Vec3f.sub(x, tempv);
        Vec3f.normalize(x);

        // z = x cross y
        Vec3f.cross(z, x, y);

        Mat4f.set(R1, x[0], y[0], z[0], 0, x[1], y[1], z[1], 0, x[2], y[2],
                z[2], 0, 0, 0, 0, 1);
        Mat4f.mul(R1, R1, sewT);
        return false;
    }

    /**
     * Find the elbow rotation
     */
    private boolean solveRy(double l1, double l2, double l3)
    {
        if (l1 + l2 >= l3)
        {
            // cosinus rule
            double cosAlpha = (l1 * l1 + l2 * l2 - l3 * l3) / (2 * l1 * l2);
            float alpha = elbowStartRotation - (float) Math.acos(cosAlpha);

            // possible optimisation: remove the acos above and fill out the 3x3
            // rotation matrix by hand
            Mat4f.setIdentity(Rx);
            Mat4f.setRotationFromAxisAngle(Rx, elbowRotAxis, -alpha);
            return true;
        }
        return false;
    }

    /**
     * @return Returns the R1.
     */
    public float[] getR1()
    {
        return R1;
    }

    /**
     * @return Returns the Rx.
     */
    public float[] getRx()
    {
        return Rx;
    }

    /**
     * @return Returns the center of the elbow rotation circle.
     */
    public float[] getC()
    {
        return C;
    }

    /**
     * @return Returns the radius of the elbow rotation circle.
     */
    public double getR()
    {
        return R;
    }

    public float[] getA()
    {
        return a;
    }

    /**
     * Project onto local sphere?
     * 
     * @param proj
     *            new project value
     */
    public void setProject(boolean proj)
    {
        project = proj;
    }

    /**
     * Set the length (=radius of projection sphere)
     * 
     * @param length
     *            the new projection length
     */
    public void setProjectionLength(float length)
    {
        projectionLength = length;
    }

    /**
     * Translates a vector src in obj1-coordinates to the local coordinate
     * system for a visual object obj2, excluding obj2's rotation
     * 
     * @param obj1
     *            the visual object in which the src coordinates are defined
     * @param obj2
     *            the visual object in which the dst coordinates should be
     *            defined
     * @param src
     *            the vector in world coordinates
     */
    public static void translateToLocalSystem(VJoint obj1, VJoint obj2,
            float src[], float dst[])
    {
        float q[] = new float[4];
        obj2.getParent().getPathRotation(obj1, q);

        float v[] = new float[3];
        obj2.getPathTranslation(obj1, v);
        // float s = obj2.getPathScale(obj1);
        float s = 1.0f;

        float m[] = new float[16];
        Mat4f.setFromTRS(m, v, q, s);
        Mat4f.invertRigid(m);

        // Vec3f.set(dst,src);
        Mat4f.transformPoint(m, dst, src);
    }

    // temp vars
    private float x[] = new float[3];
    private float y[] = new float[3];
    private float z[] = new float[3];
    private float w[] = new float[3];
    private float n[] = new float[3];
    private float e[] = new float[3];
    private float[] tempv = new float[3];
    private float forward[] = new float[3];
    private float axis[] = new float[3];
    private float backAxis[] = new float[3];
    private float[] planeNormal = new float[3];
    private float aRot[] = new float[9];
    private float eNorm[] = new float[3];
    private float u[] = new float[3];
    private float[] v = new float[3];
}

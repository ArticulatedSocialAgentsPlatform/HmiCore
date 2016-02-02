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
package hmi.animation;

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
            elbowStartRotation = (float) Math.acos((l1 * l1 + l2 * l2 - l3 * l3) / (2 * l1 * l2));
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

            Mat4f.set(sewT, x[0], y[0], z[0], 0, x[1], y[1], z[1], 0, x[2], y[2], z[2], 0, 0, 0, 0, 1);
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
                Mat4f.set(sewT, 0, 0, -1, 0, 1, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 1);
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
    public AnalyticalIKSolver(float[] sv, float[] tv, LimbPosition limbPosition, float projectionLength)
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
     * This provides an accurate swivel for the stretched arm.
     */
    public double getSwivelFromShoulderElbowAndGoal(float qSho[], float qElb[], float goal[])
    {
        float l3 = Vec3f.length(goal);

        if (l1 + l2 < l3 - 0.01f || Quat4f.epsilonEquals(Quat4f.getIdentity(), qElb, 0.01f))
        {
            float n[] = Vec3f.getVec3f(goal);
            Vec3f.normalize(n);
            return Quat4f.getTwist(qSho, n);
        }
        float[] elbVec = Vec3f.getVec3f(Tv);
        Quat4f.transformVec3f(qSho, elbVec);        
        return getSwivel(elbVec, goal);
    }

    /**
     * Calculate the swivel angle, given an elbow/wrist position and a goal
     * 
     * @param e
     *            elbow/wrist position in shoulder/hip coordinates
     * @param g
     *            the goal position
     * @return the swivel angle
     * @throws RunTimeException when the arm is fully stretched.
     */
    public double getSwivel(float[] e, float[] g)
    {
        Vec3f.set(goal, g);

        Vec3f.normalize(n, goal);
        if (limbPosition == LimbPosition.ARM)
        {
            float q[] = Quat4f.getFromVectors(new float[] { 0, 0, 1 }, n);
            Vec3f.set(a, 0, -1, 0);
            Quat4f.transformVec3f(q, a);
        }
        else
        {
            float q[] = Quat4f.getFromVectors(new float[] { 0, -1, 0 }, n);
            Vec3f.set(a, 0, 0, 1);
            Quat4f.transformVec3f(q, a);
        }
        
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
        if (l1 + l2 >= l3)
        {
            float cosAlpha = (l3 * l3 + l1 * l1 - l2 * l2) / (2 * l3 * l1);

            // C = cos(alpha)L1n, circle center
            Vec3f.set(C, n);
            Vec3f.scale(cosAlpha * l1, C);
            Vec3f.set(eNorm, e);
            Vec3f.sub(eNorm, C);
            Vec3f.normalize(eNorm);
            Vec3f.set(u, a);

            float v[] = Vec3f.getVec3f();
            float res[] = Vec3f.getVec3f();
            Vec3f.cross(v, u, n);
            
            Vec3f.decompose(res, eNorm, u, v, n);
            //return Math.acos(Vec3f.dot(eNorm, u));
            return Math.atan2(res[1],res[0]);
        }
        else
        {
            throw new RuntimeException("Can't calculate swivel from elbow and wrist if the arm is fully stretched");
        }
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
        Vec3f.set(goal, g);
        Vec3f.normalize(n, goal);

        float qmin[];
        if (limbPosition == LimbPosition.ARM)
        {
            qmin = Quat4f.getFromVectors(new float[] { 0, 0, 1 }, n);
            Vec3f.set(a, 0, -1, 0);
            Quat4f.transformVec3f(qmin, a);
        }
        else
        {
            qmin = Quat4f.getFromVectors(new float[] { 0, -1, 0 }, n);
            Vec3f.set(a, 0, 0, 1);
            Quat4f.transformVec3f(qmin, a);
        }        

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

            Mat4f.set(R1, x[0], y[0], z[0], 0, x[1], y[1], z[1], 0, x[2], y[2], z[2], 0, 0, 0, 0, 1);
            Mat4f.mul(R1, R1, sewT);
            return true;
        }

        // aim
        Mat4f.setIdentity(Rx);

        // y = -n (=a)
        Vec3f.set(y, n);
        Vec3f.scale(-1, y);

        qmin = Quat4f.getFromVectors(new float[] { 0, -1, 0 }, n);

        Vec3f.set(x, 1, 0, 0);
        Quat4f.transformVec3f(qmin, x);
        Quat4f.transformVec3f(Quat4f.getQuat4fFromAxisAngle(y[0], y[1], y[2], (float) swiv), x);

        // z = x cross y
        Vec3f.cross(z, x, y);
        
        Mat4f.set(R1, x[0], y[0], z[0], 0, x[1], y[1], z[1], 0, x[2], y[2], z[2], 0, 0, 0, 0, 1);

        // Mat4f.mul(R1, R1, sewT);
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
    public static void translateToLocalSystem(VJoint obj1, VJoint obj2, float src[], float dst[])
    {
        float q[] = new float[4];
        obj2.getParent().getFullPathRotation(obj1, q);

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
    /*
    private float forward[] = new float[3];
    private float axis[] = new float[3];
    private float backAxis[] = new float[3];
    private float[] planeNormal = new float[3];
    private float aRot[] = new float[9];
    */
    private float eNorm[] = new float[3];
    private float u[] = new float[3];
    private float[] v = new float[3];
}

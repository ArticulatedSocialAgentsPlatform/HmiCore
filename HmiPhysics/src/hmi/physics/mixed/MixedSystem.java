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
package hmi.physics.mixed;

import hmi.animation.SkeletonPose;
import hmi.animation.VJoint;
import hmi.math.Mat4f;
import hmi.math.Quat4f;
import hmi.math.Vec3f;
import hmi.physics.PhysicalHumanoid;
import hmi.physics.PhysicalSegment;
import hmi.physics.inversedynamics.IDBranch;
import hmi.physics.inversedynamics.IDSegment;
import hmi.util.PhysicsSync;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A system that contains a group of physically steered joints and 0 or more
 * groups of kinematically steered groups, connected to the physical group with
 * a @see{Connector}. Each frame, the torques and forces calculated from the
 * movement of the kinematically steered part are applied to the physically
 * steered group of joints
 * 
 * @author welberge
 */
public class MixedSystem
{
    private static final Logger logger = LoggerFactory.getLogger(MixedSystem.class.getName());
    
    private ArrayList<Branch> branches = new ArrayList<Branch>();
    private float spatialV0[] = new float[6];
    private float spatialA0[] = new float[6];
    private float f[];
    private float gravity[] = new float[6];
    private float mass;
    private float com[] = new float[3];
    // private float c[]=new float[3];
    private float tempM1[] = new float[16];
    private float tempM2[] = new float[16];
    private float qTemp[] = new float[4];
    private float vTemp[] = new float[4];
    private float vTemp2[] = new float[4];
    private float connectorTransform[] = new float[16];
    private PhysicalHumanoid pHuman;
    private SkeletonPose startPose;

    /**
     * Constructor
     * 
     * @param g
     *            Vec3f of gravity acceleration
     * @param p
     *            physical humanoid used for this system
     */
    public MixedSystem(float[] g, PhysicalHumanoid p)
    {
        Vec3f.set(gravity, 3, g, 0);
        pHuman = p;
    }

    /**
     * Should be called once the Physical human is fully created, automatically
     * called in MixedSystemAssembler
     */
    public void setup()
    {
        PhysicalSegment[] phSegs = pHuman.getSegments();
        String configs[] = new String[phSegs.length];
        for (int i = 0; i < phSegs.length; i++)
        {
            configs[i] = phSegs[i].getSid();
        }
        startPose = new SkeletonPose("start", configs, "TRVW");
        startPose.setTargets(pHuman.getSegments());
        startPose.setFromTarget();
    }

    /**
     * Sets the startup pose of the physical human, that is the pose to return
     * to on reset
     */
    public void setResetPose(VJoint v)
    {
        pHuman.setPoseFromVJoint(v);
        startPose.setFromTarget();
    }

    /**
     * Set the state of the idbranches to match the state of a PhysicalHumanoid
     * 
     * @param p
     */
    private void setIDFromPHuman(PhysicalHumanoid p)
    {
        for (Branch b : branches)
        {
            boolean changed = false;
            for (IDSegment ids : b.idBranch.getSegments())
            {
                PhysicalSegment ps = p.getSegment(ids.name);
                if (ps != null)
                {
                    changed = true;
                    synchronized (PhysicsSync.getSync())
                    {
                        ps.getRotation(qTemp);
                        ps.getTranslation(vTemp);
                    }
                }
            }
            if (changed)
            {
                // TODO: actually calculate connector velocities?
                b.connector.reset();
            }
        }
    }

    private float aVel[] = new float[3];
    private float aVelPrev[] = new float[3];
    private float velPrev[] = new float[3];

    private void setPHumanFromID(ArrayList<Branch> bs, float[] q, float w[])
    {
        int iq = 0;
        int iw = 0;
        for (Branch b : bs)
        {
            synchronized (PhysicsSync.getSync())
            {
                b.connector.getWorldTransform(connectorTransform);
            }
            b.connector.getAvelocity(aVelPrev);
            b.connector.getVelocity(velPrev);

            Mat4f.set(tempM1, connectorTransform);
            for (IDSegment seg : b.idBranch.getSegments())
            {
                Mat4f.setIdentity(tempM2);
                Mat4f.setRotation(tempM2, 0, q, iq);
                Mat4f.setTranslation(tempM2, seg.translation);
                Mat4f.mul(tempM1, tempM2);
                Quat4f.setFromMat4f(qTemp, tempM1);

                // test
                /*
                 * Vec3f.set(vTemp2,seg.CoM); Vec3f.scale(-1,vTemp2);
                 * Mat4f.transformPoint(tempM1, vTemp, vTemp2);
                 */
                Mat4f.transformPoint(tempM1, vTemp, seg.com);

                Quat4f.transformVec3f(q, iq, w, iw, aVel, 0);
                Vec3f.add(aVel, aVelPrev);

                PhysicalSegment ps = pHuman.getSegment(seg.name);

                if (ps != null)
                {
                    ps.setTranslation(vTemp);
                    logger.debug("Setting pos {} {} ",seg.name,Vec3f.toString(vTemp));
                    logger.debug("Setting rot {} {} local rot {}", new Object[]{seg.name,Quat4f.toString(qTemp),Quat4f.toString(q, iq)});
                    ps.setRotation(qTemp);

                    ps.setAngularVelocity(aVel);
                    Vec3f.scale(-1, vTemp2, ps.startJointOffset);
                    Vec3f.cross(vTemp, 0, w, iw, vTemp2, 0);
                    Vec3f.add(vTemp, velPrev);
                    ps.setVelocity(vTemp);

                    // TEST
                    /*
                     * ps.setVelocity(0,0,0); ps.setAngularVelocity(0,0,0);
                     */
                }
                Vec3f.set(aVelPrev, aVel);
                Vec3f.cross(vTemp, 0, w, iw, seg.translation, 0);
                Vec3f.add(velPrev, vTemp);
                iw += 3;
                iq += 4;
            }
        }
    }

    private void setIDFromID(ArrayList<Branch> srcBranches)
    {
        for (Branch b : branches)
        {
            for (Branch bSrc : srcBranches)
            {
                if (bSrc.idBranch.getRoot().name
                        .equals(b.idBranch.getRoot().name))
                {
                    b.connector.setVel(bSrc.connector);
                }
            }
        }
    }

    /**
     * Sets the system to s. That is: - Set the state of the pHuman to match
     * that of s.pHuman - Set the state of the ID branches, to match that of
     * s.branches - Set the velocity and position of segments in the pHuman to
     * match that of the velocity in s.branches in matching segments - Set the
     * connector velocity to match the velocity in s.pHuman (currently just set
     * to 0)
     */
    public void set(MixedSystem s, float q[], float w[])
    {
        // set matching physical segments
        pHuman.set(s.pHuman);

        // previous humanoid had no segments, set CoM to current CoM
        if (s.pHuman.getRootSegment() == null && s.pHuman.getSegments().length == 0)
        {
            pHuman.updateCOM(0);
        }

        // set matching ID branches
        setIDFromID(s.branches);

        // ID from s to pHuman
        setIDFromPHuman(s.pHuman);

        // pHuman in s to ID
        setPHumanFromID(s.branches, q, w);
    }

    /**
     * Resets the velocity and acceleration of the connectors
     * 
     * @param vj
     *            next joint, will contain start pose
     */
    public void reset(VJoint vj)
    {
        synchronized (PhysicsSync.getSync())
        {
            logger.debug("Mixed system reset");
            startPose.setToTarget();
            for (PhysicalSegment ps : pHuman.getSegments())
            {
                ps.box.setForce(0, 0, 0);
                ps.box.setTorque(0, 0, 0);
            }
            // pHuman.setCOMOffset(Vec3f.getZero(), 0);
            setMassOffset(vj);

            pHuman.updateCOM(0);
            for (Branch b : branches)
            {
                b.connector.reset();
            }
        }
    }

    /**
     * Adds a kinematic branch
     * 
     * @param ch
     *            the chain to add
     * @param con
     *            the connector
     */
    public void addBranch(IDBranch idb, Connector con)
    {
        Branch b = new Branch();
        b.connector = con;
        b.idBranch = idb;
        branches.add(b);
    }

    /**
     * Set the feedback ratio for all connectors
     * 
     * @param k
     *            the new feedback ratio
     */
    public void setFeedbackRatio(float k)
    {
        for (Branch b : branches)
        {
            b.connector.setFeedbackRatio(k);
        }
    }

    public int getRots(VJoint vj, IDSegment seg, int i, float[] q)
    {
        for (IDSegment s : seg.getChildren())
        {
            vj.getPart(s.name).getRotation(q, i);
            i = getRots(vj, s, i + 4, q);
        }
        return i;
    }

    public void setMassOffset(VJoint vj)
    {
        int i = 0;
        float q[] = new float[vj.getParts().size() * 4];

        for (Branch b : branches)
        {
            for (IDSegment seg : b.idBranch.getRoot().getChildren())
            {
                vj.getPart(seg.name).getRotation(q, i);
                i = getRots(vj, seg, i + 4, q);
            }
        }
        setMassOffset(q);
    }

    /**
     * set mass offset to the physical humanoid based on joint rotations of
     * kinematicly controlled joints
     */
    public void setMassOffset(float q[])
    {
        mass = 0;
        int iq = 0;
        Vec3f.set(com, 0, 0, 0);
        for (Branch b : branches)
        {
            synchronized (PhysicsSync.getSync())
            {
                mass += b.getMassOffset(q, iq, com);
            }
            iq += 4 * b.idBranch.getSize();
        }
        synchronized (PhysicsSync.getSync())
        {
            pHuman.setCOMOffset(com, mass);
        }
    }

    /**
     * Solves ID for kinematically steered objects Applies reactive torques to
     * the physical part and sets the CoM on the physical part. Rotations,
     * velocities and angular accelerations are to be provided in one array for
     * all chains ordered as: chain1:joint1, chain1:joint2, ..,
     * chain1:jointN,chain2:joint1, ... joint 1 is the joint that is the closest
     * to the connector. Connector velocities and accelerations are calculated
     * based on connector positions from the previous frame.
     * 
     * @param timeDiff
     *            time since last update
     * @param q
     *            rotations of the joints in the branches
     * @param w
     *            angular velocity of the joints in the branches
     * @param wDiff
     *            angular acceleration of the joints in the branches
     */
    public void time(float timeDiff, float q[], float w[], float wDiff[])
    {
        int iq = 0;
        int iw = 0;
        for (Branch b : branches)
        {
            synchronized (PhysicsSync.getSync())
            {
                b.connector.getSpatialVelocityAndAcceleration(timeDiff,
                        spatialV0, spatialA0, gravity);
            }
            // System.out.println("SpatialV0 :"+SpatialVec.toString(spatialV0));
            // System.out.println("SpatialA0 :"+SpatialVec.toString(spatialA0));

            // TEST
            // SpatialVec.set(spatialA0,0,0,0,0,0,0);
            // SpatialVec.set(spatialV0,0,0,0,0,0,0);

            // Solve inverse dynamics in the chain. Possible optimization: add
            // indexing to solver
            float qi[] = new float[b.idBranch.getSize() * 4];
            float wi[] = new float[b.idBranch.getSize() * 3];
            float diffwi[] = new float[b.idBranch.getSize() * 3];
            System.arraycopy(q, iq, qi, 0, qi.length);
            System.arraycopy(w, iw, wi, 0, wi.length);
            System.arraycopy(wDiff, iw, diffwi, 0, diffwi.length);
            f = new float[6 * b.idBranch.getSize()];
            // b.idBranch.solver.solveChain(f, spatialV0, spatialA0, qi, wi,
            // diffwi);
            b.idBranch.solver.solve(f, spatialV0, spatialA0, qi, wi, diffwi);

            // System.out.println(b.kChain.joints[0].getName());
            // System.out.println(iq+": "+SpatialVec.toString(f));

            synchronized (PhysicsSync.getSync())
            {
                // System.out.println("applying torque on "+b.idBranch.getRoot().name);
                // System.out.println("torque: "+SpatialVec.toString(f));
                b.connector.applyReactiveTorque(qi, f);
            }
            iq += qi.length;
            iw += wi.length;
        }
        setMassOffset(q);
    }

    /**
     * @return the branches
     */
    public ArrayList<Branch> getBranches()
    {
        return branches;
    }

    /**
     * Creates a IDSegment, overwrite in subclasses to create a specific
     * subclass of IDSegment
     * 
     * @return a new IDSegment
     */
    public IDSegment createIDSegment()
    {
        return new IDSegment();
    }

    /**
     * @return the pHuman
     */
    public PhysicalHumanoid getPHuman()
    {
        return pHuman;
    }
}

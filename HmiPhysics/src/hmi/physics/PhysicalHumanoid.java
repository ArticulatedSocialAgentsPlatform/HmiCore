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
package hmi.physics;

import hmi.animation.VJoint;
import hmi.math.Mat4f;
import hmi.math.Quat4f;
import hmi.math.Vec3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class PhysicalHumanoid
{
    private ArrayList<PhysicalHumanoidListener> phListeners = new ArrayList<PhysicalHumanoidListener>();

    private boolean bIsEnabled = true;
    private float[] COM = new float[3];
    private float[] COMDiff = new float[3];

    private float[] COMNoContacts = new float[3];
    private float[] COMNoContactsDiff = new float[3];
    private float[] COMOffset = new float[3];
    private float[] COMOffsetDiff = new float[3];

    private float[] vecTemp = new float[3];
    private float[] qTemp = new float[4];

    // all physical segments besides the root segment
    private List<PhysicalSegment> segments = new ArrayList<PhysicalSegment>();
    protected List<PhysicalJoint> joints = new ArrayList<PhysicalJoint>();
    private PhysicalSegment rootSegment;

    // maps for quick access to physical joints and segments, segment maps include the root segment
    protected HashMap<String, PhysicalSegment> nameSegmentMap = new HashMap<String, PhysicalSegment>();
    protected HashMap<String, PhysicalJoint> namePhysicalJointMap = new HashMap<String, PhysicalJoint>();

    // simulation data to set on the humanoid
    protected float[] rootTranslationBuffer;
    protected float[] rootRotationBuffer;
    protected float[] COMBuffer = new float[3];
    protected float[] COMDiffBuffer = new float[3];
    protected float[] COMOffsetBuffer = new float[3];
    protected float[] COMOffsetDiffBuffer = new float[3];

    private float offsetMass = 0;
    private String id = "";

    // temp vars
    float m[] = new float[16];

    public void removeSegment(PhysicalSegment segment)
    {
        segments.remove(segment);
    }

    /**
     * Constructor
     */
    public PhysicalHumanoid(String id)
    {
        this.id = id;
        for (int i = 0; i < 3; i++)
        {
            COM[i] = 0;
            COMDiff[i] = 0;
            COMNoContacts[i] = 0;
            COMNoContactsDiff[i] = 0;
        }
    }

    public void addSegment(PhysicalSegment segment)
    {
        segments.add(segment);
        setupHashMaps(segment);
    }

    public void addRootSegment(PhysicalSegment segment)
    {
        rootSegment = segment;
        setupHashMaps(segment);
    }

    /**
     * Sets the CoM and derivates + all segments with the matching names to the state of ph COMOffset is set to 0
     * 
     * @param ph
     */
    public void set(PhysicalHumanoid ph)
    {
        Vec3f.set(COM, ph.COM);
        Vec3f.set(COMDiff, ph.COMDiff);
        Vec3f.set(COMNoContacts, ph.COMNoContacts);
        Vec3f.set(COMNoContactsDiff, ph.COMNoContactsDiff);
        Vec3f.set(COMOffset, 0, 0, 0);
        if (rootSegment != null && ph.rootSegment != null)
        {
            rootSegment.box.setTorque(0, 0, 0);
            rootSegment.box.setForce(0, 0, 0);
            if (rootSegment.getSid().equals(ph.rootSegment.getSid()))
            {
                ph.rootSegment.getRotation(qTemp);
                rootSegment.setRotation(qTemp);
                ph.rootSegment.getAngularVelocity(vecTemp);
                rootSegment.setAngularVelocity(vecTemp);
                ph.rootSegment.getTranslation(vecTemp);
                rootSegment.setTranslation(vecTemp);
                ph.rootSegment.getVelocity(vecTemp);
                rootSegment.setVelocity(vecTemp);

                ph.rootSegment.box.getForce(vecTemp);
                rootSegment.box.addForce(vecTemp);
                ph.rootSegment.box.getTorque(vecTemp);
                rootSegment.box.addTorque(vecTemp);
            }
        }
        for (PhysicalSegment ps : segments)
        {
            ps.box.setTorque(0, 0, 0);
            ps.box.setForce(0, 0, 0);
            for (PhysicalSegment srcPs : ph.segments)
            {
                if (ps.getSid().equals(srcPs.getSid()))
                {
                    // System.out.println("Copying "+ps.getSid());

                    srcPs.getRotation(qTemp);
                    ps.setRotation(qTemp);
                    srcPs.getAngularVelocity(vecTemp);
                    ps.setAngularVelocity(vecTemp);
                    srcPs.getTranslation(vecTemp);
                    ps.setTranslation(vecTemp);
                    srcPs.getVelocity(vecTemp);
                    ps.setVelocity(vecTemp);
                    srcPs.box.getTorque(vecTemp);
                    ps.box.addTorque(vecTemp);
                    srcPs.box.getForce(vecTemp);
                    ps.box.addForce(vecTemp);
                }
            }
        }
    }

    /**
     * Creates a PhysicalJoint
     * 
     * @param name joint name
     * @param seg1 corresponds with visual parent segment
     * @param seg2 corresponds with visual segment that rotates with the joint
     * @param anchor world position of the joint anchor
     * @return the created joint
     */
    public abstract PhysicalJoint setupJoint(String name, PhysicalSegment seg1, PhysicalSegment seg2, float[] anchor);

    /**
     * Creates a PhysicalSegment of the correct type for this PhysicalHumanoid subclass and makes sure its rigid body exists
     * 
     * @return the created segment
     */
    public abstract PhysicalSegment createSegment(String segmentId, String segmentSid);

    /**
     * Create a new PhysicalHumanoid of the same type
     */
    public abstract PhysicalHumanoid createNew(String id);

    /**
     * Gets an array of segments, including the root segment. This is a copy of the actual arraylist containing the segments, so changes to the array
     * itself do not affect the PhysicalHumanoid.
     */
    public PhysicalSegment[] getSegments()
    {
        PhysicalSegment[] ps;
        if (rootSegment != null)
        {
            ps = new PhysicalSegment[segments.size() + 1];
            ps[0] = rootSegment;
            int i = 1;
            for (PhysicalSegment p : segments)
            {
                ps[i] = p;
                i++;
            }
        }
        else
        {
            ps = new PhysicalSegment[0];
        }
        return ps;
    }

    /**
     * Get the PhysicalJoint with this name in the Humanoid
     * 
     * @param name joint name
     * @return the PhysicalJoint
     */
    public PhysicalJoint getJoint(String name)
    {
        PhysicalJoint pj = namePhysicalJointMap.get(name);
        if (pj == null)
        {
            pj = namePhysicalJointMap.get(name + "_joint");
        }
        if (pj == null)
        {
            // System.out.println("No joint named \"" + name+"\" or \"" + name+"_joint\" found in the following map:");
            // System.out.println(namePhysicalJointMap);
        }
        return pj;
    }

    /**
     * Get the PhysicalSegment with this start joint
     * 
     * @param name name of the startJoint
     * @return the PhysicalSegment
     */
    public PhysicalSegment getSegment(String name)
    {
        // System.out.println(nameSegmentMap);
        PhysicalSegment seg = nameSegmentMap.get(name);
        if (seg == null)
        {
            seg = nameSegmentMap.get(name + "_segment");
        }
        if (seg == null)
        {
            // System.out.println("No segment named \"" + name+"\" or \"" + name+"_segment\" found in the following map:");
            // System.out.println(nameSegmentMap);
            // throw new RuntimeException();
        }
        return seg;
    }

    /**
     * Removes a physical segment from the name->segment hashmap
     * 
     * @param ps the segment to remove
     */
    public void removeFromHashMap(PhysicalSegment ps)
    {
        nameSegmentMap.remove(ps.getSid());
    }

    /**
     * Add a PhysicalSegment to the name->segment HashMap
     * 
     * @param ps the segment
     */
    public void setupHashMaps(PhysicalSegment ps)
    {
        if (ps.getSid() != null)
        {
            nameSegmentMap.put(ps.getSid(), ps);
        }
    }

    /*
     * public ConfigList getCurrentConfig() { ConfigList list = new ConfigList(1);
     * 
     * return list; }
     */

    /**
     * Gets the current config, that is, position, rotation + derivates for all rigid bodies,
     */
    /*
     * public PhysicalHumanoidConfig getCurrentConfig() { ArrayList<SegmentConfiguration> segmentConfig = new ArrayList<SegmentConfiguration>();
     * 
     * SegmentConfiguration sc = new SegmentConfiguration(); sc.segmentName = rootSegment.getName(); sc.config = rootSegment.getCurrentConfig();
     * segmentConfig.add(sc);
     * 
     * for (PhysicalSegment ps:segments) { sc = new SegmentConfiguration(); sc.segmentName = ps.getName(); sc.config = ps.getCurrentConfig();
     * segmentConfig.add(sc); } PhysicalHumanoidConfig phc = new PhysicalHumanoidConfig(); phc.segConfigs = segmentConfig; return phc; }
     */
    /**
     * Sets a physicalhumanoid config, that is, position, rotation + derivates for all rigid bodies, sets all forces and torques to zero
     */
    /*
     * public void setConfig(PhysicalHumanoidConfig phc) { for (SegmentConfiguration sc:phc.segConfigs) { PhysicalSegment ps =
     * getSegment(sc.segmentName); ps.setConfig(sc.config); } for (PhysicalJoint pj: joints) { //disable motors pj.setMaximumForce(0, 0);
     * pj.setMaximumForce(1, 0); pj.setMaximumForce(2, 0); } }
     */

    /**
     * copy the simulation info to the root and joint local translation and rotation buffers
     */
    public void copy()
    {
        setRootSimInfo();

        for (PhysicalJoint pj : joints)
        {
            float[] buffer = pj.getRotationBuffer();
            if (buffer != null)
            {
                pj.getRotation(buffer);
            }
        }
        if (rootSegment != null)
        {
            rootSegment.box.copy();
        }
        for (PhysicalSegment seg : segments)
        {
            seg.box.copy();
        }
        Vec3f.set(COMBuffer, COM);
        Vec3f.set(COMDiffBuffer, COMDiff);
        Vec3f.set(COMOffsetBuffer, COMOffset);
        Vec3f.set(COMOffsetDiffBuffer, COMOffsetDiff);
        if (getCOMOffsetMass() > 0)
        {
            Vec3f.scale(1f / getCOMOffsetMass(), COMOffsetDiffBuffer);
            Vec3f.scale(1f / getCOMOffsetMass(), COMOffsetBuffer);
        }
    }

    /**
     * Checks if segments touch the ground and updates their onGrounds accordingly. The default implementation sets all onGrounds to false, subclasses
     * have to implement the handleCollisions function to detect ground collision.
     */
    public void updateCollision()
    {
        // clear ground contacts
        for (PhysicalSegment ps : segments)
        {
            ps.onGround = false;
        }
        rootSegment.onGround = false;

        handleCollisions();
    }

    /**
     * Disables or enables the PhysicalHumanoid. Disabled Humanoids are not updated with the physical simulation step and do not collide with other
     * bodies.
     * 
     * @param enabled
     */
    public void setEnabled(boolean enabled)
    {
        bIsEnabled = enabled;
        if (rootSegment != null)
        {
            rootSegment.setEnabled(enabled);
        }
        for (PhysicalSegment ps : segments)
        {
            ps.setEnabled(enabled);
        }
        notifyPhysicalHumanListeners();
    }

    public boolean isEnabled()
    {
        return bIsEnabled;
    }

    public void addPhysicalHumanoidListener(PhysicalHumanoidListener phl)
    {
        synchronized (phListeners)
        {
            phListeners.add(phl);
        }
    }

    public void removePhysicalHumanoidListener(PhysicalHumanoidListener phl)
    {
        synchronized (phListeners)
        {
            phListeners.remove(phl);
        }
    }

    private void notifyPhysicalHumanListeners()
    {
        synchronized (phListeners)
        {
            for (PhysicalHumanoidListener phl : phListeners)
            {
                phl.physicalHumanEnabled(this, bIsEnabled);
            }
        }
    }

    /**
     * Gets the total mass of the humanoid
     * 
     * @return the total mass
     */
    public float getTotalMass()
    {
        float totalMass = rootSegment.box.getMass();
        for (PhysicalSegment ps : segments)
        {
            totalMass += ps.box.getMass();
        }
        return totalMass;
    }

    /**
     * Offsets the COM
     * 
     * @param offset offset, scaled by mass
     * @param mass mass of the offset
     */
    public void setCOMOffset(float offset[], float mass)
    {
        Vec3f.set(COMOffsetDiff, offset);
        Vec3f.sub(COMOffsetDiff, COMOffset);
        Vec3f.set(COMOffset, offset);
        offsetMass = mass;
    }

    public float getCOMOffsetMass()
    {
        return offsetMass;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP", justification = "Representations are exposed for efficiency reasons")
    public float[] getCOMOffset()
    {
        return COMOffset;
    }

    /**
     * Calculates the new Center Of Mass COM and COMDiff (=dCOM/dt)
     * 
     * @param timeStep time since last update
     */
    public void updateCOM(float timeStep)
    {
        for (int i = 0; i < 3; i++)
        {
            v1[i] = 0;
            v3[i] = 0;
        }

        float totalMass = 0;
        float noContactMass = 0;
        if (rootSegment != null)
        {
            rootSegment.box.getTranslation(v2);
            Vec3f.scale(rootSegment.box.getMass(), v2);
            Vec3f.add(v1, v2);
            Vec3f.add(v3, v2);

            noContactMass = rootSegment.box.getMass();
            totalMass = rootSegment.box.getMass();
        }

        totalMass += offsetMass;
        Vec3f.add(v1, COMOffset);
        Vec3f.add(v3, COMOffset);

        for (PhysicalSegment ps : segments)
        {
            ps.box.getTranslation(v2);
            Vec3f.scale(ps.box.getMass(), v2);
            Vec3f.add(v1, v2);
            totalMass += ps.box.getMass();
            if (!ps.onGround)
            {
                Vec3f.add(v3, v2);
                noContactMass += ps.box.getMass();
            }
        }
        Vec3f.scale(1.0f / totalMass, v1);
        Vec3f.scale(1.0f / noContactMass, v3);

        if (timeStep == 0.0)
        {
            for (int i = 0; i < 3; i++)
            {
                COMDiff[i] = 0;
                COMOffsetDiff[i] = 0;
                COMNoContactsDiff[i] = 0;
            }
        }
        else
        {

            Vec3f.scale(1f / timeStep, COMOffsetDiff);

            Vec3f.set(v2, v1);
            Vec3f.sub(v2, COM);
            Vec3f.scale(1f / timeStep, v2);
            Vec3f.set(COMDiff, v2);

            Vec3f.set(v2, v3);
            Vec3f.sub(v2, COMNoContacts);
            Vec3f.scale(1f / timeStep, v2);
            Vec3f.set(COMNoContactsDiff, v2);
        }
        Vec3f.set(COM, v1);
        Vec3f.set(COMNoContacts, v3);

        if (Vec3f.length(COMDiff) > 1)
        {
            // System.out.println("com offset ("+timeStep+")"+Vec3f.toString(COMOffset));
            // System.out.println("com diff   ("+timeStep+")"+Vec3f.toString(COMDiff));
        }

    }

    /**
     * Get String representation
     */
    @Override
    public String toString()
    {
        return "Segments: " + segments.toString() + "\n" + "Joints: " + joints.toString();
    }

    /**
     * @return the last calculated COM, use updateCOM to calculate the current COM
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP", justification = "Representations are exposed for efficiency reasons")
    public float[] getCOM()
    {
        return COM;
    }

    /**
     * @return the last calculated COMDiff, use updateCOM to calculate the current COMDiff
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP", justification = "Representations are exposed for efficiency reasons")
    public float[] getCOMDiff()
    {
        return COMDiff;
    }

    /**
     * Get the COM excluding feet weights
     * 
     * @return the COMNoFeet
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP", justification = "Representations are exposed for efficiency reasons")
    public float[] getCOMNoContacts()
    {
        return COMNoContacts;
    }

    /**
     * Get the dCOM/dt excluding feet weights
     * 
     * @return the COMNoFeetDiff
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP", justification = "Representations are exposed for efficiency reasons")
    public float[] getCOMNoContactsDiff()
    {
        return COMNoContactsDiff;
    }

    /**
     * @param rootRotationBuffer the rootRotationBuffer to set
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Representations are exposed for efficiency reasons")
    public void setRootRotationBuffer(float[] rootRotationBuffer)
    {
        this.rootRotationBuffer = rootRotationBuffer;
    }

    /**
     * @param rootTranslationBuffer the rootTranslationBuffer to set
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Representations are exposed for efficiency reasons")
    public void setRootTranslationBuffer(float[] rootTranslationBuffer)
    {
        this.rootTranslationBuffer = rootTranslationBuffer;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Representations are exposed for efficiency reasons")
    public void setCOMBuffer(float[] COMBuffer)
    {
        this.COMBuffer = COMBuffer;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Representations are exposed for efficiency reasons")
    public void setCOMDiffBuffer(float[] COMDiffBuffer)
    {
        this.COMDiffBuffer = COMDiffBuffer;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Representations are exposed for efficiency reasons")
    public void setCOMOffsetDiffBuffer(float[] COMOffsetDiffBuffer)
    {
        this.COMOffsetDiffBuffer = COMOffsetDiffBuffer;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Representations are exposed for efficiency reasons")
    public void setCOMOffsetBuffer(float[] COMOffsetBuffer)
    {
        this.COMOffsetBuffer = COMOffsetBuffer;
    }

    private void setRootSimInfo()
    {
        if (rootSegment != null)
        {
            Vec3f.set(v1, rootSegment.startJointOffset);
            rootSegment.box.getRotation(rootRotationBuffer);

            Quat4f.transformVec3f(rootRotationBuffer, v1);
            rootSegment.box.getTranslation(v2);
            Vec3f.add(rootTranslationBuffer, v1, v2);
        }
    }

    /**
     * Cleans up joint structure, call when the human is removed
     */
    public void clear()
    {
        if (rootSegment != null)
        {
            rootSegment.clear();
        }
        for (PhysicalSegment s : segments)
        {
            s.clear();
        }
        segments.clear();
        joints.clear();
        rootSegment = null;
        nameSegmentMap.clear();
        namePhysicalJointMap.clear();
    }

    /**
     * Translates the physical humanoid
     * 
     * @param trans new physical world position of the humanoid
     */
    public void setRelativeTranslation(float[] trans)
    {
        float oldTrans[] = new float[3];
        float absTrans[] = new float[3];
        rootSegment.getTranslation(oldTrans);
        Vec3f.add(absTrans, trans, oldTrans);
        rootSegment.setTranslation(absTrans);
        for (PhysicalSegment s : segments)
        {
            s.getTranslation(oldTrans);
            Vec3f.add(absTrans, trans, oldTrans);
            s.setTranslation(absTrans);
        }
    }

    /**
     * Rotates the physical humanoid around some center
     * 
     * @param center
     * @param q
     */
    public void setRotation(float[] center, float q[])
    {
        float oldTrans[] = new float[3];
        float trans[] = new float[3];

        rootSegment.getTranslation(oldTrans);
        Vec3f.sub(trans, oldTrans, center);
        rootSegment.setRotation(q);
        Quat4f.transformVec3f(q, trans);
        rootSegment.setTranslation(trans);

        for (PhysicalSegment s : segments)
        {
            s.getTranslation(oldTrans);
            Vec3f.sub(trans, oldTrans, center);
            Quat4f.transformVec3f(q, trans);
            s.setRotation(q);
            s.setTranslation(trans);
        }
    }

    /**
     * Translates the physical humanoid
     */
    public void setRelativeTranslation(float x, float y, float z)
    {
        float oldTrans[] = new float[3];
        float absTrans[] = new float[3];
        rootSegment.getTranslation(oldTrans);
        Vec3f.set(absTrans, x, y, z);
        Vec3f.add(absTrans, oldTrans);
        rootSegment.setTranslation(absTrans);
        for (PhysicalSegment s : segments)
        {
            s.getTranslation(oldTrans);
            Vec3f.set(absTrans, x, y, z);
            Vec3f.add(absTrans, oldTrans);
            s.setTranslation(absTrans);
        }
    }

    protected void handleCollisions()
    {

    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    private void setJoint(VJoint vj)
    {
        PhysicalSegment ps = getSegment(vj.getSid());
        float v[] = new float[3];
        if (ps != null)
        {
            m = vj.getGlobalMatrix();
            Quat4f.setFromMat4f(qTemp, m);
            Vec3f.scale(-1, v, ps.startJointOffset);
            Mat4f.transformPoint(m, vecTemp, v);
            ps.setTranslation(vecTemp);
            ps.setRotation(qTemp);
        }
    }

    public void setPoseFromVJoint(VJoint vj)
    {
        for (VJoint vjParts : vj.getParts())
        {
            setJoint(vjParts);
        }
    }

    public void setNullPoseFromVJoint(VJoint vj)
    {
        vj.calculateMatrices();
        for (VJoint vjParts : vj.getParts())
        {
            setNullPoseForJoint(vjParts);
        }
    }

    private void setNullPoseForJoint(VJoint vj)
    {
        PhysicalSegment ps = getSegment(vj.getSid());
        float v[] = new float[3];
        float vTemp[] = new float[3];
        float vTempOld[] = new float[3];
        float qTemp2[] = new float[4];
        if (ps != null)
        {
            ps.getTranslation(vTempOld);
            m = vj.getGlobalMatrix();
            Quat4f.setFromMat4f(qTemp, m);
            Vec3f.scale(-1, v, ps.startJointOffset);
            Mat4f.transformPoint(m, vecTemp, v);
            ps.setTranslation(vecTemp);

            // rotate collision representation
            for (CollisionShape sh : ps.box.getCollisionShapes())
            {
                sh.getTranslation(vTemp);
                Quat4f.transformVec3f(qTemp, vTemp);
                sh.setTranslation(vTemp);

                sh.getRotation(qTemp2);
                Quat4f.mul(qTemp2, qTemp, qTemp2);
                Quat4f.normalize(qTemp2);
                sh.setRotation(qTemp2);
                // sh.setRotation(qTemp);
            }

            // set inertia tensor
            ps.box.rotateInertiaTensor(qTemp);
            // Vec3f.sub(vecTemp, vTempOld);
            // ps.box.translateInertiaTensor(vecTemp);

            // reset joint, joint offset
            Quat4f.transformVec3f(qTemp, ps.startJointOffset);
            float anchor[] = new float[3];
            vj.getPathTranslation(null, anchor);
            if (ps.startJoint != null)
            {
                ps.startJoint.setAnchor(anchor[0], anchor[1], anchor[2]);
            }
        }
    }

    // temp vars, so it's not necesary to recreate them every simulation step
    private float[] v1 = new float[3];
    private float[] v2 = new float[3];
    private float[] v3 = new float[3];

    /**
     * @return the cOMOffsetDiff, scaled by mass
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP", justification = "Representations are exposed for efficiency reasons")
    public float[] getCOMOffsetDiff()
    {
        return COMOffsetDiff;
    }

    /**
     * @return the rootSegment
     */
    public PhysicalSegment getRootSegment()
    {
        return rootSegment;
    }

    public void setRootSegment(PhysicalSegment rs)
    {
        rootSegment = rs;
    }

    /**
     * @return an unmodifiable view of the joint list
     */
    public List<PhysicalJoint> getJoints()
    {
        return Collections.unmodifiableList(joints);
    }
}

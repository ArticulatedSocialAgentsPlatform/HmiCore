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
package hmi.physics.ode;

import hmi.math.Vec3f;
import hmi.physics.CollisionShape;
import hmi.physics.PhysicalHumanoid;
import hmi.physics.PhysicalJoint;
import hmi.physics.PhysicalSegment;

import java.util.HashMap;

import org.odejava.GeomPlane;
import org.odejava.JointGroup;
import org.odejava.Space;
import org.odejava.World;
import org.odejava.collision.Contact;
import org.odejava.collision.JavaCollision;

public class OdeHumanoid extends PhysicalHumanoid
{
    private World world;
    private Space space;
    private JointGroup jointGroup;
    private GeomPlane groundGeom = null;
    private JavaCollision collision = null;
    private Contact contact = null;

    // geom id -> PhsyicalSegment
    private HashMap<Long, PhysicalSegment> geomSegmentMap = new HashMap<Long, PhysicalSegment>();

    /**
     * Constructor
     * 
     * @param w Ode world
     * @param s Ode collision space
     */
    public OdeHumanoid(String id, World w, Space s)
    {
        super(id);
        world = w;
        space = s;
        jointGroup = new JointGroup();
    }

    @Override
    public OdeHumanoid createNew(final String id)
    {
        return new OdeHumanoid(id, world, space);
    }

    /**
     * removes all segments
     */
    @Override
    public void clear()
    {
        jointGroup.empty();
        geomSegmentMap.clear();
        super.clear();
    }

    @Override
    public PhysicalJoint setupJoint(String name, PhysicalSegment seg1, PhysicalSegment seg2, float[] anchor)
    {
        OdeJoint joint = new OdeJoint(seg2.jointType, name, world, jointGroup);
        joint.attach(seg1.box, seg2.box);

        joint.setAnchor(anchor[0], anchor[1], anchor[2]);

        if (Vec3f.epsilonEquals(seg2.axis1, Vec3f.getUnitY(), 0.001f))// ODE-specific hack, prevents warnings of axis lining up
        {
            joint.setAxis(1, seg2.axis2[0], seg2.axis2[1], seg2.axis2[2]);
            joint.setAxis(0, seg2.axis1[0], seg2.axis1[1], seg2.axis1[2]);
        }
        else
        {
            joint.setAxis(0, seg2.axis1[0], seg2.axis1[1], seg2.axis1[2]);
            joint.setAxis(1, seg2.axis2[0], seg2.axis2[1], seg2.axis2[2]);
        }

        /*
         * joint.setJointMin(0, seg2.loStop[0]); joint.setJointMin(1, seg2.loStop[1]); joint.setJointMin(2, seg2.loStop[2]); joint.setJointMax(0,
         * seg2.hiStop[0]); joint.setJointMax(1, seg2.hiStop[1]); joint.setJointMax(2, seg2.hiStop[2]);
         */
        joint.setJointMax(0, -seg2.loStop[0]);
        joint.setJointMax(1, -seg2.loStop[1]);
        joint.setJointMax(2, -seg2.loStop[2]);
        joint.setJointMin(0, -seg2.hiStop[0]);
        joint.setJointMin(1, -seg2.hiStop[1]);
        joint.setJointMin(2, -seg2.hiStop[2]);

        joints.add(joint);
        namePhysicalJointMap.put(name, joint);
        return joint;
    }

    @Override
    public void setupHashMaps(PhysicalSegment ps)
    {
        super.setupHashMaps(ps);
        if (ps.box != null)
        {
            for (CollisionShape cs : ps.box.getCollisionShapes())
            {
                OdeCollisionShape ocs = (OdeCollisionShape) cs;
                geomSegmentMap.put(ocs.getCollisionGeom().getNativeAddr(), ps);
            }
        }
    }

    public void setCollision(JavaCollision col)
    {
        collision = col;
        contact = new Contact(collision.getContactIntBuffer(), collision.getContactFloatBuffer());
    }

    /**
     * Determine collisions, for now just with the ground
     */
    @Override
    protected void handleCollisions()
    {
        if (collision != null)
        {
            int collisions = collision.getContactCount();
            long groundId = groundGeom.getNativeAddr();
            for (int i = 0; i < collisions; i++)
            {
                contact.setIndex(i);
                PhysicalSegment ps = null;
                /*
                 * System.out.println("Collision: contact 1 "+contact.getGeomID1()); System.out.println("           contact 2 "+contact.getGeomID2());
                 * System.out.println("           ground    "+groundId);
                 */
                if (contact.getGeomID1() == groundId)
                {
                    ps = getSegment(contact.getGeomID2());
                }
                else if (contact.getGeomID2() == groundId)
                {
                    ps = getSegment(contact.getGeomID1());
                }
                if (ps != null)
                {
                    ps.onGround = true;
                }
            }
        }
    }

    /**
     * @param groundGeom the groundGeom to set
     */
    public void setGroundGeom(GeomPlane groundGeom)
    {
        this.groundGeom = groundGeom;
    }

    /**
     * Get the PhysicalSegment corresponding with this geometry id
     * 
     * @param geomId the geometry id
     * @return the PhysicalSegment, null if not found
     */
    public PhysicalSegment getSegment(long geomId)
    {
        return geomSegmentMap.get(geomId);
    }

    @Override
    public PhysicalSegment createSegment(String segmentId, String segmentSID)
    {
        return new OdePhysicalSegment(segmentId, segmentSID, world, space);
    }
}

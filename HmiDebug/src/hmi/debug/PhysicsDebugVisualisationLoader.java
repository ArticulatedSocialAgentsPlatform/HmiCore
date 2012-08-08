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
package hmi.debug;

import hmi.animation.VJoint;
import hmi.environmentbase.EmbodimentLoader;
import hmi.environmentbase.Environment;
import hmi.environmentbase.Loader;
import hmi.math.Quat4f;
import hmi.math.Vec3f;
import hmi.physics.CollisionBox;
import hmi.physics.CollisionCapsule;
import hmi.physics.CollisionShape;
import hmi.physics.CollisionSphere;
import hmi.physics.JointType;
import hmi.physics.PhysicalHumanoid;
import hmi.physics.PhysicalHumanoidListener;
import hmi.physics.PhysicalSegment;
import hmi.physics.RigidBody;
import hmi.physicsenvironment.OdePhysicalEmbodiment;
import hmi.renderenvironment.HmiRenderEnvironment;
import hmi.renderenvironment.HmiRenderEnvironment.RenderStyle;
import hmi.util.PhysicsSync;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Builds VGLNodes to visualise physical objects (RigidBodies, PhysicalHumanoids, ...).
 * 
 */
@Slf4j
public class PhysicsDebugVisualisationLoader implements Loader, PhysicalHumanoidListener
{

    // some parameters for caching during XML loading

    private XMLStructureAdapter adapter = new XMLStructureAdapter();
    private HmiRenderEnvironment hre = null;
    private OdePhysicalEmbodiment ope = null;

    @Getter
    @Setter
    private String id = "";
    private String vhId = "";

    private HashMap<PhysicalHumanoid, String> debugJoints = new HashMap<PhysicalHumanoid, String>();

    private float[] grey = new float[] { 0.7f, 0.7f, 0.7f, 1f };

    @Override
    public void unload()
    {
        for (Entry<PhysicalHumanoid, String> e : debugJoints.entrySet())
        {
            hre.unloadObject(e.getValue());
        }
    }

    @Override
    public void readXML(XMLTokenizer tokenizer, String loaderId, String vhId, String vhName, Environment[] environments, Loader... requiredLoaders)
    throws IOException
	{
		this.id = loaderId;
		this.vhId = vhId;

        for (Environment e : environments)
        {
            if (e instanceof HmiRenderEnvironment) hre = (HmiRenderEnvironment) e;
        }
        for (Loader e : requiredLoaders)
        {
            if (e instanceof EmbodimentLoader && ((EmbodimentLoader) e).getEmbodiment() 
                    instanceof OdePhysicalEmbodiment) ope = (OdePhysicalEmbodiment) ((EmbodimentLoader) e)
                    .getEmbodiment();
        }
        if (ope == null)
        {
            throw new RuntimeException("PhysicsDebugVisualisationLoader requires an Embodiment of type OdePhysicalEmbodiment");
        }
        if (hre == null)
        {
            throw new RuntimeException("PhysicsDebugVisualisationLoader requires an HmiRenderEnvironment");
        }
        float[] debugTranslation = new float[] { 0, 0, 0 };
        float[] debugOrientation = new float[] { 0, 0, 0, 0 };

        if (tokenizer.atSTag("Placement"))
        {
            HashMap<String, String> attrMap = tokenizer.getAttributes();
            String offsetString = adapter.getOptionalAttribute("offset", attrMap, "0 0 0");
            debugTranslation = XMLStructureAdapter.decodeFloatArray(offsetString);
            if (debugTranslation.length != 3) throw tokenizer.getXMLScanException("Placement.offset must containg a 3-float array");
            String rotString = adapter.getOptionalAttribute("rotation", attrMap, "0 0 0 0");
            debugOrientation = XMLStructureAdapter.decodeFloatArray(rotString);
            if (debugOrientation.length != 4) throw tokenizer.getXMLScanException("Placement.rotation must containg a 4-float array");
            tokenizer.takeSTag("Placement");
            tokenizer.takeETag("Placement");
        }

        ArrayList<PhysicalHumanoid> phs = ope.getPhysicalHumans();
        for (PhysicalHumanoid ph : phs)
        {
            VJoint vjPH = getPhysicalHumanDebugVisualisation(ph);
            VJoint parent = hre.insertParentJointForObject(vjPH.getId());
            parent.setTranslation(debugTranslation);
            float[] qRot = new float[4];
            Quat4f.setFromAxisAngle4f(qRot, debugOrientation);
            parent.rotate(qRot);
            debugJoints.put(ph, parent.getId());
            ph.addPhysicalHumanoidListener(this);
            hre.setObjectVisible(parent.getId(), false);
        }
    }

    @Override
    public void physicalHumanEnabled(PhysicalHumanoid ph, boolean enabled)
    {
        log.debug(ph.getId());
        hre.setObjectVisible(debugJoints.get(ph), enabled);
    }

    /**
     * @param renderCenter if true, a small ball is shown in the center coordinates (is NOT necessarily the center of gravity!)
     */
    public VJoint getRigidBodyDebugVisualisation(RigidBody rigidBody, String rbId, boolean renderCenter)
    {
        hre.loadEmptyObject(rbId);
        VJoint rbVJoint = hre.getObjectRootJoint(rbId);

        if (renderCenter)
        {
            hre.loadSphere(rbId + "_center", 0.02f, 4, 4, RenderStyle.FILL, grey, grey, grey, grey);
            hre.setObjectParent(rbId + "_center", rbId);
        }

        // add children for every collisionshape
        synchronized (PhysicsSync.getSync())
        {
            int i = 0;
            for (CollisionShape collShape : rigidBody.getCollisionShapes())
            {
                VJoint vjColl = getCollisionShapeDebugVisualisation(collShape, rbId + (i++));
                hre.setObjectParent(vjColl.getId(), rbId);
            }
        }

        // connect buffers to the rigidbody
        rigidBody.addTranslationBuffer(rbVJoint.getTranslationBuffer());
        rigidBody.addRotationBuffer(rbVJoint.getRotationBuffer());

        return rbVJoint;
    }

    public VJoint getCollisionShapeDebugVisualisation(CollisionShape collShape, String id)
    {
        if (collShape instanceof CollisionBox)
        {
            CollisionBox box = (CollisionBox) collShape;
            hre.loadBox(id, box.halfExtends, RenderStyle.LINE, grey, grey, grey, grey);
        }
        else if (collShape instanceof CollisionSphere)
        {
            CollisionSphere s = (CollisionSphere) collShape;
            hre.loadSphere(id, s.radius, 8, 8, RenderStyle.LINE, grey, grey, grey, grey);
        }
        else if (collShape instanceof CollisionCapsule)
        {
            CollisionCapsule c = (CollisionCapsule) collShape;
            hre.loadCapsule(id, c.radius, c.height, 8, 8, RenderStyle.LINE, grey, grey, grey, grey);
        }
        else
        {
            hre.loadSphere(id, 0.2f, 8, 8, RenderStyle.VERTEX, grey, grey, grey, grey);
        }
        VJoint csVJoint = hre.insertParentJointForObject(id);
        float tr[] = new float[3];
        collShape.getTranslation(tr);
        csVJoint.setTranslation(tr); // because the collisionshape may be not
                                     // exactly centegrey in the rigid body
        float q[] = new float[4];
        collShape.getRotation(q);
        csVJoint.setRotation(q); // because the collisionshape may be not
                                 // exactly centegrey in the rigid body
        return csVJoint;
    }

    public VJoint getPhysicalSegmentDebugVisualisation(PhysicalSegment ps, String idPrefix)
    {

        VJoint vjPS = getRigidBodyDebugVisualisation(ps.box, idPrefix + ps.box.getId(), true);
        // also, add the joint indicators
        if (ps.startJoint != null)
        {
            if (ps.startJoint.getType() != JointType.FIXED)
            {
                float jointOffset[] = new float[3];
                float[] pos = new float[3];
                float[] rot = new float[4];
                ps.box.getTranslation(pos);
                ps.box.getRotation(rot);
                ps.startJoint.getAnchor(jointOffset);
                Vec3f.sub(jointOffset, pos);
                Quat4f.inverse(rot);
                Quat4f.transformVec3f(rot, jointOffset);

                float ambient[] = { 0, 0, 0, 0 };
                float emission[] = { 0, 0, 0, 1 };
                float specular[] = { 0, 0, 0, 1 };
                String sjId = idPrefix + ps.box.getId() + "_sj";
                switch (ps.startJoint.getType())
                {
                case HINGE:
                    hre.loadDisc(sjId, 0.01f, 0.03f, 0.03f, 10, 10, RenderStyle.FILL, new float[] { 1f, 0.0f, 0.0f, 1 }, specular, ambient,
                            emission);
                    break;
                case UNIVERSAL:
                    hre.loadBox(sjId, new float[] { 0.01f, 0.01f, 0.01f }, RenderStyle.FILL, new float[] { 0f, 1f, 0.0f, 1 }, specular,
                            ambient, emission);
                    break;
                case BALL:
                    hre.loadSphere(sjId, 0.03f, 10, 10, RenderStyle.FILL, new float[] { 0f, 0f, 1f, 1 }, specular, ambient, emission);
                    break;
                default:
                    break;
                }
                VJoint joint = hre.getObjectRootJoint(sjId);
                joint.setTranslation(jointOffset);
                hre.setObjectParent(sjId, vjPS.getId());
            }
        }
        return vjPS;

    }

    public VJoint getPhysicalHumanDebugVisualisation(PhysicalHumanoid ph)
    {
        String idPrefix = vhId + "_phdebug_" + ph.getId() + "_";
        hre.loadSphere(idPrefix, 0.02f, 4, 4, RenderStyle.FILL, grey, grey, grey, grey);
        VJoint phVJoint = hre.getObjectRootJoint(idPrefix);
        hre.setObjectVisible(idPrefix, false);

        synchronized (PhysicsSync.getSync())
        {
            if (ph.getRootSegment() != null)
            {
                getRigidBodyDebugVisualisation(ph.getRootSegment().box, idPrefix + "_root", false);
                hre.setObjectParent(idPrefix + "_root", idPrefix);
            }
            for (PhysicalSegment ps : ph.getSegments())
            {
                getRigidBodyDebugVisualisation(ps.box, idPrefix + "_" + ps.getId(), false);
                // also, add the joint indicators
                if (ps.startJoint != null)
                {
                    if (ps.startJoint.getType() != JointType.FIXED)
                    {
                        float jointOffset[] = new float[3];
                        float[] pos = new float[3];
                        float[] rot = new float[4];
                        ps.box.getTranslation(pos);
                        ps.box.getRotation(rot);
                        ps.startJoint.getAnchor(jointOffset);
                        Vec3f.sub(jointOffset, pos);
                        Quat4f.inverse(rot);
                        Quat4f.transformVec3f(rot, jointOffset);

                        float ambient[] = { 0, 0, 0, 0 };
                        float emission[] = { 0, 0, 0, 1 };
                        float specular[] = { 0, 0, 0, 1 };
                        String sjId = idPrefix + ps.box.getId() + "_sj";
                        switch (ps.startJoint.getType())
                        {
                        case HINGE:
                            hre.loadDisc(sjId, 0.01f, 0.03f, 0.03f, 10, 10, RenderStyle.FILL, new float[] { 1f, 0.0f, 0.0f, 1 }, specular,
                                    ambient, emission);
                            break;
                        case UNIVERSAL:
                            hre.loadBox(sjId, new float[] { 0.01f, 0.01f, 0.01f }, RenderStyle.FILL, new float[] { 0f, 1f, 0.0f, 1 },
                                    specular, ambient, emission);
                            break;
                        case BALL:
                            hre.loadSphere(sjId, 0.03f, 10, 10, RenderStyle.FILL, new float[] { 0f, 0f, 1f, 1 }, specular, ambient,
                                    emission);
                            break;
                        default:
                            break;
                        }
                        VJoint joint = hre.getObjectRootJoint(sjId);
                        joint.setTranslation(jointOffset);
                        hre.setObjectParent(sjId, idPrefix + "_" + ps.getId());
                    }
                }
                hre.setObjectParent(idPrefix + "_" + ps.getId(), idPrefix);
            }
        }
        /*
         * VJoint com = new VJoint(ph.getId() + "-CoM");
         * VJoint comDiff = new VJoint(ph.getId() + "-CoMDiff");
         * ph.setCOMBuffer(com.getTranslationBuffer());
         * ph.setCOMDiffBuffer(comDiff.getTranslationBuffer());
         * theVGLNode.addChild(getPHCOMDebugVisualisation(com, comDiff,getCoMMaterial()));
         * 
         * if(ph.getCOMOffsetMass()>0)
         * {
         * VJoint comOffset = new VJoint(ph.getId() + "-CoMOffset");
         * VJoint comOffsetDiff = new VJoint(ph.getId() + "-CoMOffsetDiff");
         * ph.setCOMOffsetBuffer(comOffset.getTranslationBuffer());
         * ph.setCOMOffsetDiffBuffer(comOffsetDiff.getTranslationBuffer());
         * theVGLNode.addChild(getPHCOMDebugVisualisation(comOffset, comOffsetDiff,getCoMOffsetMaterial()));
         * }
         */
        return phVJoint;

    }

}

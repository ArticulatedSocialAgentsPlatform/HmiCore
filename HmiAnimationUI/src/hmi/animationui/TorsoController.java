package hmi.animationui;

import hmi.animation.Hanim;
import hmi.animation.VJoint;
import hmi.neurophysics.Torso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * The controller handles torso rotation input from the viewer and updates the torso
 * @author hvanwelbergen
 */
public class TorsoController implements RotationsController
{
    private final ImmutableList<VJoint> thoracicJoints;
    private final ImmutableList<VJoint> torsoJoints;

    private List<VJoint> gatherJoints(String[] spineGroup, VJoint skeleton)
    {
        List<VJoint> joints = new ArrayList<>();
        for (String sid : spineGroup)
        {
            if (skeleton.getPartBySid(sid) != null)
            {
                joints.add(skeleton.getPart(sid));
            }
        }
        Collections.reverse(joints);
        return joints;
    }

    public TorsoController(VJoint model)
    {
        List<VJoint> joints = gatherJoints(Hanim.LUMBAR_JOINTS, model);
        thoracicJoints = ImmutableList.copyOf(gatherJoints(Hanim.THORACIC_JOINTS, model));
        joints.addAll(thoracicJoints);
        torsoJoints = ImmutableList.copyOf(joints);
    }

    public JointView constructTorsoView()
    {
        return new JointView(this, ImmutableList.of("Torso"));
    }

    public void setJointRotations(Collection<JointRotationConfiguration> rotations)
    {
        JointRotationConfiguration config = rotations.iterator().next();
        float q[] = new float[torsoJoints.size() * 4];
        Torso.setTorsoRollPitchYawDegrees(q, config.getRpyDeg()[0], config.getRpyDeg()[1], config.getRpyDeg()[2], torsoJoints.size()
                - thoracicJoints.size(), thoracicJoints.size());
        int i = 0;
        for (VJoint vj : torsoJoints)
        {
            vj.setRotation(q, i * 4);
            i++;
        }
    }
}

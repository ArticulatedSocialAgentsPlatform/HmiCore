package hmi.animationui;

import java.util.Collection;

import hmi.animation.VJoint;
import hmi.animation.VJointUtils;

/**
 * The controller handles input from the viewer and updates the vjoint structure
 * @author hvanwelbergen
 */
public class JointController implements RotationsController
{
    private final VJoint model;
    
    public JointController(VJoint vj)
    {
        model = vj;        
    }
    
    public JointView constructJointView()
    {
        return new JointView(this, VJointUtils.transformToSidList(model.getParts()));
    }
    
    public void setJointRotations(Collection<JointRotationConfiguration> rotations)
    {
        for(JointRotationConfiguration rotConf:rotations)
        {
            model.getPart(rotConf.getJointName()).setRotation(rotConf.getQ());
        }
    }
}

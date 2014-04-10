package hmi.animationui;

import hmi.animation.VJoint;
import hmi.animation.VJointUtils;

import java.util.Collection;
import java.util.Map;

/**
 * The controller handles input from the viewer and updates the vjoint structure
 * 
 * @author hvanwelbergen
 */
public class JointController implements RotationsController
{
    private final VJoint model;
    private JointView jv;

    public JointController(VJoint vj)
    {
        model = vj;
    }

    public JointView constructJointView()
    {
        jv = new JointView(this, VJointUtils.transformToSidList(model.getParts()));
        return jv;
    }

    /**
     * This method sets the joints of the model to the specified configurations.
     * It does not affect the sliders.
     */
    public void setJointRotations(Collection<JointRotationConfiguration> rotations)
    {
        for (JointRotationConfiguration rotConf : rotations)
        {
            model.getPart(rotConf.getJointName()).setRotation(rotConf.getQ());
        }        
    }

    /**
     * This method sets the slider of each affected joint to the values
     * specified in the JointRotationConfigurations. As a result of that, the
     * models joints adjust themselves to the sliders configurations.
     * 
     * @param rotations
     */
    public void setJointRotationConfigurations(Collection<JointRotationConfiguration> rotations)
    {
        jv.setJointRotationConfiguration(rotations);
    }

    public Collection<JointRotationConfiguration> getJointRotations()
    {
        return jv.getJointRotationConfigurations();
    }

    public Collection<JointRotationConfiguration> getSelectedJointRotations()
    {
        return jv.getSelectedJointRotationConfigurations();
    }

    /**
     * Sets the values of all sliders for the joints in <i>joints</i> to the
     * value of the corresponding joint.
     * 
     * @param righthandJoints
     */
    public void adjustSliderToModel(Collection<String> joints)
    {
        jv.adjustSliderToModel(model, joints);
    }
    
    /**
     * Excludes every joint from the animation.
     */
    public void deselectAll(){
    	jv.deselectAll();
    }
}

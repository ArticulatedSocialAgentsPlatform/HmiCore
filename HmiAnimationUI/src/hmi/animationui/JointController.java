package hmi.animationui;

import java.util.ArrayList;
import java.util.Collection;

import hmi.animation.VJoint;
import hmi.animation.VJointUtils;

/**
 * The controller handles input from the viewer and updates the vjoint structure
 * 
 * @author hvanwelbergen
 */
public class JointController implements RotationsController {
	private final VJoint model;
	// private Collection<JointRotationConfiguration> rotationConfigurations;
	private JointView jv;

	public JointController(VJoint vj) {
		model = vj;
	}

	public JointView constructJointView() {
		jv = new JointView(this, VJointUtils.transformToSidList(model
				.getParts()));
		return jv;
	}

	/**
	 * This method sets the joints of the model to the specified configurations.
	 * It does not affect the sliders.
	 */
	public void setJointRotations(
			Collection<JointRotationConfiguration> rotations) {
		for (JointRotationConfiguration rotConf : rotations) {
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
	public void setJointRotationConfigurations(
			Collection<JointRotationConfiguration> rotations) {
		jv.setJointRotationConfiguration(rotations);
	}

	public Collection<JointRotationConfiguration> getJointRotations() {
		return jv.getJointRotationConfigurations();
	}

	public Collection<JointRotationConfiguration> getSelectedJointRotations() {
		return jv.getSelectedJointRotationConfigurations();
	}
}

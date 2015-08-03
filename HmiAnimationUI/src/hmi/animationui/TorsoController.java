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
package hmi.animationui;

import hmi.animation.Hanim;
import hmi.animation.VJoint;
import hmi.animation.VJointUtils;
import hmi.neurophysics.Spine;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

/**
 * The controller handles torso rotation input from the viewer and updates the
 * torso
 * 
 * @author hvanwelbergen
 */
public class TorsoController implements RotationsController {
	private final ImmutableList<VJoint> thoracicJoints;
	private final ImmutableList<VJoint> torsoJoints;
	private final Collection<String> controlledJoints;
	private JointController jc;

	public TorsoController(VJoint model) {
		this(model, null);
	}

	public TorsoController(VJoint model, JointController jc) {
		this.jc = jc;
		List<VJoint> joints = VJointUtils.gatherJoints(Hanim.LUMBAR_JOINTS,
				model);
		thoracicJoints = ImmutableList.copyOf(VJointUtils.gatherJoints(
				Hanim.THORACIC_JOINTS, model));
		joints.addAll(thoracicJoints);
		torsoJoints = ImmutableList.copyOf(joints);
		controlledJoints = VJointUtils.transformToSidSet(torsoJoints);
	}

	public JointView constructTorsoView() {
		return new JointView(this, ImmutableList.of("Torso ("
				+ Joiner.on(",").join(
						VJointUtils.transformToSidList(torsoJoints)) + ")"));
	}

	public void setJointRotations(
			Collection<JointRotationConfiguration> rotations) {
		JointRotationConfiguration config = rotations.iterator().next();
		float q[] = new float[torsoJoints.size() * 4];
		Spine.setTorsoRollPitchYawDegrees(q, config.getRpyDeg()[0],
				config.getRpyDeg()[1], config.getRpyDeg()[2],
				torsoJoints.size() - thoracicJoints.size(),
				thoracicJoints.size());
		int i = 0;
		for (VJoint vj : torsoJoints) {
			vj.setRotation(q, i * 4);
			i++;
		}
		if (jc != null) {
			jc.adjustSliderToModel(controlledJoints);
		}
	}
}

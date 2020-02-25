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

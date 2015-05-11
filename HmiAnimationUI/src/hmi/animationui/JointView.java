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

import hmi.animation.VJoint;
import hmi.math.Quat4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import lombok.Getter;

/**
 * A user interface to set and update joint rotations
 * 
 * @author hvanwelbergen
 */
public class JointView {
	private final RotationsController controller;

	@Getter
	private JPanel panel = new JPanel();

	@Getter
	private Map<String, JointRotationPanel> rotationPanels = new HashMap<>();

	public JointView(RotationsController controller, Collection<String> joints) {
		this.controller = controller;
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		for (String vj : joints) {
			JointRotationPanel rp = new JointRotationPanel(vj, this);
			rotationPanels.put(vj, rp);
			panel.add(rp.getPanel());
		}
		panel.add(Box.createVerticalGlue());
	}

	public Collection<JointRotationConfiguration> getJointRotationConfigurations() {
		Collection<JointRotationConfiguration> rotationConfigurations = new ArrayList<JointRotationConfiguration>();
		for (JointRotationPanel rp : rotationPanels.values()) {
			rotationConfigurations.add(rp.getRotationConfiguration());
		}
		return rotationConfigurations;
	}

	public Collection<JointRotationConfiguration> getSelectedJointRotationConfigurations() {
		Collection<JointRotationConfiguration> rotationConfigurations = new ArrayList<JointRotationConfiguration>();
		for (JointRotationPanel rp : rotationPanels.values()) {
			if (rp.useInKeyFrame()) {
				rotationConfigurations.add(rp.getRotationConfiguration());
			}
		}
		return rotationConfigurations;
	}

	public void setJointRotationConfiguration(
			Collection<JointRotationConfiguration> rotationConfigurations) {
		reset();
		for (JointRotationConfiguration j : rotationConfigurations) {
			JointRotationPanel rp = rotationPanels.get(j.getJointName());
			if (rp != null) {
				rp.setJointRotationConfiguration(j);
			}
		}
	}

	public void updateAll() {
		List<JointRotationConfiguration> jrcList = new ArrayList<>();
		for (JointRotationPanel rp : rotationPanels.values()) {
			jrcList.add(rp.getRotationConfiguration());
		}
		controller.setJointRotations(jrcList);
	}

	public void update(Collection<JointRotationConfiguration> jrcList) {
		controller.setJointRotations(jrcList);
	}

	public void update(JointRotationConfiguration jrc) {
		controller.setJointRotations(Arrays.asList(jrc));
	}

	public void reset() {
		for (JointRotationPanel j : rotationPanels.values()) {
			j.reset();
		}
	}

	/**
	 * Sets the values of all sliders for the joints in <i>joints</i> to the
	 * value of the corresponding joint according to the current model pose.
	 * 
	 * @param joints
	 */
	public void adjustSliderToModel(VJoint model, Collection<String> joints) {
		float q[] = Quat4f.getQuat4f();
		for (String j : joints) {
			JointRotationPanel rp = rotationPanels.get(j);
			if (rp != null) {
				VJoint p = model.getPart(rp.getRotationConfiguration()
						.getJointName());
				p.getRotation(q);
				float rpyDeg[] = new float[3];
				Quat4f.getRollPitchYaw(q, rpyDeg);
				for (int i = 0; i < rpyDeg.length; i++) {
					rpyDeg[i] = (float) Math.toDegrees(rpyDeg[i]);
				}
				rp.adjustSliderToModel(new JointRotationConfiguration(rp
						.getRotationConfiguration().getJointName(), q, rpyDeg));
			}
		}
	}

	public void deselectAll() {
		for (JointRotationPanel p : rotationPanels.values()) {
			p.deselect();
		}
	}

}

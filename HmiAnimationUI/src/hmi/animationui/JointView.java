package hmi.animationui;

import java.util.ArrayList;
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

	private Map<String, JointRotationPanel> rotPanels = new HashMap<>();

	public JointView(RotationsController controller, Collection<String> joints) {
		this.controller = controller;
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		for (String vj : joints) {
			JointRotationPanel rp = new JointRotationPanel(vj, this);
			rotPanels.put(vj, rp);
			panel.add(rp.getPanel());
		}
		panel.add(Box.createVerticalGlue());
	}

	public Collection<JointRotationConfiguration> getJointRotationConfigurations() {
		Collection<JointRotationConfiguration> rotationConfigurations = new ArrayList<JointRotationConfiguration>();
		for (JointRotationPanel rp : rotPanels.values()) {
			rotationConfigurations.add(rp.getRotationConfiguration());
		}
		return rotationConfigurations;
	}

	public Collection<JointRotationConfiguration> getSelectedJointRotationConfigurations() {
		Collection<JointRotationConfiguration> rotationConfigurations = new ArrayList<JointRotationConfiguration>();
		for (JointRotationPanel rp : rotPanels.values()) {
			if (rp.useInKeyFrame()) {
				rotationConfigurations.add(rp.getRotationConfiguration());
			}
		}
		return rotationConfigurations;
	}

	public void setJointRotationConfiguration(
			Collection<JointRotationConfiguration> rotationConfigurations) {
		for (JointRotationConfiguration j : rotationConfigurations) {
			JointRotationPanel rp = rotPanels.get(j.getJointName());
			rp.setJointRotationConfiguration(j);
		}
	}

	public void update() {
		List<JointRotationConfiguration> jrcList = new ArrayList<>();
		for (JointRotationPanel rp : rotPanels.values()) {
			jrcList.add(rp.getRotationConfiguration());
		}
		controller.setJointRotations(jrcList);
	}
}

package hmi.animationui;

import hmi.math.Quat4f;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lombok.Getter;

/**
 * UI element for the rotation of a single joint
 * 
 * @author hvanwelbergen
 */
public class JointRotationPanel {
	@Getter
	private final JPanel panel = new JPanel();
	private final String jointName;
	private final JSlider pitchSlider;
	private final JSlider yawSlider;
	private final JSlider rollSlider;
	private final JCheckBox useInKeyFrameCheckBox;
	private final JointView jointView;

	private void setupSlider(final JSlider s) {
		JPanel sliderPanel = new JPanel();
		final JLabel sliderLabel = new JLabel("0");
		sliderLabel.setPreferredSize(new Dimension(30, 20));
		s.setPreferredSize(new Dimension(100, 20));
		sliderPanel.add(sliderLabel);
		sliderPanel.add(s);
		panel.add(sliderPanel);
		s.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				jointView.update();
				sliderLabel.setText("" + s.getValue());
				useInKeyFrameCheckBox.setSelected(true);

			}
		});
	}

	private void setupCheckbox(final JCheckBox c) {
		JPanel checkboxPanel = new JPanel();
		// final JLabel checkboxLabel = new JLabel("use:");
		// checkboxLabel.setPreferredSize(new Dimension(40, 20));
		c.setPreferredSize(new Dimension(20, 20));
		// checkboxPanel.add(checkboxLabel);
		checkboxPanel.add(c);
		panel.add(checkboxPanel);
	}

	public JointRotationPanel(String jointName, JointView jointView) {
		this.jointName = jointName;
		this.jointView = jointView;
		JLabel label = new JLabel(jointName);
		JPanel pL = new JPanel();
		pL.setLayout(new FlowLayout(FlowLayout.LEFT));
		pL.add(label);
		panel.setLayout(new GridLayout());
		panel.add(pL);

		pitchSlider = new JSlider(JSlider.HORIZONTAL, -180, 180, 0);
		yawSlider = new JSlider(JSlider.HORIZONTAL, -180, 180, 0);
		rollSlider = new JSlider(JSlider.HORIZONTAL, -180, 180, 0);

		useInKeyFrameCheckBox = new JCheckBox();
		setupSlider(pitchSlider);
		setupSlider(yawSlider);
		setupSlider(rollSlider);

		setupCheckbox(useInKeyFrameCheckBox);
	}

	public JointRotationConfiguration getRotationConfiguration() {
		float q[] = Quat4f.getQuat4f();
		float rpyDeg[] = new float[] { rollSlider.getValue(),
				pitchSlider.getValue(), yawSlider.getValue() };
		Quat4f.setFromRollPitchYawDegrees(q, rpyDeg[0], rpyDeg[1], rpyDeg[2]);
		return new JointRotationConfiguration(jointName, q, rpyDeg);
	}

	public void setJointRotationConfiguration(JointRotationConfiguration j) {
		rollSlider.setValue((int) j.getRpyDeg()[0]);
		pitchSlider.setValue((int) j.getRpyDeg()[1]);
		yawSlider.setValue((int) j.getRpyDeg()[2]);
		useInKeyFrameCheckBox.setSelected(true);
	}

	public boolean useInKeyFrame() {
		return useInKeyFrameCheckBox.isSelected();
	}

	public void reset() {
		rollSlider.setValue(0);
		pitchSlider.setValue(0);
		yawSlider.setValue(0);
		useInKeyFrameCheckBox.setSelected(false);
	}
}

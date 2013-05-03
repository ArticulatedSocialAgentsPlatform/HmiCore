package hmi.animationui;

import hmi.math.Quat4f;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
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
	private final JButton undoButton;

	/**
	 * Class used to implement und-functionality for these sliders (and
	 * checkBox).
	 * 
	 * @author sjebbara
	 * 
	 */
	private class State {
		JSlider lastUsedSlider;
		boolean previousStateOfCheckbox;
		Map<JSlider, Integer> previousSliderValues;
		Map<JSlider, Integer> currentSliderValues;

		// init maps for all sliders
		public State() {
			previousStateOfCheckbox = false;
			previousSliderValues = new HashMap<>();
			currentSliderValues = new HashMap<>();
			previousSliderValues.put(pitchSlider, 0);
			previousSliderValues.put(yawSlider, 0);
			previousSliderValues.put(rollSlider, 0);

			currentSliderValues.put(pitchSlider, 0);
			currentSliderValues.put(yawSlider, 0);
			currentSliderValues.put(rollSlider, 0);
		}
	}

	private State panelState;

	private static final String UNDO_BUTTON_TOOL_TIP = "sets slider to previous value";
	private static final String CHECKBOX_TOOL_TIP = "if checked, this joint is\n"
			+ "included in animation frame";

	private void setupSlider(final JSlider s) {
		JPanel sliderPanel = new JPanel();
		final JLabel sliderLabel = new JLabel("0");

		sliderLabel.setPreferredSize(new Dimension(50, 20));
		s.setPreferredSize(new Dimension(100, 20));
		sliderPanel.add(sliderLabel);
		sliderPanel.add(s);
		panel.add(sliderPanel);
		s.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!s.getValueIsAdjusting()) {
					panelState.previousStateOfCheckbox = useInKeyFrameCheckBox
							.isSelected();
					panelState.previousSliderValues.put(s,
							panelState.currentSliderValues.get(s) == null ? 0
									: panelState.currentSliderValues.get(s));

					panelState.currentSliderValues.put(s, s.getValue());

					panelState.lastUsedSlider = s;
					useInKeyFrameCheckBox.setSelected(true);
				}
				jointView.update();
				sliderLabel.setText("" + s.getValue());
			}
		});
	}

	private void setupCheckbox(final JCheckBox c) {
		JPanel checkboxPanel = new JPanel();
		c.setToolTipText(CHECKBOX_TOOL_TIP);
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
		undoButton = new JButton();
		panelState = new State();
		JPanel pL = new JPanel();
		pL.setLayout(new FlowLayout(FlowLayout.LEFT));
		pL.add(label);
		pL.add(undoButton);
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

		setupUndoButton();
	}

	private void setupUndoButton() {
		// undoButton.setIcon(new ImageIcon(UNDO_ICON_FILENAME));
		undoButton.setText("undo");
		// undoButton.setPreferredSize(new Dimension(50, 50));
		undoButton.setToolTipText(UNDO_BUTTON_TOOL_TIP);
		undoButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (panelState != null && panelState.lastUsedSlider != null) {
					int tmpValue = panelState.lastUsedSlider.getValue();
					boolean tmpCheckBox = useInKeyFrameCheckBox.isSelected();
					boolean tmpPrevCheckBox = panelState.previousStateOfCheckbox;

					panelState.lastUsedSlider
							.setValue(panelState.previousSliderValues
									.get(panelState.lastUsedSlider));
					panelState.previousSliderValues.put(
							panelState.lastUsedSlider, tmpValue);
					panelState.currentSliderValues.put(
							panelState.lastUsedSlider,
							panelState.lastUsedSlider.getValue());

					useInKeyFrameCheckBox.setSelected(tmpPrevCheckBox);
					panelState.previousStateOfCheckbox = tmpCheckBox;

				}
			}
		});
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

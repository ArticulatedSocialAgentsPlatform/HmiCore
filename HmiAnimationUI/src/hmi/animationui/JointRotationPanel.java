package hmi.animationui;

import hmi.math.Quat4f;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
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
	private final MouseWheelListener mouseWheelListener;

	private State panelState;

	private static final String UNDO_BUTTON_TOOL_TIP = "sets slider to previous value";
	private static final String CHECKBOX_TOOL_TIP = "if checked, this joint is\n"
			+ "included in animation frame";
	private static final int SLIDER_VALUE_WIDTH = 35;
	private static final int SLIDER_WIDTH = 100;
	private static final int LABEL_PANEL_WIDTH = 250;
	private static final int DEFAULT_HEIGHT = 20;
	private static final int CHECKBOX_WIDTH = DEFAULT_HEIGHT;

	private void setupSlider(final JSlider slider) {
		JPanel sliderPanel = new JPanel();
		final JLabel sliderLabel = new JLabel("0");

		sliderLabel.setPreferredSize(new Dimension(SLIDER_VALUE_WIDTH,
				DEFAULT_HEIGHT));
		slider.setPreferredSize(new Dimension(SLIDER_WIDTH, DEFAULT_HEIGHT));
		sliderPanel.add(sliderLabel);
		sliderPanel.add(slider);
		panel.add(sliderPanel);
		slider.addMouseWheelListener(mouseWheelListener);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!slider.getValueIsAdjusting()) {
					panelState.previousStateOfCheckbox = useInKeyFrameCheckBox
							.isSelected();
					panelState.previousSliderValues.put(
							slider,
							panelState.currentSliderValues.get(slider) == null ? 0
									: panelState.currentSliderValues
											.get(slider));

					panelState.currentSliderValues.put(slider,
							slider.getValue());

					panelState.lastUsedSlider = slider;
					useInKeyFrameCheckBox.setSelected(true);
				}
				// if(slider.hasFocus())
				// {
				jointView.update(getRotationConfiguration());
				// }
				sliderLabel.setText("" + slider.getValue());
			}
		});
	}

	private void setupCheckbox(final JCheckBox c) {
		JPanel checkboxPanel = new JPanel();
		c.setToolTipText(CHECKBOX_TOOL_TIP);
		// final JLabel checkboxLabel = new JLabel("use:");
		// checkboxLabel.setPreferredSize(new Dimension(40, 20));
		c.setPreferredSize(new Dimension(CHECKBOX_WIDTH, DEFAULT_HEIGHT));
		// checkboxPanel.add(checkboxLabel);
		checkboxPanel.add(c);
		panel.add(checkboxPanel);
	}

	public JointRotationPanel(String jointName, JointView jointView) {
		this.jointName = jointName;
		this.jointView = jointView;
		this.mouseWheelListener = new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				if (arg0.getSource() instanceof JSlider) {
					// JSlider slider = (JSlider) arg0.getSource();
					// slider.setValue(slider.getValue() +
					// arg0.getUnitsToScroll());
				}
			}
		};
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		undoButton = new JButton();
		panelState = new State();
		pitchSlider = new JSlider(JSlider.HORIZONTAL, -180, 180, 0);
		yawSlider = new JSlider(JSlider.HORIZONTAL, -180, 180, 0);
		rollSlider = new JSlider(JSlider.HORIZONTAL, -180, 180, 0);
		useInKeyFrameCheckBox = new JCheckBox();
		setupLabel(jointName);
		setupSlider(pitchSlider);
		setupSlider(yawSlider);
		setupSlider(rollSlider);
		setupCheckbox(useInKeyFrameCheckBox);

		setupUndoButton();
	}

	private void setupLabel(String jointName) {
		JLabel label = new JLabel(jointName);
		JPanel labelPanel = new JPanel();
		labelPanel.setPreferredSize(new Dimension(LABEL_PANEL_WIDTH,
				DEFAULT_HEIGHT));
		labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		labelPanel.add(label);
		labelPanel.add(undoButton);
		// panel.setLayout(new GridLayout());
		panel.add(labelPanel);
	}

	private void setupUndoButton() {
		// undoButton.setIcon(new ImageIcon(UNDO_ICON_FILENAME));
		undoButton.setText("(undo)");
		undoButton.setForeground(Color.blue);
		Font original = undoButton.getFont();
		Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>(
				original.getAttributes());
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		undoButton.setFont(original.deriveFont(attributes));
		// undoButton.setFocusPainted(false);
		undoButton.setMargin(new Insets(0, 0, 0, 0));
		undoButton.setContentAreaFilled(false);
		undoButton.setBorderPainted(false);
		undoButton.setOpaque(false);
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

	/**
	 * In contrast to setJointRotationConfiguration, this method should just set
	 * the slider to the specified values without triggering a model update.
	 * 
	 * @param j
	 */
	public void adjustSliderToModel(JointRotationConfiguration j) {

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

	/**
	 * Class used to implement undo-functionality for these sliders (and
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

}

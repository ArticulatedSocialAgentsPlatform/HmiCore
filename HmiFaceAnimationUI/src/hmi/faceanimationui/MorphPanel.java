package hmi.faceanimationui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lombok.Getter;

/**
 * UI element for the deformation of a single morph target
 * 
 * @author hvanwelbergen
 */
public class MorphPanel {
	@Getter
	private final JPanel panel = new JPanel();
	private final String morphName;
	private final JSlider morphSlider;
	private final JCheckBox useInKeyFrameCheckBox;
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
		boolean previousStateOfCheckbox = false;
		int previousSliderValue = 0;
		int currentSliderValue = 0;
	}

	private State panelState;

	private static final String UNDO_BUTTON_TOOL_TIP = "sets slider to previous value";
	private static final String CHECKBOX_TOOL_TIP = "if checked, this joint is\n"
			+ "included in animation frame";

	public MorphPanel(String morphName, final MorphView morphView) {
		this.morphName = morphName;
		panel.setLayout(new GridLayout());
		morphSlider = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);
		JPanel sliderPanel = new JPanel();
		undoButton = new JButton();
		panelState = new State();
		final JLabel sliderLabel = new JLabel("0");
		sliderLabel.setPreferredSize(new Dimension(50, 20));
		sliderPanel.add(sliderLabel);
		sliderPanel.add(morphSlider);
		JPanel pL = new JPanel();
		pL.setLayout(new FlowLayout(FlowLayout.LEFT));
		pL.add(new JLabel(morphName));
		pL.add(undoButton);
		panel.add(pL);
		panel.add(sliderPanel);
		morphSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (!morphSlider.getValueIsAdjusting()) {
					panelState.previousStateOfCheckbox = useInKeyFrameCheckBox
							.isSelected();
					panelState.previousSliderValue = panelState.currentSliderValue;

					panelState.currentSliderValue = morphSlider.getValue();

					panelState.lastUsedSlider = morphSlider;
					useInKeyFrameCheckBox.setSelected(true);
				}
				morphView.update();
				sliderLabel.setText("" + morphSlider.getValue());
			}
		});
		useInKeyFrameCheckBox = new JCheckBox();
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
							.setValue(panelState.previousSliderValue);
					panelState.previousSliderValue = tmpValue;
					panelState.currentSliderValue = panelState.lastUsedSlider
							.getValue();

					useInKeyFrameCheckBox.setSelected(tmpPrevCheckBox);
					panelState.previousStateOfCheckbox = tmpCheckBox;

				}
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

	public MorphConfiguration getMorphConfiguration() {
		return new MorphConfiguration(morphName, Math.max(0,
				Math.min((float) morphSlider.getValue() / 100.0f, 1)));
	}

	public void setMorphConfiguration(MorphConfiguration j) {
		morphSlider.setValue((int) (j.getValue() * 100));
		useInKeyFrameCheckBox.setSelected(true);
	}

	public boolean useInKeyFrame() {
		return useInKeyFrameCheckBox.isSelected();
	}

	public void reset() {
		morphSlider.setValue(0);
		useInKeyFrameCheckBox.setSelected(false);
	}
}

package hmi.faceanimationui;

import java.awt.Dimension;
import java.awt.GridLayout;

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

	public MorphPanel(String morphName, final MorphView morphView) {
		this.morphName = morphName;
		panel.setLayout(new GridLayout());
		morphSlider = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);
		JPanel sliderPanel = new JPanel();
		final JLabel sliderLabel = new JLabel("0");
		sliderLabel.setPreferredSize(new Dimension(30, 20));
		sliderPanel.add(sliderLabel);
		sliderPanel.add(morphSlider);
		panel.add(new JLabel(morphName));
		panel.add(sliderPanel);
		morphSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				morphView.update();
				sliderLabel.setText("" + morphSlider.getValue());
				useInKeyFrameCheckBox.setSelected(true);
			}
		});
		useInKeyFrameCheckBox = new JCheckBox();
		setupCheckbox(useInKeyFrameCheckBox);
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

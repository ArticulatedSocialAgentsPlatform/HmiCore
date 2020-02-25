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
package hmi.faceanimationui;

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
 * UI element for the deformation of a single morph target
 * 
 * @author hvanwelbergen
 */
public class MorphPanel {
	@Getter
	private final JPanel panel = new JPanel();
	private final String morphName;
	private final JSlider morphSlider;
	private final MorphView morphView;
	private final JCheckBox useInKeyFrameCheckBox;
	private final JButton undoButton;
	private static final int SLIDER_VALUE_WIDTH = 35;
	private static final int SLIDER_WIDTH = 200;
	private static final int LABEL_PANEL_WIDTH = 350;
	private static final int DEFAULT_HEIGHT = 20;
	private static final int CHECKBOX_WIDTH = DEFAULT_HEIGHT;

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
	private MouseWheelListener mouseWheelListener;

	private static final String UNDO_BUTTON_TOOL_TIP = "sets slider to previous value";
	private static final String CHECKBOX_TOOL_TIP = "if checked, this joint is\n"
			+ "included in animation frame";

	public MorphPanel(String morphName, final MorphView morphView) {
		this.morphName = morphName;
		this.morphView = morphView;
		this.mouseWheelListener = new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				if (arg0.getSource() instanceof JSlider) {
//					JSlider slider = (JSlider) arg0.getSource();
//					slider.setValue(slider.getValue() + arg0.getUnitsToScroll());
				}
			}
		};
		panel.setLayout(new BoxLayout(panel,BoxLayout.LINE_AXIS));
		undoButton = new JButton();
		panelState = new State();
		morphSlider = new JSlider(JSlider.HORIZONTAL, -100, 100, 0);

		setupLabel(morphName);
		setupSlider();
		useInKeyFrameCheckBox = new JCheckBox();
		setupCheckbox(useInKeyFrameCheckBox);
		setupUndoButton();
	}

	private void setupSlider() {

		JPanel sliderPanel = new JPanel();
		final JLabel sliderLabel = new JLabel("0");
		sliderLabel.setPreferredSize(new Dimension(SLIDER_VALUE_WIDTH,
				DEFAULT_HEIGHT));
		morphSlider
				.setPreferredSize(new Dimension(SLIDER_WIDTH, DEFAULT_HEIGHT));
		sliderPanel.add(sliderLabel);
		sliderPanel.add(morphSlider);
		panel.add(sliderPanel);
		morphSlider.addMouseWheelListener(mouseWheelListener);
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
	}

	private void setupLabel(String jointName) {
		JLabel label = new JLabel(jointName);
		JPanel labelPanel = new JPanel();
		labelPanel.setPreferredSize(new Dimension(LABEL_PANEL_WIDTH,
				DEFAULT_HEIGHT));
		labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		labelPanel.add(label);
		labelPanel.add(undoButton);
		panel.add(labelPanel);
	}

	private void setupUndoButton() {
		undoButton.setText("(undo)");
		undoButton.setForeground(Color.blue);
		Font original = undoButton.getFont();
		Map<TextAttribute,Object> newAttributes = new HashMap<>(original.getAttributes());		
		newAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		undoButton.setFont(original.deriveFont(newAttributes));
		undoButton.setMargin(new Insets(0, 0, 0, 0));
		undoButton.setContentAreaFilled(false);
		undoButton.setBorderPainted(false);
		undoButton.setOpaque(false);
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
		c.setPreferredSize(new Dimension(CHECKBOX_WIDTH, DEFAULT_HEIGHT));
		checkboxPanel.add(c);
		panel.add(checkboxPanel);
	}

	public MorphConfiguration getMorphConfiguration() {
		return new MorphConfiguration(morphName, Math.max(-100,
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

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
package hmi.faceanimationui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import lombok.Getter;

/**
 * A user interface to set and update morph target deformations
 * 
 * @author hvanwelbergen
 */
public class MorphView {
	@Getter
	private final JPanel panel = new JPanel();
	private final MorphController morphController;
	@Getter
	private Map<String, MorphPanel> morphPanels = new HashMap<>();

	public MorphView(MorphController mc, Collection<String> morphs) {
		this.morphController = mc;
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		for (String morph : morphs) {
			MorphPanel mp = new MorphPanel(morph, this);
			morphPanels.put(morph, mp);
			panel.add(mp.getPanel());
		}
	}

	public void update() {
		List<MorphConfiguration> mc = new ArrayList<>();
		for (MorphPanel mp : morphPanels.values()) {
			mc.add(mp.getMorphConfiguration());
		}
		morphController.update(mc);
	}

	public void setMorphConfiguration(Collection<MorphConfiguration> rotations) {
		reset();
		for (MorphConfiguration j : rotations) {
			MorphPanel rp = morphPanels.get(j.getName());
			rp.setMorphConfiguration(j);
		}
	}

	public Collection<MorphConfiguration> getMorphConfigurations() {
		Collection<MorphConfiguration> rotationConfigurations = new ArrayList<MorphConfiguration>();
		for (MorphPanel rp : morphPanels.values()) {
			rotationConfigurations.add(rp.getMorphConfiguration());
		}
		return rotationConfigurations;
	}

	public Collection<MorphConfiguration> getSelectedMorphConfigurations() {
		Collection<MorphConfiguration> rotationConfigurations = new ArrayList<MorphConfiguration>();
		for (MorphPanel rp : morphPanels.values()) {
			if (rp.useInKeyFrame()) {
				rotationConfigurations.add(rp.getMorphConfiguration());
			}
		}
		return rotationConfigurations;
	}

	public void reset() {
		for (MorphPanel m : morphPanels.values()) {
			m.reset();
		}
	}
}

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

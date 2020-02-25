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

import hmi.faceanimation.MorphFaceController;

import java.util.Collection;

/**
 * The controller handles input from the viewer and updates the face using the
 * facecontroller
 * 
 * @author hvanwelbergen
 */
public class MorphController {
	private final MorphFaceController model;
	private MorphView mv;

	public MorphController(MorphFaceController mfc) {
		model = mfc;
	}

	public MorphView constructMorphView() {
		mv = new MorphView(this, model.getPossibleFaceMorphTargetNames());
		return mv;
	}

	public void update(Collection<MorphConfiguration> morphs) {
		String morphIds[] = new String[morphs.size()];
		float values[] = new float[morphs.size()];

		int i = 0;
		for (MorphConfiguration mc : morphs) {
			morphIds[i] = mc.getName();
			values[i] = mc.getValue();
			i++;
		}
		model.setMorphTargets(morphIds, values);
	}

	public void setMorphConfigurations(
			Collection<MorphConfiguration> rotations) {
		mv.setMorphConfiguration(rotations);
	}

	public Collection<MorphConfiguration> getMorphConfigurations() {
		return mv.getMorphConfigurations();
	}

	public Collection<MorphConfiguration> getSelectedMorphConfigurations() {
		return mv.getSelectedMorphConfigurations();
	}
}

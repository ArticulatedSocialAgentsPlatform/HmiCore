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
